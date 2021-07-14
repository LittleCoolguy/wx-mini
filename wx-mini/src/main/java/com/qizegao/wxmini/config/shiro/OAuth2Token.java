package com.qizegao.wxmini.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/18 22:18
 */

//令牌封装成认证对象

public class OAuth2Token implements AuthenticationToken {

    //用来保存Token
    private String token;

    public OAuth2Token(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
