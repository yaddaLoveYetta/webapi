package com.yadda.api.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解告诉我们的API网关这个方法需要往外暴露的名称
 *
 * @author yadda
 * @ClassName ApiMapping
 * @date 2017-09-01 14:41:25 星期五
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMapping {
    /**
     * 暴露的API接口名称
     *
     * @return String
     * @Title value
     * @date 2017-09-04 11:39:46 星期一
     */
    String value();

    /**
     * 请求类型
     *
     * @return
     */
    MethodEnum methodType() default MethodEnum.ALL;

    /**
     * 接口描述
     *
     * @return String
     * @Title description
     * @date 2017-09-04 11:40:36 星期一
     */
    String description() default "";

    /**
     * 是否要进行签名验证
     *
     * @return boolean
     * @Title useLogin
     * @date 2017-10-27 23:05:49 星期五
     */
    boolean useLogin() default false;
}
