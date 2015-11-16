package com.touguyun.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.touguyun.R;
import com.touguyun.module.DealHistory;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.TiaocangHistoryItemView;

/**
 * Created by zhengyonghui on 15/9/5.
 */
public class TiaocangHistoryFragment extends RefreshListFragment<DealHistory> {
    private TextView tiaocang_history_start_date;
    private TextView tiaocang_history_end_date;
    private String startDate, endDate;
    private long pid;
    private String code;
    private NetErrorUtils netErrorUtils;
    @Override
    public View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_tiaocang_history, container, false);
        }
        return fragmentView;
    }
    @Override
    public void initView() {
        tiaocang_history_start_date = (TextView) fragmentView.findViewById(R.id.tiaocang_history_start_date);
        tiaocang_history_end_date = (TextView) fragmentView.findViewById(R.id.tiaocang_history_end_date);
        refreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        netErrorUtils = new NetErrorUtils(fragmentView.findViewById(R.id.error_network),fragmentView.findViewById(R.id.error_services),errorOnclick,refreshView);
        loadData(false);
    }
    private View.OnClickListener errorOnclick = new View.OnClickListener() {
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
    public void setData(long pid,String code) {
        this.pid = pid;
        this.code = code;
    }
    @Override
    public void loadData(boolean getMore) {
        if(!getMore){
            lastid=0;
        }
        isclearList = !getMore;
        UIShowUtil.showDialog(getActivity(), true);
        Http.portfolioStockDealHistory(lastid, pid, code, "", "", callback);
    }
    private Http.Callback callback = new Http.Callback<JSONObject>() {
        @Override
        public void onSuccess(JSONObject obj) {
            super.onSuccess(obj);
            if(isclearList){
                list.clear();
            }
            if (obj != null) {
                startDate = obj.getString("start");
                endDate = obj.getString("end");
                if(StringUtils.isNotEmpty(obj.get("nextPageFlag"))){
                    lastid = obj.getLong("nextPageFlag");
                }
                JSONArray array = obj.getJSONArray("list");
                if (array != null && array.size() > 0) {
                    list.addAll(TouguJsonObject.parseList(array, DealHistory.class));
                }else if(list!=null && list.size()>0){
                    UIShowUtil.toast(getActivity(), "暂无更多");
                }
            }
            hasMore = lastid>0;
            tiaocang_history_start_date.setText(startDate);
            tiaocang_history_end_date.setText(endDate);
            if(list.size()==0){
                list.add(TouguJsonObject.parseObject("{id:-101}",DealHistory.class));
            }
            if (adapter == null) {
                adapter = new MyAdapter(list);
                refreshView.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            refreshView.onRefreshComplete();
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
    };
    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        if(list.get(position).id == -101){
            return ViewUtils.getListNullView(getActivity(), R.color.white, (int) (60 * dp), 0, "暂无调仓历史");
        }else if (view != null && view instanceof TiaocangHistoryItemView) {
            ((TiaocangHistoryItemView) view).setData(list.get(position));
        } else {
            view = new TiaocangHistoryItemView(getActivity());
            ((TiaocangHistoryItemView) view).setData(list.get(position));
        }
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
}
