package com.touguyun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
public class ImageProgressBar extends View {

	private DecimalFormat df;
	private int maxProgress;
	private int processColor;
	private int borderColor;
	private int progress;
	private int bgColor;
	private RectF oval;

	private void initData(){
		processColor= Color.parseColor("#2ebdff");
		bgColor= Color.parseColor("#88000000");
		borderColor= Color.parseColor("#ffffff");
		df = new DecimalFormat("#%");
		oval = new RectF();
		maxProgress = 100;
		progress = 0;
	}
	public ImageProgressBar(Context context) {
		super(context );
		initData();
	}
	public ImageProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs );
		initData();
	}
	public ImageProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData();
	}

	public void setHaveDouble(){
		df = new DecimalFormat("#.0%");
	}
	
	public void setColor(int bgColor,int borderColor,int processColor){
		this.bgColor=bgColor;
		this.borderColor=borderColor;
		this.processColor=processColor;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();
		if(width!=height)
		{
			int min= Math.min(width, height);
			width=min;
			height=min;
		}
		int progressStrokeWidth=width/25;
		Paint paint = new Paint();
		paint.setColor(bgColor);
		canvas.drawCircle(width/2, width/2, width/2, paint);
		paint.setAntiAlias(true); 
		paint.setColor(borderColor); 
		paint.setStrokeWidth(progressStrokeWidth); 
		paint.setStyle(Style.STROKE);
 
		oval.left = progressStrokeWidth / 2; 
		oval.top = progressStrokeWidth / 2;
		oval.right = width - progressStrokeWidth / 2; 
		oval.bottom = height - progressStrokeWidth / 2; 
		
		canvas.drawArc(oval, -90, 360, false, paint); 
		paint.setColor(processColor);
		canvas.drawArc(oval, -90, ((float) progress / maxProgress * 360), false, paint); 
 
		String text ;
		paint.setStrokeWidth(1);
		if (progress<=0) {
			text="0%";
		} else {
			text = df.format((float)progress/maxProgress);
		}
		int textHeight = height / 4;
		paint.setTextSize(textHeight);
		int textWidth = (int) paint.measureText(text, 0, text.length());
		paint.setStyle(Style.FILL);
		canvas.drawText(text, width / 2 - textWidth / 2, height / 2 +textHeight/2, paint);
	}
	
	public void setMax(int max) {
		this.maxProgress = max;
	}
	
	public void setProgress(int progress) {
		/*this.progress=progress;
		if (progress < 0) {
            this.progress = 0;
        }
        if (progress > maxProgress) {
        	this.progress = maxProgress;
        }
        invalidate();*/
		setProgressNotInUiThread(progress);
//		AQUtility.debug("Progress", "Loding:"+this.progress+"-Max:"+maxProgress);
	}
	
	public void setProgressNotInUiThread(int progress) {
		this.progress=progress;
		if (progress < 0) {
            this.progress = 0;
        }
        if (progress > maxProgress) {
        	this.progress = maxProgress;
        }
		postInvalidate();
	}
	
	public int getMax() {
		return maxProgress;
	}
	
	public int getProgress() {
		return progress;
	}
	
	public boolean isIndeterminate(){
		if (progress>=maxProgress||progress<=0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void incrementProgressBy(int diff){
		setProgress(progress+diff);
	}
	
}