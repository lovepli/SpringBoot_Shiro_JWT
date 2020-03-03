#### SpringBoot + SpringDataJPA + Shiro +JWT 整合案例

`主要实现了jwt和shiro的整合，关闭了shiro的session，全局捕捉异常，实现前后端分离，
解决跨域问题，RBAC权限模型，实现快速开发 ，Shiro基于SpringBoot +JWT搭建简单的restful服务
`
###### GlobalExceptionHandler(全局异常捕捉)
```java

/**
 * @Author zhangyukang
 * @Date 2020/3/2 8:21
 * @Version 1.0
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {


    //处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseBean BindExceptionHandler(BindException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return ResponseBean.error(message);
    }

    //处理请求参数格式错误 @RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseBean ConstraintViolationExceptionHandler(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining());
        return ResponseBean.error(message);
    }

    //处理请求参数格式错误 @RequestBody上validate失败后抛出的异常是MethodArgumentNotValidException异常。
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseBean MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return ResponseBean.error(message);
    }

    /**
     * 处理自定义的业务异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public  ResponseBean bizExceptionHandler(HttpServletRequest req, BizException e){
        return ResponseBean.error(e.getErrorCode(),e.getErrorMsg());
    }

    /**
     * shiro的异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public ResponseBean handle401(ShiroException e) {
        return new ResponseBean(401, e.getMessage(), null);
    }


    /**
     * 自定义的授权异常
     * UnauthorizedException
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseBean handle401(UnauthorizedException e) {
        return new ResponseBean(401, e.getMessage(), null);
    }

    /**
     * 捕捉其他所有异常
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseBean globalException(HttpServletRequest request, Throwable ex) {
        return new ResponseBean(getStatus(request).value(), "服务器异常"+ex.getMessage(), null);
    }

    /**
     * 获取状态码
     * @param request
     * @return
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}

```


###### UserController (用户API接口)
```java

/**
 * @Author zhangyukang
 * @Date 2020/3/1 14:21
 * @Version 1.0
 **/
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 用户登入
     * @param userVO
     * @return
     */
    @PostMapping("/login")
    public ResponseBean login(@Valid UserVO userVO){
        @NotNull(message = "用户名不为空") String username = userVO.getUsername();
        @NotNull(message = "密码不为空") String password = userVO.getPassword();
        User user = userService.findByUsername(username);
        String salt="";
        if(user!=null){
            salt=user.getPasswordSalt();
        }
        //生成token
        String token = JWTUtils.sign(username, MD5Utils.md5Encryption(password,salt));
        //执行登入：（出现异常被全局异常捕捉）
        SecurityUtils.getSubject().login(new JWTToken(token));

        return ResponseBean.success(token);
    }

    /**
     * 获取用户菜单
     * @return
     */
    @GetMapping("/listMenu")
    public ResponseBean listMenu(){
        List<MenuVO> menuVOList=userService.findUserMenus();
        return ResponseBean.success(menuVOList);
    }

    /**
     * 用户列表
     * @return
     */
    @GetMapping("/list")
    public ResponseBean listUser(UserPageQueryVO pageQueryVO){
        PageVO<User> page = userService.findUserByQueryVO(pageQueryVO);
        return ResponseBean.success(page);
    }

    /**
     * 删除用户
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public ResponseBean delete(@PathVariable Long id){
        userService.deleteById(id);
        return ResponseBean.success();
    }

    /**
     * 保存用户
     * @param user
     * @return
     */
    @PostMapping("/add")
    public ResponseBean add(User user){
        userService.add(user);
        return ResponseBean.success();
    }

}
```

###### MyRealm

```java


@Service
public class MyRealm extends AuthorizingRealm {


    private UserRepository userRepository;

    @Autowired
    public void setUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 大坑！，必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        User user = (User) principals.getPrimaryPrincipal();
        if(user.getType()==0){
            //超级管理员，系统最高权限
            authorizationInfo.addStringPermission("*:*");
        }else {
            for (Role role : user.getRoles()) {                                 //获取 角色
                authorizationInfo.addRole(role.getName());                      //添加 角色
                for (Permission permission : role.getPermissions()) {           //获取 权限
                    authorizationInfo.addStringPermission(permission.getName());//添加 权限
                }
            }
        }
        return authorizationInfo;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String username = JWTUtils.getUsername(token);
        if (username == null) {
            throw new AuthenticationException(" token失效，请重新登入！");
        }

        User userBean = userRepository.findByUsername(username);
        if (userBean == null) {
            throw new AuthenticationException("用户不存在!");
        }

        if (! JWTUtils.verify(token, username, userBean.getPassword())) {
            throw new AuthenticationException("用户名或密码错误!");
        }

        return new SimpleAuthenticationInfo(userBean, token, getName());
    }
}

```


###### JWTFilter
```java

@Slf4j
public class JWTFilter extends BasicHttpAuthenticationFilter {
    /**
     * 认证之前执行该方法
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = SecurityUtils.getSubject();
        return null != subject && subject.isAuthenticated();
    }

    /**
     * 认证未通过执行该方法
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response){
        //完成token登入
        //1.检查请求头中是否含有token
        HttpServletRequest httpServletRequest= (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization");
        //2. 如果客户端没有携带token，拦下请求
        if(null==token||"".equals(token)){
            response401(response,"无权访问(Unauthorized):请求头中没有token");
            return false;
        }
        //3. 如果有，对进行进行token验证
        JWTToken jwtToken = new JWTToken(token);
        try {
            SecurityUtils.getSubject().login(jwtToken);
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            response401(response,e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * 无需转发，直接返回Response信息
     */
    private void response401(ServletResponse response, String msg) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = httpServletResponse.getWriter()) {
            String data = new Gson().toJson(new ResponseBean(HttpStatus.UNAUTHORIZED.value(), "无权访问(Unauthorized):" + msg, null));
            out.append(data);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
```
