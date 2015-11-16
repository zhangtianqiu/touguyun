package com.touguyun.activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.touguyun.R;
import com.touguyun.fragment.ZuheFliterFragment;
import com.touguyun.fragment.ZuheSelfFragment;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.MainTopToolsView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/2.
 *
 * 我的订阅
 */
@EActivity(R.layout.titlebar_toors_viewpager)
public class MyZuheActivity extends BaseFragmentActivity{

    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    MainTopToolsView touguyun_main_top_tools;


    @ViewById
    ViewPager touguyun_viewpaper;

    private List<Fragment> viewFragments;
    private MainPagerAdapter adapter;


    @AfterViews
    void initView(){
        touguyun_titleBar.showTitle(R.string.my_zuhe_title);
        touguyun_titleBar.showLeft(0,R.drawable.back_icon_black);
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
        if(UserUtils.isTougu()){
            touguyun_titleBar.showRight("",R.drawable.add_black_icon);
            touguyun_main_top_tools.setData(new String[]{"创建的组合","订阅的组合"},toolsClickListener);
        }else{
            touguyun_main_top_tools.setVisibility(View.GONE);
        }
        initFragmentList();
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        touguyun_viewpaper.setAdapter(adapter);
        touguyun_viewpaper.setOnPageChangeListener(pageChangeListener);
    }

    private void initFragmentList(){
        viewFragments = new ArrayList<>();
        if(UserUtils.isTougu()){
            viewFragments.add(new ZuheSelfFragment().setData(1,true,true));
        }
        viewFragments.add(new ZuheSelfFragment().setData(2,true,true));
    }


    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
                onBackPressed();
            }else{
                ActivityUtil.goCreateZuhe1(MyZuheActivity.this,14);
            }
        }
    };

    private MainTopToolsView.MainTopToolsClickListener toolsClickListener = new MainTopToolsView.MainTopToolsClickListener(){
        @Override
        public void onTopClick(int position, View view) {
            touguyun_viewpaper.setCurrentItem(position);
        }
    };

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            touguyun_main_top_tools.setPosition(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private class MainPagerAdapter extends FragmentPagerAdapter {
        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
            if(viewFragments == null || viewFragments.size()==0){
                initFragmentList();
            }
        }

        @Override
        public int getCount() {
            return viewFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            if(viewFragments!=null){
                return viewFragments.get(position);
            } else {
                return null;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 14 && UserUtils.isTougu() && viewFragments!=null && viewFragments.size()>0){
            ((ZuheSelfFragment)viewFragments.get(0)).loadData(false);
        }
    }
}
