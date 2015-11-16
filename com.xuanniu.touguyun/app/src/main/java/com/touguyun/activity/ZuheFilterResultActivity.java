package com.touguyun.activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.ListModule;
import com.touguyun.module.PortFolio;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.TitleBar;
import com.touguyun.view.ZuheItemView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
/**
 * Created by zhengyonghui on 15/9/4.
 */
@EActivity(R.layout.activity_title_refresh_list)
public class ZuheFilterResultActivity extends BasePullRreshActivity<PortFolio>{

    private int filterType;

    @ViewById
    PullToRefreshListView refresh_list;

    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    View error_network;
    @ViewById
    View error_services;
    private NetErrorUtils netErrorUtils;

    @AfterViews
    void initView(){
        filterType = getIntent().getIntExtra("type",0);
        touguyun_titleBar.showTitle(R.string.search_result_title);
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.color.white);
        refresh_list.setMode(PullToRefreshBase.Mode.BOTH);
        refresh_list.setOnRefreshListener(this);
        refresh_list.setOnItemClickListener(this);
        if(list == null){
            list = new ArrayList<PortFolio>();
        }
        netErrorUtils = new NetErrorUtils(error_network,error_services,errorOnclick,refresh_list);
        addLists(false);
    }
    private View.OnClickListener errorOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.error_network:
                    if(netErrorUtils!=null){
                        netErrorUtils.hideErrorView();
                    }
                    addLists(false);
                    break;
                case R.id.error_services:
                    if(netErrorUtils!=null){
                        netErrorUtils.hideErrorView();
                    }
                    addLists(false);
            }
        }
    };

    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        if(view!=null && view instanceof ZuheItemView){
            ((ZuheItemView) view).setData(list.get(position),-2,filterType);
        }else{
            view = new ZuheItemView(ZuheFilterResultActivity.this);
            ((ZuheItemView) view).setData(list.get(position),-2,filterType);
        }
        view.setPadding((int)(23*getDM().density),0,(int)(23*getDM().density),0);
        return view;
    }
    @Override
    public void addLists(boolean getMore) {
        hasMore = true;
        if(!getMore){
            lastid=0;
        }
        isclearList = !getMore;
        UIShowUtil.showDialog(ZuheFilterResultActivity.this, true);
        Http.getPortfolioSearch(lastid,filterType,new Http.Callback<ListModule>(){
            @Override
            public void onSuccess(ListModule obj) {
                super.onSuccess(obj);
                if(isclearList){
                    list.clear();
                }
                if(obj!=null){
                    lastid = obj.nextPageFlag;
                    hasMore = obj.nextPageFlag!=0;
                    if(obj.list!=null){
                        list.addAll(TouguJsonObject.parseList(obj.list,PortFolio.class));
                    }
                }
                if(adapter == null){
                    adapter = new RefreshAdapter();
                    refresh_list.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                onRefreshComplete();
            }
            @Override
            public void onNetworkError(VolleyError error) {
                super.onNetworkError(error);
                if(netErrorUtils!=null){
                    netErrorUtils.showNetError(true);
                }
            }
            @Override
            public void onBusinessError(int errorCode, String errorMessage) {
                super.onBusinessError(errorCode, errorMessage);
                if(netErrorUtils!=null){
                    netErrorUtils.showNetError(false);
                }
            }
        });
    }

    @Override
    public void onRefreshComplete() {
        if(refresh_list!=null){
            refresh_list.onRefreshComplete();
        }
    }
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        if(list!=null && list.get(position)!=null){
            ActivityUtil.goCombinationInfo(ZuheFilterResultActivity.this,list.get(position).id);
        }
    }
}
