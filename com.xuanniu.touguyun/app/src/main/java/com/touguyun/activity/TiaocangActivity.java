package com.touguyun.activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

import com.touguyun.R;
import com.touguyun.fragment.TiaocangBuyFragment;
import com.touguyun.fragment.TiaocangBuyFragment_;
import com.touguyun.fragment.TiaocangHistoryFragment;
import com.touguyun.fragment.TiaocangSellFragment;
import com.touguyun.fragment.TiaocangSellFragment_;
import com.touguyun.module.PortfolioStock;
import com.touguyun.net.Http;
import com.touguyun.utils.StringUtils;
import com.touguyun.view.MainTopToolsView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by zhengyonghui on 15/9/5.
 */
@EActivity(R.layout.activity_tiaocang)
public class TiaocangActivity extends BaseFragmentActivity implements MainTopToolsView.MainTopToolsClickListener{

    private long pid;
    private String code;

    @ViewById
    TitleBar touguyun_titleBar;
    @ViewById
    MainTopToolsView touguyun_main_top_tools;

    private TiaocangHistoryFragment historyFragment;
    private TiaocangBuyFragment buyFragment;
    private TiaocangSellFragment sellFragment;

    @AfterViews
    void initView(){
        pid = getIntent().getLongExtra("pid",0);
        code = getIntent().getStringExtra("code");
        touguyun_main_top_tools.setData(new String[]{"买入","卖出","历史"},this);
        onTopClick(0,null);
    }

    @Override
    public void onTopClick(int position, View view) {
        switch (position){
            case 0:
                if(buyFragment == null){
                    buyFragment = new TiaocangBuyFragment_();
                }
                currentPage= 0;
                startTimer();
                replaceFragment(buyFragment);
                break;
            case 1:
                if(sellFragment == null){
                    sellFragment = new TiaocangSellFragment_();
                }
                currentPage= 1;
                startTimer();
                replaceFragment(sellFragment);
                break;
            case 2:
                if(historyFragment == null){
                    historyFragment = new TiaocangHistoryFragment();
                    historyFragment.setData(pid,code);
                }
                currentPage= 2;
                replaceFragment(historyFragment);
                break;
        }
    }

    private int currentPage;

    private void replaceFragment(Fragment fragment) {
        if (null!=fragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.tiaocang_context,fragment).commitAllowingStateLoss();
        }
    }

    private Timer timer;

    public void startTimer(){
        if(timer !=null){
            timer.cancel();
        }
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(28);
            }
        }, 500,15*1000);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 28:
                        Http.portfolioDetailStock(pid, code, callback);
                    break;
                default:
                    break;
            }
        }
    };

    private Http.Callback callback = new Http.Callback<PortfolioStock>(){
        @Override
        public void onSuccess(PortfolioStock obj) {
            if(obj!=null){
                if("0.00".equals(obj.nowPrice)){
                    if(timer!=null){
                        timer.cancel();
                    }
                }
                if(StringUtils.isEmpty(touguyun_titleBar.getTitle())){
                    touguyun_titleBar.showTitle(obj.name);
                }
                if(currentPage == 0 && buyFragment!=null){
                    buyFragment.setDate(obj,pid);
                }else if(currentPage == 1 && sellFragment!=null){
                    sellFragment.setDate(obj,pid);
                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
    }
}
