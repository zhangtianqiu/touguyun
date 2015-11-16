package com.touguyun.view;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/8/31.
 */
public class CombinationLineChatView extends LinearLayout{
    public CombinationLineChatView(Context context) {
        super(context);
        initView(context);
    }
    public CombinationLineChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public CombinationLineChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private TextView view_comb_line_title_right;
    private LineChartView view_comb_line_chat;
    private long pid;


    private void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_combination_line_chat, this);
        view_comb_line_title_right = (TextView)findViewById(R.id.view_comb_line_title_right);
        view_comb_line_chat = (LineChartView)findViewById(R.id.view_comb_line_chat);
        view_comb_line_title_right.setOnClickListener(myOnclick);
        view_comb_line_chat.setOnClickListener(myOnclick);
    }

    private OnClickListener myOnclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.view_comb_line_title_right){
                ActivityUtil.goHistoryNet((Activity)getContext(),pid);
            }
        }
    };

    public void setData(List<String> dateList,long pid){
        this.pid = pid;
        view_comb_line_chat.setData(dateList);
    }

    public void addLine(int color,String name,List<Float> datas){
        if(datas!=null && datas.size()>=3){
            view_comb_line_chat.addLine(color,name,datas);
        }
    }
}
