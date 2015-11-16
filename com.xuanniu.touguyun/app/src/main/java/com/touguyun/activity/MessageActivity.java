package com.touguyun.activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.MessageList;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.MessageMainView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/3.
 */
@EActivity(R.layout.activity_title_refresh_list)
public class MessageActivity extends BasePullRreshActivity<MessageList>{

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
        netErrorUtils = new NetErrorUtils(error_network,error_services,errorOnclick,refresh_list);
        touguyun_titleBar.showTitle(R.string.message_title);
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.color.white);
        refresh_list.setMode(PullToRefreshBase.Mode.DISABLED);
        refresh_list.setOnItemClickListener(this);
        refresh_list.setOnRefreshListener(this);
        list = new ArrayList<MessageList>();
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
        if(view!=null && view instanceof MessageMainView){
            ((MessageMainView) view).setData(list.get(position));
        }else{
            view = new MessageMainView(MessageActivity.this);
            ((MessageMainView) view).setData(list.get(position));
        }
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(list!=null && list.get(position-1)!=null){
            list.get(position-1).unReadNum = 0;
            adapter.notifyDataSetChanged();
            ActivityUtil.goMessageList(MessageActivity.this,list.get(position-1).name,list.get(position-1).id);
        }

    }
    @Override
    public void addLists(boolean getMore) {
        isclearList = !getMore;
        UIShowUtil.showDialog(MessageActivity.this, true);
        Http.getMessageCate(new Http.Callback<List<MessageList>>(){
            @Override
            public void onSuccess(List<MessageList> obj) {
                if (isclearList) {
                    list.clear();
                }
                if (obj != null && obj.size()>0) {
                    list.addAll(obj);
                    hasMore = lastid == 0;
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
