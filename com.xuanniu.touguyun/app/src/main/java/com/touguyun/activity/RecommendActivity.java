package com.touguyun.activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.Consultant;
import com.touguyun.net.Http;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.TitleBar;
import com.touguyun.view.TouguItemView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/9/6.
 */
@EActivity(R.layout.activity_recommend)
public class RecommendActivity extends BasePullRreshActivity<Consultant>{

    @ViewById
    PullToRefreshListView refresh_list;
    @ViewById
    TitleBar touguyun_titleBar;
    @ViewById
    Button recomment_button;

    private Map<String,String> chooseMap;

    @ViewById
    View error_network;
    @ViewById
    View error_services;
    private NetErrorUtils netErrorUtils;

    @AfterViews
    void initView() {
        UserUtils.saveFirstRegister(false);
        chooseMap = new HashMap<String,String>();
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.color.white);
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
        refresh_list.setMode(PullToRefreshBase.Mode.DISABLED);
        refresh_list.setOnItemClickListener(this);
        refresh_list.setOnRefreshListener(this);
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
    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if (isLeft) {
                onBackPressed();
            } else {
                onBackPressed();
            }
        }
    };

    private class ViewHolder{
        public ImageView check;
        public TouguItemView tougu;
    }
    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        ViewHolder holder = null;
        if (view == null || view.getTag() == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(RecommendActivity.this).inflate(R.layout.list_item_tougu_choose_view, null);
            holder.check = (ImageView)view.findViewById(R.id.item_icon);
            holder.tougu = (TouguItemView)view.findViewById(R.id.item_view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(list!=null && list.get(position)!=null && list.get(position).uid!=0){
            holder.check.setImageResource(chooseMap.containsKey(list.get(position).uid+",")?R.drawable.check_box_checked_icon:R.drawable.check_box_default_icon);
            holder.tougu.setData(list.get(position),-1);
        }
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(chooseMap.containsKey(list.get(position-1).uid+",")){
            chooseMap.remove(list.get(position-1).uid+",");
        }else{
            chooseMap.put(list.get(position-1).uid+",","");
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public void addLists(boolean getMore) {
        if(list!=null){
            list.clear();
        }else{
            list = new ArrayList<Consultant>();
        }
        UIShowUtil.showDialog(RecommendActivity.this, true);
        Http.recommendConsult(new Http.Callback<List<Consultant>> () {
            @Override
            public void onSuccess(List<Consultant> obj) {
                super.onSuccess(obj);
                if (obj != null && obj.size()>0) {
                        list.addAll(obj);
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

    @Click
    void recomment_button(){
        if(chooseMap!=null && chooseMap.size()>0){
            StringBuffer sb = new StringBuffer();
            for(String str:chooseMap.keySet()){
                sb.append(str);
            }
            UIShowUtil.showDialog(RecommendActivity.this, true);
            Http.attentionBatch(sb.toString().substring(0, sb.length() - 1), new Http.Callback<Boolean>() {
                @Override
                public void onSuccess(Boolean obj) {
                    super.onSuccess(obj);
                    finish();
                }
                @Override
                public void onBusinessError(int errorCode, String errorMessage) {
                    super.onBusinessError(errorCode, errorMessage);
                }
            });
        }else{
            finish();
        }
    }
}
