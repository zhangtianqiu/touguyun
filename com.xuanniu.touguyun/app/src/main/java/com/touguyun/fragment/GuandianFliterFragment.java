package com.touguyun.fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSONArray;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.touguyun.R;
import com.touguyun.module.Opinion;
import com.touguyun.module.PortFolio;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.CombOpinionView;

import java.util.ArrayList;
/**
 * Created by zhengyonghui on 15/9/9.
 */
public class GuandianFliterFragment extends OlnyFreshFragment<PortFolio>{

    private int filterType;

    public static final int TYPE_MY_LIST = -1;
    public static final int TYPE_FOLLOW_LIST = -2;

    public GuandianFliterFragment(){
    }

    public GuandianFliterFragment setFilterType(int filterType){
        this.filterType = filterType;
        return this;
    }
    @Override
    public void initView() {
        refreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView.setSelector(R.drawable.list_item_selector_bg);
        netErrorUtils = new NetErrorUtils(fragmentView.findViewById(R.id.error_network),fragmentView.findViewById(R.id.error_services),errorOnclick,refreshView);
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
        if(filterType == TYPE_MY_LIST){
            Http.opinionListByUid(0,lastid,3,callback);
        }else if(filterType == TYPE_FOLLOW_LIST){
            Http.opinionConsultantList(lastid,callback);
        }else{
            Http.getOpinionIndex(lastid, filterType, callback);
        }
    }
    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        if(((Opinion)list.get(position)).id == -101){
            view = ViewUtils.getListNullView(getActivity(), R.color.white, (int)(120*dp),R.drawable.error_guandian_icon,"暂无观点信息");
        }else if(view!=null && view instanceof CombOpinionView){
            ((CombOpinionView) view).setData((Opinion)list.get(position),false);
        }else{
            view = new CombOpinionView(getActivity());
            ((CombOpinionView) view).setData((Opinion)list.get(position),false);
        }
        return view;
    }
    @Override
    public void addListData(JSONArray array) {
        if(list  == null){
            list =  new ArrayList();
        }
        list.addAll(TouguJsonObject.parseList(array, Opinion.class));
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(list!=null && list.get(position-1)!=null && list.get(position-1) instanceof Opinion &&  ((Opinion)list.get(position-1)).id!=-101){
            ActivityUtil.goGuandianInfo(getActivity(),((Opinion) list.get(position-1)).id);
        }

    }
}
