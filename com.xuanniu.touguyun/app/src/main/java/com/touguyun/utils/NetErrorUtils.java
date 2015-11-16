package com.touguyun.utils;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * Created by zhengyonghui on 15/7/10.
 */
public class NetErrorUtils implements View.OnClickListener{

    private View error_netword,error_services;
    private View.OnClickListener clickListener;
    private PullToRefreshListView mPullDownView;


    public NetErrorUtils(View error_netword, View error_services, View.OnClickListener clickListener, PullToRefreshListView mPullDownView){
        this.error_netword = error_netword;
        this.error_services = error_services;
        this.clickListener = clickListener;
        this.mPullDownView = mPullDownView;
        if(error_netword!=null){
            error_netword.setOnClickListener(clickListener!=null?clickListener:this);
        }
        if(error_services!=null){
            error_services.setOnClickListener(clickListener!=null?clickListener:this);
        }
    }


    public void showNetError(boolean isNetError){
        if(mPullDownView!=null){
            mPullDownView.onRefreshComplete();
        }
        if(error_netword!=null){
            error_netword.setVisibility(isNetError? View.VISIBLE: View.GONE);
        }
        if(error_services!=null){
            error_services.setVisibility(isNetError? View.GONE: View.VISIBLE);
        }
    }

    public void hideErrorView(){
        if(error_netword!=null){
            error_netword.setVisibility(View.GONE);
        }
        if(error_services!=null){
            error_services.setVisibility(View.GONE);
        }
    }
    @Override
    public void onClick(View view) {
        if((error_netword!=null && view.getId() == error_netword.getId()) ||
                error_services!=null && view.getId() == error_services.getId()){
            hideErrorView();
        }
    }
}
