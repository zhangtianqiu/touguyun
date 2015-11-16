package com.touguyun.activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Window;

import com.touguyun.utils.ActivityStackControlUtil;
import com.touguyun.utils.UIShowUtil;
/**
 * Created by zhengyonghui on 15/8/31.
 */
public class BaseFragmentActivity extends FragmentActivity{
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
//            UiShowUtil.toast(this,this.getClass().getName());
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
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        inBackground = true;
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    public boolean isInBackground(){
        return inBackground;
    }
}
