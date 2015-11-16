package com.touguyun.utils;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.touguyun.MainApplication;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;
/**
 * Created by zhengyonghui on 15/8/24.
 */
public class AppUtils {
        private static String versionName;
        private static int versionCode;
        public static String getVersionName() {
            return versionName == null ? versionName = getPackageInfo().versionName : versionName;
        }
        private static PackageInfo getPackageInfo() {
            try {
                MainApplication context = MainApplication.getInstance();
                return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
        public static int getVersionCode() {
            return versionCode == -1 ? versionCode = getPackageInfo().versionCode : versionCode;
        }
    /**
     * 自动更新初始化
     */
    public static void setUpdateDefault() {
        UmengUpdateAgent.setAppkey(null);
        UmengUpdateAgent.setChannel(null);
        UmengUpdateAgent.setUpdateOnlyWifi(true);
        UmengUpdateAgent.setDeltaUpdate(true);
        UmengUpdateAgent.setUpdateAutoPopup(true);
        UmengUpdateAgent.setRichNotification(true);
        UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_DIALOG);
        UmengUpdateAgent.setUpdateListener(null);
        UmengUpdateAgent.setDialogListener(null);
        UmengUpdateAgent.setDownloadListener(null);
    }
    }