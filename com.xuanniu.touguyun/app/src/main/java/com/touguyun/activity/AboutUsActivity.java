package com.touguyun.activity;
import android.widget.TextView;

import com.touguyun.MainApplication;
import com.touguyun.R;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.UIShowUtil;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/9/1.
 */
@EActivity(R.layout.activity_about_us)
public class AboutUsActivity extends BaseActivity{

    @ViewById
    TextView about_us_version_code;

    @AfterViews
    void initView(){
        about_us_version_code.setText("版本号：v"+((MainApplication) (this.getApplicationContext())).getVersionName());
    }

    @Click
    void about_us_update(){
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case 0: // has update
                        UmengUpdateAgent.showUpdateDialog(AboutUsActivity.this, updateInfo);
                        break;
                    case 1: // has no update
                        UIShowUtil.toast(AboutUsActivity.this, "你使用的已经是最新版本");
                        break;
                    case 2: // none wifi
                        UIShowUtil.toast(AboutUsActivity.this, "没有wifi连接， 只在wifi下更新");
                        break;
                    case 3: // time out
                        UIShowUtil.toast(AboutUsActivity.this, "连接超时，请稍后重试");
                        break;
                }
            }

        });
    }

    @Click
    void about_us_welcome(){
        ActivityUtil.goGuide(AboutUsActivity.this);
    }
}
