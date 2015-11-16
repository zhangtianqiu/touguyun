package com.touguyun.activity;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;

import com.touguyun.utils.ActivityStackControlUtil;
import com.touguyun.utils.UIShowUtil;
import com.umeng.analytics.MobclickAgent;
/**
 * Created by zhengyonghui on 15/8/24.
 */
public class BaseActivity extends Activity{

    private boolean inBackground = false;

    private DisplayMetrics dm;
    public DisplayMetrics getDM(){
        if (null==dm) {
            dm=getResources().getDisplayMetrics();
        }
        return dm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        MobclickAgent.onError(this);
        ActivityStackControlUtil.add(this);
//        if(Http.API_HOST.contains(".248")) {
//            Toast.makeText(this, this.getClass().getName(), Toast.LENGTH_LONG).show();
//        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIShowUtil.closeBroads(this);
        ActivityStackControlUtil.remove(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        inBackground = false;
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        inBackground = true;
        super.onPause();
        MobclickAgent.onPause(this);
        UIShowUtil.closeBroads(this);
    }

    public boolean isInBackground(){
        return inBackground;
    }
}
