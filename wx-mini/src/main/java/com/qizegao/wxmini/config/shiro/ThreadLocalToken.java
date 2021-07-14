package com.qizegao.wxmini.config.shiro;

import org.springframework.stereotype.Component;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/18 22:30
 */

//保存OAuth2Filter中生成的新Token，用来传递给AOP切面类

@Component
public class ThreadLocalToken {

    //保存新Token
    private ThreadLocal<String> local=new ThreadLocal<>();

    public void setToken(String token){
        local.set(token);
    }

    public String getToken(){
        return local.get();
    }

    public void clear(){
        local.remove();
    }
}