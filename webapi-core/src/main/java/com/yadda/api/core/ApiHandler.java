package com.yadda.api.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import com.yadda.api.common.ApiException;
import com.yadda.api.common.JsonUtil;
import com.yadda.api.common.Md5Util;
import com.yadda.api.common.ResultCode;
import com.yadda.api.common.ResultObject;
import com.yadda.api.common.Result;
import com.yadda.api.core.ApiContainer.ApiRunnable;

public class ApiHandler implements InitializingBean, ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);

	private static final String PARAMS = "params";
	private static final String METHOD = "method";
	private static final String VERSION = "version";

	private ApiContainer apiContainer;
	private final ParameterNameDiscoverer parameterUtil;

	private TokenService tokenService;

	public ApiHandler() {
		parameterUtil = new LocalVariableTableParameterNameDiscoverer();
	}

	public void afterPropertiesSet() {
		apiContainer.loadApiFromSpringbeans();
	}

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		apiContainer = new ApiContainer(context);
		tokenService = context.getBean(TokenService.class);
		System.out.println(context.getBeanDefinitionNames().length);
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		// 系统参数校验
		String params = request.getParameter(PARAMS);
		String method = request.getParameter(METHOD);

		Result r = new Result();

		ApiRunnable apiRun = null;

		ApiRequest apiRequest = null; // 增加家口签名验证

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

			// result统一格式化成json,如果接口返回基本类型eg:String,int,boolean...用data做key格式化成json形式
			result = resultWarp(result);

			r.setCode(ResultCode.SUCCESS);
			r.setMsg("Success");
			r.setData(result);

		} catch (ApiException e) {
			logger.error("请求接口={" + method + "} 参数=" + params + "", e);
			r.setCode(ResultCode.PARAMETER_ERROR);
			r.setMsg(e.getMessage());
		} catch (InvocationTargetException e) {
			logger.error("请求接口={" + method + "} 参数=" + params + "", e);
			r.setCode(ResultCode.SYS_ERROR);
			r.setMsg(e.getMessage());

			// result = handleError(e.getTargetException());
		} catch (Exception e) {
			// response.setStatus(500); // 封装异常并返回
			logger.error("其他业务异常");

			r.setCode(ResultCode.BUSINESS_ERROR);
			r.setMsg(e.getMessage());
		}

		// 统一返回结果
		returnResult(r, response);

	}

	/**
	 * 封装接口签名信息
	 * 
	 * @Title buildApiRequest
	 * @param request
	 * @return
	 * @return ApiRequest
	 * @date 2017-10-28 00:16:50 星期六
	 */
	private ApiRequest buildApiRequest(HttpServletRequest request) {

		ApiRequest apiRequest = new ApiRequest();
		apiRequest.setAccessToken(request.getParameter("token"));
		apiRequest.setSign(request.getParameter("sign"));
		apiRequest.setTimestamp(request.getParameter("timestamp"));
		apiRequest.seteCode(request.getParameter("eCode"));
		apiRequest.setuCode(request.getParameter("uCode"));
		apiRequest.setParams(request.getParameter("params"));

		return apiRequest;
	}

	/**
	 * 对result进行包装，将不能格式化成json{}|[]的对象包装成ResultObject对象
	 * 
	 * @Title resultWarp
	 * @param result
	 * @return
	 * @return Object
	 * @date 2017-09-06 11:55:39 星期三
	 */
	private Object resultWarp(Object result) {

		if (result == null || Integer.class.equals(result.getClass()) || Long.class.equals(result.getClass()) || Float.class.equals(result.getClass()) || Double.class.equals(result.getClass())
				|| Date.class.equals(result.getClass()) || String.class.equals(result.getClass()) || Character.class.equals(result.getClass()) || Byte.class.equals(result.getClass())
				|| Short.class.equals(result.getClass())) {

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

			logger.error("服务中心相应异常");
			throw new RuntimeException(e);
		}

	}

	/**
	 * 校验是否符合api接口规范
	 * 
	 * @Title sysParamsValdate
	 * @param request
	 * @return 返回对应api
	 * @return ApiRunnable
	 * @date 2017-09-02 01:51:51 星期六
	 */
	private ApiRunnable sysParamsValdate(HttpServletRequest request) {

		String json = request.getParameter(PARAMS);
		String apiName = request.getParameter(METHOD);
		String version = request.getParameter(VERSION);

		ApiRunnable api;

		if ((api = apiContainer.findApiRunnable(apiName, version)) == null) {
			throw new ApiException("调用失败，指定的API不存在,API:" + apiName + ",version:" + version);
		} else if (apiName == null || apiName.trim().equals("")) {
			throw new ApiException("调用失败，参数'method'不能为空");
		} else if (json == null) {
			throw new ApiException("调用失败，参数'params'不能为空");
		}

		// 可多一个签名做校验
		return api;
	}

	/**
	 * 封装api接口参数
	 * 
	 * @Title buildParams
	 * @param api
	 *            api
	 * @param paramJson
	 *            参数
	 * @param request
	 * @param response
	 * @return
	 * @return Object[]
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
			requestParams = new HashMap<String, Object>();
		}

		Method method = api.getTargetMethod();
		// 目标方法的参数名-javaassist/ASM
		List<String> methodParamNames = Arrays.asList(parameterUtil.getParameterNames(method));
		// 目标方法的参数类型
		Class<?>[] paramTypes = method.getParameterTypes();

		// 判断传递参数是否符合目标方法定义--目标方法中是存在传递的参数
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
	 * @Title convertJsonToBean
	 * @param paramName
	 *            目标方法参数名
	 * @param val
	 *            请求参数值
	 * @param targetClass
	 *            目标方法参数类型
	 * @return
	 * @throws Exception
	 * @return Object
	 * @date 2017-09-08 08:41:42 星期五
	 */
	private <T> Object convertJsonToBean(String paramName, Object val, Class<T> targetClass) throws Exception {

		Object result = null;
		if (val == null) {
			return null;
		} else if (Integer.class.equals(targetClass)) {
			result = Integer.parseInt(val.toString());
		} else if (Long.class.equals(targetClass)) {
			result = Long.parseLong(val.toString());
		} else if (Float.class.equals(targetClass)) {
			result = Float.parseFloat(val.toString());
		} else if (Double.class.equals(targetClass)) {
			result = Double.parseDouble(val.toString());
		} else if (Date.class.equals(targetClass)) {
			if (val.toString().matches("[0-9]+")) {
				result = new Date(Long.parseLong(val.toString()));
			} else {
				throw new IllegalArgumentException(paramName + ":日期必须是长整型的时间戳");
			}

		} else if (String.class.equals(targetClass)) {
			if (val instanceof String) {
				result = val;
			} else {
				throw new IllegalArgumentException(paramName + ":转换目标类型为字符串");
			}
		} else {
			result = JsonUtil.toBean(val, targetClass);
		}

		return result;
	}

	/**
	 * 签名校验
	 * 
	 * @Title signCheck
	 * @param request
	 * @return
	 * @throws ApiException
	 * @return ApiRequest
	 * @date 2017-10-28 00:22:53 星期六
	 */
	private ApiRequest signCheck(ApiRequest request) throws ApiException {

		request.setChecked(false);// 默认不通过

		Token token = tokenService.getToken(request.getAccessToken());

		if (token == null) {
			throw new ApiException("验证失败，指定的Token不存在！");
		}

		if (token.getExpiresTime().before(new Date())) {
			throw new ApiException("验证失败，Token已失效！");
		}

		// 生成签名
		String methodName = request.getMethodName();

		String accessToken = token.getAccessToken();
		String secret = token.getSecret();
		String params = request.getParams();
		String timestamp = request.getTimestamp();

		String sign = Md5Util.MD5Encode(secret + methodName + params + token + timestamp + secret, "");

		if (!sign.toUpperCase().equals(request.getSign())) {
			throw new ApiException("验证失败，非法的签名!");
		}

		// 时间验证
		if (Math.abs(Long.valueOf(timestamp)) - System.currentTimeMillis() > 8 * 60 * 1000) {
			// 签名8小时过期
			throw new ApiException("验证失败，签名失效!");
		}

		request.setChecked(true);
		request.setMemberId(token.getMemberId());
		return request;
	}

	public static void main(String[] args) {
		Set<String> set = new HashSet<String>();
		set.add("dddfdsf");

		System.out.println(set.contains("dddfdsf"));
	}
}
