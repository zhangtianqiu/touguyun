package com.touguyun.utils;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.touguyun.MainApplication;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/8/24.
 */
public class UserUtils {


    public static boolean isLogin() {
        return StringUtils.isNotEmpty(getToken());
    }

    public static String getToken(){
        String token = MainApplication.getInstance().getToken();
        if(StringUtils.isEmpty(token)){
            SharedPreferences share = MainApplication.getInstance().getApplicationContext().getSharedPreferences("UserMess", 0 | 4);
            token = share.getString("token","");
            int roleType = share.getInt("roleType",0);
            int registType = share.getInt("registType",0);
            if(StringUtils.isNotEmpty(token)){
                MainApplication.getInstance().setLoginUser(token,roleType,registType);
            }
        }
        return token;
    }
    public static void saveUser(String token,int roleType,int registType){
        MainApplication.getInstance().setLoginUser(token,roleType,registType);
        SharedPreferences.Editor editor = MainApplication.getInstance().getApplicationContext().getSharedPreferences("UserMess", 0 | 4).edit();
        editor.clear();
        editor.putString("token", token);
        editor.putInt("roleType", roleType);
        editor.putInt("registType", registType);
        editor.commit();
    }
    /**
     * 是否投顾角色
     * @return
     */
    public static boolean isTougu(){
        int roleType = MainApplication.getInstance().getRoleType();
        if(roleType <= 0){
            SharedPreferences share = MainApplication.getInstance().getApplicationContext().getSharedPreferences("UserMess", 0 | 4);
            roleType = share.getInt("roleType",0);
        }
        return roleType==1;
    }
    public static boolean isThrid(){
        int registType = MainApplication.getInstance().getRegistType();
        if(registType <= 0){
            SharedPreferences share = MainApplication.getInstance().getApplicationContext().getSharedPreferences("UserMess", 0 | 4);
            registType = share.getInt("registType",0);
        }
        return registType>0;
    }
    /**
     * 注册成功后弹出投顾推荐页
     * @param isfrist
     */
    public static void saveFirstRegister(boolean isfrist){
        SharedPreferences.Editor editor = MainApplication.getInstance().getApplicationContext().getSharedPreferences("AppFirst", 0 | 4).edit();
        editor.putBoolean("registerFirst", isfrist);
        editor.commit();
    }
    /**
     * 是否弹出投顾推荐页
     * @return
     */
    public static boolean getFirstRegister(){
        SharedPreferences share = MainApplication.getInstance().getApplicationContext().getSharedPreferences("AppFirst", 0 | 4);
        return share.getBoolean("registerFirst", false);
    }
    /**
     * 存储clientid
     * @param registerID
     */
    public static void saveRegistrationId(String registerID){
        SharedPreferences.Editor editor = MainApplication.getInstance().getApplicationContext().getSharedPreferences("JPush", 0 | 4).edit();
        editor.putString("JPush_clientID", registerID);
        editor.commit();
    }
    /**
     * 获取clientid
     * @return
     */
    public static String getRegistrationId(){
        SharedPreferences share = MainApplication.getInstance().getApplicationContext().getSharedPreferences("JPush", 0 | 4);
        return share.getString("JPush_clientID","");
    }


    /**
     * 存储搜索历史
     * @param searchKey 搜索词
     * @param type      搜索类型
     */
    public static void saveSearchHistory(String searchKey,int type) {
        SharedPreferences.Editor editor = MainApplication.getInstance().getApplicationContext().getSharedPreferences("search_history", 0).edit();
        List<String> result = getSearchHistory(type);
        if(result==null){
            result = new ArrayList<String>();
        }
        if(result.size()>=5){
            result.remove(result.size()-1);
        }
        result.add(0,searchKey);
        editor.putString(""+type, JSON.toJSONString(result));
        editor.commit();
    }
    public static void clearSearchHistory(int type) {
        SharedPreferences.Editor editor = MainApplication.getInstance().getApplicationContext().getSharedPreferences("search_history", 0).edit();
        editor.putString(""+type, "");
        editor.commit();
    }
    /**
     * 取搜索历史列表
     * @param type
     * @return
     */
    public static List<String> getSearchHistory(int type){
        List<String> result = null;
        if (MainApplication.getInstance() != null && MainApplication.getInstance().getApplicationContext() != null) {
            SharedPreferences shared = MainApplication.getInstance().getApplicationContext().getSharedPreferences("search_history", 0);
            String str =  shared.getString(""+type,"");
            if(StringUtils.isNotEmpty(str)){
                result = JSON.parseArray(str,String.class);
            }
        }
        return result;
    }

    public static boolean isFirstOpen(){
        SharedPreferences share = MainApplication.getInstance().getApplicationContext().getSharedPreferences("my_pref", 0 | 4);
        return share == null || share.getBoolean("guide_activity",false) || share.getInt("version",0) != AppUtils.getVersionCode();
    }
    public static void saveFirstOpen(boolean isFirst){
        SharedPreferences.Editor editor = MainApplication.getInstance().getApplicationContext().getSharedPreferences("my_pref", 0).edit();
        editor.clear();
        editor.putBoolean("guide_activity", isFirst);
        editor.putInt("version", AppUtils.getVersionCode());
        editor.commit();
    }
}
