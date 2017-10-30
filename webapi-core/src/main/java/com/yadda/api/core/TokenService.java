package com.yadda.api.core;

public interface TokenService {

	/**
	 * 创建token
	 * 
	 * @Title createToken
	 * @return
	 * @return Token
	 * @date 2017-10-27 22:07:47 星期五
	 */
	Token createToken();

	/**
	 * 获取token
	 * 
	 * @Title getToken
	 * @param token
	 * @return
	 * @return Token
	 * @date 2017-10-27 22:07:55 星期五
	 */
	Token getToken(String token);

}
