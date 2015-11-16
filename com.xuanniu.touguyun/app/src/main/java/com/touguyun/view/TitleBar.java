package com.touguyun.view;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.utils.StringUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
@EViewGroup(R.layout.view_titlebar)
public class TitleBar extends RelativeLayout {
	
	@ViewById
	TextView view_titlebar_title;
	@ViewById
	TextView view_titleBar_left;
	@ViewById
	TextView view_titleBar_right;
	@ViewById
	TextView view_titleBar_right_point;
    @ViewById
    View view_titleBar_line;
	
	private int leftIcon,leftText,rightIcon,rightText,titleStr,textColor,rightColor;
    private boolean showLine;
	private TitleBarClickListener listener;
	private Context mContext;
	public TitleBar(Context context) {
        super(context);
        initArrrs(context,null,0);
    }
	
	@Click
	void view_titleBar_left(){
		if(listener!=null){
			listener.onBarClick(true);
		}else{
			((Activity)getContext()).onBackPressed();
		}
	}

	@Click
	void view_titleBar_right(){
		if(listener!=null){
			listener.onBarClick(false);
		}
	}
	
    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initArrrs(context,attrs,0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initArrrs(context,attrs,defStyle);
    }

    public void initArrrs(Context context,AttributeSet attrs,int defStyle){
    	this.mContext = context;
    	TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyle, 0);
    	if(a!=null){
    		leftIcon=a.getResourceId(R.styleable.TitleBar_leftIcon,0);
    		leftText=a.getResourceId(R.styleable.TitleBar_leftText,0);
    		rightIcon=a.getResourceId(R.styleable.TitleBar_rightIcon, 0);
    		rightText=a.getResourceId(R.styleable.TitleBar_rightText, 0);
    		titleStr=a.getResourceId(R.styleable.TitleBar_bartitle,0);
    		textColor=a.getResourceId(R.styleable.TitleBar_textColor, android.R.color.white);
    		rightColor=a.getResourceId(R.styleable.TitleBar_rightColor, textColor);
            showLine = a.getBoolean(R.styleable.TitleBar_showLine, true);
    		a.recycle();
		}
    }
    
    @AfterViews
    public void initView(){
        showTitle(titleStr);
        showLeft(leftText, leftIcon);
        showRight(rightText, rightIcon);
        view_titleBar_line.setVisibility(showLine?VISIBLE:GONE);
    }
    
    public void showTitle(int resId){
    	view_titlebar_title.setTextColor(getResources().getColor(textColor));
    	view_titlebar_title.setText(resId==0?"":mContext.getString(resId));
    }
    public void showTitle(String title){
    	view_titlebar_title.setTextColor(getResources().getColor(textColor));
    	view_titlebar_title.setText(StringUtils.returnStr(title));
    }

    public void showNewPoint(boolean isShow){
        view_titleBar_right_point.setVisibility(isShow?VISIBLE:GONE);
    }

    public String getTitle(){
        return view_titlebar_title.getText().toString();
    }
    public void showLeft(int resIdTxt,int resIdIcon){
    	view_titleBar_left.setTextColor(getResources().getColor(textColor));
		view_titleBar_left.setText(resIdTxt==0?"":mContext.getString(resIdTxt));
    	if(resIdIcon!=0){
    		view_titleBar_left.setCompoundDrawablesWithIntrinsicBounds(resIdIcon, 0, 0, 0);
    	}
    	view_titleBar_left.setVisibility(resIdIcon != 0 || resIdTxt != 0 ? VISIBLE : GONE);
    }
    public void showRight(int resIdTxt,int resIdIcon){
    	view_titleBar_right.setTextColor(getResources().getColor(rightColor!=textColor?rightColor:textColor));
    	view_titleBar_right.setText(resIdTxt==0?"":mContext.getString(resIdTxt));
    	if(resIdIcon!=0){
    		view_titleBar_right.setCompoundDrawablesWithIntrinsicBounds(0, 0,resIdIcon, 0);
    	}
    	view_titleBar_right.setVisibility(resIdIcon!=0||resIdTxt!=0?VISIBLE:GONE);
    }
    public void showRight(String str,int resIdIcon){
    	view_titleBar_right.setTextColor(getResources().getColor(rightColor!=textColor?rightColor:textColor));
    	view_titleBar_right.setText(StringUtils.returnStr(str));
    	if(resIdIcon!=0){
    		view_titleBar_right.setCompoundDrawablesWithIntrinsicBounds(0, 0,resIdIcon, 0);
    	}
    	view_titleBar_right.setVisibility(resIdIcon!=0||StringUtils.isNotEmpty(str)?VISIBLE:GONE);
    }
    public void hideButton(boolean isLeft){
    	if(isLeft){
    		view_titleBar_left.setVisibility(GONE);
    	}else{
    		view_titleBar_right.setVisibility(GONE);
    	}
    }

    public void setRightTextColor(int color){
        view_titleBar_right.setTextColor(color);
    }

    public void setTitleBarClickListener(TitleBarClickListener listener){
    	this.listener = listener;
    }
    
    public interface TitleBarClickListener{
    	public void onBarClick(boolean isLeft);
    }
}
