package com.touguyun.fragment;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.touguyun.R;
import com.touguyun.module.MessageList;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.CircleImageView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/9/1.
 */

@EFragment(R.layout.fragment_me)
public class MainMeFragment extends BaseFragment{

    private User loginUser;
    private int newPointCount;
    @ViewById
    TitleBar touguyun_titleBar;
    @ViewById
    RelativeLayout fragment_me_tougu_layout;
    @ViewById
    TextView fragment_me_fans;
    @ViewById
    TextView fragment_me_yield;

    @ViewById
    CircleImageView fragment_me_header;
    @ViewById
    TextView fragment_me_title;

    @ViewById
    View error_network;
    @ViewById
    View error_services;
    private NetErrorUtils netErrorUtils;

    @Click
    void fragment_me_tougu(){
        ActivityUtil.goMyTougu(getActivity());
    }
    @Click
    void fragment_me_zuhe(){
        ActivityUtil.goMyZuhe(getActivity());
    }
    @Click
    void fragment_me_guandian(){
        ActivityUtil.goMyGuandian(getActivity());
    }
    @Click
    void fragment_me_dingyue(){
        ActivityUtil.goMyDingyue(getActivity());
    }
    @Click
    void fragment_me_set_up(){
        if(loginUser!=null){
            ActivityUtil.goSetUp(getActivity(),loginUser.toString());
        }
    }
    @Click
    void fragment_me_header(){
        if(loginUser!=null){
            ActivityUtil.goUserPage(getActivity(),loginUser.uid);
//            ActivityUtil.goSetUp(getActivity(),loginUser.toString());
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        initView();
    }
    @AfterViews
    void initView(){
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
        fragment_me_tougu_layout.setVisibility(UserUtils.isLogin()&&UserUtils.isTougu()?View.VISIBLE:View.GONE);
        netErrorUtils = new NetErrorUtils(error_network,error_services,errorOnclick,null);
        loadData();
    }

    private void loadData(){
        Http.BatchedCallback callback = new Http.BatchedCallback(){
            @Override
            public void onSuccess(Map<Object, JSONObject> obj) {
                super.onSuccess(obj);
                if(obj.containsKey(1)){
                    loginUser = TouguJsonObject.parseObjectFromBody(obj.get(1),User.class);
                }
                if(obj.containsKey(2)){
                    List<MessageList> messageLists = TouguJsonObject.parseListFromBody(obj.get(2),MessageList.class);
                    newPointCount = 0;
                    if(messageLists!=null && messageLists.size()>0){
                        for (MessageList message:messageLists){
                            newPointCount += message.unReadNum;
                        }
                    }
                }
                updateViewWithData();
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
        UIShowUtil.showDialog(getActivity(), true);
        Http.executeBatchRequest(
                Http.consultantIndex(callback, 1),
                Http.getMessageCate(callback, 2)
        );
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

//    private Http.Callback callback = new Http.Callback<User>(){
//        @Override
//        public void onSuccess(User obj) {
//            super.onSuccess(obj);
//            loginUser = obj;
//            updateViewWithData();
//        }
//        @Override
//        public void onNetworkError(VolleyError error) {
//            super.onNetworkError(error);
//            if(netErrorUtils!=null){
//                netErrorUtils.showNetError(true);
//            }
//        }
//        @Override
//        public void onBusinessError(int errorCode, String errorMessage) {
//            super.onBusinessError(errorCode, errorMessage);
//            if(netErrorUtils!=null){
//                netErrorUtils.showNetError(false);
//            }
//        }
//    };

    public void updateViewWithData(){
        if(loginUser!=null){
            if(StringUtils.startWithHttp(loginUser.userImg)){
                ImageLoader.getInstance().showImage(loginUser.userImg,fragment_me_header);
            }else{
                fragment_me_header.setImageResource(R.drawable.default_header);
            }
            fragment_me_title.setText(StringUtils.returnStr(loginUser.name));
            if(loginUser.roleType == User.USER_TYPE_TOUGU){
                fragment_me_fans.setText(loginUser.fansNum+"");
                fragment_me_yield.setText(StringUtils.returnStr(loginUser.profitSum));
                fragment_me_yield.setTextColor(ViewUtils.getTextColorByTxt(getActivity(),StringUtils.returnStr(loginUser.profitSum)));
            }
            fragment_me_tougu_layout.setVisibility(loginUser.roleType == User.USER_TYPE_TOUGU?View.VISIBLE:View.GONE);
            touguyun_titleBar.showNewPoint(newPointCount>0);
        }
    }

    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(!isLeft){
                if(UserUtils.isLogin()){
                    ActivityUtil.goMessage(getActivity());
                }else{
                    ActivityUtil.goLogin(getActivity());
                }
            }
        }
    };

    @Override
    public void onHttpError(boolean isNet, String methodName, int errorType, String msg) {
    }
}
