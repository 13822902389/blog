package com.example.shiro;

import com.example.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自定义的UserRealm
 */
@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    /**
     * 授权
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取当前登录的用户
        AccountProfile profile = (AccountProfile) principals.getPrimaryPrincipal();

        // 给id为2的admin赋予admin角色
        if (profile.getId() == 2) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRole("admin");
            return info;
        }

        return null;
    }

    /**
     * 权限认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

        AccountProfile profile = userService.login(usernamePasswordToken.getUsername(), String.valueOf(usernamePasswordToken.getPassword()));

        //登录成功之后把user对象放进session里面
        SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

        //进行验证，将正确数据给shiro处理
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(profile, token.getCredentials(), getName());
        return info;
    }
}
