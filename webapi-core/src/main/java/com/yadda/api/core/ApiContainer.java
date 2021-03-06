package com.yadda.api.core;

import com.yadda.api.common.ApiException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

/**
 * web api IOC大仓库
 *
 * @author yadda
 */
public class ApiContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiContainer.class);
    private ApplicationContext applicationContext;
    /**
     * APi接口集合
     */
    private HashMap<String, ApiRunnable> apiMap = new HashMap<String, ApiRunnable>();

    public ApiContainer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 解析出所有api
     */
    public void loadApi() throws ApiException {

        // spring ioc容器中所有bean
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        Class<?> type;

        // 反射
        for (String beanName : beanNames) {

            type = applicationContext.getType(beanName);

            // 类上的APIMapping注解
            ApiMapping classApiMapping = type.getAnnotation(ApiMapping.class);

            // 类上的ApiVersion注解
            ApiVersion classApiVersion = type.getAnnotation(ApiVersion.class);

            for (Method method : type.getDeclaredMethods()) {

                // 方法上的APIMapping注解
                ApiMapping methodApiMapping = method.getAnnotation(ApiMapping.class);
                // 方法上的ApiVersion注解
                ApiVersion methodApiVersion = method.getAnnotation(ApiVersion.class);

                if (methodApiMapping != null) {
                    // 方法上必须有ApiMapping注解，否则该方法不被解析为合法api
                    // 类上的注解加上方法上的注解构成最终调用方法的Api名称
                    addApiItem(classApiMapping, methodApiMapping, classApiVersion, methodApiVersion, beanName, method);
                }
            }
        }
    }

    /**
     * 添加api</br>
     * <p>
     * APIMapping配置的api最终将会调用beanName中的method方法</br>
     * <p>
     * 如果方法上跟类上都配置了版本信息，方法上的版本号将覆盖类上的版本号
     *
     * @param classApiMapping  类上 api配置
     * @param methodApiMapping 方法上api配置
     * @param classApiVersion  类上 api版本配置
     * @param methodApiVersion 方法上api版本配置
     * @param beanName         bean在spring中的名称
     * @param method           api调用的真实方法
     * @date 2017-09-04 10:37:55 星期一
     */
    private void addApiItem(ApiMapping classApiMapping, ApiMapping methodApiMapping, ApiVersion classApiVersion,
                            ApiVersion methodApiVersion, String beanName, Method method) throws ApiException {

        //执行接口返回模型规范的自动校验
        ApiSpecificationCheck(method);

        // method上的版本号优先级高于类上的版本号
        ApiVersion version = methodApiVersion == null ? classApiVersion : methodApiVersion;

        String ver = "";
        if (version != null) {
            ver = version.version().trim().replace(" ", "");
        }

        // 类上的注解加上方法上的注解构成最终调用方法的Api名称,忽略空格
        String apiName = methodApiMapping.value().trim().replace(" ", "");

        if (classApiMapping != null) {

            String classApiName = classApiMapping.value().trim().replace(" ", "");

            if (!StringUtils.endsWith(classApiName, ".")) {
                apiName = classApiName + "." + apiName;
            } else {
                apiName = classApiName + apiName;
            }

            if (StringUtils.isBlank(apiName)) {
                // 不合法的Api名称直接忽略
                return;
            }

        }

        // api加上版本号
        if (!ver.isEmpty()) {
            apiName = apiName + "_" + ver;
        }

        // ApiMapping配置重名校验-不支持存在重复的ApiMapping配置
        if (apiMap.containsKey(apiName)) {
            ApiRunnable apiRunExt = apiMap.get(apiName);
            throw new ApiException(
                    "API:[" + apiName + "]在" + apiRunExt.getTargetName() + "与" + beanName + "中存在重复配置，请检查配置");
        }

        ApiRunnable apiRun = new ApiRunnable();
        apiRun.apiName = apiName;
        apiRun.methodType = methodApiMapping.methodType();
        apiRun.targetMethod = method;
        apiRun.targetName = beanName;
        apiRun.signCheck = methodApiMapping.signCheck();

        apiMap.put(apiName, apiRun);

        LOGGER.info("add api " + apiName);

    }

    /**
     * api接口规范校验
     *
     * @param method Method
     * @throws ApiException ApiException
     */
    private void ApiSpecificationCheck(Method method) throws ApiException {

        //1:接口不能直接返回Object模糊类型
        if (method.getReturnType().equals(Object.class)) {
            throw new ApiException(String.format("你的接口模型不符合规范:[%s] 请修正方法:[%s]", "不允许直接返回Object类型", method.getName()));
        }

        //2:接口返回类型不能有Object模糊类型属性
        for (Field field : method.getReturnType().getDeclaredFields()) {
            if (field.getType().equals(Object.class)) {
                throw new ApiException(
                        String.format("你的接口模型不符合规范:[%s] 请修正方法:[%s]", "返回类型不允许定义Object属性", method.getName()));
            }
        }

        // 4:接口方法入参不能直接用Object模糊类型
        for (Class<?> aClass : method.getParameterTypes()) {
            checkDeclaredFields(aClass, method);
        }
    }

    /**
     * 检验method方法参数是否包含object模糊类型
     *
     * @param clazz  Class
     * @param method Method
     */
    private void checkDeclaredFields(Class clazz, Method method) throws ApiException {

        if (Collection.class.isAssignableFrom(clazz)) {
            // 集合类型忽略校验
            return;
        }
        // 接口方法入参类型不能是Object模糊类型
        if (clazz.equals(Object.class)) {
            throw new ApiException(
                    String.format("你的接口模型不符合规范:[%s] 请修正方法:[%s]", "接口方法入参不允许定义Object类型", method.getName()));
        }

        // 接口方法入参类型不能有Object模糊类型属性
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().equals(Object.class)) {
                throw new ApiException(
                        String.format("你的接口模型不符合规范:[%s] 请修正方法:[%s]", "接口方法入参类型不允许定义Object类型属性", method.getName()));
            }
            checkDeclaredFields(field.getType(), method);
        }
    }

    /**
     * 查找无版本控制api
     *
     * @param apiName apiName
     * @return ApiRunnable
     * @date 2017-09-06 13:40:42 星期三
     */
    public ApiRunnable findApiRunnable(String apiName) {
        return apiMap.get(apiName);
    }

    /**
     * 查找对应版本api
     *
     * @param apiName apiName
     * @param version version
     * @return ApiRunnable
     * @date 2017-09-06 13:40:54 星期三
     */
    public ApiRunnable findApiRunnable(String apiName, String version) {
        if (StringUtils.isBlank(version)) {
            return apiMap.get(apiName);
        }
        return apiMap.get(apiName + "_" + version);
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * api执行器，用于执行对应的api方法
     */
    @Setter
    @Getter
    @ToString
    @Accessors(chain = true)
    public class ApiRunnable {

        /**
         * api名称
         */
        String apiName;
        /**
         * api 请求类型
         */
        MethodEnum methodType;
        /**
         * IOC bean 名称
         */
        String targetName;
        /**
         * 方法实现实例
         */
        Object target;
        /**
         * 具体方法
         */
        Method targetMethod;
        /**
         * 方法是否需要进行签名验证
         */
        boolean signCheck;

        public Object execute(Object... args)
                throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {

            if (target == null) {
                // 到springIOC容器中找bean
                target = applicationContext.getBean(targetName);
            }

            return targetMethod.invoke(target, args);

        }

    }
}
