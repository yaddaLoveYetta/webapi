package com.yadda.api.core;
// api IOC大仓库

import com.yadda.api.common.ApiException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;

public class ApiContainer {

    private ApplicationContext applicationContext;
    // APi接口集合
    private HashMap<String, ApiRunnable> apiMap = new HashMap<String, ApiRunnable>();

    public ApiContainer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void loadApiFromSpringbeans() {

        // spring ioc容器中所有bean
        String[] names = applicationContext.getBeanDefinitionNames();

        Class<?> type;

        // 反射
        for (String name : names) {

            type = applicationContext.getType(name);

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
                    // 类上的注解加上方法上的注解构成最终调用方法的Api名称
                    addApiItem(classApiMapping, methodApiMapping, classApiVersion, methodApiVersion, name, method);
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
     * @return void
     * @Title addApiItem
     * @date 2017-09-04 10:37:55 星期一
     */
    private void addApiItem(ApiMapping classApiMapping, ApiMapping methodApiMapping, ApiVersion classApiVersion, ApiVersion methodApiVersion, String beanName, Method method) {

        // 执行接口返回模型规范的自动校验
        for (Field f : method.getReturnType().getDeclaredFields()) {
            if (f.getType().equals(Object.class)) {
                throw new RuntimeException("你的接口返回模型不符合规范，请改正:" + method.getName());
            }
        }
        // 执行接口参数规范的自动校验
        Type[] parameterTypes = method.getGenericParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {

            if (Object.class.equals(parameterTypes[i].getClass())) {
                throw new RuntimeException("你的接口模型不符合规范，请改正:" + method.getName());
            }
            if (String.class.equals(parameterTypes[i].getClass())) {
                throw new RuntimeException("你的接口模型不符合规范，请改正:" + method.getName());
            }
        }

        ApiVersion version = methodApiVersion == null ? classApiVersion : methodApiVersion;
        String ver = "";
        if (version != null) {
            ver = String.valueOf(version.version());
        }

        // 类上的注解加上方法上的注解构成最终调用方法的Api名称
        String apiName = methodApiMapping.value().trim();

        if (classApiMapping != null) {

            String classApiName = classApiMapping.value().trim();

            if (!classApiName.endsWith(".")) {
                classApiName += ".";
            }

            apiName = classApiName + apiName;
        }

        // api加上版本号
        if (!ver.isEmpty()) {
            apiName = apiName + "_" + ver;
        }

        // ApiMapping配置重名校验-不支持存在重复的ApiMapping配置

        if (apiMap.containsKey(apiName)) {
            ApiRunnable apiRunExt = apiMap.get(apiName);
            throw new ApiException("API:[" + apiName + "]在" + apiRunExt.getTargetName() + "与" + beanName + "中存在重复配置，请检查配置");
        }

        ApiRunnable apiRun = new ApiRunnable();
        apiRun.apiName = apiName;
        apiRun.targetMethod = method;
        apiRun.targetName = beanName;
        apiRun.needCheck = methodApiMapping.useLogin();
        apiMap.put(apiName, apiRun);

    }

    /**
     * 查找无版本控制api
     *
     * @param apiName
     * @return ApiRunnable
     * @Title findApiRunnable
     * @date 2017-09-06 13:40:42 星期三
     */
    public ApiRunnable findApiRunnable(String apiName) {
        return apiMap.get(apiName);
    }

    /**
     * 查找对应版本api
     *
     * @param apiName
     * @param version
     * @return ApiRunnable
     * @Title findApiRunnable
     * @date 2017-09-06 13:40:54 星期三
     */
    public ApiRunnable findApiRunnable(String apiName, String version) {
        if (version == null) {
            return apiMap.get(apiName);
        }
        return apiMap.get(apiName + "_" + version);
    }

    // public List<ApiRunnable> findApiRunnables(String apiName) {
    // if (apiName == null) {
    // throw new IllegalArgumentException("api name must not null");
    // }
    //
    // List<ApiRunnable> list = new ArrayList<ApiRunnable>();
    // }

    public boolean containApi(String apiName, String version) {
        return apiMap.containsKey(apiName + "_" + version);
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 用于执行对应的api方法
    public class ApiRunnable {

        String apiName; // api名称
        String targetName; // IOC bean 名称
        Object target; // 方法实现实例
        Method targetMethod;// 具体方法
        /**
         * 方法是否需要进行签名验证
         */
        boolean needCheck; // 是否要进行签名验证

        public Object execute(Object... args) throws InvocationTargetException, IllegalAccessException, IllegalArgumentException {

            if (target == null) {
                // 到springIOC容器中找bean
                target = applicationContext.getBean(targetName);
            }

            return targetMethod.invoke(target, args);

        }

        public Class<?>[] getParamTypes() {
            return targetMethod.getParameterTypes();
        }

        public String getApiName() {
            return apiName;
        }

        public void setApiName(String apiName) {
            this.apiName = apiName;
        }

        public String getTargetName() {
            return targetName;
        }

        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }

        public Object getTarget() {
            return target;
        }

        public void setTarget(Object target) {
            this.target = target;
        }

        public Method getTargetMethod() {
            return targetMethod;
        }

        public void setTargetMethod(Method targetMethod) {
            this.targetMethod = targetMethod;
        }

        public boolean isNeedCheck() {
            return needCheck;
        }

        public void setNeedCheck(boolean needCheck) {
            this.needCheck = needCheck;
        }

    }
}
