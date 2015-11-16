package com.touguyun.view;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.touguyun.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/1.
 */
public class MainTopToolsView  extends LinearLayout{
    public MainTopToolsView(Context context) {
        super(context);
        initView(context);
    }
    public MainTopToolsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public MainTopToolsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        this.setOrientation(HORIZONTAL);
        setData(new String[]{"综合榜", "综合榜", "综合榜", "综合榜"}, null);
        this.setBackgroundColor(getResources().getColor(R.color.white_F7FAFD));
    }

    private String[] titles;
    private List<CircleAngleTitleView> viewList;

    public void setData(String[] titles,MainTopToolsClickListener listener){
        this.titles = titles;
        this.listener = listener;
        this.removeAllViews();
        viewList = new ArrayList<CircleAngleTitleView>();
        for (int i=0;i<titles.length;i++){
            CircleAngleTitleView item = new CircleAngleTitleView(getContext());
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1);
            if(titles.length<4){
                float windowWidth = getResources().getDisplayMetrics().widthPixels;
                int marginWidth = (int)windowWidth/3/titles.length;
                params.setMargins(marginWidth/2,0,marginWidth/2,0);
            }
            item.setLayoutParams(params);
            item.setGravity(Gravity.CENTER);
            item.setBackAndFrameColor(getResources().getColor(R.color.white_F7FAFD));
            item.setFrameEnable(false);
            item.setTextColor(getResources().getColor(R.color.black_323232));
            item.setTextSize(15);
            item.setText(titles[i]);
            item.setOnClickListener(clickListener);
            item.setTag(i);
            viewList.add(item);
            this.addView(item);
        }
        setPosition(0);
    }

    private int lastPosition = -1;
    private MainTopToolsClickListener listener;
    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v!=null && v.getTag()!=null){
                int position = Integer.parseInt(v.getTag().toString());
                setPosition(position);
                if(listener!=null){
                    listener.onTopClick(position,v);
                }
            }

        }
    };
    public void setPosition(int position){
        if(viewList!=null && viewList.size()>position){
            viewList.get(position).setTextColor(getResources().getColor(R.color.white));
            viewList.get(position).setBackAndFrameColor(getResources().getColor(R.color.red_F65D5D));
            if(lastPosition!=-1 && lastPosition!=position){
                viewList.get(lastPosition).setTextColor(getResources().getColor(R.color.black_323232));
                viewList.get(lastPosition).setBackAndFrameColor(Color.TRANSPARENT);
            }
            lastPosition = position;
        }
    }

    public interface MainTopToolsClickListener {
        public void onTopClick(int position,View view);
    }
}
