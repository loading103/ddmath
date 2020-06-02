package com.tsinghuabigdata.edu.ddmath.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * json转换
 * Created by HP on 2016/4/22.
 */
public class JsonUtils {

    /**
     * json反序列化.
     * @param json
     * @param <TYPE>
     * @return 返回泛型所执行的类
     */
    public static <TYPE> TYPE fromJson(String json, TypeToken<TYPE> type) {
        return new Gson().fromJson(json, type.getType());
    }

    /**
     * 生成JSON
     * @param objects
     * @return
     */
    public static JSONObject buildJson(Object ... objects) {
        if (objects.length % 2 != 0) {
            throw new IllegalArgumentException("参数是键值对应,请检查是否一一对应");
        }
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < objects.length; i+=2) {
            String json = objects[i].toString();
            Object value = objects[i+1];
            try {
                jsonObject.put(json, value);
            } catch (JSONException e) {
                AppLog.w(ErrTag.TAG_JSON, "json put失败", e);
            }
        }
        return jsonObject;
    }

}
