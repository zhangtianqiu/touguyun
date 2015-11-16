package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.DealHistory;
import com.touguyun.utils.StringUtils;
/**
 * Created by zhengyonghui on 15/9/5.
 */
public class TiaocangHistoryItemView extends RelativeLayout{
    public TiaocangHistoryItemView(Context context) {
        super(context);
        initViews(context);
    }
    public TiaocangHistoryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }
    public TiaocangHistoryItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }
    private CircleAngleTitleView item_icon;
    private TextView item_time,item_buy_price,item_count,item_turn_volume;
    private void initViews(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_tiaocang_history, this);
        item_icon = (CircleAngleTitleView)findViewById(R.id.item_icon);
        item_time = (TextView)findViewById(R.id.item_time);
        item_buy_price = (TextView)findViewById(R.id.item_buy_price);
        item_count = (TextView)findViewById(R.id.item_count);
        item_turn_volume = (TextView)findViewById(R.id.item_turn_volume);
    }

    public void setData(DealHistory dealHistory){
        if(dealHistory!=null){
            item_icon.setText(dealHistory.type == 1?"买":"卖");
            item_icon.setBackAndFrameColor(getResources().getColor(dealHistory.type == 1?R.color.red_FD4E4E:R.color.blue_4691EE));
            item_time.setText(StringUtils.returnStr(dealHistory.dealDate));
            item_buy_price.setText(StringUtils.returnStr(dealHistory.price));
            item_count.setText(StringUtils.returnStr(dealHistory.count));
            item_turn_volume.setText(StringUtils.returnStr(dealHistory.totalPrice));
        }
    }
}
