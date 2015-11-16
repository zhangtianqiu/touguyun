package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.SellAndBuy;
import com.touguyun.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/9/5.
 */
public class SellBuyTopView extends LinearLayout{

    private Map<String,String> titleMap = new HashMap<String,String>(){
        {
            put("0","卖5");
            put("1","卖4");
            put("2","卖3");
            put("3","卖2");
            put("4","卖1");
            put("5","买1");
            put("6","买2");
            put("7","买3");
            put("8","买4");
            put("9","买5");
        }
    };

    private List<ViewHolder> holderList;

    public SellBuyTopView(Context context) {
        super(context);
        initViews(context);
    }
    public SellBuyTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }
    public SellBuyTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private class ViewHolder{
        public TextView title;
        public TextView price;
        public TextView count;
    }

    private void initViews(Context context){
        holderList = new ArrayList<ViewHolder>();
        this.removeAllViews();
        this.setOrientation(VERTICAL);
        for (int i=0;i<10;i++){
            ViewHolder holder = new ViewHolder();
            View itemView = LayoutInflater.from(context).inflate(R.layout.view_top_buy_sell, null);
            holder.title = (TextView)itemView.findViewById(R.id.item_title);
            holder.price = (TextView)itemView.findViewById(R.id.item_buy_price);
            holder.count = (TextView)itemView.findViewById(R.id.item_count);
            holder.title.setText(titleMap.get(i+""));
            holder.price.setText("-");
            holder.count.setText("-");
            itemView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1));
            this.addView(itemView);
            this.holderList.add(holder);
        }
    }

    public void setData(List<SellAndBuy> list){
        if(holderList == null || holderList.size()!=10){
            initViews(getContext());
        }
        for (int i=0;i<list.size();i++){
            holderList.get(i).price.setText(StringUtils.isNotEmpty(list.get(i).price)||"0.00".equals(list.get(i).price)?list.get(i).price:"-");
            if(list.get(i).colorRid!=0){
                holderList.get(i).price.setTextColor(getResources().getColor(list.get(i).colorRid));
            }
            holderList.get(i).count.setText(list.get(i).count==0?"-":list.get(i).count+"");
        }
    }
}
