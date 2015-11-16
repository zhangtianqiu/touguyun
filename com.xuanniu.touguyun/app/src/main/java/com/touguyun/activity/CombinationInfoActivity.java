package com.touguyun.activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.Comment;
import com.touguyun.module.Opinion;
import com.touguyun.module.PortFolio;
import com.touguyun.module.StockGroup;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.ShareUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.BaseDataView;
import com.touguyun.view.CombCommendView;
import com.touguyun.view.CombOpinionView;
import com.touguyun.view.CombinationLineChatView;
import com.touguyun.view.CombinationUserView;
import com.touguyun.view.CombinationtRadingRecordView;
import com.touguyun.view.TitleBar;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/8/27.
 */
@EActivity(R.layout.activity_combination_info)
public class CombinationInfoActivity extends BasePullRreshActivity<Comment> implements TitleBar.TitleBarClickListener {

    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    PullToRefreshListView refresh_list;

    @ViewById
    View error_network;
    @ViewById
    View error_services;

    private NetErrorUtils netErrorUtils;

    private long pid;
    private JSONObject baseJson;
    private PortFolio portFolio;
    private List<StockGroup> stockGroupList;
    private List<Opinion> opinionList;

    private String shareTitle;
    private String shareContext;

    @AfterViews
    void initView(){
        pid = getIntent().getLongExtra("pid",0);
        netErrorUtils = new NetErrorUtils(error_network,error_services,errorOnclick,refresh_list);
        touguyun_titleBar.setTitleBarClickListener(this);
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.color.white);
        refresh_list.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        refresh_list.setOnRefreshListener(this);
        if(list == null){
            list = new ArrayList<Comment>();
        }
        shareTitle = OnlineConfigAgent.getInstance().getConfigParams(CombinationInfoActivity.this, "SHARE_PORTFOLIO_TITLE");
        shareContext = OnlineConfigAgent.getInstance().getConfigParams(CombinationInfoActivity.this, "SHARE_PORTFOLIO_CONTEXT");
        initData();
    }

    private View.OnClickListener errorOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.error_network:
                    if(netErrorUtils!=null){
                        netErrorUtils.hideErrorView();
                    }
                    initData();
                    break;
                case R.id.error_services:
                    if(netErrorUtils!=null){
                        netErrorUtils.hideErrorView();
                    }
                    initData();
            }
        }
    };

    private void initData(){
        UIShowUtil.showDialog(CombinationInfoActivity.this, true);
        Http.BatchedCallback callback = new Http.BatchedCallback(){
            @Override
            public void onSuccess(Map<Object, JSONObject> obj) {
                super.onSuccess(obj);
                UIShowUtil.cancelDialog();
                if(obj.containsKey(1)){
                    baseJson = TouguJsonObject.parseObjectFromBody(obj.get(1),JSONObject.class);
                    if(StringUtils.isNotEmpty(baseJson.get("baseInfo"))){
                        portFolio = TouguJsonObject.parseObject(baseJson.getJSONObject("baseInfo"),PortFolio.class);
                    }
                    if(StringUtils.isNotEmpty(baseJson.get("stockGroup"))){
                        stockGroupList = TouguJsonObject.parseList(baseJson.getJSONArray("stockGroup"),StockGroup.class);
                    }
                    if(StringUtils.isNotEmpty(baseJson.get("opinion"))){
                        opinionList = TouguJsonObject.parseList(baseJson.getJSONArray("opinion"),Opinion.class);
                    }
                }
                list = new ArrayList<Comment>();
                list.add(TouguJsonObject.parseObject("{\"id\":\"-101\"}",Comment.class) );
                list.add(TouguJsonObject.parseObject("{\"id\":\"-102\"}",Comment.class) );
                if(obj.containsKey(2)){
                    JSONObject temp = TouguJsonObject.parseObjectFromBody(obj.get(2),JSONObject.class);
                    if(StringUtils.isNotEmpty(temp.get("nextPageFlag"))){
                        lastid = temp.getLongValue("nextPageFlag");
                    }else{
                        hasMore = false;
                    }
                    if(StringUtils.isNotEmpty(temp.get("list"))){
                        List<Comment> resultList = TouguJsonObject.parseList(temp.getJSONArray("list"),Comment.class);
                        if(resultList!=null && resultList.size()>0){
                            list.addAll(resultList);
                        }
                    }
                }
                if(list.size()<=2){
                    list.add(TouguJsonObject.parseObject("{\"id\":\"-103\"}",Comment.class) );
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
                Http.getPortfolioDetail(pid,callback,1),
                Http.getPortfolioComment(pid,0,callback,2)
        );
    }

    @Override
    public void onBarClick(boolean isLeft) {
        if(isLeft){
            onBackPressed();
        }else{
            ShareUtil.getInstance().openShare(CombinationInfoActivity.this,
                    StringUtils.isNotEmpty(shareTitle)?shareTitle:getString(R.string.share_portfolio_title),
                    StringUtils.isNotEmpty(shareContext)?shareContext:getString(R.string.share_portfolio_context),
                    ShareUtil.AppHost);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 12 && resultCode == RESULT_OK){
            initData();
        }else{
            ShareUtil.getInstance().onActivityResult(requestCode,resultCode,data);
        }
    }

    @Click
    void comb_info_comment_bt(){
        if(UserUtils.isLogin()){
            ActivityUtil.goCommentCreate(CombinationInfoActivity.this,pid,CommentCreateActivity.TYPE_COMB,12);
        }else{
            ActivityUtil.goLogin(CombinationInfoActivity.this);
        }
    }
    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        if(list!=null && list.get(position)!=null){
            if(list.get(position).id == -101){
                view = getFirstView();
            }else if(list.get(position).id == -102){
                view = getSecondView();
            }else if(list.get(position).id == -103){
                view = ViewUtils.getListNullView(CombinationInfoActivity.this, R.color.white, (int) (20 * getDM().density), 0, "暂无组合讨论");
            }else if(view instanceof CombCommendView){
                ((CombCommendView) view).setData(list.get(position));
            }else{
                view = new CombCommendView(CombinationInfoActivity.this);
                ((CombCommendView) view).setData(list.get(position));
            }
        }
        return view;
    }
    @Override
    public void addLists(boolean getMore) {
        if(!getMore){
            lastid=0;
        }
        isclearList = !getMore;
        UIShowUtil.showDialog(CombinationInfoActivity.this, true);
        Http.getPortfolioComment(pid,lastid,new Http.Callback<JSONObject>(){
            @Override
            public void onSuccess(JSONObject obj) {
                super.onSuccess(obj);
                if(isclearList || (list.size()==3 && list.get(0).id == -101&& list.get(1).id == -102&& list.get(2).id == -103)){
                    list.clear();
                    list.add(TouguJsonObject.parseObject("{\"id\":\"-101\"}",Comment.class) );
                    list.add(TouguJsonObject.parseObject("{\"id\":\"-102\"}",Comment.class) );
                }
                if(obj!=null){
                    if(StringUtils.isNotEmpty(obj.get("nextPageFlag"))){
                        lastid = obj.getLongValue("nextPageFlag");
                    }else{
                        hasMore = false;
                    }
                    if(StringUtils.isNotEmpty(obj.get("list"))){
                        list.addAll(TouguJsonObject.parseList(obj.getJSONArray("list"),Comment.class));
                    }
                }
                if(list.size()<=2){
                    list.add(TouguJsonObject.parseObject("{\"id\":\"-103\"}",Comment.class) );
                }
                if(adapter == null){
                    adapter = new RefreshAdapter();
                    refresh_list.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                onRefreshComplete();

            }
        });
    }
    @Override
    public void onRefreshComplete() {
        if(refresh_list!=null){
            refresh_list.onRefreshComplete();
        }
    }
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
    }

    public View getFirstView(){
        float dp = getResources().getDisplayMetrics().density;
        LinearLayout layout = new LinearLayout(CombinationInfoActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if(portFolio!=null) {

            if(StringUtils.isNotEmpty(portFolio.name)){
                touguyun_titleBar.showTitle(portFolio.name);
            }
            //用户信息
            CombinationUserView userView = new CombinationUserView(CombinationInfoActivity.this);
            userView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (160 * dp)));
            userView.setBackgroundResource(R.drawable.combination_profile_banner_bg);
            userView.setData(portFolio);
            userView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(portFolio!=null){
                        ActivityUtil.goCombinationProfile(CombinationInfoActivity.this,portFolio.toString());
                    }
                }
            });
            layout.addView(userView);
            //基础数据
            BaseDataView dataView = new BaseDataView(CombinationInfoActivity.this);
            dataView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (66 * dp)));
            dataView.setBackgroundColor(getResources().getColor(R.color.white_F7FAFD));
            dataView.setData(portFolio.netValue, portFolio.dayProfit, portFolio.position, portFolio.betaValue);
            layout.addView(dataView);
        }

        //组合仓位
        CombinationtRadingRecordView positionView = new CombinationtRadingRecordView(CombinationInfoActivity.this);
        positionView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        positionView.setData(stockGroupList,pid);
        layout.addView(positionView);


        return layout;

    }
    public View getSecondView(){
        float dp = getResources().getDisplayMetrics().density;
        LinearLayout layout = new LinearLayout(CombinationInfoActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //收益走势
        if(StringUtils.isNotEmpty(baseJson.get("lineDate")) && StringUtils.isNotEmpty(baseJson.get("sh300Line")) && StringUtils.isNotEmpty(baseJson.get("selfLine"))){
            CombinationLineChatView lineChatView = new CombinationLineChatView(CombinationInfoActivity.this);
            lineChatView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            lineChatView.setData(TouguJsonObject.parseList(baseJson.getJSONArray("lineDate"),String.class),pid);
            lineChatView.addLine(this.getResources().getColor(R.color.blue_15A1FF), "沪深300", TouguJsonObject.parseList(baseJson.getJSONArray("sh300Line"), Float.class));
            lineChatView.addLine(this.getResources().getColor(R.color.red_FB3636),portFolio!=null&&StringUtils.isNotEmpty(portFolio.name)?portFolio.name:"组合",
                    TouguJsonObject.parseList(baseJson.getJSONArray("selfLine"),Float.class));
            layout.addView(lineChatView);
        }
        if(opinionList!=null && opinionList.size()>0){
            //组合观点标题
            TextView opinionTitle = new TextView(CombinationInfoActivity.this);
            opinionTitle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(34*dp)));
            opinionTitle.setPadding((int) (23 * dp), 0, (int) (23 * dp), 0);
            opinionTitle.setTextSize(16);
            opinionTitle.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            opinionTitle.setTextColor(getResources().getColor(R.color.black_333333));
            opinionTitle.setBackgroundColor(getResources().getColor(R.color.white_F7FAFD));
            opinionTitle.setText("组合观点");
            layout.addView(opinionTitle);
            //组合观点
            for (Opinion opinion: opinionList){
                CombOpinionView opinionView = new CombOpinionView(CombinationInfoActivity.this);
                opinionView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                opinionView.setData(opinion,true);
                layout.addView(opinionView);
            }
        }

        //组合讨论
        TextView commentTitle = new TextView(CombinationInfoActivity.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(34*dp));
        params.setMargins(0,(int)(16*dp),0,0);
        commentTitle.setLayoutParams(params);
        commentTitle.setPadding((int)(23*dp),0,(int)(23*dp),0);
        commentTitle.setTextSize(16);
        commentTitle.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        commentTitle.setTextColor(getResources().getColor(R.color.black_333333));
        commentTitle.setBackgroundColor(getResources().getColor(R.color.white_F7FAFD));
        commentTitle.setText("组合讨论");
        layout.addView(commentTitle);
        return layout;
    }


}
