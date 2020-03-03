#### SpringBoot + SpringDataJPA + Shiro +JWT 整合案例

`主要实现了jwt和shiro的整合，关闭了shiro的session，全局捕捉异常，实现前后端分离，
解决跨域问题，RBAC权限模型，实现快速开发 ，Shiro基于SpringBoot +JWT搭建简单的restful服务
`

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


######JWTFilter
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