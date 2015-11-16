package com.touguyun.view;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * Created by zhengyonghui on 15/8/31.
 */
public class IconTabView extends LinearLayout {

    private TextView text;
    private ImageView icon;
    public IconTabView(Context context) {
        super(context);
        initView();
    }
    public IconTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView(){
        if (null==text||null==icon) {
            this.removeAllViews();
            this.setOrientation(HORIZONTAL);
            this.setGravity(Gravity.CENTER);
            LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            text=new TextView(getContext());
            text.setSingleLine();
            text.setBackgroundColor(Color.TRANSPARENT);
            this.addView(text,params);
            icon=new ImageView(getContext());
            icon.setContentDescription("");
            icon.setBackgroundColor(Color.TRANSPARENT);
            params.setMargins(5, 0, 0, 0);
            this.addView(icon, params);
            icon.setVisibility(View.GONE);
        }
    }

    public void setText(String str){
        initView();
        text.setText(str);
    }
    public void setText(int strId){
        initView();
        text.setText(strId);
    }
    public void setTextColor(int color){
        initView();
        text.setTextColor(color);
    }
    // --Commented out by Inspection START (15/7/9 上午11:49):
//	public void setTextSize(float size){
//		initView();
//		text.setTextSize(size);
//	}
// --Commented out by Inspection STOP (15/7/9 上午11:49)
    public void setTextSize(int unit,float size){
        initView();
        text.setTextSize(unit, size);
    }
    public void setTypeface(Typeface tf,int style){
        initView();
        text.setTypeface(tf, style);
    }

    public CharSequence getText(){
        initView();
        return text.getText();
    }

    public void setIcon(boolean isShow,int resId){
        initView();
        if (isShow) {
            icon.setImageResource(resId);
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }
    }

}
