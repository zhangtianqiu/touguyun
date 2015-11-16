package com.touguyun.activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.crop.ImageCropper;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityStackControlUtil;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.FileUtils;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.Images;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UploaderUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.Alert;
import com.touguyun.view.CircleImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
/**
 * Created by zhengyonghui on 15/9/1.
 */
@EActivity(R.layout.activity_set_up)
public class SetUpActivity extends BaseActivity implements ImageCropper.ICropListener,UploaderUtil.FileUpdateListener{

    private ImageCropper cropper;
    private File imageFile;

    private User loginUser;
    
    @ViewById
    CircleImageView set_up_user_header;

    @ViewById
    TextView set_up_user_name;

    @AfterViews
    void initView(){
        String userStr = getIntent().getStringExtra("user");
        if(StringUtils.isNotEmpty(userStr)){
            loginUser = TouguJsonObject.parseObject(userStr,User.class);
        }
        cropper = new ImageCropper(this);
        cropper.setMaxSize(800,true);
        cropper.setCropListener(this);
        if(loginUser!=null){
            if(StringUtils.startWithHttp(loginUser.userImg)){
                ImageLoader.getInstance().showImage(loginUser.userImg,set_up_user_header);
            }else{
                set_up_user_header.setImageResource(R.drawable.default_header);
            }
            set_up_user_name.setText(StringUtils.returnStr(loginUser.name));
        }
    }

    @Click
    void set_up_user_header_layout(){
        cropper.show(false,1);
    }
    @Click
    void set_up_user_name_layout(){
        if(loginUser!=null){
            ActivityUtil.goModifyName(SetUpActivity.this,loginUser.name,23);
        }
    }
    @Click
    void set_up_change_pass(){
        ActivityUtil.goResetPassword(SetUpActivity.this);
    }
    @Click
    void set_up_feedback(){
        ActivityUtil.goFeedback(SetUpActivity.this);
    }
    @Click
    void set_up_about_us(){
        ActivityUtil.goAboutUs(SetUpActivity.this);
    }
    @Click
    void set_up_logout(){
        new Alert.Builder(SetUpActivity.this)
                .setMessage("退出后不会删除任何历史数据，下次登录依然可以使用本账号")
                .setLeftButton(R.string.assent,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       Http.logout(new Http.Callback<Boolean>());
                        ActivityStackControlUtil.finishActivity();
                        ActivityUtil.goMain(SetUpActivity.this);
                       UserUtils.saveUser("",0,0);
                    }
                })
                .setRightButton(R.string.cancel, null)
                .create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (cropper!=null &&  cropper.isMyResult(requestCode)){
            cropper.onActivityResult(requestCode,resultCode,data);
        }else if(requestCode == 23 && data!=null){
            String name = data.getStringExtra("name");
            if(loginUser!=null){
                loginUser.name = name;
            }
            set_up_user_name.setText(StringUtils.returnStr(name));
        }
    }
    @Override
    public void onCleanImage(int indexImage) {
    }
    @Override
    public void onFinish(String filePath, int index) {
        if(StringUtils.isNotEmpty(filePath)){
            if(index == 1){
                imageFile = new File(FileUtils.getTempPath(),filePath);
                UploaderUtil.getInstance().asyncUpload(imageFile.getAbsolutePath(),UploaderUtil.STYLE_HEADER, UserUtils.getToken(),this);
                UIShowUtil.toast(SetUpActivity.this, "修改头像成功");
            }
        }
    }
    @Override
    public void onUpdateFinish(boolean success, String url) {
        if(success){
            Http.updateUserInfo(url,"",new Http.Callback<Boolean>(){
                @Override
                public void onSuccess(Boolean obj) {
                    super.onSuccess(obj);
                    set_up_user_header.setImageBitmap(Images.decodeFile(imageFile.getAbsolutePath(), 800, true));
                }
            });
        }else {
            UIShowUtil.toast(SetUpActivity.this, "修改头像失败");
        }
    }


}
