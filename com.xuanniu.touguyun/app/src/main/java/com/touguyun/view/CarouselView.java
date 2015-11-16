package com.touguyun.view;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.touguyun.R;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.StringUtils;

import org.w3c.dom.DocumentType;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/15.
 */
public class CarouselView extends FrameLayout {

    private int count;
    private List<ImageView> imageViews;
    private Context context;
    private ViewPager vp;
    private boolean isAutoPlay;
    private int currentItem;
    private int delayTime;
    private LinearLayout ll_dot;
    private List<ImageView> iv_dots;
    private Handler handler = new Handler();
    private OnCarouselViewItemClick onitemclick;

    public CarouselView(Context context) {
        this(context, null);
    }
    public CarouselView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initData();

    }
    private void initData() {
        imageViews = new ArrayList<ImageView>();
        iv_dots = new ArrayList<ImageView>();
        delayTime = 2000;
        if(context!=null && context instanceof OnCarouselViewItemClick){
            onitemclick = (OnCarouselViewItemClick)context;
        }
    }
    public void setOnViewItemclickListener(OnCarouselViewItemClick onitemclick){
        this.onitemclick = onitemclick;
    }
    public void setImagesUrl(String[] imagesUrl) {
        initLayout();
        initImgFromNet(imagesUrl);
        showTime();
    }
    public void setImagesRes(int[] imagesRes) {
        initLayout();
        initImgFromRes(imagesRes);
        showTime();
    }
    private void initLayout() {
        imageViews.clear();
        iv_dots.clear();
        this.removeAllViews();
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_carouse_layout, this, true);
        vp = (ViewPager) view.findViewById(R.id.view_carouse_viewpager);
        ll_dot = (LinearLayout) view.findViewById(R.id.view_carouse_dot);
        ll_dot.removeAllViews();
    }

    private void initImgFromRes(int[] imagesRes) {
        float dp = getResources().getDisplayMetrics().density;
        count = imagesRes.length;
        for (int i = 0; i < count; i++) {
            ImageView iv_dot = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = (int)(5*dp);
            params.rightMargin = (int)(5*dp);
            iv_dot.setImageResource(R.drawable.dot_blur);
            ll_dot.addView(iv_dot, params);
            iv_dots.add(iv_dot);
        }
        iv_dots.get(0).setImageResource(R.drawable.dot_focus);

        for (int i = 0; i <= count + 1; i++) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
//            iv.setBackgroundResource(R.drawable.loading);
            if (i == 0) {
                ImageLoader.getInstance().showImage(imagesRes[count - 1], iv);
                iv.setTag(count - 1);
            } else if (i == count + 1) {
                ImageLoader.getInstance().showImage(imagesRes[0], iv);
                iv.setTag(0);
            } else {
                ImageLoader.getInstance().showImage(imagesRes[i - 1], iv);
                iv.setTag(i - 1);
            }
            iv.setOnClickListener(onClickListener);
            imageViews.add(iv);
        }
    }

    private void initImgFromNet(String[] imagesUrl) {
        count = imagesUrl.length;
        for (int i = 0; i < count; i++) {
            ImageView iv_dot = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            params.leftMargin = 5;
            params.rightMargin = 5;
            iv_dot.setImageResource(R.drawable.dot_blur);
            ll_dot.addView(iv_dot, params);
            iv_dots.add(iv_dot);
        }
        iv_dots.get(0).setImageResource(R.drawable.dot_focus);

        for (int i = 0; i <= count + 1; i++) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
//            iv.setBackgroundResource(R.mipmap.loading);
            if (i == 0) {
                ImageLoader.getInstance().showImage(imagesUrl[count - 1], iv);
                iv.setTag(count - 1);
            } else if (i == count + 1) {
                ImageLoader.getInstance().showImage(imagesUrl[0], iv);
                iv.setTag(0);
            } else {
                ImageLoader.getInstance().showImage(imagesUrl[i - 1], iv);
                iv.setTag(i - 1);
            }
            iv.setOnClickListener(onClickListener);
            imageViews.add(iv);
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(StringUtils.isNotEmpty(v.getTag()) && onitemclick!=null){
                onitemclick.onViewclick(Integer.parseInt(v.getTag().toString()));
            }
        }
    };

    private void showTime() {
        vp.setAdapter(new CarousePagerAdapter());
        vp.setFocusable(true);
        vp.setCurrentItem(1);
        currentItem = 1;
        vp.setOnPageChangeListener(new MyOnPageChangeListener());
        startPlay();
    }

    private void startPlay() {
        isAutoPlay = true;
        handler.removeCallbacks(task);
        handler.postDelayed(task, 2000);
    }

    private final Runnable task = new Runnable() {

        @Override
        public void run() {
            if (isAutoPlay) {
                currentItem = currentItem % (count + 1) + 1;
                if (currentItem == 1) {
                    vp.setCurrentItem(currentItem, false);
                    handler.post(task);
                } else {
                    vp.setCurrentItem(currentItem);
                    handler.postDelayed(task, 3000);
                }
            } else {
                handler.postDelayed(task, 5000);
            }
        }
    };

    class CarousePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViews.get(position));
            return imageViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViews.get(position));
        }

    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:
                    isAutoPlay = false;
                    break;
                case 2:
                    isAutoPlay = true;
                    break;
                case 0:
                    if (vp.getCurrentItem() == 0) {
                        vp.setCurrentItem(count, false);
                    } else if (vp.getCurrentItem() == count + 1) {
                        vp.setCurrentItem(1, false);
                    }
                    currentItem = vp.getCurrentItem();
                    isAutoPlay = true;
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < iv_dots.size(); i++) {
                if (i == arg0 - 1) {
                    iv_dots.get(i).setImageResource(R.drawable.dot_focus);
                } else {
                    iv_dots.get(i).setImageResource(R.drawable.dot_blur);
                }
            }
        }

    }

    public interface OnCarouselViewItemClick {
        public void onViewclick(int position);
    }
}
