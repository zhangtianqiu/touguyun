package com.touguyun.module;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/7.
 */
public class PortfolioStock extends TouguJsonObject{
    // 股票代码
    public String code;
    // 股票名称
    public String name;
    //昨日收盘价
    public String prevClose;
    // 涨停
    public String highPrice;
    // 跌停
    public String lowPrice;
    // 市值
    public String asset;
    // 盈亏
    public String profit;
    // 持有数
    public int count;
    // 成本买入价格
    public String buyPrice;
    // 现价
    public String nowPrice;
    // 买一价格
    public String buy1;
    // 买一交易数
    public int buy1Count;
    // 买二价格
    public String buy2;
    // 买二交易数
    public int buy2Count;
    // 买三价格
    public String buy3;
    // 买三交易数
    public int buy3Count;
    // 买四价格
    public String buy4;
    // 买四交易数
    public int buy4Count;
    // 买五价格
    public String buy5;
    // 买五交易数
    public int buy5Count;
    // 卖一价格
    public String sell1;
    // 卖一交易数
    public int sell1Count;
    // 卖二价格
    public String sell2;
    // 卖二交易数
    public int sell2Count;
    // 卖三价格
    public String sell3;
    // 卖三交易数
    public int sell3Count;
    // 卖四价格
    public String sell4;
    // 卖四交易数
    public int sell4Count;
    // 卖五价格
    public String sell5;
    // 卖五交易数
    public int sell5Count;
    //买入最大数
    public int maxBuyCount;
    //卖出最大数
    public int maxSellCount;

    private List<SellAndBuy> sellAndBuys;

    public List<SellAndBuy> getSellAndBuy(){
        if(sellAndBuys == null){
            sellAndBuys = new ArrayList<SellAndBuy>();
            sellAndBuys.add(new SellAndBuy(buy5,buy5Count,prevClose));
            sellAndBuys.add(new SellAndBuy(buy4,buy4Count,prevClose));
            sellAndBuys.add(new SellAndBuy(buy3,buy3Count,prevClose));
            sellAndBuys.add(new SellAndBuy(buy2,buy2Count,prevClose));
            sellAndBuys.add(new SellAndBuy(buy1,buy1Count,prevClose));
            sellAndBuys.add(new SellAndBuy(sell1,sell1Count,prevClose));
            sellAndBuys.add(new SellAndBuy(sell2,sell2Count,prevClose));
            sellAndBuys.add(new SellAndBuy(sell3,sell3Count,prevClose));
            sellAndBuys.add(new SellAndBuy(sell4,sell4Count,prevClose));
            sellAndBuys.add(new SellAndBuy(sell5,sell5Count,prevClose));
        }
        return sellAndBuys;
    }
}
