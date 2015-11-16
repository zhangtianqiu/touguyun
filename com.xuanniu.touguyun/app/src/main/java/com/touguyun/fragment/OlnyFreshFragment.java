package com.touguyun.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.touguyun.R;
import com.touguyun.module.ListModule;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
/**
 * Created by zhengyonghui on 15/9/9.
 */

public abstract class OlnyFreshFragment<T> extends RefreshListFragment {

    public NetErrorUtils netErrorUtils;

    @Override
    public View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_only_pulltorefresh, container, false);
        }

        return fragmentView;
    }
    @Override
    public void initView() {
        refreshView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setSelector(R.color.white);
        loadData(false);
    }

    public View.OnClickListener errorOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.error_network:
                    if(netErrorUtils!=null){
                        netErrorUtils.hideErrorView();
                    }
                    loadData(false);
                    break;
                case R.id.error_services:
                    if(netErrorUtils!=null){
                        netErrorUtils.hideErrorView();
                    }
                    loadData(false);
            }
        }
    };
    public Http.Callback<ListModule> callback = new Http.Callback<ListModule>(){
        @Override
        public void onSuccess(ListModule obj) {
            super.onSuccess(obj);
            if (isclearList) {
                list.clear();
            }
            if (obj != null) {
                lastid = obj.nextPageFlag;
                hasMore = obj.nextPageFlag != 0;
                if (obj.list != null) {
                    addListData(obj.list);
                }
            }
            if(list == null || list.size() == 0){
                addListData(JSONArray.parseArray("[{id:-101}]"));
            }
            if (adapter == null) {
                adapter = new MyAdapter(list);
                refreshView.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            refreshView.onRefreshComplete();
        }
        @Override
        public void onBusinessError(int errorCode, String errorMessage) {
            super.onBusinessError(errorCode, errorMessage);
            if(netErrorUtils!=null){
                netErrorUtils.showNetError(false);
            }
        }
        @Override
        public void onNetworkError(VolleyError error) {
            super.onNetworkError(error);
            if(netErrorUtils!=null){
                netErrorUtils.showNetError(true);
            }
        }
    };

    public abstract void addListData(JSONArray array);
}
