package com.touguyun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

import com.touguyun.R;
import com.touguyun.fragment.MainFragment_;
import com.touguyun.fragment.MainGuandianFragment_;
import com.touguyun.fragment.MainMeFragment_;
import com.touguyun.fragment.MainTouguFragment_;
import com.touguyun.fragment.MainZuheFragment_;
import com.touguyun.utils.ActivityStackControlUtil;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ShareUtil;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.MainBottomToolsView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengUpdateAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cn.jpush.android.api.JPushInterface;
/**
 * Created by zhengyonghui on 15/8/24.
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseFragmentActivity implements MainBottomToolsView.MainToolsListener {

    private MainFragment_ mainFrag;
    private MainTouguFragment_ mainTougu;
    private MainZuheFragment_ mainZuhe;
    private MainGuandianFragment_ mainGuandian;
    private MainMeFragment_ mainMe;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }
    @ViewById
    MainBottomToolsView main_tools;



    @AfterViews
    void initView(){
        main_tools.setListener(this);
        onToolsClick(MainBottomToolsView.MAIN_TOOLS_TYPE_INDEX);
         /*拉取在线参数*/
        OnlineConfigAgent.getInstance().updateOnlineConfig(MainActivity.this);
        /*更新提示，不论是否wifi都提示*/
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateAutoPopup(true);
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.silentUpdate(this);  //静默下载
        /*错误统计*/
        MobclickAgent.updateOnlineConfig(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        if(UserUtils.getFirstRegister() && UserUtils.isLogin()){
            ActivityUtil.goRecommend(MainActivity.this);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }



    @Override
    public void onToolsClick(int type) {
        switch (type){
            case MainBottomToolsView.MAIN_TOOLS_TYPE_INDEX:
                if(mainFrag == null){
                    mainFrag = new MainFragment_();
                }
                replaceFragment(mainFrag);
                break;
            case MainBottomToolsView.MAIN_TOOLS_TYPE_TOUGU:
                if(mainTougu == null){
                    mainTougu = new MainTouguFragment_();
                }
                replaceFragment(mainTougu);
                break;
            case MainBottomToolsView.MAIN_TOOLS_TYPE_COMB:
                if(mainZuhe == null){
                    mainZuhe = new MainZuheFragment_();
                }
                replaceFragment(mainZuhe);
                break;
            case MainBottomToolsView.MAIN_TOOLS_TYPE_OPINION:
                if(mainGuandian == null){
                    mainGuandian = new MainGuandianFragment_();
                }
                replaceFragment(mainGuandian);
                break;
            case MainBottomToolsView.MAIN_TOOLS_TYPE_ME:
                if(mainMe == null){
                    mainMe = new MainMeFragment_();
                }
                replaceFragment(mainMe);
                break;

        }
    }
    private long onbackTime=0;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis()-onbackTime>1000*2){
            onbackTime = System.currentTimeMillis();
            UIShowUtil.toast(MainActivity.this, "再按一次退出投顾云");
        }else{
            ActivityStackControlUtil.finishProgram();
            super.onBackPressed();

        }
    }
    private void replaceFragment(Fragment fragment) {
        if (null!=fragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_context,fragment).commitAllowingStateLoss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareUtil.getInstance().onActivityResult(requestCode,resultCode,data);
    }
}
