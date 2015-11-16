package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.utils.StringUtils;

/**
 * Created by zhengyonghui on 15/8/26.
 */
public class CombinedPositionItemView extends RelativeLayout{
    public CombinedPositionItemView(Context context) {
        super(context);
        initView(context);
    }
    public CombinedPositionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public CombinedPositionItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private TextView item_name,item_profit,item_time;

    private void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_combined_position_item, this);
        item_name = ((TextView) findViewById(R.id.item_name));
        item_profit = ((TextView) findViewById(R.id.item_profit));
        item_time = ((TextView) findViewById(R.id.item_time));
    }

    public void setdata(String name,String profit,String time){
        if(item_name!=null){
            item_name.setText(StringUtils.isNotEmpty(name)?name:"");
        }
        if(item_profit!=null){
            item_profit.setText(StringUtils.isNotEmpty(profit)?profit:"");
        }
        if(item_time!=null){
            item_time.setText(StringUtils.isNotEmpty(time)?time:"");
        }
    }

}
