package com.touguyun.utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.touguyun.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
/**
 * Created by zhengyonghui on 15/9/14.
 */
public class ShareUtil {
    public static String UM_SHARE_NAME = "TOUGUYUN_SHARE";

    public static final String WX_APP_ID = "wxcb711eb19aca8939";
    public static final String WX_APP_SECRET = "ec615f25174b704ed07b0a2512cadf08";

    public static final String QQ_APP_ID = "1104832236";
    public static final String QQ_APP_SECRET = "QR32xJt1d1qUrkRN";

    public static final String sina_app_id="3460883107";
    public static final String sina_app_Secret="d5cd582b20550728dfb7233eda69d258";

    public static String AppHost = "http://www.touguyun.com";


    private static ShareUtil INSTANCE;
    public static ShareUtil getInstance() {
        return INSTANCE == null ? INSTANCE = new ShareUtil() : INSTANCE;
    }

    private UMSocialService mController;

    //    private Bitmap bitmap;
    private static final String defaultTitle = "我发现了一个投资组合，快来一起赚钱吧！";
    private static final String defaultContext = "我发现了一个投资组合，收益不错，快来一起赚钱吧！";
//    private String imageUrl;

    private Activity mContext;

    public ShareUtil(){}

    public void openShare(Activity context,String title,String description,String url){
        if(context!=null){
            this.mContext = context;
            initShare(title,description,url);
            mController.openShare(mContext,false);
        }

    }

    private void initShare(String title,String description,String url) {
        if (mController == null) {
            mController = UMServiceFactory.getUMSocialService(UM_SHARE_NAME);
        }
        UMImage localImage = new UMImage(mContext, R.drawable.logo);
        localImage.setTitle(StringUtils.isNotEmpty(title)?title:defaultTitle);
        localImage.setTargetUrl(StringUtils.startWithHttp(url) ? url : AppHost);
        mController.setShareContent(StringUtils.isNotEmpty(description)?description:defaultContext);
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mContext, WX_APP_ID, WX_APP_SECRET);
        wxHandler.addToSocialSDK();
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setTitle(StringUtils.isNotEmpty(title)?title:defaultTitle);
        weixinContent.setShareContent(StringUtils.isNotEmpty(description)?description:defaultContext);
        weixinContent.setTargetUrl(StringUtils.startWithHttp(url) ? url : AppHost);
        weixinContent.setShareImage(localImage);
        mController.setAppWebSite(SHARE_MEDIA.WEIXIN, AppHost);
        mController.setShareMedia(weixinContent);
        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, WX_APP_ID, WX_APP_SECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(StringUtils.isNotEmpty(description)?description:defaultContext);
        //设置朋友圈title
        circleMedia.setTitle(StringUtils.isNotEmpty(title)?title:defaultTitle);
        circleMedia.setShareImage(localImage);
        circleMedia.setTargetUrl(StringUtils.startWithHttp(url) ? url : AppHost);
        mController.setAppWebSite(SHARE_MEDIA.WEIXIN_CIRCLE, AppHost);
        mController.setShareMedia(circleMedia);
        //分享给QQ好友
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext, QQ_APP_ID, QQ_APP_SECRET);
        mController.setAppWebSite(SHARE_MEDIA.QQ, AppHost);
        qqSsoHandler.addToSocialSDK();
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(StringUtils.isNotEmpty(description)?description:defaultContext);
        qqShareContent.setTitle(StringUtils.isNotEmpty(title)?title:defaultTitle);
        qqShareContent.setShareImage(localImage);
        qqShareContent.setTargetUrl(StringUtils.startWithHttp(url) ? url : AppHost);
        mController.setShareMedia(qqShareContent);
        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        mController.setAppWebSite(SHARE_MEDIA.SINA, AppHost);
        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setTitle(StringUtils.isNotEmpty(title)?title:defaultTitle);
        sinaContent.setTargetUrl(StringUtils.startWithHttp(url) ? url : AppHost);
        sinaContent.setShareImage(localImage);
        sinaContent.setAppWebSite(StringUtils.startWithHttp(url) ? url : AppHost);
        sinaContent.setShareContent((StringUtils.isNotEmpty(description)?description:defaultContext) + " @投顾云 " +AppHost);
        mController.setShareMedia(sinaContent);
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
        mController.getConfig().removePlatform(SHARE_MEDIA.DOUBAN, SHARE_MEDIA.QZONE, SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.LAIWANG, SHARE_MEDIA.GOOGLEPLUS, SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.LAIWANG_DYNAMIC, SHARE_MEDIA.TWITTER, SHARE_MEDIA.YIXIN, SHARE_MEDIA.YIXIN_CIRCLE, SHARE_MEDIA.TENCENT, SHARE_MEDIA.RENREN, SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL);
//        if (mController.getConfig().getListener(SocializeListeners.SnsPostListener.class) == null) {
            mController.getConfig().registerListener(getListener());
//        }
    }
    public SocializeListeners.SnsPostListener getListener() {
        return new SocializeListeners.SnsPostListener() {

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                if (eCode == 200) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = platform.name();
                    handler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = 2;
                    msg.arg1 = eCode;
                    msg.obj = platform.name();
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onStart() {
            }

        };
    }

    private Handler handler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //分享成功后回调内容
                    break;
                case 2:
                    //分享失败后回调内容
                    break;
                default:
                    break;
            }

        }

        ;
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**使用SSO授权必须添加如下代码 */
        if(mController!=null){
            UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
            if(ssoHandler != null){
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

}
