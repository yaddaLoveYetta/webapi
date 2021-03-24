package com.yadda.api.common;

import java.io.Serializable;

/**
 * 非json格式数据包装成可json序列化对象,如int,char,String,boolean 等
 *
 * @author yadda
 */
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
