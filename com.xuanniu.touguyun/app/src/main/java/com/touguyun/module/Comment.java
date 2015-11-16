package com.touguyun.module;
/**
 * Created by zhengyonghui on 15/8/27.
 */
public class Comment extends TouguJsonObject{
    public long id;
    public long createTime;
    public String content;
    public String replyContent;
    public User user;
    public User replyUser;
}
