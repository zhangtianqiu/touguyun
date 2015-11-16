package com.touguyun.module;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;
/**
 * Created by zhengyonghui on 15/8/25.
 */
public class TouguJsonObject  implements Serializable {
    public static <T> T parseObjectFromBody(JSONObject json, Class<T> clazz) {
        return parseObject(json, "body", clazz);
    }
    public static <T> T parseObject(JSONObject json, String key, Class<T> clazz) {
        return parseObject(json.getJSONObject(key), clazz);
    }
    public static <T> T parseObject(JSONObject json, Class<T> clazz) {
        return JSON.toJavaObject(json, clazz);
    }
    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
    public static <T> List<T> parseListFromBody(JSONObject json, Class<T> clazz) {
        return parseList(json, "body", clazz);
    }
    public static <T> List<T> parseList(JSONObject json, String key, Class<T> clazz) {
        return parseList(json.getJSONArray(key).toJSONString(), clazz);
    }
    public static <T> List<T> parseList(JSONArray jsonArray, Class<T> clazz) {
        return parseList(jsonArray.toJSONString(), clazz);
    }
    public static <T> List<T> parseList(String string, Class<T> clazz) {
        return JSON.parseArray(string, clazz);
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
