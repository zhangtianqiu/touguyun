package com.touguyun.activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

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
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.DingyueItemView;
import com.touguyun.view.MainTopToolsView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/2.
 *
 * 我的订阅
 */
@EActivity(R.layout.activity_my_dingyue)
public class MyDingyueActivity extends BasePullRreshActivity<PortFolio>{

    @ViewById
    PullToRefreshListView refresh_list;

    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    TextView my_dingyue_count_title;
    @ViewById
    TextView my_dingyue_count_txt;

    @ViewById
    MainTopToolsView touguyun_main_top_tools;


    @ViewById
    View error_network;
    @ViewById
    View error_services;

    private NetErrorUtils netErrorUtils;

    private boolean isOne = true;
    private List<PortFolio> oneList;
    private List<PortFolio> twoList;
    public long lastidtwo;   //已创建组合
    public int oneCount,twoCount;

    @AfterViews
    void initView(){
        touguyun_titleBar.showTitle(R.string.my_dingyue_title);
        if(UserUtils.isTougu()){
            touguyun_main_top_tools.setData(new String[]{"被订阅","已订阅"},toolsClickListener);
            isOne = true;
        }else{
            touguyun_main_top_tools.setVisibility(View.GONE);
            isOne = false;
        }
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.drawable.list_item_selector_bg);
        refresh_list.setMode(PullToRefreshBase.Mode.BOTH);
        refresh_list.setOnRefreshListener(this);
        refresh_list.setOnItemClickListener(this);
        if(list == null){
            list = new ArrayList<PortFolio>();
            oneList = new ArrayList<PortFolio>();
            twoList = new ArrayList<PortFolio>();
        }
        netErrorUtils = new NetErrorUtils(error_network,error_services,errorOnclick,refresh_list);
        updateBottomData();
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

    private void updateBottomData(){
        my_dingyue_count_title.setText(isOne ?"被订阅总数":"已订阅总数");
        my_dingyue_count_txt.setText(isOne ?oneCount+"":twoCount+"");
    }

    private MainTopToolsView.MainTopToolsClickListener toolsClickListener = new MainTopToolsView.MainTopToolsClickListener(){
        @Override
        public void onTopClick(int position, View view) {
            if(position == 0){
                isOne = true;
                if(oneList!=null && oneList.size()>0){
                    list.clear();
                    list.addAll(oneList);
                    adapter.notifyDataSetChanged();
                }else{
                    addLists(false);
                }
            }else{
                isOne = false;
                if(twoList!=null && twoList.size()>0){
                    list.clear();
                    list.addAll(twoList);
                    adapter.notifyDataSetChanged();
                }else{
                    addLists(false);
                }
            }
            hasMore = true;
            updateBottomData();
        }
    };


    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        if((list.get(position)).id == -101) {
            view = ViewUtils.getListNullView(MyDingyueActivity.this, R.color.white, (int) (60 * getDM().density), R.drawable.error_zuhe_icon, "暂无订阅信息");
        }else if(view!=null && view instanceof DingyueItemView){
            ((DingyueItemView) view).setData(list.get(position), isOne,clickListener);
        }else{
            view = new DingyueItemView(MyDingyueActivity.this);
            ((DingyueItemView) view).setData(list.get(position), isOne,clickListener);
        }
        return view;
    }

    private PortFolio delPortlio;

    //点击取消
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.item_button && v.getTag()!=null){
                delPortlio = (PortFolio)(v.getTag());
                if(delPortlio!=null){
                    UIShowUtil.showDialog(MyDingyueActivity.this, true);
                    Http.unSubPortfolio(delPortlio.id,new Http.Callback<Boolean>(){
                        @Override
                        public void onSuccess(Boolean obj) {
                            super.onSuccess(obj);
                            UIShowUtil.toast(MyDingyueActivity.this, "组合 \"" + delPortlio.name + "\" 取消订阅成功");
                            updateListView();
                        }
                    });
                }

            }
        }
    };

    private void updateListView(){
        if(delPortlio!=null && twoList!=null){
            for (int i=0;i<twoList.size();i++){
                if(twoList.get(i).id == delPortlio.id){
                    twoList.remove(i);
                    delPortlio = null;
                    list.clear();
                    list.addAll(twoList);
                    if(adapter!=null){
                        adapter.notifyDataSetChanged();
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(list!=null && list.get(position-1)!=null&& list.get(position-1).id!=-101){
            ActivityUtil.goCombinationInfo(MyDingyueActivity.this,list.get(position-1).id);
        }
    }
    @Override
    public void addLists(boolean getMore) {
        isclearList = !getMore;
        UIShowUtil.showDialog(MyDingyueActivity.this, true);
        if(isOne){
            Http.myPortfolioCreateList(getMore ? lastid : 0, callback);
        }else{
            Http.myPortfolioSubList(getMore ? lastidtwo : 0, callback);
        }
    }
    @Override
    public void onRefreshComplete() {
        if(refresh_list!=null){
            refresh_list.onRefreshComplete();
        }
    }
    private Http.Callback callback = new Http.Callback<ListModule>() {
        @Override
        public void onSuccess(ListModule obj) {
            super.onSuccess(obj);
            if (isOne) {
                if (isclearList) {
                    oneList.clear();
                }
                if (obj != null && StringUtils.isNotEmpty(obj.list)) {
                    oneList.addAll(TouguJsonObject.parseList(obj.list, PortFolio.class));
                    lastid = obj.nextPageFlag;
                    hasMore = lastid == 0;
                    oneCount =obj.count;
                }
                list.clear();
                list.addAll(oneList);
            } else {
                if (isclearList) {
                    twoList.clear();
                }
                if (obj != null && StringUtils.isNotEmpty(obj.list)) {
                    twoList.addAll(TouguJsonObject.parseList(obj.list, PortFolio.class));
                        lastidtwo = obj.nextPageFlag;
                        hasMore = lastidtwo==0;
                        twoCount =obj.count;
                }
                list.clear();
                list.addAll(twoList);
            }
            if(list.size() == 0){
                list.add(TouguJsonObject.parseObject("{id:-101}", PortFolio.class));
            }
            if (adapter == null) {
                adapter = new RefreshAdapter();
                refresh_list.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            updateBottomData();
            refresh_list.onRefreshComplete();
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
}
