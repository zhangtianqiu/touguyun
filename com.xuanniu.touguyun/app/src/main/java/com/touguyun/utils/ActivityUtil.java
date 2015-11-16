package com.touguyun.utils;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.activity.AboutUsActivity_;
import com.touguyun.activity.AgreementActivity_;
import com.touguyun.activity.CombinationFilterActivity_;
import com.touguyun.activity.CombinationInfoActivity_;
import com.touguyun.activity.CombinationProfileActivity_;
import com.touguyun.activity.CommentCreateActivity_;
import com.touguyun.activity.CreateGuandianActivity_;
import com.touguyun.activity.CreateZuheFirstActivity_;
import com.touguyun.activity.CreateZuheSecondActivity_;
import com.touguyun.activity.CreateZuheThirdActivity_;
import com.touguyun.activity.FeedbackActivity_;
import com.touguyun.activity.ForgotPassOneActivity_;
import com.touguyun.activity.ForgotPassThreeActivity_;
import com.touguyun.activity.ForgotPassTwoActivity_;
import com.touguyun.activity.GuandianInfoActivity_;
import com.touguyun.activity.GuideActivity;
import com.touguyun.activity.HistoryNetValueActivity_;
import com.touguyun.activity.LoginActivity_;
import com.touguyun.activity.MainActivity_;
import com.touguyun.activity.MessageActivity_;
import com.touguyun.activity.MessageContextActivity_;
import com.touguyun.activity.MessageListActivity_;
import com.touguyun.activity.ModifyNameActivity_;
import com.touguyun.activity.MyDingyueActivity_;
import com.touguyun.activity.MyGuandianActivity_;
import com.touguyun.activity.MyTouguActivity_;
import com.touguyun.activity.MyZuheActivity_;
import com.touguyun.activity.RecommendActivity_;
import com.touguyun.activity.RegisterFourActivity_;
import com.touguyun.activity.RegisterOneActivity_;
import com.touguyun.activity.RegisterThreeActivity_;
import com.touguyun.activity.RegisterTwoActivity_;
import com.touguyun.activity.ResetPasswordActivity_;
import com.touguyun.activity.SearchActivity_;
import com.touguyun.activity.SearchGuPiaoActivity_;
import com.touguyun.activity.SearchGuandianResultActivity_;
import com.touguyun.activity.SearchTouguResultActivity_;
import com.touguyun.activity.SetUpActivity_;
import com.touguyun.activity.TiaocangActivity_;
import com.touguyun.activity.TradingRecordsActivity_;
import com.touguyun.activity.UserPageActivity_;
import com.touguyun.activity.UserPageInfoActivity_;
import com.touguyun.activity.WebActivity_;
import com.touguyun.activity.ZuheFilterResultActivity_;
/**
 * Created by zhengyonghui on 15/8/25.
 */
public class ActivityUtil {
    //首页
    public static void goMain(Activity a){
        a.startActivity(new Intent(a,MainActivity_.class));
    }
    //引导页
    public static void goGuide(Activity a){
        a.startActivity(new Intent(a,GuideActivity.class));
    }
    //用户登录
    public static void goLogin(Activity a){
        a.startActivity(new Intent(a,LoginActivity_.class));
    }
    //用户注册 第一步，输入手机号
    public static void goUserRegister(Activity a, int type,int result){
        a.startActivityForResult(new Intent(a, RegisterOneActivity_.class).putExtra("type", type), result);
    }
    //用户注册 第二步，验证手机号
    public static void goUserRegisterTwo(Activity a, String user,int result){
        a.startActivityForResult(new Intent(a, RegisterTwoActivity_.class).putExtra("user", user), result);
    }
    //用户注册 第三步，输入密码（投顾证书）
    public static void goUserRegisterThree(Activity a, String user,int result){
        a.startActivityForResult(new Intent(a, RegisterThreeActivity_.class).putExtra("user", user), result);
    }
    //用户注册 第四步，补充信息
    public static void goUserRegisterFour(Activity a, String user,int type,int result){
        a.startActivityForResult(new Intent(a, RegisterFourActivity_.class).putExtra("user", user).putExtra("type",type), result);
    }
    //组合简介
    public static void goCombinationProfile(Activity a,String portFolioStr){
        a.startActivity(new Intent(a, CombinationProfileActivity_.class).putExtra("portFolio",portFolioStr));
    }
    //组合详情
    public static void goCombinationInfo(Activity a,long pid){
        a.startActivity(new Intent(a, CombinationInfoActivity_.class).putExtra("pid",pid));
    }
    //交易记录
    public static void goTradingRecords(Activity a, long pid){
        a.startActivity(new Intent(a,TradingRecordsActivity_.class).putExtra("pid",pid));
    }
    //发布评论
    public static void goCommentCreate(Activity a,long targetId,int type,int result){
        a.startActivityForResult(new Intent(a, CommentCreateActivity_.class).putExtra("targetId",targetId).putExtra("type", type),result);
    }
    //组合筛选
    public static void goCombinationFilter(Activity a){
        a.startActivity(new Intent(a, CombinationFilterActivity_.class));
    }
    //历史净值
    public static void goHistoryNet(Activity a,long pid){
        a.startActivity(new Intent(a, HistoryNetValueActivity_.class).putExtra("pid",pid));
    }
    //个人简介
    public static void goUserPageInfo(Activity a,long uid) {
        a.startActivity(new Intent(a, UserPageInfoActivity_.class).putExtra("uid",uid));
    }
    //个人主页
    public static void goUserPage(Activity a,long uid) {
        a.startActivity(new Intent(a, UserPageActivity_.class).putExtra("uid",uid));
    }
    //设置界面
    public static void goSetUp(Activity a,String UserStr) {
        a.startActivity(new Intent(a, SetUpActivity_.class).putExtra("user",UserStr));
    }
    //关于我们
    public static void goAboutUs(Activity a) {
        a.startActivity(new Intent(a, AboutUsActivity_.class));
    }
    //修改名字
    public static void goModifyName(Activity a,String name,int result) {
        a.startActivityForResult(new Intent(a, ModifyNameActivity_.class).putExtra("name", name), result);
    }
    //意见反馈
    public static void goFeedback(Activity a) {
        a.startActivity(new Intent(a, FeedbackActivity_.class));
    }
    //重置密码
    public static void goResetPassword(Activity a) {
        a.startActivity(new Intent(a, ResetPasswordActivity_.class));
    }
    //我的观点
    public static void goMyGuandian(Activity a) {
        a.startActivity(new Intent(a, MyGuandianActivity_.class));
    }
    //编辑观点
    public static void goCreateGuandian(Activity a) {
        a.startActivity(new Intent(a, CreateGuandianActivity_.class));
    }
    //我的投顾
    public static void goMyTougu(Activity a) {
        a.startActivity(new Intent(a, MyTouguActivity_.class));
    }
    //我的订阅
    public static void goMyDingyue(Activity a) {
        a.startActivity(new Intent(a, MyDingyueActivity_.class));
    }
    //我的组合
    public static void goMyZuhe(Activity a) {
        a.startActivity(new Intent(a, MyZuheActivity_.class));
    }
    //消息类型
    public static void goMessage(Activity a) {
        a.startActivity(new Intent(a, MessageActivity_.class));
    }
    //消息列表
    public static void goMessageList(Activity a,String title,long messageListId) {
        a.startActivity(new Intent(a, MessageListActivity_.class).putExtra("title",title).putExtra("id",messageListId));
    }
    //消息详情 每日财经
    public static void goMessageContext(Activity a,String title) {
        a.startActivity(new Intent(a, MessageContextActivity_.class).putExtra("title", title));
    }
    //创建组合1
    public static void goCreateZuhe1(Activity a,int result) {
        a.startActivityForResult(new Intent(a, CreateZuheFirstActivity_.class),result);
    }
    //创建组合2
    public static void goCreateZuhe2(Activity a,String name,String imgPath,int result) {
        a.startActivityForResult(new Intent(a, CreateZuheSecondActivity_.class).putExtra("name", name).putExtra("imgPath", imgPath), result);
    }
    //创建组合3
    public static void goCreateZuhe3(Activity a,long pid) {
        a.startActivity(new Intent(a, CreateZuheThirdActivity_.class).putExtra("pid", pid));
    }
    //搜索股票
    public static void goSearchGupiao(Activity a,long pid,int result) {
        a.startActivityForResult(new Intent(a, SearchGuPiaoActivity_.class).putExtra("pid", pid), result);
    }
    //组合筛选结果
    public static void goZuheFilterResult(Activity a,int type){
        a.startActivity(new Intent(a, ZuheFilterResultActivity_.class).putExtra("type", type));
    }
    //搜索界面
    public static void goSearch(Activity a,int search_type){
        a.startActivity(new Intent(a, SearchActivity_.class).putExtra("type", search_type));
    }
    //投顾搜索结果页
    public static void goSearchTouguResult(Activity a,String key){
        a.startActivity(new Intent(a, SearchTouguResultActivity_.class).putExtra("key",key));
    }
    //组合搜索结果页
    public static void goSearchGuandianResult(Activity a,String keyword){
        a.startActivity(new Intent(a, SearchGuandianResultActivity_.class).putExtra("keyword",keyword));
    }
    //调仓页
    public static void goTiaocang(Activity a,long pid,String code){
        a.startActivity(new Intent(a, TiaocangActivity_.class).putExtra("pid",pid).putExtra("code", code));
    }
    //忘记密码1
    public static void goForgotPass1(Activity a){
        a.startActivity(new Intent(a, ForgotPassOneActivity_.class));
    }
    //忘记密码2
    public static void goForgotPass2(Activity a,String user,int result){
        a.startActivityForResult(new Intent(a, ForgotPassTwoActivity_.class).putExtra("user", user), result);
    }
    //忘记密码3
    public static void goForgotPass3(Activity a,String user,int result){
        a.startActivityForResult(new Intent(a, ForgotPassThreeActivity_.class).putExtra("user",user),result);
    }
    //用户协议
    public static void goAgreement(Activity a,int roleType){
        a.startActivity(new Intent(a, AgreementActivity_.class).putExtra("roleType",roleType));
    }
    //投顾推荐
    public static void goRecommend(Activity a){
        a.startActivity(new Intent(a, RecommendActivity_.class));
    }
    //观点详情
    public static void goGuandianInfo(Activity a,long oid){
        a.startActivity(new Intent(a, GuandianInfoActivity_.class).putExtra("oid",oid));
    }
    public static void goWebActivity(Activity a,String url,String title){
        a.startActivity(new Intent(a, WebActivity_.class).putExtra("url",url).putExtra("title",title));
    }
    //活动广告跳转
    public static void goActionActivity(Activity a, JSONObject json, boolean isMessageList) {
        if(json!=null && json.getInteger("messageType")!=null){
            switch (json.getIntValue("messageType")){
                case 100:   //打开APP，如果已经打开，则不处理
                    if(!ActivityStackControlUtil.hasMainActivity()){
                        goMain(a);
                    }
                    break;
                case 101:   //使用 webView  打开url,一般会同时带有weburl参数
                        goWebActivity(a,json.getString("weburl"),json.getString("title"));
                    break;
                case 201:   //打开消息中心
                    if(!isMessageList){
                        goMessage(a);
                    }
                    break;
                case 301:   //组合 增仓   打开组合页面，一般会同时带有portFolioId参数
                    goCombinationInfo(a,json.getLongValue("portFolioId"));
                    break;
                case 302:   //组合 减仓   打开组合页面，一般会同时带有portFolioId参数
                    goCombinationInfo(a,json.getLongValue("portFolioId"));
                    break;
                case 303:   //组合 平仓   打开组合页面，一般会同时带有portFolioId参数
                    goCombinationInfo(a,json.getLongValue("portFolioId"));
                    break;
                case 400:   //组合详情    打开组合页面，一般会同时带有portFolioId参数
                        goCombinationInfo(a,json.getLongValue("portFolioId"));
                    break;
                case 401:   //查看观点    打开观点详情页面，一般会同时带有opinionId参数
                        goGuandianInfo(a,json.getLongValue("opinionId"));
                    break;
                case 402:   //查看投顾    打开组合页面，一般会同时带有uid参数
                        goUserPage(a,json.getLongValue("uid"));
                    break;
                default:
                    break;
            }
        }

    }
}
