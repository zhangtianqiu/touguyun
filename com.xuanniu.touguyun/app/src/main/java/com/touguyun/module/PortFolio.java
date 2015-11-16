package com.touguyun.module;
/**
 * Created by zhengyonghui on 15/9/2.
 *
 * 组合
 */
public class PortFolio extends TouguJsonObject{

    // 主键id
    public Long id;
    // 组合名称
    public String name;
    // 组合图片
    public String imgPath;
    // 组合投顾名称
    public String userName;
    // 净值
    public String netValue;
    // 净值
    public String netVal;
    // 日收益
    public String dayProfit;
    // 日收益
    public String profitVal;
    // 周收益
    public String weekProfit;
    // 月收益
    public String monthProfit;
    // 订阅数
    public Integer subCount;
    // 仓位比例
    public String position;
    // 贝塔值
    public String betaValue;
    // 创建日期
    public String createDate;
    // 创建日期
    public String createTime;
    // 总收益
    public String totalProfit;
    // 用户头像
    public String userImgPath;
    // 止损率
    public String stopLose;
    // 标的
    public String tag;
    // 投资理念
    public String remark;
    // 组合总金额
    public String totalMoney;
    // 认证状态
    public Integer authState;
    // 是否已订阅 1已订阅，0未订阅
    public int hasSub;
}
