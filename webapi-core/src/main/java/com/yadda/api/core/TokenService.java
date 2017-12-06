package com.yadda.api.core;

/**
 * @author yadda
 */
public interface TokenService {

    /**
     * 创建token
     *
     * @param appId
     * @param appSecret
     * @return
     */
    Token create(String appId, String appSecret);

    /**
     * 获取token
     *
     * @param token
     * @return Token
     * @Title get
     * @date 2017-10-27 22:07:55 星期五
     */
    Token get(String token);

    /**
     * 清除指定token
     *
     * @param token
     */
    void remove(String token);

    /**
     * 清除所有token
     */
    void removeAll();

}
