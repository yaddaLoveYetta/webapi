package com.yadda.api.core;

/**
 * token相关操作
 *
 * @author yadda
 */
public interface TokenHandler {

    /**
     * token检验成功后调用此方法
     *
     * @param token
     */
    void tokenCheckSuccess(Token token);

    /**
     * token调用失败调用此方法
     *
     * @param token
     */
    void tokenCheckFail(Token token);
}
