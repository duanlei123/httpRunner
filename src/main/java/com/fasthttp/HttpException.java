package com.fasthttp;

/**
 * @Author: duanlei
 * @Date: 2019/3/11 11:30 AM
 * @Version 1.0
 */
public class HttpException extends Exception{

    private static final long serialVersionUID = -238091758285157331L;

    private String errCode;
    private String errMsg;

    public HttpException() {
        super();
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(String errCode, String errMsg) {
        super(errCode + ":" + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }
}
