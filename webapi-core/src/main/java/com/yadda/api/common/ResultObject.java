package com.yadda.api.common;

import java.io.Serializable;

public class ResultObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object value;


	public ResultObject(Object data) {
		super();
		this.value = data;
	}


	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}

}
