package com.touguyun.module;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
/**
 * Created by zhengyonghui on 15/9/15.
 */
public class MessageBody extends TouguJsonObject{
    /**
     * 消息内容
     */
    public String content;
    /**
     * 控制字段、消息外延内容
     */
    public String option;
    public JSONObject optionJson;

    public String html;

    public String title;

    public String emailSubject;
    public void setOption(String option) {
        this.option = option;
        try {
            this.optionJson = TouguJsonObject.parseObject(option,JSONObject.class);
        }catch (JSONException e){
        }
    }
}
