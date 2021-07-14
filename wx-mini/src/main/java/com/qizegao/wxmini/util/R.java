package com.qizegao.wxmini.util;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/14 20:56
 * 封装服务器返回数据的统一格式
 */
public class R extends HashMap {

    //构造器
    public R() {
        put("code", HttpStatus.SC_OK);
        put("msg", "success");
    }

    @Override
    public R put(Object key, Object value) {
        super.put(key, value);
        return this;
    }

    //ok方法
    public static R ok() {
        return new R();
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    //error方法
    public static R error(int code, String msg) {
        R r = new R();
        r.put("msg", msg);
        r.put("code",code);
        return r;
    }

    public static R error(String msg) {
        //默认出错使用状态码500
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static R error() {
        //使用500状态码和固定的msg
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }
}
