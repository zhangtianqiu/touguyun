package com.touguyun.net;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.touguyun.BuildConfig;
import com.touguyun.MainApplication;
import com.touguyun.module.Consultant;
import com.touguyun.module.ListModule;
import com.touguyun.module.MessageList;
import com.touguyun.module.Opinion;
import com.touguyun.module.PortfolioStock;
import com.touguyun.module.StockInfo;
import com.touguyun.module.User;
import com.touguyun.utils.MyJsonObject;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/8/24.
 */
public class Http  extends HttpInternal {

//    public static final String API_HOST = "http://api.touguyun.com";
//    public static final String API_HOST = "http://192.168.1.106:8080/";   //杨峥
//    public static final String API_HOST = "http://192.168.1.248/";
//      public static final String API_HOST = "http://172.32.21.69:8080/";    //陈拓
//      public static final String API_HOST = "http://192.168.1.105/";    //小龙
    public static final String API_HOST = "http://123.56.126.175/";
    public static final long COUNT = 10;



    public static class Callback<T> {
        protected String _url;

        public String getUrl() {
            return this._url;
        }

        public void onSuccess(T obj) {
        }

        public void onBusinessError(int errorCode, String errorMessage) {

            switch (errorCode) {
                case 403:           //用户登录过期
                    UserUtils.saveUser("",0,0);
                    UIShowUtil.toast(MainApplication.getInstance(), StringUtils.isNotEmpty(errorMessage) ? errorMessage : errorCode + "");
                    break;
                case 905:           //用户不存在
                    UserUtils.saveUser("",0,0);
                    UIShowUtil.toast(MainApplication.getInstance(), StringUtils.isNotEmpty(errorMessage) ? errorMessage : errorCode + "");
                    break;
                default:
                    UIShowUtil.toast(MainApplication.getInstance(), StringUtils.isNotEmpty(errorMessage) ? errorMessage : errorCode + "");
                    break;
            }
        }

        public void onNetworkError(VolleyError error) {
            UIShowUtil.toast(MainApplication.getInstance(), "网络连接错误，请检查网络设置");
        }
    }
    public static class BatchedCallback extends Callback<Map<Object, JSONObject>> {
        private Map<Object, JSONObject> result = new HashMap();
        private int totalRequestCount = 0;
        private int hasFinishedRequest = 0;
        protected boolean hasCallOnNetworkError;
        protected boolean hasCallOnBusinessError;

        protected void addRequestCount() {
            this.totalRequestCount++;
        }

        protected void onBatchedSuccess(Object batchName, JSONObject data) {
            result.put(batchName, data);
            if (result.size() == totalRequestCount) {
                onSuccess(result);
            }
        }

        protected void hideLoading() {
            d("call batch.hideLoading:hasFinishedRequest=%d,totalRequestCount=%d", hasFinishedRequest, totalRequestCount);
            hasFinishedRequest++;
            if (hasFinishedRequest >= totalRequestCount) {
                d("UiShowUtil.cancelDialog");
                UIShowUtil.cancelDialog();
            }
        }
    }

    private abstract static class BatchedCallbackListener<T> implements Response.Listener<T> {
        protected Object batchName;
        public BatchedCallbackListener(Object batchName) {
            this.batchName = batchName;
        }
    }

    /**
     * 组合筛选结果
     * @param id
     * @param sortType
     * @param callback
     */
    public static void getPortfolioSearch(long id,int sortType,Callback<ListModule> callback) {
        doGet("/v1/api0/portfolio/search", getParamsWithToken("id", id, "sortType", sortType, "pageSize", COUNT), ListModule.class, callback);
    }
    /**
     * 创建组合
     * @param name
     * @param imgPath
     * @param callback
     */
    public static void createPortfolio(String name,String imgPath,String remark,Callback<Integer> callback) {
        doGet("/v1/api1/portfolio/create", getParamsWithToken("name", name, "imgPath", imgPath, "remark", remark), Integer.class, callback);
    }
    /**
     * 查看组合股票信息
     * @param id        组合ID
     * @param callback
     */
    public static void getPortfolioStockList(long id,Callback<JSONObject> callback) {
        doGet("/v1/api1/portfolio/stock/list", getParamsWithToken("id", id), JSONObject.class, callback);
    }
    /**
     * 搜索股票
     * @param pid
     * @param codeOrName
     * @param callback
     */
    public static void searchStock(long pid,String codeOrName,Callback<List<StockInfo>> callback) {
        doGet("/v1/api1/portfolio/stock/search", getParamsWithToken("pid", pid, "codeOrName", codeOrName), StockInfo.class, callback);
    }
    /**
     * 添加股票到组合
     * @param pid       组合id
     * @param codes     股票codes，"23423","234234"
     * @param callback
     */
    public static void portfolioAddStock(long pid,String codes,Callback<Boolean> callback) {
        doGet("/v1/api1/portfolio/stock/add", getParamsWithToken("pid", pid, "codes", codes), Boolean.class, callback);
    }
    /**
     * 从组合删除股票
     * @param pid       组合id
     * @param code      股票code
     * @param callback
     */
    public static void portfolioDelStock(long pid,String code,Callback<Boolean> callback) {
        doGet("/v1/api1/portfolio/stock/del", getParamsWithToken("pid", pid, "code", code), Boolean.class, callback);
    }
    /**
     *  股票买卖数据（实时刷新）
     * @param pid       组合id
     * @param code      股票编码
     * @param callback
     */
    public static void portfolioDetailStock(long pid,String code,Callback<PortfolioStock> callback) {
        doGet("/v1/api1/portfolio/stock/detail", getParamsWithToken("pid", pid, "code", code), PortfolioStock.class, callback);
    }
    /**
     * 股票买入
     * @param pid       组合id
     * @param code      股票编码
     * @param count     买入股数
     * @param remark    调仓思路
     * @param callback
     */
    public static void portfolioBuyStock(long pid,String code,int count,String remark,Callback<Boolean> callback) {
        doGet("/v1/api1/portfolio/stock/buy", getParamsWithToken("pid", pid, "code", code, "count", count, "remark", remark), Boolean.class, callback);
    }
    /**
     * 股票卖出
     * @param pid       组合id
     * @param code      股票编码
     * @param count     买入股数
     * @param remark    调仓思路
     * @param callback
     */
    public static void portfolioSellStock(long pid,String code,int count,String remark,Callback<Boolean> callback) {
        doGet("v1/api1/portfolio/stock/sell", getParamsWithToken("pid", pid, "code", code, "count", count, "remark", remark), Boolean.class, callback);
    }
    /**
     *  组合内股票交易记录
     * @param id    上一页返回对象中的nextPageFlag字段 long 第一页可不传
     * @param pid   组合id
     * @param code   股票编码
     * @param start 搜索开始日期 可不传
     * @param end   搜索结束日期 可不传
     * @param callback
     */
    public static void portfolioStockDealHistory(long id, long pid, String code, String start, String end, Callback<JSONObject> callback) {
        doGet("v1/api1/portfolio/stock/dealHistory", getParamsWithToken("id", id, "pid", pid, "code", code, "start", start, "end", end), JSONObject.class, callback);
    }
    /**
     * 组合详情交易记录
     * @param id    上一页返回对象中的nextPageFlag字段 long 第一页可不传
     * @param pid   组合id
     * @param callback
     */
    public static void portfolioDealHistory(long id,long pid,Callback<JSONObject> callback){
        doGet("v1/api0/portfolio/dealHistory", getParamsWithToken("id", id, "pid", pid, "pageSize", COUNT), JSONObject.class, callback);

    }
    /**
     * 我的组合(创建),我的订阅(被订阅)
     * @param id    上一页返回对象中的nextPageFlag字段 long
     * @param callback
     */
    public static void myPortfolioCreateList(long id,Callback<ListModule> callback) {
        doGet("v1/api1/portfolio/myList", getParamsWithToken("id", id), ListModule.class, callback);
    }
    /**
     * 我的组合(订阅),我的订阅(已订阅)
     * @param id    上一页返回对象中的nextPageFlag字段 long
     * @param callback
     */
    public static void myPortfolioSubList(long id,Callback<ListModule> callback) {
        doGet("v1/api1/portfolio/mySubList", getParamsWithToken("id", id), ListModule.class, callback);
    }
    /**
     * 组合取消订阅
     * @param pid   组合id
     * @param callback
     */
    public static void unSubPortfolio(long pid,Callback<Boolean> callback) {
        doGet("v1/api1/portfolio/unSub", getParamsWithToken("pid", pid), Boolean.class, callback);
    }
    /**
     * 组合详情基本数据
     * @param pid
     * @param callback
     */
    public static Request getPortfolioDetail(long pid,Callback callback,Object batchName) {
        return doGet("v1/api0/portfolio/detail", getParamsWithToken("pid", pid), JSONObject.class, callback, batchName);
    }
    /**
     * 组合详情讨论区数据
     * @param targetId  组合的编号
     * @param sortVal   每页条目
     * @param callback  response中的nextRequestFlag，首次加载可以不传
     */
    public static Request getPortfolioComment(long targetId,long sortVal,Callback callback,Object batchName) {
        return doGet("v1/api0/portfolio/commentList", getParamsWithToken("targetId", targetId, "pageSize", COUNT, "sortVal", sortVal), JSONObject.class, callback, batchName);
    }
    public static void getPortfolioComment(long targetId,long sortVal,Callback<JSONObject> callback) {
        getPortfolioComment(targetId,sortVal,callback,null);
    }
    /**
     * 我的投顾列表
     * @param sortVal   response中的nextRequestFlag，首次加载可以不传
     * @return
     */
    public static Request myConsultantList(long sortVal,Callback<ListModule> callback) {
        return doGet("v1/api1/user/myConsultantList", getParamsWithToken("sortVal", sortVal), ListModule.class, callback);
    }
    /**
     * 投顾收益榜、人气榜、好评榜
     * @param sortVal
     * @param sortType  5收益榜，7人气榜，8好评榜
     * @param callback
     * @return
     */
    public static Request getConsultantList(long sortVal,int sortType,Callback<ListModule> callback) {
        return doGet("v1/api0/user/getConsultantList", getParamsWithToken("sortVal", sortVal, "sortType", sortType, "pageSize", COUNT), ListModule.class, callback);
    }
    /**
     *  投顾推荐榜
     * @param callback
     * @return
     */
    public static Request getConsultantRecommendList(Callback<List<Consultant>> callback) {
        return doGet("v1/api0/user/recommendConsult", getParamsWithToken(), Consultant.class, callback);
    }
    /**
     * 观点榜单
     * @param sortVal   response中的nextRequestFlag
     * @param queryType  最新（6）热评（1）人气（7）
     * @param callback
     * @return
     */
    public static Request getOpinionIndex(long sortVal,int queryType,Callback<ListModule> callback) {
        return doGet("v1/api0/opinion/index", getParamsWithToken("sortVal",sortVal,"queryType",queryType,"pageSize",COUNT), ListModule.class, callback);
    }
    /**
     * 首页336数据
     * @param callback
     * @return
     */
    public static Request getIndexTab(Callback<JSONObject> callback) {
        return doGet("v1/api0/app/indexTab", getParamsWithToken(), JSONObject.class, callback);
    }
    public static Request getIndexTab(Callback callback,Object batchName ) {
        return doGet("v1/api0/app/indexTab", getParamsWithToken(), JSONObject.class, callback,batchName);
    }
//    /**
//     * 查看我的信息（投顾）
//     * @param callback
//     * @return
//     */
//    public static Request consultantInfo(Callback<User> callback){
//        return doGet("v1/api1/user/consultantInfo", getParamsWithToken(), User.class, callback);
//    }
    /**
     * 查看我的信息
     * @param callback
     * @return
     */
    public static Request consultantIndex(Callback<User> callback){
        return doGet("v1/api1/user/consultantIndex", getParamsWithToken(), User.class, callback);
    }
    public static Request consultantIndex(Callback callback,Object batchName){
        return doGet("v1/api1/user/consultantIndex", getParamsWithToken(), JSONObject.class, callback,batchName);
    }
    /**
     * 获取上传信息
     * @param callback
     * @return
     */
    public static Request getUpdateMess(Callback<MyJsonObject> callback){
        return doGet("v1/api1/user/getFilePath", getParamsWithToken(), MyJsonObject.class, callback);
    }
    /**
     * 修改用户资料
     * @param userImg   头像
     * @param name      名称
     * @param callback
     * @return
     */
    public static Request updateUserInfo(String userImg,String name,Callback<Boolean> callback){
        return doGet("v1/api1/user/updateUserInfo", getParamsWithToken("userImg",userImg,"name",name), MyJsonObject.class, callback);
    }
    /**
     * 重置密码
     * @param pwd
     * @param newPwd
     * @param callback
     * @return
     */
    public static Request resetPwd(String pwd,String newPwd,Callback<Boolean> callback){
        return doGet("v1/api1/user/resetPwd", getParamsWithToken("pwd",StringUtils.Md5(pwd),"newPwd",StringUtils.Md5(newPwd)), Boolean.class, callback);
    }
    /**
     * 意见反馈
     * @param content
     * @param callback
     * @return
     */
    public static Request feedback(String content,Callback<Boolean> callback){
        return doGet("v1/api0/feedback/create", getParamsWithToken("pwd",content), Boolean.class, callback);
    }
    /**
     * 退出登录
     * @param callback
     * @return
     */
    public static Request logout(Callback<Boolean> callback){
        return doGet("v1/api0/user/logout", getParamsWithToken(), Boolean.class, callback);
    }
    /**
     *  注册第一步/重新发送验证码
     * @param mobile
     * @param callback
     * @param verfyType 0注册，1修改密码
     * @return  成功返回token
     *
     * {
        "token": "2403A456A4346A3D22D3949193F9533B",
        "code": null
        }
     */
    public static Request register(String mobile,String token,int verfyType,Callback<JSONObject> callback){
        return doGet("v1/api0/user/register", getParamsWithToken("mobile",mobile,"token",token,"verfyType",verfyType), JSONObject.class, callback);
    }
    /**
     * 注册验证短信
     * @param mobileCode    验证码
     * @param callback
     * @param verfyType 0注册，1修改密码
     * @return
     */
    public static Request registerVerifySms(String mobileCode,String token,int verfyType,Callback<JSONObject> callback){
        return doGet("v1/api0/user/verifySms", getParamsWithToken("mobileCode",mobileCode,"token",token,"verfyType",verfyType), JSONObject.class, callback);
    }
    /**
     * （设置密码，投顾证书号）
     * @param password
     * @param certificate
     * @param token
     * @param roleType
     * @param callback
     * @return
     */
    public static Request setUserPassword(String password,String token,String certificate,int roleType,Callback<JSONObject> callback){
        return doGet("v1/api0/user/setUserPassword", getParamsWithToken("password",StringUtils.Md5(password),"certificate",certificate,"token",token,"roleType",roleType), JSONObject.class, callback);
    }
    /**
     * 注册用户补充资料
     * @param userImg
     * @param name
     * @param token
     * @param callback
     * @return
     */
    public static Request setUserInfo(String userImg,String name,String token,Callback<Boolean> callback){
        return doGet("v1/api1/user/setUserInfo", getParamsWithToken("userImg",userImg,"name",name,"token",token), Boolean.class, callback);
    }
    /**
     * 用户登录
     * @param mobile
     * @param password
     * @param callback
     * @return
     */
    public static Request login(String mobile,String password,Callback<JSONObject> callback){
        return doGet("v1/api0/user/login", getParamsWithToken("mobile",mobile,"password",StringUtils.Md5(password)), JSONObject.class, callback);
    }
    /**
     * 忘记密码 请求短信验证码
     * @param mobile
     * @param callback
     * @param verfyType 0注册，1修改密码
     * @return
     */
    public static Request forgotReqSms(String mobile,String token,int verfyType,Callback<JSONObject> callback){
        return doGet("v1/api0/user/reqSms", getParamsWithToken("mobile",mobile,"token",token,"verfyType",verfyType), JSONObject.class, callback);
    }
    /**
     * 忘记密码 验证验证码
     * @param mobileCode
     * @param token
     * @param verfyType 0注册，1修改密码
     * @param callback
     * @return
     */
    public static Request forgotVerifySms(String mobileCode,String token,int verfyType,Callback<JSONObject> callback){
        return doGet("v1/api0/user/verifySms", getParamsWithToken("mobileCode",mobileCode,"token",token,"verfyType",verfyType), JSONObject.class, callback);
    }
    /**
     * 忘记密码 设置新密码
     * @param password
     * @param token
     * @param callback
     * @return
     */
    public static Request forgotResetPwd(String password,String token,Callback<JSONObject> callback){
        return doGet("v1/api0/user/setNewPwd", getParamsWithToken("password",StringUtils.Md5(password),"token",token), JSONObject.class, callback);
    }
    /**
     * 热门投顾推荐(注册完成之后)
     * @param callback
     * @return
     */
    public static Request recommendConsult(Callback<List<Consultant>> callback){
        return doGet("v1/api0/user/recommendConsult", getParamsWithToken(), Consultant.class, callback);
    }
    /**
     * 首页批量关注
     * @param uidArray  投顾uid数组 1,2,3,4
     * @param callback
     * @return
     */
    public static Request attentionBatch(String uidArray,Callback<Boolean> callback){
        return doGet("v1/api1/user/attentionBatch", getParamsWithToken("uidArray",uidArray), Boolean.class, callback);
    }
    /**
     * 搜索头顾
     * @param key
     * @param sortVal
     * @param callback
     * @return
     */
    public static Request searchConsultList(String key,long sortVal,Callback<ListModule> callback){
        return doGet("v1/api0/user/searchConsultList", getParamsWithToken("key",key,"sortVal",sortVal), ListModule.class, callback);
    }
    /**
     * 投顾个人简介
     * @param uid
     * @param callback
     * @return
     */
    public static Request consultInfoFromUser(long uid,Callback<Consultant> callback){
        return doGet("v1/api0/user/consultInfoFromUser", getParamsWithToken("uid",uid), Consultant.class, callback);
    }
    /**
     * 投顾个人主页(用户信息)
     * @param uid
     * @param callback
     * @param batchName
     * @return
     */
    public static Request consultListFromUser(long uid,Callback callback,Object batchName){
        return doGet("v1/api0/user/consultListFromUser", getParamsWithToken("uid",uid), JSONObject.class, callback,batchName);
    }
    /**
     * 投顾个人主页(投顾净值折线图)
     * @param callback
     * @param batchName
     * @return
     */
    public static Request consultCompSh300(long uid,Callback callback,Object batchName){
        return doGet("v1/api0/consultant/compSh300", getParamsWithToken("uid",uid), JSONObject.class, callback,batchName);
    }
    /**
     * 投顾个人主页(TA的组合)
     * @param uid
     * @param callback
     * @param batchName
     * @return
     */
    public static Request portfolioListByUid(long uid,Callback callback,Object batchName){
        return doGet("v1/api0/portfolio/listByUid", getParamsWithToken("uid",uid), JSONObject.class, callback,batchName);
    }
    /**
     *  投顾个人主页(TA的观点)
     * @param uid        用户的编号
     * @param sortVal    response中的nextRequestFlag，首次加载可以不传
     * @param sortType   排序方式0评论数，1喜欢数，2订阅数，3按时间
     * @param callback
     * @param batchName
     * @return
     */
    public static Request opinionListByUid(long uid,long sortVal,int sortType,Callback callback,Object batchName){
        return doGet("v1/api0/opinion/consultant/myList", getParamsWithToken("uid",uid,"sortVal",sortVal,"sortType",sortType,"pageSize",COUNT), JSONObject.class, callback,batchName);
    }
    public static Request opinionListByUid(long uid,long sortVal,int sortType,Callback<ListModule> callback){
        return doGet("v1/api0/opinion/consultant/myList", getParamsWithToken("uid",uid,"sortVal",sortVal,"sortType",sortType,"pageSize",COUNT), ListModule.class, callback);
    }
    /**
     * 关注投顾
     * @param uid
     * @param callback
     * @return
     */
    public static Request attentionToUser(long uid,Callback<Boolean> callback){
        return doGet("v1/api1/user/attentionTo", getParamsWithToken("uid",uid), Boolean.class, callback);
    }
    /**
     *  发布观点
     * @param title 标题
     * @param summary   摘要
     * @param tags      标签
     * @param content   全文
     * @param osid      观点话题编号
     * @param cid       组合编号
     * @param callback
     * @return
     */
    public static Request opinionCreate(String title,String summary,String tags,String content,long osid,long cid,Callback<Boolean> callback){
        return doGet("v1/api1/opinion/create", getParamsWithToken("title", title, "summary", summary, "tags", tags, "content", content, "osid", osid, "cid", cid), Boolean.class, callback);
    }
    /**
     * 观点可选的话题
     * @param callback
     * @return
     */
    public static Request opinionSubjectList(Callback<JSONObject> callback){
        return doGet("v1/api0/opinion/subjectList", getParamsWithToken(), JSONObject.class, callback);
    }
    /**
     * 我关注的观点
     * @param callback
     * @return
     */
    public static Request opinionConsultantList(long sortVal,Callback<ListModule> callback){
        return doGet("v1/api1/opinion/user/consultantList", getParamsWithToken("sortVal",sortVal), ListModule.class, callback);
    }
    /**
     * 搜索观点
     * @param keyword
     * @param sortVal
     * @param callback
     * @return
     */
    public static Request opinionSearch(String keyword,long sortVal,Callback<ListModule> callback){
        return doGet("v1/api0/opinion/search", getParamsWithToken("keyword",keyword,"sortVal",sortVal,"pageSize",COUNT), ListModule.class, callback);
    }
    /**
     * 观点详情
     * @param id
     * @param callback
     * @return
     */
    public static Request opinionDetail(long id,Callback<Opinion> callback){
        return doGet("v1/api0/opinion/detail", getParamsWithToken("id",id), Opinion.class, callback);
    }
    public static Request opinionDetail(long id,Callback callback,Object batchName){
        return doGet("v1/api0/opinion/detail", getParamsWithToken("id",id), JSONObject.class, callback,batchName);
    }
    /**
     * 观点详情评论列表
     * @param targetId
     * @param sortVal
     * @param callback
     * @return
     */
    public static Request opinionCommentList(long targetId,long sortVal,Callback<ListModule> callback){
        return doGet("v1/api0/opinion/commentList", getParamsWithToken("targetId",targetId,"sortVal",sortVal), ListModule.class, callback);
    }
    public static Request opinionCommentList(long targetId,long sortVal,Callback callback,Object batchName){
        return doGet("v1/api0/opinion/commentList", getParamsWithToken("targetId",targetId,"sortVal",sortVal), JSONObject.class, callback,batchName);
    }
    /**
     * 观点点赞
     * @param opinionId
     * @param callback
     * @return
     */
    public static Request opinionLike(long opinionId,Callback<Boolean> callback){
        return doGet("v1/api1/opinion/like", getParamsWithToken("opinionId",opinionId), Boolean.class, callback);
    }
    /**
     *  发布观点的评论
     * @param targetId  评论对象的ID
     * @param content   正文
     * @param callback
     * @return
     */
    public static Request opinionSentComment(long targetId,String content,Callback<Boolean> callback){
        return doGet("v1/api1/opinion/comment", getParamsWithToken("targetId",targetId,"content",content), Boolean.class, callback);
    }
    /**
     * 发布组合的评论
     * @param targetId  评论对象的ID
     * @param content   正文
     * @param callback
     * @return
     */
    public static Request portfolioSentComment(long targetId,String content,Callback<Boolean> callback){
        return doGet("v1/api1/portfolio/comment", getParamsWithToken("targetId",targetId,"content",content), Boolean.class, callback);
    }
    /**
     * 注册regid
     * @param regId
     * @param callback
     * @return
     */
    public static Request updateRegId(String regId,Callback<Boolean> callback){
        return doGet("v1/api0/device/reg", getParamsWithToken("regId",regId), Boolean.class, callback);
    }
    /**
     * 取首页banner
     * @param callback
     * @return
     */
    public static Request imageAD(Callback<List<JSONObject>> callback){
        return doGet("v1/api0/imagead/index", getParamsWithToken(), JSONObject.class, callback);
    }
    public static Request imageAD(Callback callback,Object batchName){
        return doGet("v1/api0/imagead/index", getParamsWithToken(), JSONObject.class, callback,batchName);
    }
    /**
     * 取消息频道列表
     * @param callback
     * @return
     */
    public static Request getMessageCate(Callback<List<MessageList>> callback){
        return doGet("v1/api1/message/cate", getParamsWithToken(), MessageList.class, callback);
    }
    public static Request getMessageCate(Callback callback,Object batchName){
        return doGet("v1/api1/message/cate", getParamsWithToken(), JSONObject.class, callback,batchName);
    }
    /**
     * 取消息列表
     * @param cid
     * @param sortVal
     * @param callback
     * @return
     */
    public static Request getMessageList(long cid,long sortVal,Callback<ListModule> callback){
        return doGet("v1/api1/message/list", getParamsWithToken("cid",cid,"sortVal",sortVal,"pageSize",COUNT), ListModule.class, callback);
    }
    /**
     *  第三方登录
     * @param weixinCode    //微信登录
     * @param tid           //QQ登录
     * @param sid           //微博登录
     * @param accessToken   //accessToken(QQ or sina)
     * @return
     *  {
        "registResult": 1,      //0未注册 1已注册
        "roleType": 0,          //用户类型
        "registType": 1,      //0手机，>0第三方
        "token": "235DD6C069FFCCBBB70A94671F669C2B" //用户token
        }
     *
     */
    public static Request thirdLogin(String weixinCode,String tid,String sid,String accessToken,Callback<JSONObject> callback){
        return doGet("v1/api0/thirdparty/login", getParamsWithToken("weixinCode",weixinCode,"tid",tid,"sid",sid,"accessToken",accessToken), JSONObject.class, callback);
    }
    /**
     * 第三方注册
     * @param name
     * @param userImg
     * @param token
     * @param callback
     * @return
     *  {
    "registType": 1,      //0手机，>0第三方
    "roleType": 0,          //用户类型
    "token": "235DD6C069FFCCBBB70A94671F669C2B" //用户token
    }
     */
    public static Request thirdRegister(String name,String userImg,String token,Callback<JSONObject> callback){
        return doGet("v1/api0/thirdparty/regist", getParamsWithToken("name",name,"userImg",userImg,"token",token), JSONObject.class, callback);
    }
    /**
     * 取历史净值
     * @param callback
     * @return
     */
    public static Request getNetValueList(long pid,long lastid,Callback<ListModule> callback){
        return doGet("v1/api0/portfolio/netValueList", getParamsWithToken("pid",pid,"id",lastid), ListModule.class, callback);
    }
    /**
     * 订阅组合
     * @param pid   组合id
     * @param callback
     * @return
     */
    public static Request subscribePortfolio(long pid,Callback<Boolean> callback){
        return doGet("v1/api1/portfolio/subscribe", getParamsWithToken("pid",pid), Boolean.class, callback);
    }
    /**
     * 取消订阅
     * @param uid
     * @param callback
     * @return
     */
    public static Request attentionCancel(long uid,Callback<Boolean> callback){
        return doGet("v1/api1/user/attentionCancel",getParamsWithToken("uid",uid),Boolean.class,callback);
    }

    protected static void d(String format, Object... args) {
        if (BuildConfig.DEBUG) {
            VolleyLog.d(format, args);
        }
    }


}
