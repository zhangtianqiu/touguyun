package com.touguyun.module;
/**
 * Created by zhengyonghui on 15/9/15.
 */
public class Message extends TouguJsonObject{


    public long mid;

    public String channel;
    public User post;
    public User receive;
    public MessageBody body;
    public String upStream;
    public String priority;
    public long postTime;
}
