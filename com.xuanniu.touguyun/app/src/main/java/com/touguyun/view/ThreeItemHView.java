package com.touguyun.view;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.StringUtils;
/**
 * Created by zhengyonghui on 15/8/31.
 */
public class ThreeItemHView extends LinearLayout{
    public ThreeItemHView(Context context) {
        super(context);
        initView(context);
    }
    public ThreeItemHView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public ThreeItemHView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private TextView leftView,centerView,rightView;
    private long id;

    public void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_three_item_h, this);
        leftView = (TextView)findViewById(R.id.view_item_left);
        centerView = (TextView)findViewById(R.id.view_item_center);
        rightView = (TextView)findViewById(R.id.view_item_right);
    }

    public void setText(int left,int center,int right){
        setText(getResources().getString(left),getResources().getString(center),getResources().getString(right));
    }
    public void setText(String left,String center,String right){
        leftView.setText(StringUtils.returnStr(left));
        centerView.setText(StringUtils.returnStr(center));
        rightView.setText(StringUtils.returnStr(right));
    }
    public void setColor(int leftColorRid,int centerColorRid,int rightColorRid){
        leftView.setTextColor(getResources().getColor(leftColorRid));
        centerView.setTextColor(getResources().getColor(centerColorRid));
        rightView.setTextColor(getResources().getColor(rightColorRid));
    }
    public void setTextSize(int sizeSP){
//        float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sizeSP, getResources().getDisplayMetrics());
        leftView.setTextSize(sizeSP);
        centerView.setTextSize(sizeSP);
        rightView.setTextSize(sizeSP);
    }
    public void setData(String left,String center,String right,int leftColorRid,int centerColorRid,int rightColorRid,int sizeSP){
        setText(left, center, right);
        setColor(leftColorRid, centerColorRid, rightColorRid);
        setTextSize(sizeSP);
    }
    public void setData(String left,String center,String right,int leftColorRid,int centerColorRid,int rightColorRid,int sizeSP,long id){
        this.id = id;
        setData(left, center, right, leftColorRid, centerColorRid, rightColorRid, sizeSP);
        if(id>0){
            leftView.setOnClickListener(titleOnclick);
        }
    }
    public void setData(int left,int center,int right,int leftColorRid,int centerColorRid,int rightColorRid,int sizeSP){
        setText(left,center,right);
        setColor(leftColorRid,centerColorRid,rightColorRid);
        setTextSize(sizeSP);
    }

    private OnClickListener titleOnclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.view_item_left && id>0){
                ActivityUtil.goCombinationInfo((Activity)getContext(),id);
            }
        }
    };
}
