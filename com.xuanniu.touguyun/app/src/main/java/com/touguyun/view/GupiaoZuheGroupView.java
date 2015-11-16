package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.StockGroup;
import com.touguyun.utils.StringUtils;
/**
 * Created by zhengyonghui on 15/9/3.
 */
public class GupiaoZuheGroupView extends LinearLayout{
    public GupiaoZuheGroupView(Context context) {
        super(context);
        initView(context);
    }
    public GupiaoZuheGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public GupiaoZuheGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private TextView item_title;
    private LinearLayout item_context;
    private void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_create_zuhe_gupiao_group, this);
        item_title = (TextView)findViewById(R.id.item_title);
        item_context = (LinearLayout)findViewById(R.id.item_context);
    }

    public void setData(StockGroup stockGroup,OnClickListener onClickListener){
        if(stockGroup!=null){
            item_title.setText(StringUtils.returnStr(stockGroup.plate)+" "+StringUtils.returnStr(stockGroup.percent));
            if(stockGroup.stockInfos!=null){
                item_context.removeAllViews();
                float dp = getResources().getDisplayMetrics().density;
                for (int i=0;i<stockGroup.stockInfos.size();i++){
                    GupiaoZuheItemView itemView = new GupiaoZuheItemView(getContext());
                    itemView.setData(stockGroup.stockInfos.get(i),onClickListener);
                    if(onClickListener!=null){
                        itemView.setId(R.id.item_view);
                        itemView.setTag(stockGroup.stockInfos.get(i).code);
                        itemView.setBackgroundResource(R.drawable.list_item_selector_bg);
                        itemView.setOnClickListener(onClickListener);
                    }
                    item_context.addView(itemView);
                    if(i!=stockGroup.stockInfos.size()-1){
                        View view = new View(getContext());
                        view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(0.7*dp)));
                        view.setBackgroundColor(getResources().getColor(R.color.gray_D8D8D8));
                        item_context.addView(view);
                    }
                }
            }

        }



    }
}
