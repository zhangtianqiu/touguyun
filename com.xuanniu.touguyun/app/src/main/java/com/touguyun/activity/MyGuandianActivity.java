package com.touguyun.activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.fragment.GuandianFliterFragment;
import com.touguyun.module.Opinion;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ShareUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.CombOpinionView;
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
 * 我的观点
 */
@EActivity(R.layout.titlebar_toors_viewpager)
public class MyGuandianActivity extends BaseFragmentActivity{


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
        touguyun_titleBar.showTitle(R.string.my_guandian_title);
        touguyun_titleBar.showLeft(0,R.drawable.back_icon_black);
        if(UserUtils.isTougu()){
            touguyun_titleBar.showRight(0,R.drawable.my_guandian_edit_icon);
            touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
            touguyun_main_top_tools.setData(new String[]{"已发布","关注"},toolsClickListener);
        }else{
            touguyun_titleBar.hideButton(false);
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
            viewFragments.add(new GuandianFliterFragment().setFilterType(-1));
        }
        viewFragments.add(new GuandianFliterFragment().setFilterType(-2));
    }

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

    private MainTopToolsView.MainTopToolsClickListener toolsClickListener = new MainTopToolsView.MainTopToolsClickListener(){
        @Override
        public void onTopClick(int position, View view) {
        touguyun_viewpaper.setCurrentItem(position);
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


    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
                onBackPressed();
            }else{
                ActivityUtil.goCreateGuandian(MyGuandianActivity.this);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareUtil.getInstance().onActivityResult(requestCode,resultCode,data);
    }

}
