package com.yadda.api.common;

/**
 * Api调用异常
 *
 * @author yadda
 */
public class ApiException extends BaseException {

    //private static final Integer CODE = ResultCode.SYS_ERROR;

    private Integer code;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public void setCode(Integer code) {
        this.code = code;
    }

    public ApiException(String msg) {
        super(msg);
    }

    public ApiException(Integer code, String msg) {
        super(code, msg);
        this.code = code;
    }

    public ApiException(Integer code, String msg, Throwable cause) {
        super(code, msg, cause);
        this.code = code;
    }

}
