package com.app.mvpdemo.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.app.mvpdemo.util.log.LogUtil;

import java.lang.reflect.Type;

/**
 * 
 * json数据处理类
 * 
 * @author weiwen
 * @version [版本号, 2015年3月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class JsonUtil {
    private static final String TAG = "LogEngine_t_JsonUtil";

    public static <T> T fromJson(String json, Class<T> classOfT) {
        long start = 0;
        T fromJson = null;
        long end = 0;
        try {
            start = System.currentTimeMillis();
            Gson gson = new Gson();
            fromJson = (T)gson.fromJson(json, classOfT);

            end = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d(TAG, "fromJson(String json, Class<T> classOfT)   time= " , (end - start));
        return fromJson;
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        long start = System.currentTimeMillis();
        Gson gson = new Gson();
        T fromJson = (T)gson.fromJson(json, typeOfT);
        long end = System.currentTimeMillis();
        LogUtil.d(TAG, "fromJson(String json, Type typeOfT)   time= "
                 , (end - start));
        return fromJson;
    }

    public static <T> T fromJsonEnableComplexMapKeySerialization(String json,
                                                                 Class<T> classOfT) {
        long start = System.currentTimeMillis();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                .create();
        T fromJson = (T)gson.fromJson(json, classOfT);
        long end = System.currentTimeMillis();
        LogUtil.d(TAG,
                        "fromJsonEnableComplexMapKeySerialization(String json, Class<T> classOfT)   time= " , (end - start));
        return fromJson;
    }

    public static <T> T fromJsonEnableComplexMapKeySerialization(String json,
                                                                 Type typeOfT) {
        long start = System.currentTimeMillis();
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                .create();
        T fromJson = (T)gson.fromJson(json, typeOfT);
        long end = System.currentTimeMillis();
        LogUtil.d(TAG,
                        "fromJsonEnableComplexMapKeySerialization(String json, Type typeOfT)   time= " , (end - start));
        return fromJson;
    }

    public static String toJson(Object object) {
        String json = null;
        try {
            long start = System.currentTimeMillis();
            Gson gson = new Gson();
            json = gson.toJson(object);
            long end = System.currentTimeMillis();
            LogUtil.d(TAG, "toJson(Object object)   time= "
                     , (end - start));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String toJson(Object fromJson, Type typeOfSrc) {
        long start = System.currentTimeMillis();
        Gson gson = new Gson();
        String json = gson.toJson(fromJson, typeOfSrc);
        long end = System.currentTimeMillis();
        LogUtil.d(TAG, "toJson(Object fromJson, Type typeOfSrc)   time= " , (end - start));
        return json;
    }

}
