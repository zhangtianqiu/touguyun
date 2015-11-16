package com.touguyun.fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
/**
 * Created by zhengyonghui on 15/8/31.
 */
public abstract class BaseFragment extends Fragment{

    public float dp;
    public boolean isLoading;
    public int screenW,screenH;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dp=getResources().getDisplayMetrics().density;
        screenW=getResources().getDisplayMetrics().widthPixels;
        screenH=getResources().getDisplayMetrics().heightPixels;
    }

    public abstract void onHttpError(boolean isNet,String methodName,int errorType,String msg);
}
