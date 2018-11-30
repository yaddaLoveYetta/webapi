package com.yadda.api.common;

/**
 * Api调用异常
 *
 * @author yadda
 */
public class ApiException extends Exception {

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

    public ApiException(String msg) {
        super(msg);
    }

    public ApiException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public ApiException(Integer code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

}
