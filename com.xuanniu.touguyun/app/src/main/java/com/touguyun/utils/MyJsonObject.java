package com.touguyun.utils;
import com.alibaba.fastjson.JSONObject;
/**
 * Created by zhengyonghui on 15/9/8.
 */
public class MyJsonObject extends JSONObject{

    public String getMyString(String key){
        if(StringUtils.isNotEmpty(this.get(key))){
            return getString(key);
        }else{
            return "";
        }
    }

    public long getMyLong(String key){
        if(ProvingUtil.isNumber(getMyString(key))){
            return this.getLongValue(key);
        }
        return 0l;
    }
    public long getMyInt(String key){
        if(ProvingUtil.isNumber(getMyString(key))){
            return this.getInteger(key);
        }
        return 0;
    }

}
