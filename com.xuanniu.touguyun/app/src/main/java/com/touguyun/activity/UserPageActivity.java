package com.touguyun.activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.R;
import com.touguyun.module.Consultant;
import com.touguyun.module.ListModule;
import com.touguyun.module.Opinion;
import com.touguyun.module.PortFolio;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.ShareUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.CircleAngleTitleView;
import com.touguyun.view.CircleImageView;
import com.touguyun.view.CombOpinionView;
import com.touguyun.view.LineChartView;
import com.touguyun.view.ThreeItemHView;
import com.touguyun.view.TitleBar;
import com.umeng.onlineconfig.OnlineConfigAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/8/31.
 */
@EActivity(R.layout.activity_user_page)
public class UserPageActivity extends BaseActivity{

    private long uid;

    @ViewById
    TitleBar touguyun_titleBar;
    @ViewById
    CircleImageView user_page_header;   //用户头像
//    @ViewById
//    CircleAngleTitleView user_page_find_ta;  //找TA开户
    @ViewById
    CircleAngleTitleView user_page_follow_ta;  //关注TA
    @ViewById
    CircleAngleTitleView user_page_unfollow_ta;  //已关注TA
    @ViewById
    TextView user_page_username;  //用户名称及会员标识
    @ViewById
    TextView user_page_tag;  //12年从业经历
    @ViewById
    TextView user_page_subscribe_count;  //订阅数
    @ViewById
    TextView user_page_fans_count;  //粉丝数
    @ViewById
    TextView user_page_proceeds_count;  //收益数

    @ViewById
    TextView user_page_tab_comb;
    @ViewById
    TextView user_page_tab_opinion;

    @ViewById
    LinearLayout user_page_comb_layout;
    @ViewById
    LinearLayout user_page_opinion_layout;

    private Consultant baseData;
    private JSONObject lineJson;
    private List<PortFolio> portFolioList;
    private List<Opinion> opinionList;

    private String shareTitle;
    private String shareContext;

    @AfterViews
    void initView(){
        uid = getIntent().getLongExtra("uid",0);
        touguyun_titleBar.setTitleBarClickListener(barClickListener);
        Http.BatchedCallback callback = new Http.BatchedCallback(){
            @Override
            public void onSuccess(Map<Object, JSONObject> obj) {
                super.onSuccess(obj);
                if(obj.containsKey(1)){
                    baseData = TouguJsonObject.parseObjectFromBody(obj.get(1), Consultant.class);
                }
                if(obj.containsKey(2)){
                    lineJson = TouguJsonObject.parseObjectFromBody(obj.get(2), JSONObject.class);
                }
                if(obj.containsKey(3)){
                    portFolioList = TouguJsonObject.parseListFromBody(obj.get(3), PortFolio.class);
                }
                if(obj.containsKey(4)){
                    ListModule module = TouguJsonObject.parseObjectFromBody(obj.get(4), ListModule.class);
                    if(module!=null && module.list!=null){
                        opinionList = TouguJsonObject.parseList(module.list,Opinion.class);
                    }
                }
                initUserBaseView();
                getCombView(user_page_comb_layout);
                getOpinoinView(user_page_opinion_layout);
            }
        };
        shareTitle = OnlineConfigAgent.getInstance().getConfigParams(UserPageActivity.this, "SHARE_CONSULTANT_TITLE");
        shareContext = OnlineConfigAgent.getInstance().getConfigParams(UserPageActivity.this, "SHARE_CONSULTANT_CONTEXT");
        UIShowUtil.showDialog(UserPageActivity.this, true);
        Http.executeBatchRequest(
                Http.consultListFromUser(uid, callback, 1),         //基本信息
                Http.consultCompSh300(uid, callback, 2),            //拆线图
                Http.portfolioListByUid(uid, callback, 3),          //组合
                Http.opinionListByUid(uid, 0, 3, callback, 4)       //观点
        );
    }


    private TitleBar.TitleBarClickListener barClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
                onBackPressed();
            }else{
                ShareUtil.getInstance().openShare(UserPageActivity.this,
                        StringUtils.isNotEmpty(shareTitle)?shareTitle:getString(R.string.share_consultant_title),
                        StringUtils.isNotEmpty(shareContext)?shareContext:getString(R.string.share_consultant_context),
                        ShareUtil.AppHost);
            }
        }
    };

    @Click
    void user_page_header(){
        ActivityUtil.goUserPageInfo(UserPageActivity.this,uid);
    }

    @Click
    void user_page_tab_comb(){
        if(user_page_opinion_layout.getVisibility() == View.VISIBLE){
            user_page_tab_comb.setTextColor(getResources().getColor(R.color.red_FB3636));
            user_page_tab_comb.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.user_page_tab_line);
            user_page_tab_opinion.setTextColor(getResources().getColor(R.color.black_232323));
            user_page_tab_opinion.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            user_page_opinion_layout.setVisibility(View.GONE);
            user_page_comb_layout.setVisibility(View.VISIBLE);
        }
    }

    @Click
    void user_page_tab_opinion(){
        if(user_page_comb_layout.getVisibility() == View.VISIBLE){
            user_page_tab_comb.setTextColor(getResources().getColor(R.color.black_232323));
            user_page_tab_comb.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            user_page_tab_opinion.setTextColor(getResources().getColor(R.color.red_FB3636));
            user_page_tab_opinion.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.user_page_tab_line);
            user_page_comb_layout.setVisibility(View.GONE);
            user_page_opinion_layout.setVisibility(View.VISIBLE);
        }
    }


    private void initUserBaseView(){
        if(baseData!=null){
            if(StringUtils.startWithHttp(baseData.userImg)){
                ImageLoader.getInstance().showImage(baseData.userImg,user_page_header);
            }else{
                user_page_header.setImageResource(R.drawable.default_header);
            }
            user_page_username.setText(StringUtils.returnStr(baseData.name));
            user_page_username.setCompoundDrawablesWithIntrinsicBounds(0,0,baseData.authState == User.USER_TYPE_TOUGU?R.drawable.tougu_v_icon:0,0);
            user_page_tag.setText(StringUtils.returnStr(baseData.yearsEmployment));
            user_page_subscribe_count.setText(baseData.subscribeNum+"");
            user_page_fans_count.setText(baseData.fansNum+"");
            user_page_proceeds_count.setText(baseData.profitSum);
            user_page_follow_ta.setVisibility(baseData.isSelf==0&&baseData.attentionState == 0?View.VISIBLE:View.GONE);
            user_page_unfollow_ta.setVisibility(baseData.isSelf==0&&baseData.attentionState == 1?View.VISIBLE:View.GONE);
        }
    }
    private void getCombView(LinearLayout layout){
        float dp = getDM().density;
        layout.removeAllViews();

        //投资收益
        if(portFolioList!=null && portFolioList.size()>0){
            ThreeItemHView title = new ThreeItemHView(UserPageActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins((int)(23*dp),(int)(13*dp),(int)(23*dp),0);
            title.setLayoutParams(params);
            title.setData("投资组合","净值","日收益",R.color.black_333333,R.color.black_333333,R.color.black_333333,15);
            layout.addView(title);
            for (int i=0;i<portFolioList.size();i++){
                ThreeItemHView item = new ThreeItemHView(UserPageActivity.this);
                LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemParams.setMargins((int)(23*dp),(int)(12*dp),(int)(23*dp),0);
                item.setLayoutParams(itemParams);
                item.setData(portFolioList.get(i).name,portFolioList.get(i).netVal,portFolioList.get(i).profitVal,R.color.blue_15A1FF,R.color.black_333333,R.color.red_FB3636,14,portFolioList.get(i).id);
                layout.addView(item);
            }
        }
        //收益走势

        if(lineJson!=null && StringUtils.isNotEmpty(lineJson.get("lineDate")) && StringUtils.isNotEmpty(lineJson.get("sh300Line"))
                && StringUtils.isNotEmpty(lineJson.get("selfLine"))){
            LineChartView lineChatView = new LineChartView(UserPageActivity.this);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (150 * dp));
            lineParams.setMargins((int) (23 * dp), (int) (19 * dp), (int) (23 * dp), 0);
            lineChatView.setLayoutParams(lineParams);
            lineChatView.setData(TouguJsonObject.parseList(lineJson.getJSONArray("lineDate"), String.class));
            lineChatView.addLine(this.getResources().getColor(R.color.blue_15A1FF), "沪深300", TouguJsonObject.parseList(lineJson.getJSONArray("sh300Line"), Float.class));
            lineChatView.addLine(this.getResources().getColor(R.color.red_FB3636),"投顾收益", TouguJsonObject.parseList(lineJson.getJSONArray("selfLine"),Float.class));
            layout.addView(lineChatView);
        }
        if(layout.getChildCount() == 0){
            layout.addView(ViewUtils.getListNullView(UserPageActivity.this, R.color.white, (int) (40 * dp), 0, "暂无组合信息"));
        }

    }

    private void getOpinoinView(LinearLayout layout){
        if(opinionList!=null && opinionList.size()>0){
            for (int i=0;i<opinionList.size();i++){
                CombOpinionView opinionView = new CombOpinionView(UserPageActivity.this);
                opinionView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                opinionView.setData(opinionList.get(i), true);
                opinionView.setBackgroundResource(R.drawable.list_item_selector_bg);
                opinionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v !=null && v instanceof CombOpinionView){
                            CombOpinionView combOpinionView = (CombOpinionView)v;
                            if(combOpinionView.getData()!=null){
                                ActivityUtil.goGuandianInfo(UserPageActivity.this,combOpinionView.getData().id);
                            }
                        }
                    }
                });
                layout.addView(opinionView);
            }
        }else{
            layout.addView(ViewUtils.getListNullView(UserPageActivity.this, R.color.white, (int) (40 * getDM().density), 0, "暂无观点信息"));
        }

    }

    @Click
    void user_page_follow_ta(){
        if(UserUtils.isLogin()){
            Http.attentionToUser(uid,new Http.Callback<Boolean>(){
                @Override
                public void onSuccess(Boolean obj) {
                    super.onSuccess(obj);
                    UIShowUtil.toast(UserPageActivity.this, "关注成功");
                    if(baseData!=null){
                        baseData.attentionState = 1;
                        initUserBaseView();
                    }else{
                        initView();
                    }
                }
            });
        }else{
            ActivityUtil.goLogin(UserPageActivity.this);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareUtil.getInstance().onActivityResult(requestCode,resultCode,data);
    }
}
