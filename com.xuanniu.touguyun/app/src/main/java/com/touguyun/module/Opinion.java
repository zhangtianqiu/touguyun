package com.touguyun.module;
/**
 * Created by zhengyonghui on 15/8/27.
 */
public class Opinion extends TouguJsonObject{

    public long id;
    public User user;
    public String time;
    public String tag;
    public String title;
    public String content;
    public String summary;
    public int shareCount;
    public String hideContent;
    public int commentCount;
    public int praise;
    public String tags;
        //好评数
    public int likeNum;
    //回复数
    public int commentNum;
    public Long postUid;

    public String subject;

    public long createTime;
    public long lastCommentTime;


    public int viewNum = 0;

    public int subscribeNum = 0;
    public int liked;   //1已赞 0未赞



}
