package com.touguyun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.touguyun.R;
public class FilletLinearLayout extends LinearLayout {

    private float fillet=5;
    private int defaultColor;

    public FilletLinearLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public FilletLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FilletBtView, defStyle, 0);
        fillet=a.getDimension(R.styleable.FilletBtView_fillet,fillet);
        defaultColor=a.getColor(R.styleable.FilletBtView_defaultColor, Color.WHITE);
        a.recycle();
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    public void setBgColor(int dColor){
        this.defaultColor=dColor;
        this.invalidate();
    }

    public void setFillet(float f){
        this.fillet=f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setColor(defaultColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(new RectF(0,0,getWidth(),getHeight()),fillet,fillet,paint);
        super.onDraw(canvas);
    }

}
