package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.PortFolio;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.ViewUtils;
/**
 * Created by zhengyonghui on 15/9/2.
 */
public class ZuheItemView extends RelativeLayout{
    public ZuheItemView(Context context) {
        super(context);
        initView(context);
    }
    public ZuheItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public ZuheItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private TextView item_count,item_title,item_name,item_value,item_profit,item_profit_title;
    private ImageView item_next,item_icon;

    private PortFolio portFolio;

    public void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_zuhe_view, this);
        item_icon = (ImageView)findViewById(R.id.item_icon);
        item_next = (ImageView)findViewById(R.id.item_next);
        item_count = (TextView)findViewById(R.id.item_count);
        item_title = (TextView)findViewById(R.id.item_title);
        item_name = (TextView)findViewById(R.id.item_name);
        item_value = (TextView)findViewById(R.id.item_value);
        item_profit = (TextView)findViewById(R.id.item_profit);
        item_profit_title = (TextView)findViewById(R.id.item_profit_title);

    }

    public PortFolio getData(){
        return this.portFolio;
    }

    /**
     * 写入数据
     * @param portFolio
     * @param position  0及以上，显示左边色块和记数，小于0不显示。-1，显示右边箭头，-2，左右都不显示
     */
    public void setData(PortFolio portFolio,int position){
        setData(portFolio,position,0);
    }
    public void setData(PortFolio portFolio,int position,int filterType){
        this.portFolio = portFolio;
        if(position>=0){
            item_count.setText(position+"");
            item_count.setVisibility(VISIBLE);
        }else{
            item_count.setVisibility(GONE);
        }
        item_next.setVisibility(position == -1?VISIBLE:GONE);
        if(portFolio!=null && portFolio.id!=0){
            if(StringUtils.startWithHttp(portFolio.imgPath)){
                ImageLoader.getInstance().showImage(portFolio.imgPath,item_icon);
            }else{
                item_icon.setImageResource(R.drawable.default_zuhe_icon);
            }
            item_title.setText(StringUtils.returnStr(portFolio.name));
            item_name.setText(StringUtils.returnStr(portFolio.userName));
            item_value.setText(StringUtils.returnStr(portFolio.netVal));
            if(filterType == 1){
                item_profit.setText(StringUtils.returnStr(portFolio.weekProfit));
                item_profit.setTextColor(ViewUtils.getTextColorByTxt(getContext(),portFolio.weekProfit));
                item_profit_title.setText("周收益");
            }else if(filterType == 2){
                item_profit.setText(StringUtils.returnStr(portFolio.monthProfit));
                item_profit.setTextColor(ViewUtils.getTextColorByTxt(getContext(),portFolio.monthProfit));
                item_profit_title.setText("月收益");
            }else{
                item_profit.setText(StringUtils.returnStr(portFolio.profitVal));
                item_profit.setTextColor(ViewUtils.getTextColorByTxt(getContext(),portFolio.profitVal));
                item_profit_title.setText("日收益");
            }
        }
    }


}
