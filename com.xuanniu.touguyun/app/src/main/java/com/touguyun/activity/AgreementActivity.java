package com.touguyun.activity;
import android.text.Html;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.FileUtils;
import com.touguyun.utils.StringUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/9/6.
 */
@EActivity(R.layout.activity_agreement)
public class AgreementActivity extends BaseActivity{

    private int roleType;

    @ViewById
    WebView agreement_context;

    @AfterViews
    void initViews(){
        roleType = getIntent().getIntExtra("roleType", User.USER_TYPE_PUTONG);
        initWebView();
        agreement_context.loadUrl(roleType==User.USER_TYPE_PUTONG?"file:///android_asset/agreement_html/agreement_user.html":"file:///android_asset/agreement_html/agreement_tougu.html");
    }

    private void initWebView() {
        agreement_context.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings settings = agreement_context.getSettings();
        settings.setJavaScriptEnabled(true);//可处理js
        settings.setAppCachePath(FileUtils.getWebTempCache(this).getAbsolutePath());
        settings.setAppCacheEnabled(true);
        if(Http.isNetworkAvailable()){
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else{
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
    }
}
