package com.qizegao.wxmini.exception;

import lombok.Data;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/14 20:48
 */

//自定义异常类

@Data
public class EmosException extends RuntimeException{

    private String msg; //异常消息
    private int code = 500; //状态码

    //仅包含异常消息
    public EmosException(String msg) {
        super(msg); //调用父类异常类打印异常消息
        this.msg = msg;
    }

    //包含异常消息和异常原因
    public EmosException(String msg, Throwable throwable) {
        super(msg, throwable);
        this.msg = msg;
    }

    //包含异常消息和状态码
    public EmosException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    //包含异常消息、状态码、异常原因
    public EmosException(String msg, int code, Throwable throwable) {
        super(msg, throwable);
        this.msg = msg;
        this.code = code;
    }

}
