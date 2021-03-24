package com.yadda.api.core;

/**
 * <p>
 * 请求签名验证对象
 * <p>
 * <pre>
 * 签名规则：
 * 1:将请求参数params按照字典顺序升序排序得到字符串s1
 * 2:appSecret+timestamp+s1 得到字符串s2
 * 3:将s2进行MD5加密得到字符串s3
 * 4:将s3转换为小写得到签名sign
 * </pre>
 * 客户端将token，sign，timestamp，params作为参数提交请求业务接口
 *
 * @author yadda
 */
public class ApiRequest {

    /**
     * token（从平台获取）
     */
    private String accessToken;
    /**
     * 业务参数
     */
    private String params;
    /**
     * 签名
     */
    private String sign;
    /**
     * 请求时间
     */
    private String timestamp;
    /**
     * 签名验证是否通过
     */
    private boolean checked;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 是否通过了签名校验
     *
     * @return boolean
     * @Title isChecked
     * @date 2017-10-28 00:21:46 星期六
     */
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
