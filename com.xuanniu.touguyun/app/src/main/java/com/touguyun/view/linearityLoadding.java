package com.touguyun.view;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * Created by zhengyonghui on 15/8/25.
 */
public class linearityLoadding extends LinearLayout{
    private static final int MAX_PROGRESS = 10000;
    private ClipDrawable mClipDrawable;
    private int mProgress = 0;
    private boolean running;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 如果消息是本程序发送的
            if (msg.what == 0x123) {
                mClipDrawable.setLevel(mProgress);
            }
        }
    };

    public linearityLoadding(Context context) {
        this(context, null);
    }

    public linearityLoadding(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(int iconA, int iconB, String msg) {
        if (iconA>0&&iconB>0) {
            this.removeAllViews();
            this.setOrientation(LinearLayout.VERTICAL);
            this.setBackgroundColor(Color.TRANSPARENT);
            mClipDrawable=new ClipDrawable(getResources().getDrawable(iconA), Gravity.BOTTOM, ClipDrawable.VERTICAL);
            LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity= Gravity.CENTER;
            ImageView icon=new ImageView(getContext());
            icon.setLayoutParams(params);
            icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            icon.setBackgroundResource(iconB);
            icon.setImageDrawable(mClipDrawable);
            addView(icon);
            TextView textView=new TextView(getContext());
            textView.setPadding(0, (int)(5*getResources().getDisplayMetrics().density), 0, 0);
            textView.setTextColor(Color.parseColor("#aaffffff"));
            textView.setLayoutParams(params);
            textView.setText(msg);
            addView(textView);
            Thread s = new Thread(r);
            s.start();
        }
    }

    public void stop() {
        mProgress = 0;
        running = false;
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            running = true;
            while (running) {
                handler.sendEmptyMessage(0x123);
                if (mProgress > MAX_PROGRESS) {
                    mProgress = 0;
                }
                mProgress += 100;
                try {
                    Thread.sleep(18);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
