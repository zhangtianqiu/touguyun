package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.PortFolio;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.StringUtils;
/**
 * Created by zhengyonghui on 15/9/2.
 */
public class DingyueItemView extends RelativeLayout{
    public DingyueItemView(Context context) {
        super(context);
        initView(context);
    }
    public DingyueItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public DingyueItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private TextView item_title,item_time,item_name,item_count,item_context;
    private CircleImageView item_icon;
    private ImageView item_next;
    private CircleAngleTitleView item_button;

    private PortFolio portFolio;

    public void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_dingyue_view, this);
        item_icon = (CircleImageView)findViewById(R.id.item_icon);
        item_next = (ImageView)findViewById(R.id.item_next);
        item_button = (CircleAngleTitleView)findViewById(R.id.item_button);
        item_title = (TextView)findViewById(R.id.item_title);
        item_time = (TextView)findViewById(R.id.item_time);
        item_name = (TextView)findViewById(R.id.item_name);
        item_count = (TextView)findViewById(R.id.item_count);
        item_context = (TextView)findViewById(R.id.item_context);

    }

    public void setData(PortFolio portFolio,boolean isDingyue,OnClickListener listener){
        this.portFolio = portFolio;
        if(portFolio!=null && portFolio.id!=0){
            if(StringUtils.startWithHttp(portFolio.imgPath)){
                ImageLoader.getInstance().showImage(portFolio.imgPath,item_icon);
            }else{
                item_icon.setImageResource(R.drawable.default_zuhe_icon);
            }
            item_title.setText(StringUtils.returnStr(portFolio.name));
            if(isDingyue){
                item_time.setText(StringUtils.returnStr(portFolio.createTime));
                item_time.setVisibility(VISIBLE);
                item_count.setText(portFolio.subCount+"");
                item_count.setVisibility(VISIBLE);
                item_context.setVisibility(VISIBLE);
                item_next.setVisibility(VISIBLE);
                item_name.setVisibility(GONE);
                item_button.setVisibility(GONE);
            }else{
                item_name.setText(StringUtils.returnStr(portFolio.userName));
                item_name.setVisibility(VISIBLE);
                item_button.setVisibility(VISIBLE);
                item_button.setOnClickListener(listener);
                item_button.setTag(portFolio);
                item_time.setVisibility(GONE);
                item_count.setVisibility(GONE);
                item_next.setVisibility(GONE);
                item_context.setVisibility(GONE);

            }
        }
    }

}
