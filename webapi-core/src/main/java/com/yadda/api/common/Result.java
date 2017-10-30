package com.yadda.api.common;

import java.io.Serializable;

public class Result implements Serializable {

	private static final long serialVersionUID = -6963503022738848863L;

	private int code = 200; // 默认成功
	private String msg = "Success";
	private Object data;

	public Result() {
		super();
	}

	public Result(int code, String message, Object data) {
		super();
		this.code = code;
		this.msg = message;
		this.data = data;
	}

	public Result(int code, String message) {
		super();
		this.code = code;
		this.msg = message;
	}

	public Result(int code, Object data) {
		super();
		this.code = code;
		this.data = data;
	}

	public Result(Object data) {
		super();
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String message) {
		this.msg = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		if (data != null) {
			return "Result [code=" + code + ", msg=" + msg + ", data=" + data.toString() + "]";
		}
		return "Result [code=" + code + ", msg=" + msg + ", data=" + data + "]";

	}
}
