package com.touguyun.activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.touguyun.R;
import com.touguyun.utils.ActivityStackControlUtil;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.AppUtils;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.UserUtils;

import java.util.ArrayList;

public class GuideActivity extends BaseActivity{

	 private ViewPager viewPager;
	    
	    /**装分页显示的view的数组*/
	    private ArrayList<View> pageViews;
//	    private ImageView imageView;


	    /**将小圆点的图片用数组表示*/
//	    private ImageView[] imageViews;
	    
	    //包裹滑动图片的LinearLayout
	    private ViewGroup viewPics;
	    
// --Commented out by Inspection START (15/7/9 下午12:05):
//	    //包裹小圆点的LinearLayout
//	    private ViewGroup viewPoints;
// --Commented out by Inspection STOP (15/7/9 下午12:05)

	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        //将要分页显示的View装入数组中
	        LayoutInflater inflater = getLayoutInflater();
	        pageViews = new ArrayList<View>();
	        pageViews.add(inflater.inflate(R.layout.activity_guide_viewpager, null));
	        pageViews.add(inflater.inflate(R.layout.activity_guide_viewpager, null));
	        pageViews.add(inflater.inflate(R.layout.activity_guide_viewpager, null));
            ImageLoader.getInstance().showImage(R.drawable.guide_001, ((ImageView) pageViews.get(0).findViewById(R.id.activity_guide_image)));
            ImageLoader.getInstance().showImage(R.drawable.guide_002, ((ImageView) pageViews.get(1).findViewById(R.id.activity_guide_image)));
            ImageLoader.getInstance().showImage(R.drawable.guide_003, ((ImageView) pageViews.get(2).findViewById(R.id.activity_guide_image)));
            ImageView bt = (ImageView)pageViews.get(2).findViewById(R.id.activity_guide_image);
            bt.setOnClickListener(Button_OnClickListener);

	        //创建imageviews数组，大小是要显示的图片的数量
//	        imageViews = new ImageView[pageViews.size()];
	        //从指定的XML文件加载视图
	        viewPics = (ViewGroup) inflater.inflate(R.layout.activity_guide, null);
	        
	        //实例化小圆点的linearLayout和viewpager
//	        viewPoints = (ViewGroup) viewPics.findViewById(R.id.viewGroup);
	        viewPager = (ViewPager) viewPics.findViewById(R.id.guidePages);
	        
	        //添加小圆点的图片
//	        for(int i=0;i<pageViews.size();i++){
//	            imageView = new ImageView(GuideActivity.this);
//	            //设置小圆点imageview的参数
//	            imageView.setLayoutParams(new LinearLayout.LayoutParams(20,20));//创建一个宽高均为20 的布局
//	            imageView.setPadding(20, 0, 20, 0);
//	            //将小圆点layout添加到数组中
//	            imageViews[i] = imageView;
//
//	            //默认选中的是第一张图片，此时第一个小圆点是选中状态，其他不是
//	            if(i==0){
//	                imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
//	            }else{
//	                imageViews[i].setBackgroundResource(R.drawable.page_indicator);
//	            }
//
//	            //将imageviews添加到小圆点视图组
//	            viewPoints.addView(imageViews[i]);
//	        }
	        
	        //显示滑动图片的视图
	        setContentView(viewPics);
	        
	        //设置viewpager的适配器和监听事件
	        viewPager.setAdapter(new GuidePageAdapter());
	        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	    }
    private Button.OnClickListener  Button_OnClickListener = new Button.OnClickListener() {
			public void onClick(View v) {
				//设置已经引导
				UserUtils.saveFirstOpen(false);
				//跳转
                if(!ActivityStackControlUtil.hasMainActivity()){
                    ActivityUtil.goMain(GuideActivity.this);
                }
				GuideActivity.this.finish();
			}
	    }; 
	    

	    
	    class GuidePageAdapter extends PagerAdapter {

	        //销毁position位置的界面
	        @Override
	        public void destroyItem(View v, int position, Object arg2) {
	            ((ViewPager)v).removeView(pageViews.get(position));
	            
	        }

	        @Override
	        public void finishUpdate(View arg0) {

	        }
	        
	        //获取当前窗体界面数
	        @Override
	        public int getCount() {
	            return pageViews.size();
	        }

	        //初始化position位置的界面
	        @Override
	        public Object instantiateItem(View v, int position) {
	            ((ViewPager) v).addView(pageViews.get(position));
	            return pageViews.get(position);
	        }

	        // 判断是否由对象生成界面
	        @Override
	        public boolean isViewFromObject(View v, Object arg1) {
	            return v == arg1;
	        }



	        @Override
	        public void startUpdate(View arg0) {

	        }

	        @Override
	        public int getItemPosition(Object object) {
	            return super.getItemPosition(object);
	        }

	        @Override
	        public void restoreState(Parcelable arg0, ClassLoader arg1) {

	        }

	        @Override
	        public Parcelable saveState() {
	            return null;
	        }
	    }
	    
	    
	    class GuidePageChangeListener implements OnPageChangeListener {

	        @Override
	        public void onPageScrollStateChanged(int arg0) {

	        }

	        @Override
	        public void onPageScrolled(int arg0, float arg1, int arg2) {

	        }

	        @Override
	        public void onPageSelected(int position) {
//	            for(int i=0;i<imageViews.length;i++){
//	                imageViews[position].setBackgroundResource(R.drawable.page_indicator_focused);
//	                //不是当前选中的page，其小圆点设置为未选中的状态
//	                if(position !=i){
//	                    imageViews[i].setBackgroundResource(R.drawable.page_indicator);
//	                }
//	            }
	            
	        }
	    }   
}
