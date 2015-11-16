package com.touguyun.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RoundedCornersImageCustom extends MaskedImage
{

	private int roundWidth;  
    private int roundHeight;  
    
    private boolean leftTop,rightTop,leftBottom,rightBottom;
    
    public RoundedCornersImageCustom(Context context)
    {
        super(context);
        roundWidth = 5;
        roundHeight = 5;
    }

    public RoundedCornersImageCustom(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public RoundedCornersImageCustom(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

// --Commented out by Inspection START (15/7/9 上午11:41):
//    public void setcornerRadius(int width,int height){
//    	roundWidth = width;
//    	roundHeight = height;
//    }
// --Commented out by Inspection STOP (15/7/9 上午11:41)

// --Commented out by Inspection START (15/7/9 上午11:41):
//    public void setRounded(boolean leftTop,boolean rightTop,boolean leftBottom,boolean rightBottom){
//    	this.leftTop = leftTop;
//    	this.rightTop = rightTop;
//    	this.leftBottom = leftBottom;
//    	this.rightBottom = rightBottom;
//    }
// --Commented out by Inspection STOP (15/7/9 上午11:41)

    public Bitmap createMask()
    {
        int w = getWidth();
        int h = getHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(1);
        paint.setColor(0xff000000);
        paint.setAntiAlias(true); 
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        if(leftTop){
        	drawLiftUp(canvas, paint);
        }
        if(leftBottom){
        	drawLiftDown(canvas, paint);
        }
        if(rightTop){
        	drawRightUp(canvas, paint);
        }
        if(rightBottom){
        	drawRightDown(canvas, paint);
        }
        return bitmap;
    }

    private void drawLiftUp(Canvas canvas,Paint paint) {
        Path path = new Path();
        path.moveTo(0, roundHeight);  
        path.lineTo(0, 0);  
        path.lineTo(roundWidth, 0);  
        path.arcTo(new RectF(
                0,   
                0,   
                roundWidth*2,   
                roundHeight*2),   
                -90,   
                -90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
      
    private void drawLiftDown(Canvas canvas,Paint paint) {
        Path path = new Path();
        path.moveTo(0, getHeight()-roundHeight);  
        path.lineTo(0, getHeight());  
        path.lineTo(roundWidth, getHeight());  
        path.arcTo(new RectF(
                0,   
                getHeight()-roundHeight*2,   
                0+roundWidth*2,   
                getHeight()),  
                90,   
                90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
      
    private void drawRightDown(Canvas canvas,Paint paint) {
        Path path = new Path();
        path.moveTo(getWidth()-roundWidth, getHeight());  
        path.lineTo(getWidth(), getHeight());  
        path.lineTo(getWidth(), getHeight()-roundHeight);  
        path.arcTo(new RectF(
                getWidth()-roundWidth*2,   
                getHeight()-roundHeight*2,   
                getWidth(),   
                getHeight()), 0, 90);  
        path.close();  
        canvas.drawPath(path, paint);  
    }  
      
    private void drawRightUp(Canvas canvas,Paint paint) {
        Path path = new Path();
        path.moveTo(getWidth(), roundHeight);  
        path.lineTo(getWidth(), 0);  
        path.lineTo(getWidth()-roundWidth, 0);  
        path.arcTo(new RectF(
                getWidth()-roundWidth*2,   
                0,   
                getWidth(),   
                0+roundHeight*2),   
                -90,   
                90);  
        path.close();  
        canvas.drawPath(path, paint);  
    } 
    
}
