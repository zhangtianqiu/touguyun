package com.touguyun.module;
import com.touguyun.R;
/**
 * Created by zhengyonghui on 15/9/5.
 */
public class SellAndBuy extends TouguJsonObject{
    public String price;
    public int count;
    public int colorRid;
    public SellAndBuy(String price,int count,String prevClose){
        this.price = price;
        this.count = count;
        if(Float.parseFloat(price)>Float.parseFloat(prevClose)){
            this.colorRid = R.color.red_F05050;
        }else if(Float.parseFloat(price)<Float.parseFloat(prevClose)){
            this.colorRid = R.color.green_00CC00;
        }else{
            this.colorRid = R.color.black_323232;
        }
    }
}
