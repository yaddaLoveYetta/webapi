package com.yadda.api.core;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yadda
 */
public class Token implements Serializable {
    
    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", appId='" + appId + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", createTime=" + createTime +
                ", expiresTime=" + expiresTime +
                ", clientType='" + clientType + '\'' +
                ", eCode='" + eCode + '\'' +
                ", uCode='" + uCode + '\'' +
                '}';
    }

    private static final long serialVersionUID = -7747594044971723288L;

    /**
     * token存储主键
     */
    private long id;
    /**
     * 平台分配的引用id
     */
    private String appId;
    /**
     * 平台分配的秘钥
     */
    private String appSecret;
    /**
     * token
     */
    private String accessToken;
    /**
     * token创建时间
     */
    private Date createTime;
    /**
     * token过期时间
     */
    private Date expiresTime;
    /**
     * 申请token的客户端类型
     */
    private String clientType;
    private String eCode;
    private String uCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(Date expiresTime) {
        this.expiresTime = expiresTime;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String geteCode() {
        return eCode;
    }

    public void seteCode(String eCode) {
        this.eCode = eCode;
    }

    public String getuCode() {
        return uCode;
    }

    public void setuCode(String uCode) {
        this.uCode = uCode;
    }
}
