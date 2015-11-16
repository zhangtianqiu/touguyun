package com.touguyun.module;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/3.
 */
public class StockGroup extends TouguJsonObject{
    // 所占仓位
    public String percent;
    // 所属模块
    public String plate;
    // 股票信息
    public List<StockInfo> stockInfos;
}
