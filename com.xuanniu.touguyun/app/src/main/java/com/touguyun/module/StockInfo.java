package com.touguyun.module;
/**
 * Created by zhengyonghui on 15/9/3.
 */
public class StockInfo extends TouguJsonObject{
    // 股票名称
    public String name;
    // 股票代码
    public String code;
    // 主键id
    public long id;
    // 买入时价格
    public String buyPrice;
    // 买入股数
    public int count;
    // 目前价格
    public String nowPrice;
    // 收益率
    public String profitPercent;
    // 仓位
    public String percent;
    // 上涨价格
    public String rise;
    //时间
    public String createTime;
    //是否已添加
    public int hasAdd;/*1为已添加，0为未添加*/

}
