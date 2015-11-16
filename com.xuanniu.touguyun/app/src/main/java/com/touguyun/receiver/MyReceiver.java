package com.touguyun.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.MainApplication;
import com.touguyun.activity.CombinationInfoActivity_;
import com.touguyun.activity.GuandianInfoActivity_;
import com.touguyun.activity.MainActivity_;
import com.touguyun.activity.MessageActivity_;
import com.touguyun.activity.UserPageActivity_;
import com.touguyun.activity.WebActivity_;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityStackControlUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UserUtils;

import cn.jpush.android.api.JPushInterface;
/**
 * Created by zhengyonghui on 15/9/14.
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            UserUtils.saveRegistrationId(regId);
            Http.updateRegId(regId, new Http.Callback<Boolean>());
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            JSONObject json = TouguJsonObject.parseObject(intent.getExtras().getString(JPushInterface.EXTRA_EXTRA),JSONObject.class);
            goMessageActivity(context, json);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            if(StringUtils.isNotEmpty(JPushInterface.getRegistrationID(MainApplication.getInstance()))){
                Http.updateRegId(JPushInterface.getRegistrationID(MainApplication.getInstance()),null);
            }
            Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            }
            else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    public static void goMessageActivity(Context context, JSONObject json) {
        if(json!=null && json.getInteger("messageType")!=null){
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if(!ActivityStackControlUtil.hasMainActivity()){
                intent.setClass(context,MainActivity_.class);
                context.startActivity(intent);
            }
            switch (json.getIntValue("messageType")){
                case 100:   //打开APP，如果已经打开，则不处理
                    if(!ActivityStackControlUtil.hasMainActivity()){
                    }
                    break;
                case 101:   //使用 webView  打开url,一般会同时带有weburl参数
                    intent.setClass(context, WebActivity_.class);
                    intent.putExtra("url",json.getString("weburl")).putExtra("title", json.getString("title"));
                    context.startActivity(intent);
                    break;
                case 201:   //打开消息中心
                    intent.setClass(context, MessageActivity_.class);
                    context.startActivity(intent);
                    break;
                case 301:   //组合 增仓   打开组合页面，一般会同时带有portFolioId参数
                    intent.setClass(context, CombinationInfoActivity_.class);
                    intent.putExtra("pid", json.getLongValue("portFolioId"));
                    context.startActivity(intent);
                    break;
                case 302:   //组合 减仓   打开组合页面，一般会同时带有portFolioId参数
                    intent.setClass(context, CombinationInfoActivity_.class);
                    intent.putExtra("pid", json.getLongValue("portFolioId"));
                    context.startActivity(intent);
                    break;
                case 303:   //组合 平仓   打开组合页面，一般会同时带有portFolioId参数
                    intent.setClass(context,CombinationInfoActivity_.class);
                    intent.putExtra("pid",json.getLongValue("portFolioId"));
                    context.startActivity(intent);
                    break;
                case 400:   //组合详情    打开组合页面，一般会同时带有portFolioId参数
                    intent.setClass(context,CombinationInfoActivity_.class);
                    intent.putExtra("pid",json.getLongValue("portFolioId"));
                    context.startActivity(intent);
                    break;
                case 401:   //查看观点    打开观点详情页面，一般会同时带有opinionId参数
                    intent.setClass(context,GuandianInfoActivity_.class);
                    intent.putExtra("oid",json.getLongValue("opinionId"));
                    context.startActivity(intent);
                    break;
                case 402:   //查看投顾    打开组合页面，一般会同时带有uid参数
                    intent.setClass(context,UserPageActivity_.class);
                    intent.putExtra("uid",json.getLongValue("uid"));
                    context.startActivity(intent);
                    break;
                default:
                    break;
            }
        }

    }


    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
//        if (MainActivity.isForeground) {
//            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (null != extraJson && extraJson.length() > 0) {
//                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//
//                }
//
//            }
//            context.sendBroadcast(msgIntent);
//        }
    }
}

