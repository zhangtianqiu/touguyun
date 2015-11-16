package com.touguyun.utils;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.touguyun.MainApplication;
import com.touguyun.activity.MainActivity_;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/10.
 */
public class ActivityStackControlUtil {

    private static List<Activity> activityList = new ArrayList<Activity>();

    public static void remove(Activity activity) {
        activityList.remove(activity);
    }

    public static void add(Activity activity) {
        activityList.add(activity);
    }

    public static void finishProgram() {
        finishActivity();
        try {
            MobclickAgent.onKillProcess(MainApplication.getInstance());
            ActivityManager mAm = (ActivityManager) MainApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
            mAm.killBackgroundProcesses(MainApplication.getInstance().getPackageName());
        } catch (Exception e) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    public static void finishActivity() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

    public static boolean hasMainActivity() {
        for (Activity activity : activityList) {
            if (activity.getClass().equals(MainActivity_.class)) {
                return true;
            }
        }
        return false;
    }
}
