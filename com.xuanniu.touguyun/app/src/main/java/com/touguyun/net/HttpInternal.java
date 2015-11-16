package com.touguyun.net;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.touguyun.BuildConfig;
import com.touguyun.MainApplication;
import com.touguyun.utils.DeviceUtil;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.utils.AppUtils;
import com.touguyun.utils.StringUtils;


import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/8/24.
 */
public class HttpInternal {
    //默认请求队列个数
    protected static RequestQueue requestQueue;
    /**
     * 从对象中获取属性，和token，并生成map
     *
     * @param obj
     * @param fieldNames
     * @return
     */
    protected static Map<String, Object> getParamsFromFieldsAndWithToken(Object obj, String... fieldNames) {
        Map<String, Object> params = getParamsFromFields(obj, fieldNames);
        params.putAll(getParamsWithToken());
        return params;
    }
    /**
     * 从对象中获取属性，并生成map
     *
     * @param obj
     * @param fieldNames
     * @return
     */
    protected static Map<String, Object> getParamsFromFields(Object obj, String... fieldNames) {
        Map<String, Object> params = putSystem(new LinkedHashMap());
        if (obj == null) {
            return params;
        }
        for (String name : fieldNames) {
            try {
                Field field = obj.getClass().getDeclaredField(name);
                Object value = field.get(obj);
                if (value != null && StringUtils.isNotEmpty(value)) {
                    params.put(name, value);
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return params;
    }
    private static String getUserAgent() {
        return "Android/" + Build.VERSION.RELEASE + "/" + Build.VERSION.SDK_INT + " Model/" + Build.MANUFACTURER + "/" + Build.MODEL + " iKaola/" + AppUtils.getVersionName();
    }
    protected synchronized static RequestQueue getRequestQueue() {
        if (requestQueue != null) {
            return requestQueue;
        }
        Network network = new BasicNetwork(Build.VERSION.SDK_INT >= 9 ? new HurlStack() : new HttpClientStack(AndroidHttpClient.newInstance(getUserAgent())));
        requestQueue = new RequestQueue(new DiskBasedCache(new File(MainApplication.getInstance().getCacheDir(), "volley"), 200 * 1024 * 1024), network, 10);
        requestQueue.start();
        return requestQueue;
    }
    protected static Map<String, Object> getParamsWithToken(Object... keyAndParamsWithoutToken) {
        Map<String, Object> map = getParams(keyAndParamsWithoutToken);
        if (UserUtils.isLogin()) {
            map.put("token", UserUtils.getToken());
        }
        return map;
    }
    protected static Map<String, Object> getParams(Object... keyAndParams) {
        if (keyAndParams.length % 2 != 0) {
            throw new IllegalArgumentException("Parameters of getParams() must appear in pairs");
        }
        Map<String, Object> params = putSystem(new LinkedHashMap<String, Object>());
        for (int i = 0; i < keyAndParams.length; i += 2) {
            if(StringUtils.isNotEmpty(keyAndParams[i + 1])){
                params.put(keyAndParams[i].toString(), keyAndParams[i + 1]);
            }
        }
        return params;
    }
    private static Map<String, Object> putSystem(Map<String, Object> params) {
        params.put("version", AppUtils.getVersionName());
        params.put("imei", DeviceUtil.getDeviceId());
        params.put("deviceType", "1");
        return params;
    }
    private static String trimLeftSlash(String api) {
        StringBuffer sb = new StringBuffer(api);
        while (sb.charAt(0) == '/') {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }
    protected static Request doGet(String groovyApi, final Map<String, Object> params, final Class clazz, final Http.Callback callback) {
        return doGet(groovyApi, params, clazz, callback, null);
    }
    // 上线时修改doget为post
    protected static Request doGet(String groovyApi, final Map<String, Object> params, final Class clazz, final Http.Callback callback, Object batchName) {
        return doHttp(Request.Method.POST, groovyApi, params, clazz, callback, batchName);
    }
    protected static Request doPost(String groovyApi, final Map<String, Object> params, final Class clazz, final Http.Callback callback) {
        return doPost(groovyApi, params, clazz, callback, null);
    }
    protected static Request doPost(String groovyApi, final Map<String, Object> params, final Class clazz, final Http.Callback callback, Object batchName) {
        return doHttp(Request.Method.POST, groovyApi, params, clazz, callback, batchName);
    }
    private static void hideLoading(Http.Callback callback) {
        if (!(callback instanceof Http.BatchedCallback)) {
            UIShowUtil.cancelDialog();
        } else {
            ((Http.BatchedCallback) callback).hideLoading();
        }
    }
    public static boolean isWifi() {
        try {
            Context context = MainApplication.getInstance();
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return false;
    }
    public static boolean isNetworkAvailable() {
        try {
            Context context = MainApplication.getInstance();
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return false;
    }
    protected static void d(String format, Object... args) {
        if (BuildConfig.DEBUG) {
            VolleyLog.e(format, args);
        }
    }
    private static Request doHttp(int method, String groovyApi, final Map<String, Object> params, final Class clazz, final Http.Callback callback, final Object batchName) {
        if (callback!=null && callback instanceof Http.BatchedCallback && batchName == null) {
            throw new IllegalArgumentException("batch name can not be null in batched request mode");
        }
        String url = Http.API_HOST + trimLeftSlash(groovyApi);
        url = method == Request.Method.POST ? url : buildUrl(url, params);
        Log.d("http",url);
        d("Volley Request->\n%s", buildUrl(url, params));
        if (null != callback) {
            callback._url = url;
        }
        StringRequest req = new StringRequest(method, url, new BatchedCallbackListener<String>(batchName) {
            @Override
            public void onResponse(String str) {
                hideLoading(callback);
                if (callback == null) {
                    return;
                }
                try {
                    JSONObject json = JSON.parseObject(str);
                    int code = json.getIntValue("code");
                    d("Volley Response->\ncode=%d,body.length=%s", code, json.get("body") != null ? json.get("body").toString().length() : -1);
                    if (code != 200) {
                        callBusinessError(code, json.getString("msg"), callback);
                        return;
                    }
                    if (callback instanceof Http.BatchedCallback) {
                        ((Http.BatchedCallback) callback).onBatchedSuccess(this.batchName, json);
                    } else {
                        Object data = json.get("body");
                        if (clazz == JSONObject.class) {
                            callback.onSuccess(data);
                        } else if (clazz == Boolean.class) {
                            callback.onSuccess(true);
                        } else if (data instanceof JSONArray) {
                            callback.onSuccess(JSON.parseArray(json.getString("body"), clazz));
                        } else {
                            callback.onSuccess(JSON.parseObject(json.getString("body"), clazz));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callBusinessError(500, "休息一下再次尝试", callback);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading(callback);
                callNetworkError(error, callback);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.putAll(super.getHeaders());
                headers.put("User-Agent", getUserAgent());
                return headers;
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> result = new LinkedHashMap();
                Iterator<String> keys = params.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = params.get(key);
                    if (value != null) {
                        result.put(key, value.toString());
                    }
                }
                return result;
            }
        };
        req.setRetryPolicy(new DefaultRetryPolicy(10*1000,1,1));
        if (callback instanceof Http.BatchedCallback) {
            ((Http.BatchedCallback) callback).addRequestCount();
            return req;
        } else {
            return getRequestQueue().add(req);
        }
    }
    private static void callNetworkError(VolleyError error, Http.Callback callback) {
        if (callback != null) {
            if (callback instanceof Http.BatchedCallback && !((Http.BatchedCallback) callback).hasCallOnNetworkError) {
                callback.onNetworkError(error);
                ((Http.BatchedCallback) callback).hasCallOnNetworkError = true;
            } else {
                callback.onNetworkError(error);
            }
        }
    }
    private static void callBusinessError(int code, String errorMessage, Http.Callback callback) {
        if (callback instanceof Http.BatchedCallback && !((Http.BatchedCallback) callback).hasCallOnBusinessError) {
            callback.onBusinessError(code, StringUtils.isNotEmpty(errorMessage) ? errorMessage : "");
            ((Http.BatchedCallback) callback).hasCallOnBusinessError = true;
        } else {
            callback.onBusinessError(code, StringUtils.isNotEmpty(errorMessage) ? errorMessage : "");
        }
    }
    private static String buildUrl(String url, Map<String, Object> params) {
        StringBuffer sb = new StringBuffer();
        Iterator<String> keys = params.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = params.get(key);
            if (value != null) {
                sb.append("&" + key + "=" + StringUtils.encodeUTF(value.toString()));
            }
        }
        return url + sb.replace(0, 1, "?");
    }
    private abstract static class BatchedCallbackListener<T> implements Response.Listener<T> {
        protected Object batchName;
        public BatchedCallbackListener(Object batchName) {
            this.batchName = batchName;
        }
    }
    public static void executeBatchRequest(Request... requests) {
        for (Request request : requests) {
            getRequestQueue().add(request);
        }
    }
    public static void cancelAll() {
        if (requestQueue != null) {
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }
}
