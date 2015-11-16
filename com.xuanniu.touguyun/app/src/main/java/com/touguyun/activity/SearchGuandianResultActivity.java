package com.touguyun.activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.ListModule;
import com.touguyun.module.Opinion;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.ShareUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.CombOpinionView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
/**
 * Created by zhengyonghui on 15/9/5.
 */
@EActivity(R.layout.activity_title_refresh_list)
public class SearchGuandianResultActivity extends BasePullRreshActivity<Opinion>{

    private String keyword;

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
        netErrorUtils = new NetErrorUtils(error_network,error_services,errorOnclick,refresh_list);
        keyword = getIntent().getStringExtra("keyword");
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.color.white);
        touguyun_titleBar.showTitle(R.string.search_title);
        refresh_list.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        refresh_list.setOnRefreshListener(this);
        refresh_list.setOnItemClickListener(this);
        list = new ArrayList<Opinion>();
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
            view = ViewUtils.getListNullView(SearchGuandianResultActivity.this, R.color.white, (int) (120 * getDM().density), R.drawable.error_guandian_icon, "没有搜索到您需要的内容\n请更新搜索词再次尝试");
        }else if(view!=null && view instanceof CombOpinionView){
            ((CombOpinionView) view).setData(list.get(position),false);
        }else{
            view = new CombOpinionView(SearchGuandianResultActivity.this);
            ((CombOpinionView) view).setData(list.get(position),false);
        }
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(list!=null && list.get(position-1)!=null && list.get(position-1).id!=-101){
            ActivityUtil.goGuandianInfo(SearchGuandianResultActivity.this,list.get(position-1).id);
        }
    }
    @Override
    public void addLists(boolean getMore) {
        isclearList = !getMore;
        UIShowUtil.showDialog(SearchGuandianResultActivity.this, true);
        Http.opinionSearch(keyword, getMore ? lastid : 0, new Http.Callback<ListModule>() {
            @Override
            public void onSuccess(ListModule obj) {
                if (isclearList) {
                    list.clear();
                }
                if (obj != null && StringUtils.isNotEmpty(obj.list)) {
                    list.addAll(TouguJsonObject.parseList(obj.list, Opinion.class));
                    lastid = obj.nextPageFlag;
                    hasMore = lastid == 0;
                }
                if(list.size() == 0){
                    list.add(TouguJsonObject.parseObject("{id:-101}", Opinion.class));
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareUtil.getInstance().onActivityResult(requestCode,resultCode,data);
    }
    @Override
    public void onRefreshComplete() {
        if(refresh_list!=null){
            refresh_list.onRefreshComplete();
        }
    }
}
