package com.coderman.realm;

import com.coderman.config.JWTToken;
import com.coderman.modal.Permission;
import com.coderman.modal.Role;
import com.coderman.modal.User;
import com.coderman.repository.UserRepository;
import com.coderman.util.JWTUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
