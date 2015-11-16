package com.touguyun.activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.Consultant;
import com.touguyun.module.ListModule;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.TitleBar;
import com.touguyun.view.TouguItemView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * Created by zhengyonghui on 15/9/5.
 */
@EActivity(R.layout.activity_title_refresh_list)
public class SearchTouguResultActivity extends BasePullRreshActivity<Consultant>{

    private String key;

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
    void initView() {
        key = getIntent().getStringExtra("key");
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.color.white);
        touguyun_titleBar.showTitle(R.string.search_title);
        refresh_list.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        refresh_list.setOnRefreshListener(this);
        refresh_list.setOnItemClickListener(this);
        list = new ArrayList<Consultant>();
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
        if((list.get(position)).id == -101) {
            view = ViewUtils.getListNullView(SearchTouguResultActivity.this, R.color.white, (int) (120 * getDM().density), R.drawable.error_tougu_icon, "没有搜索到您需要的内容\n请更新搜索词再次尝试");
        }else if (view != null && view instanceof TouguItemView) {
            ((TouguItemView) view).setData(list.get(position), -1);
        } else {
            view = new TouguItemView(SearchTouguResultActivity.this);
            ((TouguItemView) view).setData(list.get(position), -1);
            view.setPadding((int)(23*getDM().density),0,(int)(23*getDM().density),0);
        }
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(list!=null && list.get(position-1)!=null && list.get(position-1).id!=-101){
            ActivityUtil.goUserPage(SearchTouguResultActivity.this,  list.get(position - 1).uid);
        }
    }
    @Override
    public void addLists(boolean getMore) {
        isclearList = !getMore;
        UIShowUtil.showDialog(SearchTouguResultActivity.this, true);
        Http.searchConsultList(key, getMore ? lastid : 0, new Http.Callback<ListModule>() {
            @Override
            public void onSuccess(ListModule obj) {
                if(isclearList){
                    list.clear();
                }
                if(obj!=null && StringUtils.isNotEmpty(obj.list)){
                    list.addAll(TouguJsonObject.parseList(obj.list,Consultant.class));
                    lastid = obj.nextPageFlag;
                    hasMore = lastid == 0;
                }
                if(list.size() == 0){
                    list.add(TouguJsonObject.parseObject("{id:-101}", Consultant.class));
                }
                if (adapter == null) {
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
}
