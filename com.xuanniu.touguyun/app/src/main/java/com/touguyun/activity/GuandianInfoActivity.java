package com.touguyun.activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.Comment;
import com.touguyun.module.ListModule;
import com.touguyun.module.Opinion;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.ShareUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.CombCommendView;
import com.touguyun.view.CombOpinionView;
import com.touguyun.view.TitleBar;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/9/11.
 */
@EActivity(R.layout.activity_guandian_info)
public class GuandianInfoActivity extends BasePullRreshActivity<Comment>{

    private long oid;
    private Opinion opinion;
    @ViewById
    PullToRefreshListView refresh_list;
    @ViewById
    TitleBar touguyun_titleBar;
    @ViewById
    TextView guandian_info_praise;

    @ViewById
    View error_network;
    @ViewById
    View error_services;
    private NetErrorUtils netErrorUtils;

    private String shareTitle;
    private String shareContext;

    @AfterViews
    void initView() {
        netErrorUtils = new NetErrorUtils(error_network,error_services,errorOnclick,refresh_list);
        oid = getIntent().getLongExtra("oid",0);
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.color.white);
        refresh_list.setMode(PullToRefreshBase.Mode.BOTH);
        refresh_list.setOnRefreshListener(this);
        refresh_list.setOnItemClickListener(this);
        if(list == null){
            list = new ArrayList<>();
        }
        shareTitle = OnlineConfigAgent.getInstance().getConfigParams(GuandianInfoActivity.this, "SHARE_OPINION_TITLE");
        shareContext = OnlineConfigAgent.getInstance().getConfigParams(GuandianInfoActivity.this, "SHARE_OPINION_CONTEXT");
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
        if(list.get(position).id == -101){
            if (view != null && view instanceof CombOpinionView) {
                ((CombOpinionView) view).setData(opinion,false,userListener,false);
            } else {
                view = new CombOpinionView(GuandianInfoActivity.this);
                ((CombOpinionView) view).setData(opinion,false,userListener,false);
            }
        }else if(list.get(position).id == -102){
            view = ViewUtils.getListNullView(GuandianInfoActivity.this, R.color.white, (int) (60 * getDM().density), 0, "暂无观点评论");
        }else{
            if (view != null && view instanceof CombCommendView) {
                ((CombCommendView) view).setData(list.get(position));
            } else {
                view = new CombCommendView(GuandianInfoActivity.this);
                ((CombCommendView) view).setData(list.get(position));
            }
        }
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
    @Override
    public void addLists(boolean getMore) {
        isclearList = !getMore;
        UIShowUtil.showDialog(GuandianInfoActivity.this, true);
        if(getMore){
            Http.myConsultantList(getMore ? lastid : 0, callback);
        }else{
            Http.BatchedCallback callback = new Http.BatchedCallback(){
                @Override
                public void onSuccess(Map<Object, JSONObject> obj) {
                    super.onSuccess(obj);
                    UIShowUtil.cancelDialog();
                    if(obj.containsKey(1)){
                        opinion = TouguJsonObject.parseObjectFromBody(obj.get(1), Opinion.class);
                        if(isclearList || (list.size()==2 && list.get(0).id==-101 && list.get(1).id==-102)){
                            list.clear();
                            list.add(TouguJsonObject.parseObject("{id:-101}",Comment.class));
                        }
                        guandian_info_praise.setCompoundDrawablesWithIntrinsicBounds(opinion.liked==1?R.drawable.praise_red_icon:R.drawable.praise_icon,0,0,0);
                    }
                    if(obj.containsKey(2)){
                        ListModule module = TouguJsonObject.parseObjectFromBody(obj.get(2), ListModule.class);
                        lastid = module.nextPageFlag;
                        hasMore = module.nextPageFlag!=0;
                        if(module.list!=null){
                            list.addAll(TouguJsonObject.parseList(module.list, Comment.class));
                        }
                    }
                    if(list.size() == 1){
                        list.add(TouguJsonObject.parseObject("{id:-102}",Comment.class));
                    }
                    if(adapter == null){
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
            };
            Http.executeBatchRequest(
                    Http.opinionDetail(oid, callback, 1),         //基本信息
                    Http.opinionCommentList(oid,0, callback, 2) //列表
            );

        }


    }

    private Http.Callback callback = new Http.Callback<ListModule>(){
        @Override
        public void onSuccess(ListModule obj) {
            super.onSuccess(obj);
            if(isclearList){
                list.clear();
            }
            if(obj!=null){
                lastid = obj.nextPageFlag;
                hasMore = obj.nextPageFlag!=0;
                if(obj.list!=null){
                    list.addAll(TouguJsonObject.parseList(obj.list, Comment.class));
                }
            }
            if(adapter == null){
                adapter = new RefreshAdapter();
                refresh_list.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            onRefreshComplete();
        }
    };


    @Override
    public void onRefreshComplete() {
        if(refresh_list!=null){
            refresh_list.onRefreshComplete();
        }
    }

    @Click
    void guandian_info_praise_layout(){
        if(UserUtils.isLogin()){
            if(opinion!=null && opinion.liked == 0){
                Http.opinionLike(opinion.id, new Http.Callback<Boolean>(){
                    @Override
                    public void onSuccess(Boolean obj) {
                        super.onSuccess(obj);
                        UIShowUtil.toast(GuandianInfoActivity.this, "点赞成功");
                        if(opinion!=null){
                            opinion.likeNum++;
                            opinion.liked=1;
                        }
                        guandian_info_praise.setCompoundDrawablesWithIntrinsicBounds(opinion.liked==1?R.drawable.praise_red_icon:R.drawable.praise_icon,0,0,0);
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }else{
                UIShowUtil.toast(GuandianInfoActivity.this, "已赞过");
            }
        }else{
            ActivityUtil.goLogin(GuandianInfoActivity.this);
        }
    }

    @Click
    void guandian_info_recommend_layout(){
        if(UserUtils.isLogin()){
            if(opinion!=null){
                ActivityUtil.goCommentCreate(GuandianInfoActivity.this,opinion.id,CommentCreateActivity.TYPE_TOPIC,12);
            }
        }else{
            ActivityUtil.goLogin(GuandianInfoActivity.this);
        }
    }
    @Click
    void guandian_info_forward_layout(){
        ShareUtil.getInstance().openShare(GuandianInfoActivity.this,
                StringUtils.isNotEmpty(shareTitle)?shareTitle:getString(R.string.share_opinion_title),
                StringUtils.isNotEmpty(shareContext)?shareContext:getString(R.string.share_opinion_context),
                ShareUtil.AppHost);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12){
            addLists(false);
        }else{
            ShareUtil.getInstance().onActivityResult(requestCode,resultCode,data);
        }
    }

    private View.OnClickListener userListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.item_header || v.getId() == R.id.item_name) {
                if (opinion != null && opinion.user != null && opinion.user.uid > 0) {
                    ActivityUtil.goUserPage(GuandianInfoActivity.this, opinion.user.uid);
                }
            }
            if(v.getId() == R.id.item_share){
                guandian_info_forward_layout();
            }else if(v.getId() == R.id.item_comment){
                guandian_info_recommend_layout();
            }else if(v.getId() == R.id.item_praise){
                guandian_info_praise_layout();
            }
        }
    };
}

