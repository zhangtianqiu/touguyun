package com.touguyun.utils;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.touguyun.MainApplication;
/**
 * Created by zhengyonghui on 15/8/24.
 */
public class DeviceUtil {
    private static DeviceInfo deviceInfo = null;
    public static String getDeviceId() {
        DeviceInfo info = getDeviceInfo();
        if (info != null) {
            if (StringUtils.isNotEmpty(info.mac))
                return info.mac;
            else if (StringUtils.isNotEmpty(info.imei))
                return info.imei;
            else if (StringUtils.isNotEmpty(info.simNum))
                return info.simNum;
            else if (StringUtils.isNotEmpty(info.androidId))
                return info.androidId;
        }
        return "unknow";
    }
    private static final DeviceInfo getDeviceInfo() {
        if (deviceInfo != null) {
            return deviceInfo;
        }
        Context context = MainApplication.getInstance();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceInfo = new DeviceInfo();
        deviceInfo.imei = telephonyManager.getDeviceId();
        deviceInfo.simNum = telephonyManager.getSimSerialNumber();
        deviceInfo.androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceInfo.mac = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        return deviceInfo;
    }
    private static class DeviceInfo {
        public static String imei = "";
        public static String simNum = "";
        public static String androidId = "";
        public static String mac = "";
    }
}
