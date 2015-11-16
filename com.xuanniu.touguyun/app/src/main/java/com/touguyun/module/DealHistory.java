package com.touguyun.module;
/**
 * Created by zhengyonghui on 15/8/28.
 * Describe:交易记录vo
 */
public class DealHistory extends TouguJsonObject{
    public long id;
    // 交易类型
    public int type;       /*1买入 2卖出*/
    // 股票名称
    public String stockName;
    // 调仓前仓位比例
    public String beforePercent;
    // 调仓后仓位比例
    public String afterPercent;
    // 调仓价格
    public String price;
    // 调仓数量
    public int count;
    // 调仓时间
    public String time;
    // 投顾头像地址
    public String userImgPath;
    // 调仓思路
    public String remark;
    //成交额
    public String turnVolume;
    //调仓总价格
    public String totalPrice;
    //成交时间
    public String dealDate;

}
