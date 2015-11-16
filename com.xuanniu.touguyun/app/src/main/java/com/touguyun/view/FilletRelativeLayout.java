package com.touguyun.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.touguyun.R;
/**
 * Created by zhengyonghui on 15/9/4.
 */
public class FilletRelativeLayout extends RelativeLayout{

    private float fillet=5;
    private int defaultColor;
    public FilletRelativeLayout(Context context) {
        super(context);
        init(null,0);
    }
    public FilletRelativeLayout(Context context, AttributeSet attrs) {
        super(context,attrs);
        init(attrs, 0);
    }
    public FilletRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FilletRelativeLayout, defStyle, 0);
        fillet=a.getDimension(R.styleable.FilletRelativeLayout_layoutFillet,fillet);
        defaultColor=a.getColor(R.styleable.FilletRelativeLayout_defaultBackColor,0);
        a.recycle();
        this.setBackgroundColor(Color.TRANSPARENT);
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
