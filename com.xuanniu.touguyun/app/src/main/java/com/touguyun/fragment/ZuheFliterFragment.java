package com.touguyun.fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSONArray;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.touguyun.R;
import com.touguyun.module.PortFolio;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.ZuheItemView;

import java.util.ArrayList;
/**
 * Created by zhengyonghui on 15/9/9.
 */
public class ZuheFliterFragment extends OlnyFreshFragment<PortFolio>{

    private int filterType;
    private boolean canRefleshEnd;

    public ZuheFliterFragment(){
    }

    public ZuheFliterFragment setData(int filterType, boolean canRefleshEnd){
        this.filterType = filterType;
        this.canRefleshEnd = canRefleshEnd;
        return this;
    }
    @Override
    public void initView() {
        if(canRefleshEnd){
            refreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        }else{
            refreshView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        listView.setSelector(R.drawable.list_item_selector_bg);
        netErrorUtils = new NetErrorUtils(fragmentView.findViewById(R.id.error_network),fragmentView.findViewById(R.id.error_services),errorOnclick,refreshView);
        if(list!=null && adapter!=null && list.size()>0){
            adapter.notifyDataSetChanged();
        }else{
            loadData(false);
        }
    }
    @Override
    public void loadData(boolean getMore) {
        hasMore = true;
        if(!getMore){
            lastid=0;
        }
        isclearList = !getMore;
        UIShowUtil.showDialog(getActivity(), true);
        Http.getPortfolioSearch(lastid, filterType, callback);
    }
    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        if(((PortFolio)list.get(position)).id == -101){
            view = ViewUtils.getListNullView(getActivity(), R.color.white, (int)(120*dp), R.drawable.error_zuhe_icon, "暂无组合信息");
        }else if(view!=null && view instanceof ZuheItemView){
            ((ZuheItemView) view).setData((PortFolio)(list.get(position)),-2);
        }else{
            view = new ZuheItemView(getActivity());
            ((ZuheItemView) view).setData((PortFolio)(list.get(position)),-2);
        }
        view.setPadding((int)(23*dp),0,(int)(23*dp),0);
        return view;
    }
    @Override
    public void addListData(JSONArray array) {
        if(list  == null){
            list =  new ArrayList();
        }
        list.addAll(TouguJsonObject.parseList(array, PortFolio.class));
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(list!=null && list.get(position-1)!=null && ((PortFolio)list.get(position-1)).id!=-101){
            ActivityUtil.goCombinationInfo(getActivity(),((PortFolio)list.get(position-1)).id);
        }

    }
}
