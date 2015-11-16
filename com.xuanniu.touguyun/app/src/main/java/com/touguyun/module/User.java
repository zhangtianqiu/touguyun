package com.touguyun.module;
/**
 * Created by zhengyonghui on 15/8/27.
 */
public class User extends TouguJsonObject{

    public static final int USER_TYPE_PUTONG = 0;
    public static final int USER_TYPE_TOUGU = 1;

    //投顾信息主键
    public long id;
    //投顾用户uid
    public long uid;
    //投顾名字
    public String name;
    //用户头像
    public String userImg;
    //投顾粉丝数
    public int fansNum;
    //总收益
    public String profitSum;
    //加V 认证
    public int authState;
    //评论数
    public int commentNum;
    //审计状态
    public int auditState;
    //个人简介
    public String personalProfile;
    //工作经历
    public String workExperience;
    //性别
    public int sex;
    //职业证书
    public String certificate;

    public int subscribeNum;

    public String yearsEmployment;

    public int roleType;   //用户类型
    public int registType;   //用户登录类型
    public String mobile;      //手机号
    public String token;
    public String password;
    public String code;
    public String ip;
    public int terminalType;
    public String recommendCode;//推荐码
    public long recommendActivityId;//推荐活动id
    public int recommendType;//推荐平台类型
}
