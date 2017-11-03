package com.yadda.api.common;

/**
 * 错误基类
 *
 * @author yadda
 */
public abstract class BaseException extends RuntimeException {

    /**
     * 错误代码
     */
    private Integer code;
    /**
     * 错误描述
     */
    private String msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public BaseException() {
        super();
    }

    public BaseException(Integer code) {
        super();
        this.code = code;
    }

    public BaseException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BaseException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BaseException(Integer code, String message, Throwable cause) {
        super(message, cause);
    }

    protected BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.msg = message;
    }

}
