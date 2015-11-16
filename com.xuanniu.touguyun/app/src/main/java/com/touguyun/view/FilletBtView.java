package com.touguyun.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.touguyun.R;

import org.androidannotations.annotations.EView;

@EView
public class FilletBtView extends TextView {

    private float fillet=5;
    private boolean isPress;
    private OnClickListener clickListener;
    private int defaultColor,pressedColor;

    public FilletBtView(Context context) {
        super(context);
        init(null, 0);
    }

    public FilletBtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FilletBtView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FilletBtView, defStyle, 0);
        fillet=a.getDimension(R.styleable.FilletBtView_fillet,fillet);
        defaultColor=a.getColor(R.styleable.FilletBtView_defaultColor,getResources().getColor(R.color.blue_3eabfe));
        pressedColor=a.getColor(R.styleable.FilletBtView_pressedColor,getResources().getColor(R.color.blue_1E90FF));
        a.recycle();
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setBgColor(int dColor,int pColor){
        this.defaultColor=dColor;
        this.pressedColor=pColor;
        this.invalidate();
    }

    public void setFillet(float f){
        this.fillet=f;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.clickListener=l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null!=clickListener){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isPress=true;
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    isPress=false;
                    this.invalidate();
                    clickListener.onClick(this);
                    break;
                default:
                    break;
            }
            return true;
        }else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        if (isPress){
            paint.setColor(pressedColor);
        }else{
            paint.setColor(defaultColor);
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(0,0,getWidth(),getHeight()),fillet,fillet,paint);
        super.onDraw(canvas);
    }

}
