package com.yadda.api.core;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yadda
 * TokenService 默认实现
 */
@Service(value = "DefaultTokenServiceImpl")
public class DefaultTokenServiceImpl implements TokenService {

    private volatile Map<String, Object> tokens = new HashMap<>();

    @Override
    public Token createToken() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Token getToken(String token) {
        // TODO Auto-generated method stub
        return null;
    }

}
