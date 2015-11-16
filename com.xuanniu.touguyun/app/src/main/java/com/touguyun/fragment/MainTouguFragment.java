package com.touguyun.fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;

import com.touguyun.R;
import com.touguyun.activity.SearchActivity;
import com.touguyun.utils.ActivityUtil;
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
public class MainTouguFragment extends BaseFragment{

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
        touguyun_titleBar.showTitle(R.string.fragment_tougu_title);
        touguyun_titleBar.showRight(0,R.drawable.search_black_icon);
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);

        touguyun_main_top_tools.setData(new String[]{"推荐榜","收益榜","人气榜","好评榜"},topToolsClickListener);


        initFragmentList();
        adapter = new MainPagerAdapter(getChildFragmentManager());
        touguyun_viewpaper.setAdapter(adapter);
        touguyun_viewpaper.setOnPageChangeListener(pageChangeListener);
    }
    private void initFragmentList(){
        viewFragments = new ArrayList<>();
        viewFragments.add(new TouguFliterFragment().setData(0,true));
        viewFragments.add(new TouguFliterFragment().setData(5,false));
        viewFragments.add(new TouguFliterFragment().setData(7,false));
        viewFragments.add(new TouguFliterFragment().setData(8,false));
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(!isLeft){
                ActivityUtil.goSearch(getActivity(), SearchActivity.SEARCH_TYPE_TOUGU);
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
                return null;
            }
        }
    }
}
