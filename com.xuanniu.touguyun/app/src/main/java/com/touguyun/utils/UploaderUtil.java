package com.touguyun.utils;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.AuthenticationType;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.alibaba.sdk.android.oss.util.OSSToolKit;
import com.android.volley.toolbox.HttpClientStack;
import com.touguyun.MainApplication;
import com.touguyun.module.User;
import com.touguyun.net.Http;

import java.io.FileNotFoundException;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/9/6.
 */
public class UploaderUtil {

    private static String TAG = "UploaderUtil";
    private static String endpoint;
    private static String accessKey;
    private static String screctKey;
    private static String bucketName;

    private static OSSBucket bucket = null;
    private static OSSService ossService = null;

    private volatile static UploaderUtil instance;
    public static UploaderUtil getInstance() {
        if (instance == null) {
            synchronized (UploaderUtil.class) {
                if (instance == null) {
                    instance = new UploaderUtil();
                }
                initDate();
            }
        }
        return instance;
    }

    public OSSService getOssService(){
        if(ossService == null){
            ossService = OSSServiceProvider.getService();
            ossService.setApplicationContext(MainApplication.getInstance().getApplicationContext());
            ossService.setGlobalDefaultHostId(endpoint); // 设置region host 即 endpoint
            ossService.setGlobalDefaultACL(AccessControlList.PUBLIC_READ); // 默认为private
            ossService.setAuthenticationType(AuthenticationType.ORIGIN_AKSK); // 设置加签类型为原始AK/SK加签
            ossService.setGlobalDefaultTokenGenerator(new TokenGenerator() { // 设置全局默认加签器
                @Override
                public String generateToken(String httpMethod, String md5, String type, String date,
                                            String ossHeaders, String resource) {

                    String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date + "\n" + ossHeaders
                            + resource;

                    return OSSToolKit.generateToken(accessKey, screctKey, content);
                }
            });
            ossService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis() / 1000);

            ClientConfiguration conf = new ClientConfiguration();
            conf.setConnectTimeout(15 * 1000); // 设置全局网络连接超时时间，默认30s
            conf.setSocketTimeout(15 * 1000); // 设置全局socket超时时间，默认30s
            conf.setMaxConnections(50); // 设置全局最大并发网络链接数, 默认50
            ossService.setClientConfiguration(conf);
        }

        return ossService;
    }

    private OSSBucket getBucket(){
        if(bucket == null){
            bucket = getOssService().getOssBucket(bucketName);
            bucket.setBucketACL(AccessControlList.PUBLIC_READ);
        }
        return bucket;
    }

    public static final String STYLE_HEADER = "header/";
    public static final String STYLE_PORTFOLIO = "Portfolio/";



    public String getBucketFileName(String style, String token){
        StringBuffer name = new StringBuffer();
        name.append(style);
        name.append(DateUtils.getDateStrNow("yyyy/MM/dd")+"/");
        name.append(StringUtils.Md5(token + System.currentTimeMillis()));
        name.append(".jpg");
        return name.toString();
    }

    /**
     * 异步上传文件
     * @param filePath
     */
    public void asyncUpload(String filePath,String style,String token, final FileUpdateListener listener) {
        if(StringUtils.isEmpty(endpoint) || StringUtils.isEmpty(accessKey) || StringUtils.isEmpty(screctKey) || StringUtils.isEmpty(bucketName)){
            initDate();
            if(listener!=null){
                listener.onUpdateFinish(false,"初始化信息失败，请重试");
            }
            return;
        }
        OSSFile dataFile = getOssService().getOssFile(getBucket(), getBucketFileName(style, token));
        try {
            dataFile.setUploadFilePath(filePath,"image/jpg");
            dataFile.uploadInBackground(new SaveCallback() {

                @Override
                public void onSuccess(String objectKey) {
                    Log.d(TAG, "[onSuccess] - " + objectKey + " upload success!");
                    if(listener!=null){
                        listener.onUpdateFinish(true,objectKey);
                    }
                }

                @Override
                public void onProgress(String objectKey, int byteCount, int totalSize) {
                    Log.d(TAG, "[onProgress] - current upload " + objectKey + " bytes: " + byteCount + " in total: " + totalSize);
                }

                @Override
                public void onFailure(String objectKey, OSSException ossException) {
                    Log.e(TAG, "[onFailure] - upload " + objectKey + " failed!\n" + ossException.toString());
                    if(listener!=null){
                        listener.onUpdateFinish(false,objectKey);
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void initDate(){
        if(UserUtils.isLogin()){
            Http.getUpdateMess(new Http.Callback<MyJsonObject>(){
                @Override
                public void onSuccess(MyJsonObject obj) {
                    super.onSuccess(obj);
                    if(obj!=null){
                        endpoint = obj.getMyString("endpoint");
                        accessKey = obj.getMyString("accessKeyId");
                        screctKey = obj.getMyString("accessKeySecret");
                        bucketName = obj.getMyString("bucketName");
                    }
                }
            });
        }
    }


    public interface FileUpdateListener{
        public void onUpdateFinish(boolean success,String url);
    }
}
