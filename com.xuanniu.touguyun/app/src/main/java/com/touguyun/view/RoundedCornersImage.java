package com.touguyun.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
public class RoundedCornersImage extends MaskedImage
{

    public RoundedCornersImage(Context context)
    {
        super(context);
        cornerRadius = 0;
    }

    public RoundedCornersImage(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
//        cornerRadius = context.obtainStyledAttributes(attributeset, R.styleable.RoundedCornersImage).getDimensionPixelSize(0, 0);
    }

    public RoundedCornersImage(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
//        cornerRadius = context.obtainStyledAttributes(attributeset, R.styleable.RoundedCornersImage).getDimensionPixelSize(0, 0);
    }

    public Bitmap createMask()
    {
        int w = getWidth();
        int h = getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(1);
        paint.setColor(0xff000000);
        RectF rectf = new RectF(0F, 0F, w, h);
        canvas.drawRoundRect(rectf, cornerRadius, cornerRadius, paint);
        return bitmap;
    }

    private int cornerRadius;
}
