package com.touguyun.activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.MainApplication;
import com.touguyun.R;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ProvingUtil;
import com.touguyun.utils.ShareUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UploaderUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.CircleAngleTitleView;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Map;


import cn.jpush.android.api.JPushInterface;
/**
 * Created by zhengyonghui on 15/8/25.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity{


    private User thridUser;

    @ViewById
    EditText login_phone_num;

    @ViewById
    EditText login_password;

    @ViewById
    CircleAngleTitleView login_button;

    @Click
    void login_go_back(){
        this.onBackPressed();
    }

    public String openid;
    public String access_token;

    public SHARE_MEDIA media;

    @Click
    void login_button(){
        String phone = login_phone_num!=null?login_phone_num.getText().toString():"";
        String pass = login_password!=null?login_password.getText().toString():"";
        if(StringUtils.isEmpty(phone) || !ProvingUtil.isMobileNO(phone)){
            UIShowUtil.toast(LoginActivity.this, "手机号错误，请查正后重试");
            login_phone_num.requestFocus();
            return;
        }
        if(StringUtils.isEmpty(pass) || pass.length()<6 || pass.length()>18){
            UIShowUtil.toast(LoginActivity.this, "请输入正确的密码");
            login_password.requestFocus();
            return;
        }
        Http.login(phone,pass,new Http.Callback<JSONObject>(){
            @Override
            public void onSuccess(JSONObject obj) {
                super.onSuccess(obj);
                Log.d("obj-------",obj.toString());
                if(StringUtils.isNotEmpty(obj.get("token"))){
                    UserUtils.saveUser(obj.getString("token"), obj.getIntValue("roleType"), obj.getIntValue("registType"));
                    Http.updateRegId(JPushInterface.getRegistrationID(MainApplication.getInstance()),null);
                    UploaderUtil.getInstance();
                    finish();
                }

            }
        });
    }
    @Click
    void login_forgot_pass(){
        ActivityUtil.goForgotPass1(LoginActivity.this);
    }

    @Click
    void login_user_register(){
        ActivityUtil.goUserRegister(LoginActivity.this, User.USER_TYPE_PUTONG,13);
    }

    @Click
    void login_tougu_register(){
        ActivityUtil.goUserRegister(LoginActivity.this, User.USER_TYPE_TOUGU,13);
    }

    @AfterTextChange(R.id.login_phone_num)
    void phoneNumAfterTextChange(){
        initLoginButtonColor();
    }
    @AfterTextChange(R.id.login_password)
    void passwordAfterTextChange(){
        initLoginButtonColor();
    }

    public void initLoginButtonColor(){
        if(login_phone_num==null || login_password == null){
            return;
        }
        if(StringUtils.isNotEmpty(login_password.getText()) && StringUtils.isNotEmpty(login_phone_num.getText())){
            if(login_button.getBackColor() != getResources().getColor(R.color.red_F65066)){
                login_button.setBackAndFrameColor(getResources().getColor(R.color.red_F65066));
            }
        }else{
            if(login_button.getBackColor() != getResources().getColor(R.color.red_EB7D8C)){
                login_button.setBackAndFrameColor(getResources().getColor(R.color.red_EB7D8C));
            }
        }
    }

    @Click
    void login_third_wx_button(){
        UMWXHandler wxHandler;
        wxHandler = new UMWXHandler(LoginActivity.this,ShareUtil.WX_APP_ID,ShareUtil.WX_APP_SECRET);
        wxHandler.addToSocialSDK();
        media = SHARE_MEDIA.WEIXIN;
        doOauthVerify();
    }

    @Click
    void login_third_qq_button(){
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, ShareUtil.QQ_APP_ID,ShareUtil.QQ_APP_SECRET);
        qqSsoHandler.addToSocialSDK();
        media = SHARE_MEDIA.QQ;
        doOauthVerify();
    }
    @Click
    void login_third_sina_button(){
        getmController().getConfig().setSsoHandler(new SinaSsoHandler());
        media = SHARE_MEDIA.SINA;
        doOauthVerify();
    }

    private UMSocialService mController;
    private UMSocialService getmController(){
        if(mController == null){
            mController = UMServiceFactory.getUMSocialService(ShareUtil.UM_SHARE_NAME);
        }
        return mController;
    }

    private void doOauthVerify(){
        getmController().doOauthVerify(LoginActivity.this, media, new SocializeListeners.UMAuthListener() {
            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                UIShowUtil.toast(LoginActivity.this, "授权失败");
            }
            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if (value != null && !TextUtils.isEmpty(value.getString("openid"))) {
                    UIShowUtil.toast(LoginActivity.this, "授权成功.");
                    if(media == SHARE_MEDIA.QQ){
                        openid = StringUtils.returnStr(value.getString("openid"));
                        access_token = StringUtils.returnStr(value.getString("access_token"));
                    }else if(media == SHARE_MEDIA.SINA){

                    }else if(media == SHARE_MEDIA.WEIXIN){

                    }
                    getPlatformInfo();
                } else {
                    UIShowUtil.toast(LoginActivity.this, "授权失败.");
                }
            }
            @Override
            public void onCancel(SHARE_MEDIA platform) {
            }
            @Override
            public void onStart(SHARE_MEDIA platform) {
            }
        });
    }
    private void getPlatformInfo(){
        getmController().getPlatformInfo(LoginActivity.this, media, new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {
                UIShowUtil.toast(LoginActivity.this, "获取平台数据开始...");
            }
            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (status == 200 && info != null) {
                    if(thridUser == null){
                        thridUser = new User();
                    }
                    if(media == SHARE_MEDIA.QQ){
                        thridUser.userImg = StringUtils.returnStr(info.get("profile_image_url"));
                        thridUser.name = StringUtils.returnStr(info.get("screen_name"));
                        Http.thirdLogin("",openid,"",access_token,callback);
                    }else if(media == SHARE_MEDIA.SINA){

                    }else if(media == SHARE_MEDIA.WEIXIN){

                    }

                } else {
                    Log.d("TestData", "发生错误：" + status);
                }
            }
        });
    }
    public Http.Callback<JSONObject> callback = new Http.Callback<JSONObject>(){
        @Override
        public void onSuccess(JSONObject obj) {
            super.onSuccess(obj);
            if(obj.getIntValue("registResult")>0){
                UserUtils.saveUser(obj.getString("token"), obj.getIntValue("roleType"), obj.getIntValue("registType"));
                UploaderUtil.getInstance();
                Http.updateRegId(JPushInterface.getRegistrationID(MainApplication.getInstance()),null);
                finish();
            }else{
                thridUser.token = obj.getString("token");
                ActivityUtil.goUserRegisterFour(LoginActivity.this, thridUser.toString(),RegisterFourActivity.REGISTER_TYPE_THRID,13);
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 13 && resultCode == RESULT_OK){
            this.finish();
        }else if(mController!=null){
            /**使用SSO授权必须添加如下代码 */
            UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
            if(ssoHandler != null){
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }


    }
}
