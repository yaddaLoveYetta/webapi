package com.yadda.api.common;

/**
 * 前端错误码
 *
 * @author yadda
 */
public class ResultCode {

    /**
     * 业务处理成功
     */
    public static final int SUCCESS = 200;
    /**
     * 未知错误
     */
    public static final int UNKNOWN_ERROR = 1;
    /**
     * 无效的参数
     */
    public static final int INVALID_PARAMETER = 2;
    /**
     * appId无效
     */
    public static final int INVALID_APPID = 3;
    /**
     * 无效的签名
     */
    public static final int INCORRECT_SIGNATURE = 4;
    /**
     * 参数太多
     */
    public static final int TOO_MANY_PARAMETERS = 5;
    /**
     * 无效的token
     */
    public static final int ACCESS_TOKEN_INVALID_OR_NO_LONGER_VALID = 6;
    /**
     * token已过期
     */
    public static final int ACCESS_TOKEN_EXPIRED = 7;
    /**
     * 业务处理异常
     */
    public static final int BUSINESS_LOGIC_ERROR = 8;

}
