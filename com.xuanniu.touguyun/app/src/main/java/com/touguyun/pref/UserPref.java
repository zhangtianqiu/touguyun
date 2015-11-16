package com.touguyun.pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
/**
 * Created by zhengyonghui on 15/8/24.
 */
@SharedPref
public interface UserPref {
    public String token();
    public String image();
    public String name();
    public String openId();
    public long uid();
}