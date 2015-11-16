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
public class CombinedPositionTitleView extends RelativeLayout{
    public CombinedPositionTitleView(Context context) {
        super(context);
        initView(context);
    }
    public CombinedPositionTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public CombinedPositionTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private CircleAngleTitleView item_layout,item_icon;
    private TextView item_title;

    private void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_combined_position, this);
        item_layout = ((CircleAngleTitleView) findViewById(R.id.item_layout));
        item_icon = ((CircleAngleTitleView) findViewById(R.id.item_icon));
        item_title = ((TextView) findViewById(R.id.item_title));
        item_layout.setAlpha(0.45f);
    }


    public void setData(int index,String postion,String name){
        item_layout.setBackAndFrameColor(getContext().getResources().getColor(getColorRid(index)));
        item_icon.setBackAndFrameColor(getContext().getResources().getColor(getColorRid(index)));
        item_icon.setText(StringUtils.isNotEmpty(postion)?postion:"");
        item_title.setText(StringUtils.isNotEmpty(name)?name:"");
    }

    private int getColorRid(int position){
        if(position%3 == 0){
            return R.color.red_FB3636;
        }else if(position%3 == 1){
            return R.color.orange_FF8E45;
        }else{
            return R.color.yellow_FFCC66;
        }
    }
}
