package com.qizegao.wxmini.config.shiro;

import com.qizegao.wxmini.db.pojo.TbUser;
import com.qizegao.wxmini.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/18 22:20
 */

//自定义Realm类
@Component
public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection collection) {
        TbUser user= (TbUser) collection.getPrimaryPrincipal();
        int userId=user.getId();
        Set<String> permsSet=userService.searchUserPermissions(userId);
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证(验证登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String accessToken=(String)token.getPrincipal();
        int userId=jwtUtil.getUserId(accessToken);
        TbUser user=userService.searchById(userId);

        if(user==null){
            throw new LockedAccountException("用户不存在！");
        }
        SimpleAuthenticationInfo info=new SimpleAuthenticationInfo(user,accessToken,getName());
        return info;
    }
}
