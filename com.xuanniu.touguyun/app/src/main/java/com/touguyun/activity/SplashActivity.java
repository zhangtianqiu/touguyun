package com.touguyun.activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.touguyun.MainApplication;
import com.touguyun.R;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.ShareUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UploaderUtil;
import com.touguyun.utils.UserUtils;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/9/13.
 */
@EActivity(R.layout.activity_splash)
public class SplashActivity extends BaseActivity{


    public Map<String,String> map;
    private final int SPLASH_DISPLAY_LENGHT = 3000;

    @AfterViews
    void initViews(){
        ImageView bgView = (ImageView)findViewById(R.id.splash_img);
        ImageLoader.getInstance().showImage(R.drawable.splash_pic,bgView);
        MobclickAgent.updateOnlineConfig(MainApplication.getInstance());
        if(isFromWX()){
            getDataFormWX();
        }

        UploaderUtil.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (UserUtils.isFirstOpen()){
                    ActivityUtil.goGuide(SplashActivity.this);
                } else {
                    ActivityUtil.goMain(SplashActivity.this);
                    //微信跳转
                }
                finish();
            }
        },SPLASH_DISPLAY_LENGHT);
    }



    /**
     * 判断是否来自微信
     *
     * @return
     */
    private boolean isFromWX() {
        boolean is = false;
        Intent intent = this.getIntent();
        if (null == intent)
            is = false;
        String str = intent.getDataString() == null ? "" : intent.getDataString();
        if (str.contains(ShareUtil.WX_APP_ID)) {
            is = true;
        }
        return is;
    }

    /**
     * 分解微信返回数据
     */
    private void getDataFormWX() {
        Intent intent = this.getIntent();
        if (null == intent)
            return;
        String str = intent.getDataString() == null ? "" : intent.getDataString();
        try {
            str = URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("", "splash : " + str);

        if(StringUtils.isNotEmpty(str) && str.startsWith(ShareUtil.WX_APP_ID) && str.indexOf("//")>0 && str.indexOf("//")< str.length()-2){
            map = new HashMap<String, String>();
            String[] dataStr = str.substring(str.indexOf("//")+2).split("&");
            for (int i = 0; i < dataStr.length; i++) {
                map.put(dataStr[i].substring(0,dataStr[i].indexOf("=")), dataStr[i].indexOf("=")==dataStr[i].length()?"":dataStr[i].substring(dataStr[i].indexOf("=")+1,dataStr[i].length()));
            }
        }
    }
}
