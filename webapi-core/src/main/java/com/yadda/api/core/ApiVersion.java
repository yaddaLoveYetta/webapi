package com.yadda.api.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API版本号
 * 
 * @ClassName ApiVersion
 * @author yadda
 * @date 2017-09-05 16:53:44 星期二
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
	float version();
}
