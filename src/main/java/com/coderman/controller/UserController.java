package com.coderman.controller;

import com.coderman.config.JWTToken;
import com.coderman.modal.User;
import com.coderman.service.UserService;
import com.coderman.util.JWTUtils;
import com.coderman.util.MD5Utils;
import com.coderman.vo.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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
