package com.touguyun.module;
import com.touguyun.utils.StringUtils;
/**
 * Created by zhengyonghui on 15/9/2.
 *
 * 投顾
 */
public class Consultant extends TouguJsonObject {
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
    //订阅数
    public int subscribeNum;
    //从业年限
    public String yearsEmployment;
    //审计状态
    public int auditState;
    //个人简介
    public String personalProfile;
    //工作经历
    public String workExperience;
    //性别
    public int sex;
    //性别 文字
    public String sexStr;
    //职业证书
    public String certificate;
    //是否已关注（0否1是）
    public int attentionState;
    //是否自己（0否1是）
    public int isSelf;

}
