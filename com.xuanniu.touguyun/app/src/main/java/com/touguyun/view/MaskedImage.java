package com.touguyun.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
public abstract class MaskedImage extends ImageView
{

    public MaskedImage(Context context)
    {
        super(context);
    }

    public MaskedImage(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public MaskedImage(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    public abstract Bitmap createMask();

    public Object extendOption;/*用来储存扩展信息*/
    protected void onDraw(Canvas canvas)
    {
        Drawable drawable;
        drawable = getDrawable();
        if(drawable == null)
            return;
        if(paint == null)
        {
        	paint = new Paint();
            paint.setFilterBitmap(false);
            paint.setXfermode(MASK_XFERMODE);
        }
        float w = getWidth();
        float h = getHeight();
        int i = canvas.saveLayer(0F, 0F, w, h, null, Canvas.ALL_SAVE_FLAG);
        drawable.setBounds(0, 0, (int)w, (int)h);
        drawable.draw(canvas);
        if(mask == null || mask.isRecycled())
        {
        	mask = createMask();
        }
        canvas.drawBitmap(mask, 0F, 0F, paint);
        canvas.restoreToCount(i);
        mask.recycle();
        return;
    }

    private static final Xfermode MASK_XFERMODE;
    private Bitmap mask;
    private Paint paint;

    static 
    {
        MASK_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    }
}
