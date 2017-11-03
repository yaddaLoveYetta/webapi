package com.yadda.api.core;

import com.yadda.api.common.*;
import com.yadda.api.core.ApiContainer.ApiRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author yadda
 */
public class ApiHandler implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);

    private static final String PARAMS = "params";
    private static final String METHOD = "method";
    private static final String VERSION = "version";

    private ApiContainer apiContainer;
    private final ParameterNameDiscoverer parameterUtil;

    private TokenService tokenService;


    public TokenService getTokenService() {
        return tokenService;
    }

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public ApiHandler() {
        parameterUtil = new LocalVariableTableParameterNameDiscoverer();
    }

    @Override
    public void afterPropertiesSet() {
        apiContainer.loadApiFromSpringbeans();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        apiContainer = new ApiContainer(context);
        tokenService = context.getBean(TokenService.class);
        logger.info("init apiContainer & tokenService success");
        logger.info("tokenService:" + tokenService);
    }

    public void handle(HttpServletRequest request, HttpServletResponse response) {
        // 系统参数校验
        String params = request.getParameter(PARAMS);
        String method = request.getParameter(METHOD);

        Result r = new Result();

        ApiRunnable apiRun = null;

        // 增加签名验证--接口签名对象
        ApiRequest apiRequest = null;

        try {

            apiRun = sysParamsValdate(request);
            logger.info("请求接口={" + method + "} 参数=" + params + "");

            if (apiRun.needCheck) {
                // 方法需要进行签名验证

                // 构建ApiRequest
                apiRequest = buildApiRequest(request);
                // 签名验证
                if (apiRequest.getAccessToken() != null) {
                    // 签名验证--不通过会抛出异常
                    signCheck(apiRequest);
                }

                if (!apiRequest.isChecked()) {
                    throw new ApiException("调用失败，用户未登录");
                }
            }

            Object[] args = buildParams(apiRun, params, request, response);

            Object result = apiRun.execute(args);

            // result统一格式化成json,如果接口返回基本类型或者不能序列化成json类型eg:String,int,boolean...用data做key格式化成json形式
            result = resultWarp(result);

            r.setCode(ResultCode.SUCCESS);
            r.setMsg("Success");
            r.setData(result);

        } catch (BaseException e) {
            logger.error("请求接口={" + method + "} 参数=" + params + "", e);
            r.setCode(e.getCode());
            r.setMsg(e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error("请求接口={" + method + "} 参数=" + params + "", e);
            r.setCode(ResultCode.UNKNOWN_ERROR);
            r.setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error("其他业务异常");
            r.setCode(ResultCode.UNKNOWN_ERROR);
            r.setMsg(e.getMessage());
        }
        // 统一返回结果
        returnResult(r, response);

    }

    /**
     * 封装接口签名信息
     *
     * @param request
     * @return ApiRequest
     * @Title buildApiRequest
     * @date 2017-10-28 00:16:50 星期六
     */
    private ApiRequest buildApiRequest(HttpServletRequest request) {

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setAccessToken(request.getParameter("token"));
        apiRequest.setSign(request.getParameter("sign"));
        apiRequest.setParams(request.getParameter("params"));
        apiRequest.setTimestamp(request.getParameter("timestamp"));
        apiRequest.setChecked(false);

        return apiRequest;
    }

    /**
     * 对result进行包装，将不能格式化成json{}|[]的对象包装成ResultObject对象
     *
     * @param result
     * @return Object
     * @Title resultWarp
     * @date 2017-09-06 11:55:39 星期三
     */
    private Object resultWarp(Object result) {

        if (result == null || Integer.class.equals(result.getClass()) || Long.class.equals(result.getClass()) || Float.class.equals(result.getClass()) || Double.class.equals(result.getClass())
                || Date.class.equals(result.getClass()) || Character.class.equals(result.getClass()) || Byte.class.equals(result.getClass())
                || Short.class.equals(result.getClass()) || String.class.equals(result.getClass())) {

            result = new ResultObject(result);
        }

        return result;
    }

    private void returnResult(Object result, HttpServletResponse response) {

        try {

            String json = JsonUtil.toString(result);

            response.setCharacterEncoding("utf-8");
            response.setContentType("text/html/json;charset=utf-8");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expire", 0);

            if (json != null) {
                response.getWriter().write(json);
            }

        } catch (IOException e) {

            logger.error("服务中心响应异常");
            throw new RuntimeException(e);
        }

    }

    /**
     * 校验是否符合api接口规范
     *
     * @param request
     * @return ApiRunnable
     * @Title sysParamsValdate
     * @date 2017-09-02 01:51:51 星期六
     */
    private ApiRunnable sysParamsValdate(HttpServletRequest request) {

        String json = request.getParameter(PARAMS);
        String apiName = request.getParameter(METHOD);
        String version = request.getParameter(VERSION);

        ApiRunnable api;

        if ((api = apiContainer.findApiRunnable(apiName, version)) == null) {
            throw new ApiException(ResultCode.INVALID_PARAMETER, "调用失败，指定的API不存在,API:" + apiName + ",version:" + version);
        } else if (apiName == null || "".equals(apiName.trim())) {
            throw new ApiException(ResultCode.INVALID_PARAMETER, "调用失败，参数'method'不能为空");
        } else if (json == null) {
            throw new ApiException(ResultCode.INVALID_PARAMETER, "调用失败，参数'params'不能为空");
        }

        // 可多一个签名做校验
        return api;
    }

    /**
     * 封装api接口参数
     *
     * @param api       api
     * @param paramJson 参数
     * @param request
     * @param response
     * @return Object[]
     * @Title buildParams
     * @date 2017-09-02 01:53:08 星期六
     */
    private Object[] buildParams(ApiRunnable api, String paramJson, HttpServletRequest request, HttpServletResponse response) {

        // 请求参数
        Map<String, Object> requestParams = null;

        try {
            requestParams = JsonUtil.toMap(paramJson);
        } catch (Exception e) {
            throw new ApiException("调用失败，json字符串格式异常，请检查params参数");
        }

        if (requestParams == null) {
            requestParams = new HashMap<String, Object>(0);
        }

        Method method = api.getTargetMethod();
        // 目标方法的参数名-javaassist/ASM
        List<String> methodParamNames = Arrays.asList(parameterUtil.getParameterNames(method));
        // 目标方法的参数类型
        Class<?>[] paramTypes = method.getParameterTypes();

        // 判断传递参数是否符合目标方法定义--目标方法中是否存在传递的参数
        for (Map.Entry<String, Object> m : requestParams.entrySet()) {
            if (!methodParamNames.contains(m.getKey())) {
                throw new ApiException("调用失败，接口" + api.getApiName() + "不存在 " + m.getKey() + "参数，请检查params参数");
            }
        }
        // 判断传递参数是否符合接口定义-参数是否符合目标方法签名
        for (String paramName : methodParamNames) {
            if (!requestParams.containsKey(paramName)) {
                throw new ApiException("调用失败，缺少参数:" + paramName + ",请检查params参数");
            }
        }

        Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {

            if (paramTypes[i].isAssignableFrom(HttpServletRequest.class)) {
                args[i] = request;
            } else if (paramTypes[i].isAssignableFrom(HttpServletResponse.class)) {
                args[i] = response;
            } else if (requestParams.containsKey(methodParamNames.get(i))) {
                try {
                    args[i] = convertJsonToBean(methodParamNames.get(i), requestParams.get(methodParamNames.get(i)), paramTypes[i]);
                } catch (Exception e) {
                    throw new ApiException("调用失败，指定参数格式错误或值错误'" + methodParamNames.get(i) + "' " + e.getMessage());
                }
            } else {
                args[i] = null;
            }
        }

        return args;
    }

    /**
     * 将请求参数转换成目标方法参数对象
     *
     * @param paramName   目标方法参数名
     * @param paramValue  目标方法参数值
     * @param targetClass 目标方法参数类型
     * @return Object
     * @throws Exception
     * @Title convertJsonToBean
     * @date 2017-09-08 08:41:42 星期五
     */
    private <T> Object convertJsonToBean(String paramName, Object paramValue, Class<T> targetClass) throws Exception {

        Object value = null;
        String dateRegex = "[0-9]+";

        if (paramValue == null) {
            return null;
        } else if (Integer.class.equals(targetClass)) {
            value = Integer.parseInt(paramValue.toString());
        } else if (Long.class.equals(targetClass)) {
            value = Long.parseLong(paramValue.toString());
        } else if (Float.class.equals(targetClass)) {
            value = Float.parseFloat(paramValue.toString());
        } else if (Double.class.equals(targetClass)) {
            value = Double.parseDouble(paramValue.toString());
        } else if (Date.class.equals(targetClass)) {
            if (paramValue.toString().matches(dateRegex)) {
                value = new Date(Long.parseLong(paramValue.toString()));
            } else {
                throw new IllegalArgumentException(paramName + ":日期必须是长整型的时间戳");
            }

        } else if (String.class.equals(targetClass)) {
            if (paramValue instanceof String) {
                value = paramValue;
            } else {
                throw new IllegalArgumentException(paramName + ":转换目标类型为字符串");
            }
        } else {
            value = JsonUtil.toBean(paramValue, targetClass);
        }

        return value;
    }

    /**
     * 签名校验
     *
     * @param apiRequest
     * @return ApiRequest
     * @throws ApiException
     * @Title signCheck
     * @date 2017-10-28 00:22:53 星期六
     */
    private ApiRequest signCheck(ApiRequest apiRequest) throws ApiException {

        // 默认不通过
        apiRequest.setChecked(false);

        Token token = tokenService.get(apiRequest.getAccessToken());

        if (token == null) {
            throw new ApiException("验证失败，Token不存在！");
        }

        if (token.getExpiresTime().before(new Date())) {
            // clear expired token
            tokenService.remove(apiRequest.getAccessToken());
            throw new ApiException("验证失败，Token已失效！");
        }

        // 生成签名

        String accessToken = token.getAccessToken();
        String secret = token.getAppSecret();
        String timestamp = apiRequest.getTimestamp();
        Map<String, Object> params = JsonUtil.toMap(apiRequest.getParams());

        params = sortMapByKey(params);

        String sign = Md5Util.md5Encode(secret + timestamp + params);

        if (!sign.toLowerCase().equals(apiRequest.getSign())) {
            throw new ApiException("验证失败，非法的签名!");
        }

        // 时间验证
//        if (Math.abs(Long.valueOf(timestamp)) - System.currentTimeMillis() > EXPIRE_TIME) {
//            // 签名8小时过期
//            throw new ApiException("验证失败，签名失效!");
//        }

        apiRequest.setChecked(true);
        return apiRequest;
    }

    /**
     * 将Map按照key的字典顺序排序，返回TreeMap
     *
     * @param map
     * @return
     */
    private static Map<String, Object> sortMapByKey(Map<String, Object> map) {

        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Object> sortMap = new TreeMap<String, Object>(new Comparator<String>() {

            @Override
            public int compare(String str1, String str2) {
                return str1.compareTo(str2);
            }
        });

        sortMap.putAll(map);

        return sortMap;
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<String>();
        set.add("dddfdsf");

        System.out.println(set.contains("dddfdsf"));
    }
}
