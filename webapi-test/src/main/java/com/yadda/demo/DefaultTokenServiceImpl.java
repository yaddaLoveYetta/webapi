package com.yadda.demo;

import com.yadda.api.core.Token;
import com.yadda.api.core.TokenHandler;
import com.yadda.api.core.TokenService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yadda
 * TokenService 默认实现
 */
@Service
public class DefaultTokenServiceImpl implements TokenService,TokenHandler {

    private volatile Map<String, Token> tokens = new HashMap<>();

    /**
     * 创建token
     *
     * @param appId
     * @param appSecret
     * @return Token
     * @Title create
     * @date 2017-10-27 22:07:47 星期五
     */
    @Override
    public Token create(String appId, String appSecret) {

        synchronized (this) {

            // 没有定时任务维护过期token清理，此处简单处理过期token
            for (Iterator<String> it = tokens.keySet().iterator(); it.hasNext(); ) {

                String key = it.next();

                Token t = tokens.get(key);

                if (t.getExpiresTime().before(new Date()) || t.getAppId().equals(appId) || t.getAppSecret().equals(appSecret)) {
                    //appId,appSecret 不可重复，每次请求都要生成新的token
                    it.remove();
                }
            }
        }


        Token token = new Token();

        Random random = new Random();
        Calendar calendar = Calendar.getInstance();
        String accessToken = UUID.randomUUID().toString();

        token.setAppId(appId);
        token.setAppSecret(appSecret);
        token.setAccessToken(accessToken);
        token.setCreateTime(calendar.getTime());
        //token有效时间1小时
        calendar.add(Calendar.HOUR, 1);
        token.setExpiresTime(calendar.getTime());

        tokens.put(accessToken, token);

        return token;
    }

    /**
     * 获取指定token
     *
     * @param token
     * @return
     */
    @Override
    public Token get(String token) {

        return tokens.get(token);
    }

    /**
     * 清楚指定token
     *
     * @param token
     */
    @Override
    public void remove(String token) {
        tokens.remove(token);
    }

    /**
     * 清楚所有token
     */
    @Override
    public void removeAll() {
        tokens.clear();
    }

    /**
     * token检验成功后调用此方法
     *
     * @param token
     */
    @Override
    public void tokenCheckSuccess(Token token) {

    }

    /**
     * token调用失败调用此方法
     *
     * @param token
     */
    @Override
    public void tokenCheckFail(Token token) {

    }
}