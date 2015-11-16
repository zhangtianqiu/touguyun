package com.touguyun.fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.touguyun.R;
import com.touguyun.module.Consultant;
import com.touguyun.module.Opinion;
import com.touguyun.module.PortFolio;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.CarouselView;
import com.touguyun.view.CombOpinionView;
import com.touguyun.view.TouguItemView;
import com.touguyun.view.ZuheItemView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/9/1.
 */

@EFragment(R.layout.fragment_main)
public class MainFragment extends BaseFragment{

    @ViewById
    LinearLayout fragment_tougu_layout;
    @ViewById
    LinearLayout fragment_zuhe_layout;
    @ViewById
    LinearLayout fragment_guandian_layout;
    @ViewById
    CarouselView fragment_zuhe_carouseview;
    @ViewById
    PullToRefreshScrollView fragment_main_pulltorefresh_scrollview;


    private List<PortFolio> portFolioList;
    private List<Consultant> consultantList;
    private List<Opinion> opinionList;
    private List<JSONObject> actionList;

    @ViewById
    View error_network;
    @ViewById
    View error_services;
    private NetErrorUtils netErrorUtils;


    @AfterViews
    void initViews(){
        netErrorUtils = new NetErrorUtils(error_network,error_services,errorOnclick,null);
        fragment_main_pulltorefresh_scrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        fragment_main_pulltorefresh_scrollview.setOnRefreshListener(onRefreshListener);
        if(portFolioList==null||consultantList==null||opinionList==null){
            loadData();
        }else{
            updateViewWithData();
        }
    }

    private PullToRefreshBase.OnRefreshListener onRefreshListener = new PullToRefreshBase.OnRefreshListener() {
        @Override
        public void onRefresh(PullToRefreshBase refreshView) {
            loadData();
        }
    };


    private void loadData(){
        UIShowUtil.showDialog(getActivity(), true);
        Http.BatchedCallback callback = new Http.BatchedCallback(){
            @Override
            public void onSuccess(Map<Object, JSONObject> obj) {
                super.onSuccess(obj);
                if(obj.containsKey(1)){
                    JSONObject json = TouguJsonObject.parseObjectFromBody(obj.get(1),JSONObject.class);
                    if(StringUtils.isNotEmpty(json)){
                        portFolioList = TouguJsonObject.parseList(json.getJSONArray("portfolio"),PortFolio.class);
                    }
                    if(StringUtils.isNotEmpty(json)){
                        consultantList = TouguJsonObject.parseList(json.getJSONArray("consult"),Consultant.class);
                    }
                    if(StringUtils.isNotEmpty(json)){
                        opinionList = TouguJsonObject.parseList(json.getJSONArray("opinion"),Opinion.class);
                    }
                }
                if(obj.containsKey(2)){
                    actionList = TouguJsonObject.parseListFromBody(obj.get(2), JSONObject.class);
                }
                fragment_main_pulltorefresh_scrollview.onRefreshComplete();
                updateViewWithData();
            }
            @Override
            public void onNetworkError(VolleyError error) {
                super.onNetworkError(error);
                if(netErrorUtils!=null){
                    netErrorUtils.showNetError(true);
                }
                fragment_main_pulltorefresh_scrollview.onRefreshComplete();
            }
            @Override
            public void onBusinessError(int errorCode, String errorMessage) {
                super.onBusinessError(errorCode, errorMessage);
                if(netErrorUtils!=null){
                    netErrorUtils.showNetError(false);
                }
                fragment_main_pulltorefresh_scrollview.onRefreshComplete();
            }
        };
        Http.executeBatchRequest(Http.getIndexTab(callback,1),Http.imageAD(callback,2));
    }

    private View.OnClickListener errorOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.error_network:
                    if(netErrorUtils!=null){
                        netErrorUtils.hideErrorView();
                    }
                    loadData();
                    break;
                case R.id.error_services:
                    if(netErrorUtils!=null){
                        netErrorUtils.hideErrorView();
                    }
                    loadData();
            }
        }
    };

    private CarouselView.OnCarouselViewItemClick viewItemClick = new CarouselView.OnCarouselViewItemClick() {
        @Override
        public void onViewclick(int position) {
            if(actionList!=null && position>=0 && position<actionList.size()){
                ActivityUtil.goActionActivity(getActivity(),actionList.get(position).getJSONObject("extraInfo"),false);
            }
        }
    };

    //点击进入投顾主页
    private View.OnClickListener consultantLintener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             if(v instanceof  TouguItemView){
                 TouguItemView touguItemView = (TouguItemView)v;
                 if(touguItemView.getData()!=null){
                     ActivityUtil.goUserPage(getActivity(),touguItemView.getData().uid);
                 }
             }
        }
    };
    private View.OnClickListener portFolioLintener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             if(v instanceof  ZuheItemView){
                 ZuheItemView zuheItemView = (ZuheItemView)v;
                 if(zuheItemView.getData()!=null){
                     ActivityUtil.goCombinationInfo(getActivity(),zuheItemView.getData().id);
                 }
             }
        }
    };
    private View.OnClickListener opinionLintener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             if(v instanceof  CombOpinionView){
                 CombOpinionView opinionView = (CombOpinionView)v;
                 if(opinionView.getData()!=null){
                     ActivityUtil.goGuandianInfo(getActivity(),opinionView.getData().id);
                 }
             }
        }
    };

    private String[] getStringArray(List<JSONObject> imageList){
        if(imageList == null || imageList.size() == 0){
            return null;
        }else{
            String[] images = new String[imageList.size()];
            for (int i=0;i<imageList.size();i++){
                images[i] = imageList.get(i).getString("picUrl");
            }
            return images;
        }
    };

    private void updateViewWithData(){
        if(getActivity() == null){
            return;
        }
        String[] imagesUrls = getStringArray(actionList);
        if(imagesUrls == null||imagesUrls.length==0){
            fragment_zuhe_carouseview.setVisibility(View.GONE);
        }else{
            fragment_zuhe_carouseview.setVisibility(View.VISIBLE);
            fragment_zuhe_carouseview.setImagesUrl(imagesUrls);
            fragment_zuhe_carouseview.setOnViewItemclickListener(viewItemClick);
        }
        if(consultantList!=null && consultantList.size()>0){
            fragment_tougu_layout.removeAllViews();
            for (Consultant consultant:consultantList){
                TouguItemView view = new TouguItemView(getActivity());
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                view.setData(consultant, -1);
                view.setOnClickListener(consultantLintener);
                view.setPadding((int)(23*dp),0,(int)(8*dp),0);
                view.setBackgroundResource(R.drawable.list_item_selector_bg);
                fragment_tougu_layout.addView(view);
                if(consultantList.lastIndexOf(consultant)<consultantList.size()-1){
                    View lineView = new View(getActivity());
                    LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(dp*0.7));
                    lineParams.setMargins((int)(23*dp),0,(int)(23*dp),0);
                    lineView.setLayoutParams(lineParams);
                    lineView.setBackgroundColor(getResources().getColor(R.color.gray_e5e5e5));
                    fragment_tougu_layout.addView(lineView);
                }
            }
        }
        if(portFolioList!=null && portFolioList.size()>0){
            fragment_zuhe_layout.removeAllViews();
            for (PortFolio portFolio:portFolioList){
                ZuheItemView view = new ZuheItemView(getActivity());
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                view.setData(portFolio,-2);
                view.setOnClickListener(portFolioLintener);
                view.setPadding((int)(23*dp),0,(int)(23*dp),0);
                view.setBackgroundResource(R.drawable.list_item_selector_bg);
                fragment_zuhe_layout.addView(view);
                if(portFolioList.lastIndexOf(portFolio)<portFolioList.size()-1){
                    View lineView = new View(getActivity());
                    LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(dp*0.7));
                    lineParams.setMargins((int)(23*dp),0,(int)(23*dp),0);
                    lineView.setLayoutParams(lineParams);
                    lineView.setBackgroundColor(getResources().getColor(R.color.gray_e5e5e5));
                    fragment_zuhe_layout.addView(lineView);
                }
            }
        }
        if(opinionList!=null && opinionList.size()>0){
            fragment_guandian_layout.removeAllViews();
            for (Opinion opinion:opinionList){
                CombOpinionView view = new CombOpinionView(getActivity());
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                view.setData(opinion,true);
                view.setOnClickListener(opinionLintener);
                view.setBackgroundResource(R.drawable.list_item_selector_bg);
                fragment_guandian_layout.addView(view);
            }
        }
    }






    @Override
    public void onHttpError(boolean isNet, String methodName, int errorType, String msg) {
    }
}
