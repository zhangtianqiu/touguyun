package com.touguyun.activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.R;
import com.touguyun.crop.ImageCropper;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.FileUtils;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.Images;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UploaderUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.CircleAngleTitleView;
import com.touguyun.view.CircleImageView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

/**
 * Created by zhengyonghui on 15/9/6.
 */
@EActivity(R.layout.activity_register_four)
public class RegisterFourActivity extends BaseActivity  implements ImageCropper.ICropListener,UploaderUtil.FileUpdateListener{

    public static int REGISTER_TYPE_PHONE = 1;
    public static int REGISTER_TYPE_THRID = 2;

    private int type;

    private User registerUser;
    private File imageFile;

    private ImageCropper cropper;

    @ViewById
    TitleBar touguyun_titleBar;
    @ViewById
    CircleImageView register_four_header;
    @ViewById
    EditText register_four_name;
    @ViewById
    ImageView register_four_name_clear;
    @ViewById
    CircleAngleTitleView regisger_finish;

    @AfterViews
    void initViews(){
        type = getIntent().getIntExtra("type",REGISTER_TYPE_PHONE);
        String userStr = getIntent().getStringExtra("user");
        if(StringUtils.isEmpty(userStr)){
            finish();
        }
        UploaderUtil.getInstance();
        cropper = new ImageCropper(this);
        cropper.setMaxSize(1024,false);
        cropper.setCropListener(this);
        registerUser = TouguJsonObject.parseObject(userStr, User.class);
        touguyun_titleBar.showTitle(registerUser.roleType==User.USER_TYPE_TOUGU?R.string.register_tougu_title:R.string.register_title);
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
        if(type == REGISTER_TYPE_THRID){
            touguyun_titleBar.hideButton(false);
            touguyun_titleBar.showTitle("设置个人资料");
            if(StringUtils.startWithHttp(registerUser.userImg)){
                ImageLoader.getInstance().showImage(registerUser.userImg,register_four_header);
            }else{
                register_four_header.setImageResource(R.drawable.default_header);
            }
            register_four_name.setText(StringUtils.returnStr(registerUser.name));
        }
    }

    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
//                onBackPressed();
            }else{
                finish();
            }
        }
    };

    @Click
    void register_four_name_clear(){
        register_four_name.setText("");
    }

    @Click
    void register_four_header(){
        cropper.show(false,1);
    }

    @Click
    void regisger_finish(){
        if(registerUser!=null){
            if(StringUtils.isEmpty(register_four_name.getText())){
                UIShowUtil.toast(RegisterFourActivity.this, "请输入名称");
                return;
            }
            if(FileUtils.isEmptyFile(imageFile) && !StringUtils.startWithHttp(registerUser.userImg)){
                UIShowUtil.toast(RegisterFourActivity.this, "请设置头像");
                return;
            }
            UIShowUtil.showDialog(RegisterFourActivity.this, true);
            if(registerUser!=null){
                UIShowUtil.showDialog(RegisterFourActivity.this, true);
                registerUser.name = register_four_name.getText().toString();
                if(FileUtils.isNotEmptyFile(imageFile)){
                    UploaderUtil.getInstance().asyncUpload(imageFile.getAbsolutePath(), registerUser.token,UploaderUtil.STYLE_HEADER,RegisterFourActivity.this);
                }else if(StringUtils.startWithHttp(registerUser.userImg)){
                    sentHttpData(registerUser.userImg);
                }
            }
        }
    }

    @AfterTextChange(R.id.register_four_name)
    void touguAfterTextChanged(TextView tougu) {
        if(tougu!=null){
            if(register_four_name_clear!=null){
                register_four_name_clear.setVisibility(tougu.length() == 0? View.GONE:View.VISIBLE);
            }
            if(regisger_finish!=null){
                regisger_finish.setBackAndFrameColor(getResources().getColor(tougu.length() > 0  && FileUtils.isNotEmptyFile(imageFile) ?R.color.red_F65066 : R.color.red_EB7D8C));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (cropper!=null &&  cropper.isMyResult(requestCode)){
            cropper.onActivityResult(requestCode,resultCode,data);
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
                register_four_header.setImageBitmap(Images.decodeFile(imageFile.getAbsolutePath(),800,true));
                if(register_four_name!=null){
                    regisger_finish.setBackAndFrameColor(getResources().getColor(register_four_name.length() > 0  && FileUtils.isNotEmptyFile(imageFile) ?R.color.red_F65066 : R.color.red_EB7D8C));
                }
            }
        }
    }
    @Override
    public void onUpdateFinish(boolean success, String url) {
        if(success){
            sentHttpData(url);
        }else {
            UIShowUtil.toast(RegisterFourActivity.this, "提交失败");
        }
    }

    public void sentHttpData(String url){
        if(registerUser!=null){
            if(type == REGISTER_TYPE_PHONE){
                Http.setUserInfo(url,registerUser.name,registerUser.token,new Http.Callback<Boolean>(){
                    @Override
                    public void onSuccess(Boolean obj) {
                        super.onSuccess(obj);
                        finish();
                    }
                });
            }else{
                Http.thirdRegister(registerUser.name,url,registerUser.token,new Http.Callback<JSONObject>(){
                    @Override
                    public void onSuccess(JSONObject obj) {
                        super.onSuccess(obj);
                        UserUtils.saveUser(obj.getString("token"), obj.getIntValue("roleType"), obj.getIntValue("registType"));
                        UploaderUtil.getInstance();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        }else{
            UIShowUtil.toast(RegisterFourActivity.this, "提交失败");
        }
    }

}
