package com.touguyun.fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.touguyun.R;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.MainTopToolsView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/1.
 */
@EFragment(R.layout.titlebar_toors_viewpager)
public class MainZuheFragment extends BaseFragment{

    @ViewById
    TitleBar touguyun_titleBar;
    @ViewById
    MainTopToolsView touguyun_main_top_tools;
    @ViewById
    ViewPager touguyun_viewpaper;

    private List<Fragment> viewFragments;
    private MainPagerAdapter adapter;

    @AfterViews
    public void initView() {
        touguyun_titleBar.showTitle(R.string.fragment_zuhe_title);
        touguyun_titleBar.showRight(R.string.filter,0);
        if(UserUtils.isLogin() && UserUtils.isTougu()){
            touguyun_titleBar.showLeft(0,R.drawable.add_black_icon);
        }
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);

        touguyun_main_top_tools.setData(new String[]{"精选","净值","激进"},topToolsClickListener);    //9,3,10
        initFragmentList();
        adapter = new MainPagerAdapter(getChildFragmentManager());
        touguyun_viewpaper.setAdapter(adapter);
        touguyun_viewpaper.setOnPageChangeListener(pageChangeListener);
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

    private void initFragmentList(){
        viewFragments = new ArrayList<>();
        viewFragments.add(new ZuheFliterFragment().setData(9, false));
        viewFragments.add(new ZuheFliterFragment().setData(3,true));
        viewFragments.add(new ZuheFliterFragment().setData(10,true));
    }

    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
                ActivityUtil.goCreateZuhe1(getActivity(),1);
            }else{
                ActivityUtil.goCombinationFilter(getActivity());
            }
        }
    };

    private MainTopToolsView.MainTopToolsClickListener topToolsClickListener = new MainTopToolsView.MainTopToolsClickListener() {
        @Override
        public void onTopClick(int position, View view) {
            touguyun_viewpaper.setCurrentItem(position);
        }
    };


    @Override
    public void onHttpError(boolean isNet, String methodName, int errorType, String msg) {
    }


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
                return null;            }
        }
    }
}
