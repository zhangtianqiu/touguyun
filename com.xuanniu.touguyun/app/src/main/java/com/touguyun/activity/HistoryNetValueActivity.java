package com.touguyun.activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.ListModule;
import com.touguyun.module.TimeValue;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
/**
 * Created by zhengyonghui on 15/8/28.
 */
@EActivity(R.layout.activity_title_refresh_list)
public class HistoryNetValueActivity extends BasePullRreshActivity<TimeValue>{

    private long pid;

    @ViewById
    View error_network;
    @ViewById
    View error_services;
    private NetErrorUtils netErrorUtils;

    @ViewById
    PullToRefreshListView refresh_list;

    @ViewById
    TitleBar touguyun_titleBar;

    @AfterViews
    void initView(){
        netErrorUtils = new NetErrorUtils(error_network,error_services,errorOnclick,refresh_list);
        pid = getIntent().getLongExtra("pid",0);
        touguyun_titleBar.showTitle(R.string.history_net_value_title);
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.color.white);
        refresh_list.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        refresh_list.setOnRefreshListener(this);
        if(list==null){
            list = new ArrayList<>();
        }
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
        ViewHolder holder = null;
        if("-101".equals(list.get(position).date)){
            return ViewUtils.getListNullView(HistoryNetValueActivity.this, R.color.white, (int) (120 * getDM().density), R.drawable.error_zuhe_icon, "暂无历史净值");
        }else if(view != null && view.getTag()!=null && view.getTag() instanceof  ViewHolder){
            holder = (ViewHolder)view.getTag();
        }else{
            holder = new ViewHolder();
            view = LayoutInflater.from(HistoryNetValueActivity.this).inflate(R.layout.list_item_history_net_value, null);
            holder.timeView = (TextView) view.findViewById(R.id.item_time);
            holder.valueView = (TextView) view.findViewById(R.id.item_context);
        }
        if(list!=null && list.get(position)!=null){
            holder.timeView.setText(StringUtils.isNotEmpty(list.get(position).date)?list.get(position).date:"");
            holder.valueView.setText(StringUtils.isNotEmpty(list.get(position).value)?list.get(position).value:"");
        }
        return view;
    }
    @Override
    public void onRefreshComplete() {
        if(refresh_list!=null){
            refresh_list.onRefreshComplete();
        }
    }

    private class ViewHolder{
        TextView timeView;
        TextView valueView;
    }

    @Override
    public void addLists(boolean getMore) {
        if(!getMore){
            lastid=0;
        }
        isclearList = !getMore;
        UIShowUtil.showDialog(HistoryNetValueActivity.this, true);
        Http.getNetValueList(pid, lastid, new Http.Callback<ListModule>() {
            @Override
            public void onSuccess(ListModule obj) {
                super.onSuccess(obj);
                if (isclearList) {
                    list.clear();
                }
                if (obj != null) {
                    lastid = obj.nextPageFlag;
                    hasMore = lastid>0;
                    if (StringUtils.isNotEmpty(obj.list)) {
                        list.addAll(TouguJsonObject.parseList(obj.list, TimeValue.class));
                    }
                }
                if(list.size() == 0){
                    list.add(TouguJsonObject.parseObject("{date:\"-101\"}",TimeValue.class));
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
    public void onItemClick(AdapterView parent, View view, int position, long id) {
    }


}
