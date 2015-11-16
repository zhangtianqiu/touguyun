package com.touguyun.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.touguyun.R;
/**
 * Created by zhengyonghui on 15/6/8.
 */
public class CircleImageView extends BaseImageView {

    private int frameColor;

    public CircleImageView(Context context) {
        super(context);
        init(null,0);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs,defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);
        if (null!=a){
            frameColor=a.getColor(R.styleable.CircleImageView_CircleImageFrColor,frameColor);
            a.recycle();
        }
    }

    public static Bitmap getBitmap(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawOval(new RectF(0.0f, 0.0f, width, height), paint);

        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(frameColor!=0){
            Paint paintFr=new Paint(Paint.ANTI_ALIAS_FLAG);
            paintFr.setStyle(Paint.Style.STROKE);
            paintFr.setColor(frameColor);
            paintFr.setStrokeWidth(1*getResources().getDisplayMetrics().density);
            int r = getWidth()/2;
            canvas.drawCircle(r,r,r-1,paintFr);
        }
    }

    @Override
    public Bitmap getBitmap() {
        return getBitmap(getWidth(), getHeight());
    }
}