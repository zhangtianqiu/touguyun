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
import com.touguyun.utils.UserUtils;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.DingyueItemView;
import com.touguyun.view.ZuheCreateItemView;
import com.touguyun.view.ZuheItemView;

import java.util.ArrayList;
/**
 * Created by zhengyonghui on 15/9/9.
 */
public class ZuheSelfFragment extends OlnyFreshFragment<PortFolio>{

    private int filterType;
    private boolean canRefleshEnd;
    private boolean isMyZuhe = true;

    public ZuheSelfFragment(){
    }
    /**
     *
     * @param filterType   1被订阅 2已订阅
     * @param canRefleshEnd
     * @param isMyZuhe true组合，false订阅
     * @return
     */
    public ZuheSelfFragment setData(int filterType, boolean canRefleshEnd,boolean isMyZuhe){
        this.filterType = filterType;
        this.canRefleshEnd = canRefleshEnd;
        this.isMyZuhe = isMyZuhe;
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
        if(filterType ==1){
            Http.myPortfolioCreateList(lastid, callback);
        }else{
            Http.myPortfolioSubList(lastid,callback);
        }
    }
    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        if(((PortFolio)list.get(position)).id == -101) {
            view = ViewUtils.getListNullView(getActivity(), R.color.white, (int) (120 * dp), R.drawable.error_zuhe_icon, "暂无组合信息");
        }else{
            if(isMyZuhe) {
                if (filterType == 1) {
                    if (view != null && view instanceof ZuheCreateItemView) {
                        ((ZuheCreateItemView) view).setData((PortFolio) list.get(position));
                    } else {
                        view = new ZuheCreateItemView(getActivity());
                        ((ZuheCreateItemView) view).setData((PortFolio) list.get(position));
                    }
                    view.setPadding((int) (23 * dp), 0, (int) (23 * dp), 0);
                } else {
                    if (view != null && view instanceof ZuheItemView) {
                        ((ZuheItemView) view).setData((PortFolio) list.get(position), UserUtils.isTougu() ? -1 : -2);
                    } else {
                        view = new ZuheItemView(getActivity());
                        ((ZuheItemView) view).setData((PortFolio) list.get(position), UserUtils.isTougu() ? -1 : -2);
                    }
                    view.setPadding((int) (23 * dp), 0, (int) (23 * dp), 0);
                }
            }else{
                if(view!=null && view instanceof DingyueItemView){
                    ((DingyueItemView) view).setData((PortFolio)list.get(position), filterType==1,clickListener);
                }else{
                    view = new DingyueItemView(getActivity());
                    ((DingyueItemView) view).setData((PortFolio)list.get(position), filterType==1,clickListener);
                }
            }
        }
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
    /**
     * 取消订阅处理
     */
    private PortFolio delPortlio;

    private void updateListView(){
        if(delPortlio!=null && list!=null){
            for (int i=0;i<list.size();i++){
                if(((PortFolio)list.get(i)).id == delPortlio.id){
                    list.remove(i);
                    delPortlio = null;
                    list.clear();
                    list.addAll(list);
                    if(adapter!=null){
                        adapter.notifyDataSetChanged();
                    }
                    return;
                }
            }
        }
    }
    //点击取消
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.item_button && v.getTag()!=null){
                delPortlio = (PortFolio)(v.getTag());
                if(delPortlio!=null){
                    UIShowUtil.showDialog(getActivity(), true);
                    Http.unSubPortfolio(delPortlio.id,new Http.Callback<Boolean>(){
                        @Override
                        public void onSuccess(Boolean obj) {
                            super.onSuccess(obj);
                            UIShowUtil.toast(getActivity(), "组合 \"" + delPortlio.name + "\" 取消订阅成功");
                            updateListView();
                        }
                    });
                }

            }
        }
    };
}
