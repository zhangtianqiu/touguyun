package com.touguyun.fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSONArray;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.touguyun.R;
import com.touguyun.module.Consultant;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.TouguItemView;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/9.
 */
public class TouguFliterFragment extends OlnyFreshFragment<Consultant>{

    private int filterType;
    private boolean isRecommend;

    public TouguFliterFragment(){
    }
    /**
     *
     * @param filterType    筛选类型
     * @param isRecommend   是否推荐榜
     * @return
     */
    public TouguFliterFragment setData(int filterType,boolean isRecommend){
        this.filterType = filterType;
        this.isRecommend = isRecommend;
        return this;
    }
    @Override
    public void initView() {
        if(isRecommend){
            refreshView.setMode(PullToRefreshBase.Mode.DISABLED);
        }else{
            refreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        }
        netErrorUtils = new NetErrorUtils(fragmentView.findViewById(R.id.error_network),fragmentView.findViewById(R.id.error_services),errorOnclick,refreshView);
        listView.setSelector(R.drawable.list_item_selector_bg);
        loadData(false);
    }
    @Override
    public void loadData(boolean getMore) {
        hasMore = true;
        if(!getMore){
            lastid=0;
        }
        isclearList = !getMore;
        UIShowUtil.showDialog(getActivity(), true);
        if(isRecommend){
            Http.getConsultantRecommendList(listCallBack);
        }else{
            Http.getConsultantList(lastid, filterType, callback);
        }
    }


    private Http.Callback listCallBack = new Http.Callback<List<Consultant>>(){
        @Override
        public void onSuccess(List<Consultant> obj) {
            super.onSuccess(obj);
            list.clear();
            if(obj!=null){
                list.addAll(obj);
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
            refreshView.onRefreshComplete();
        }
        @Override
        public void onNetworkError(VolleyError error) {
            super.onNetworkError(error);
            refreshView.onRefreshComplete();
        }
    };

    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        if(((Consultant)list.get(position)).id == -101){
            view = ViewUtils.getListNullView(getActivity(),  R.color.white, (int)(120*dp), R.drawable.error_tougu_icon, "暂无投顾信息");
        }else if (view != null && view instanceof TouguItemView) {
            ((TouguItemView) view).setData((Consultant)list.get(position), position+1);
        } else {
            view = new TouguItemView(getActivity());
            ((TouguItemView) view).setData((Consultant)list.get(position), position+1);
//            view.setPadding((int) (23 *dp), 0, (int) (23 * dp), 0);
        }
        return view;
    }
    @Override
    public void addListData(JSONArray array) {
        if(list  == null){
            list =  new ArrayList();
        }
        list.addAll(TouguJsonObject.parseList(array, Consultant.class));
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(list!=null && list.get(position-1)!=null && ((Consultant)list.get(position-1)).id!=-101){
            ActivityUtil.goUserPage(getActivity(), ((Consultant)list.get(position - 1)).uid);
        }
    }
}
