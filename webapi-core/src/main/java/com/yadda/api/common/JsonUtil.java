package com.yadda.api.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

import java.sql.Date;
import java.util.Map;

/**
 * @author yadda
 */
public class JsonUtil {

    private static final SerializeConfig SC = new SerializeConfig(4);
    private static final SerializerFeature[] SF = new SerializerFeature[3];

    static {

        SC.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
        SC.put(java.sql.Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
        SC.put(java.sql.Timestamp.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        SC.put(java.sql.Time.class, new SimpleDateFormatSerializer("HH:mm:ss"));
        SF[0] = SerializerFeature.WriteMapNullValue;
        SF[1] = SerializerFeature.WriteNullListAsEmpty;
        SF[2] = SerializerFeature.WriteDateUseDateFormat;
    }

    /**
     * 将object转换成json格式字符串
     *
     * @param result
     * @return String
     * @Title toString
     * @date 2017-09-01 17:22:28 星期五
     */
    public static String toString(Object result) {

        // String str = JSON.toJSONString(result, SC, SerializerFeature.WriteMapNullValue,
        // SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteDateUseDateFormat);

        String str = JSON.toJSONString(result, SC, SF);
        return str;
    }

    /**
     * 将json格式请求参数转换成map
     *
     * @param paramJson
     * @return Map<String,Object>
     * @Title toMap
     * @date 2017-09-01 17:22:20 星期五
     */

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String paramJson) {
        return JSON.parseObject(paramJson, Map.class);
    }

    /**
     * 将json格式val转换成对象
     *
     * @param val         待转换对象(必须符合json规范)
     * @param targetClass 转换目标对象类型
     * @return Object
     * @Title toBean
     * @date 2017-09-01 17:22:12 星期五
     */
    public static <T> T toBean(Object val, Class<T> targetClass) {

        if (val instanceof String) {
            return JSON.parseObject(val.toString(), targetClass);
        }
        return JSON.parseObject(JSON.toJSONString(val), targetClass);
        // return JSON.parseObject(JSON.toJSONString(val), new TypeReference<T>() {
        // });
    }

    /**
     * 将json格式val转换成复杂javabean对象
     *
     * @param val
     * @param targetClass
     * @return Object
     * @Title toBean
     * @date 2017-09-01 17:22:12 星期五
     */
    public static <T> T toBean(String val, Class<T> targetClass) {
        return JSON.parseObject(val, targetClass);
    }

    public static void main(String[] args) {
        String json = "";
        System.out.println(toMap(json));
    }

}
