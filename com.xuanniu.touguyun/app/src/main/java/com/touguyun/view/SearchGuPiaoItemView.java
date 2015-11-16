package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.StockInfo;
import com.touguyun.utils.StringUtils;
/**
 * Created by zhengyonghui on 15/9/4.
 */
public class SearchGuPiaoItemView extends RelativeLayout {
    public SearchGuPiaoItemView(Context context) {
        super(context);
        initView(context);
    }
    public SearchGuPiaoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public SearchGuPiaoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private TextView item_title,item_profit,item_type;
    private FilletRelativeLayout item_add,item_cancel;
    private void initView(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_search_gupiao_view, this);
        item_title = (TextView)findViewById(R.id.item_title);
        item_profit = (TextView)findViewById(R.id.item_profit);
        item_type = (TextView)findViewById(R.id.item_type);
        item_add = (FilletRelativeLayout)findViewById(R.id.item_add);
        item_cancel = (FilletRelativeLayout)findViewById(R.id.item_cancel);
    }
    public void setData(StockInfo stockInfo,OnClickListener clickListener,boolean isChoosed){
        if(stockInfo!=null && StringUtils.isNotEmpty(stockInfo.code)){
            item_title.setText(StringUtils.returnStr(stockInfo.name));
            item_profit.setText(StringUtils.returnStr(stockInfo.code));
            item_type.setVisibility(stockInfo.hasAdd ==1?VISIBLE:GONE);
            item_add.setVisibility(stockInfo.hasAdd==0&&!isChoosed?VISIBLE:GONE);
            item_add.setTag(stockInfo.code);
            item_cancel.setVisibility(stockInfo.hasAdd==0&&isChoosed?VISIBLE:GONE);
            item_cancel.setTag(stockInfo.code);
            if(clickListener!=null){
                item_add.setOnClickListener(clickListener);
                item_cancel.setOnClickListener(clickListener);
            }

        }

    }

}
