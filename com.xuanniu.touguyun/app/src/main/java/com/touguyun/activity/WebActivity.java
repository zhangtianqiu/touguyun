package com.touguyun.activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.touguyun.R;
import com.touguyun.net.Http;
import com.touguyun.utils.FileUtils;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by zhengyonghui on 15/9/15.
 */
@EActivity(R.layout.activity_web)
public class WebActivity extends BaseActivity{

    private String url,title;

    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    WebView activity_webview;

    @ViewById
    LinearLayout activity_webview_loading;

//    @ViewById
//    ProgressBar activity_webview_progress;
//
//    @ViewById
//    TextView activity_webview_text_value;

    @ViewById
    LinearLayout activity_webview_net_error;

    private boolean isNetError = false;

    @AfterViews
    void initViews(){
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        touguyun_titleBar.setTitleBarClickListener(clickListener);
        touguyun_titleBar.showTitle(title);
        initWebView();
        if(activity_webview!=null){
            activity_webview.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {
        if(activity_webview!=null && activity_webview.canGoBack()){
            activity_webview.goBack();
        }else{
            finish();
        }
    }

    private TitleBar.TitleBarClickListener clickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
                onBackPressed();
            }else{
                if(Http.isNetworkAvailable()){
                    isNetError = false;
                    if(activity_webview!=null){
                        activity_webview.reload();
                    }
                }else{
                    UIShowUtil.toast(WebActivity.this, "网络不给力");
                }
            }
        }
    };

    private void initWebView() {
        if(StringUtils.isEmpty(url)){
            return;
        }
        activity_webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings settings = activity_webview.getSettings();
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
        activity_webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);//在内部处理url点击
                return true;
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                closeLoading();
                isNetError = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                closeLoading();
                if(isNetError){
                    showNetError();
                }else{
                    hideNetError();
                }
            }
        });
        activity_webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);//进度条
                if (newProgress > 50) {
                    closeLoading();
                }
            }
        });
        activity_webview.addJavascriptInterface(new JavascriptInterface(), "JavascriptInterface");
        activity_webview.setDownloadListener(new MyWebViewDownLoadListener());
    }



    //下载文件
    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    public class JavascriptInterface{
        public JavascriptInterface(){

        }
        @android.webkit.JavascriptInterface
        public void onWebViewClick(String json){

        }

    }
    public void closeLoading() {
        if (activity_webview_loading != null/* && url.equals(this.homepage)*/) {
            activity_webview_loading.setVisibility(View.GONE);
        }
    }
    public void showNetError() {
        if (activity_webview_net_error != null/* && url.equals(this.homepage)*/) {
            activity_webview_net_error.setVisibility(View.VISIBLE);
        }
    }
    public void hideNetError() {
        if (activity_webview_net_error != null/* && url.equals(this.homepage)*/) {
            activity_webview_net_error.setVisibility(View.GONE);
        }
    }
}
