package com.yadda.demo;

import com.yadda.api.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yadda
 */
@Service
@ApiMapping(value = "test")
public class ServiceDemo {

    @Autowired
    private TokenService defaultTokenService;

    @ApiVersion(version = 1.0f)
    @ApiMapping(value = "setAge", useLogin = true)
    public void setAge(Student student, int age, ApiRequest apiRequest) {

        student.setAge(age);
    }

    /**
     * 平台给每位用户分配一个appId，appSecret,用户用appId，appSecret向平台申请token，用token操作其他业务接口
     *
     * @param appId
     * @param appSecret
     * @return
     */
    @ApiMapping(value = "getToken", useLogin = false)
    public String getToken(String appId, String appSecret) {

//        if ("test".equalsIgnoreCase(appId) && "123".equalsIgnoreCase(appSecret)) {

        if (true) {
            // 模拟用户名密码验证成功

            // 成成token
            Token token = defaultTokenService.create(appId, appSecret);

            return token.getAccessToken();

        }
        return null;
    }

    @ApiMapping(value = "getInfo", useLogin = true)
    @ApiVersion(version = 2.0f)
    public Student getInfo(String name) {
        Student s = new Student();
        s.setName(name);
        return s;
    }

    @ApiMapping(value = "getName", useLogin = true)
    public String getName(String name) {
        return "收到消息 name =" + name;
    }

    @ApiMapping(value = "test")
    public int test(List<String> list) {
        return list.size();
    }

}
