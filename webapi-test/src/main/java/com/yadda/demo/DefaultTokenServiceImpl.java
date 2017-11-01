package com.yadda.demo;

import com.yadda.api.core.Token;
import com.yadda.api.core.TokenService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author yadda
 * TokenService 默认实现
 */
@Service
public class DefaultTokenServiceImpl implements TokenService {

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

        Token token = new Token();

        Random random = new Random();
        Calendar calendar = Calendar.getInstance();
        String accessToken = UUID.randomUUID().toString();

        token.setAppId(appId);
        token.setAppSecret(appSecret);
        token.setAccessToken(accessToken);
        token.setCreateTime(calendar.getTime());

        calendar.add(Calendar.HOUR, 8);
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

}