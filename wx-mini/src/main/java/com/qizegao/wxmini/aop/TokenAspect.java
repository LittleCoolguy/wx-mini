package com.qizegao.wxmini.aop;

import com.qizegao.wxmini.config.shiro.ThreadLocalToken;
import com.qizegao.wxmini.util.R;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/18 22:57
 */

//拦截所有web方法的返回值，在返回的R对象中添加从ThreadLocal中获取的新令牌

@Aspect
@Component
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;

    //可重用的切入点表达式
    @Pointcut("execution(public * com.qizegao.wxmini.controller.*.*(..))")
    public void aspect() {

    }

    @Around("aspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        R r = (R) point.proceed();
        String token = threadLocalToken.getToken();
        if (token != null) {
            r.put("token", token); //R对象中添加新令牌
            threadLocalToken.clear();
        }
        return r;
    }
}