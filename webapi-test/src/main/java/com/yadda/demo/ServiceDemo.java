package com.yadda.demo;

import com.yadda.api.core.ApiMapping;
import com.yadda.api.core.ApiRequest;
import com.yadda.api.core.ApiVersion;
import com.yadda.api.core.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yadda
 */
@Service
@ApiMapping(value = "test")
public class ServiceDemo {

    @Autowired
    private DefaultTokenServiceImpl defaultTokenService;

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

        if ("test".equalsIgnoreCase(appId) && "123".equalsIgnoreCase(appSecret)) {

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

    @ApiMapping(value = "getName")
    public String getName() {
        return "fsdfdsafds";
    }

    @ApiMapping(value = "test")
    public void test() {

    }

}
