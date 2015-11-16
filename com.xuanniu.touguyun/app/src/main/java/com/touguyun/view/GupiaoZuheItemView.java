package com.touguyun.view;
import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.StockInfo;
import com.touguyun.net.Http;
import com.touguyun.utils.StringUtils;
/**
 * Created by zhengyonghui on 15/9/3.
 */
public class GupiaoZuheItemView extends LinearLayout{
    public GupiaoZuheItemView(Context context) {
        super(context);
        initView(context);
    }
    public GupiaoZuheItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public GupiaoZuheItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private TextView item_name,item_code,item_buy_price,item_tag;
    private ImageView item_button;

    public void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_create_zuhe_gupiao, this);
        item_name = (TextView)findViewById(R.id.item_name);
        item_code = (TextView)findViewById(R.id.item_code);
        item_buy_price = (TextView)findViewById(R.id.item_buy_price);
        item_tag = (TextView)findViewById(R.id.item_tag);
        item_button = (ImageView)findViewById(R.id.item_button);

    }

    public void setData(StockInfo stockInfo,OnClickListener onClickListener){
        if(stockInfo!=null && StringUtils.isNotEmpty(stockInfo.code)){
            item_name.setText(StringUtils.returnStr(stockInfo.name));
            item_code.setText(StringUtils.returnStr(stockInfo.code));
            if("0.00".equals(stockInfo.nowPrice)){
                item_buy_price.setText("停牌");
            }else if(stockInfo.rise.startsWith("-")){
                item_buy_price.setText(Html.fromHtml(StringUtils.returnStr(stockInfo.nowPrice)+ "&#160;&#160;<font color='#0DE091'>("+StringUtils.returnStr(stockInfo.rise)+")</font>"));
            }else{
                item_buy_price.setText(Html.fromHtml(StringUtils.returnStr(stockInfo.nowPrice)+ "&#160;&#160;<font color='#FD4E4E'>("+StringUtils.returnStr(stockInfo.rise)+")</font>"));
            }
            item_tag.setText(StringUtils.returnStr(stockInfo.profitPercent));
            item_button.setVisibility(stockInfo.count == 0?VISIBLE:GONE);
            if(onClickListener!=null){
                item_button.setOnClickListener(onClickListener);
                item_button.setTag(stockInfo.code);
            }
        }
    }
}
