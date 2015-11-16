package com.touguyun;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.touguyun.utils.UploaderUtil;

import cn.jpush.android.api.JPushInterface;
/**
 * Created by zhengyonghui on 15/8/24.
 */
public class MainApplication extends Application{
    private static MainApplication INSTANCE;


    public MainApplication() {
        INSTANCE = this;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
    }

    public static MainApplication getInstance() {
        return INSTANCE == null ? INSTANCE = new MainApplication() : INSTANCE;
    }

    private String token;
    private int roleType;
    private int registType;

    public void setLoginUser(String token,int roleType,int registType){
        this.token = token;
        this.roleType = roleType;
        this.registType = registType;
    }
    public String getToken(){
        return token;
    }
    public int getRoleType(){
        return roleType;
    }
    public int getRegistType(){
        return registType;
    }
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.memoryCacheSizePercentage(20);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }


    public String getVersionName() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(this.getPackageName(),PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
        return info.versionName;
    }
    public int getVersionCode() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(this.getPackageName(),PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
        return info.versionCode;
    }

}
