package com.touguyun.view;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.PortFolio;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.StringUtils;
/**
 * Created by zhengyonghui on 15/9/2.
 */
public class ZuheCreateItemView extends RelativeLayout{
    public ZuheCreateItemView(Context context) {
        super(context);
        initView(context);
    }
    public ZuheCreateItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public ZuheCreateItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private ImageView item_icon;
    private TextView item_title,item_name,item_value,item_count;
    private CircleAngleTitleView item_button;

    private PortFolio portFolio;

    public void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_zuhe_create_view, this);
        item_icon = (ImageView)findViewById(R.id.item_icon);
        item_count = (TextView)findViewById(R.id.item_count);
        item_title = (TextView)findViewById(R.id.item_title);
        item_name = (TextView)findViewById(R.id.item_name);
        item_value = (TextView)findViewById(R.id.item_value);
        item_button = (CircleAngleTitleView)findViewById(R.id.item_button);

    }
    public void setData(PortFolio portFolio){
        this.portFolio = portFolio;
        if(portFolio!=null && portFolio.id!=0){
            if(StringUtils.startWithHttp(portFolio.imgPath)){
                ImageLoader.getInstance().showImage(portFolio.imgPath,item_icon);
            }else{
                item_icon.setImageResource(R.drawable.default_zuhe_icon);
            }
            item_title.setText(StringUtils.returnStr(portFolio.name));
            item_name.setText(StringUtils.returnStr(portFolio.userName));
            item_value.setText(StringUtils.returnStr(portFolio.netVal));
            item_count.setText(StringUtils.returnStr(portFolio.subCount+""));
        }
        item_button.setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.item_button){
                ActivityUtil.goCreateZuhe3((Activity)getContext(),portFolio.id);
            }
        }
    };
}
