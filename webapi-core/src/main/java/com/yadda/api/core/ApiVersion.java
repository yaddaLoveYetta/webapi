package com.yadda.api.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API版本号
 *
 * @author yadda
 * @date 2017-09-05 16:53:44 星期二
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    /**
     * api版本，同时作用在类及方法上时，已方法上的为准，将与ApiMapping组合成ApiMapping+"_"+version作为api名称
     *
     * @return String
     */
    String version();
}
