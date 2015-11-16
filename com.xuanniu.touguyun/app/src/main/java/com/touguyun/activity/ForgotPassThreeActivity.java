package com.touguyun.activity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.R;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.CircleAngleTitleView;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/9/6.
 */
@EActivity(R.layout.activity_forgot_pass_three)
public class ForgotPassThreeActivity extends BaseActivity{
    private User forgotUser;

    @ViewById
    CircleAngleTitleView forgot_pass_finish;

    @ViewById
    EditText forgot_pass_new_word;

    @ViewById
    ImageView forgot_pass_new_word_clear;

    @ViewById
    ImageView forgot_pass_swich;

    @AfterViews
    void initViews(){
        String userStr = getIntent().getStringExtra("user");
        if(StringUtils.isEmpty(userStr)){
            finish();
        }
        forgotUser = TouguJsonObject.parseObject(userStr, User.class);

    }

    @Click
    void forgot_pass_new_word_clear(){
        forgot_pass_new_word.setText("");
    }

    @AfterTextChange(R.id.forgot_pass_new_word)
    void wordAfterTextChanged(TextView newword) {
        if(newword!=null){
            if(forgot_pass_new_word_clear!=null){
                forgot_pass_new_word_clear.setVisibility(newword.length() == 0? View.GONE:View.VISIBLE);
            }
            if(forgot_pass_finish!=null){
                forgot_pass_finish.setBackAndFrameColor(getResources().getColor(newword.length() > 4 ?R.color.red_F65066 : R.color.red_EB7D8C));
            }
        }
    }

    @Click
    void forgot_pass_swich(){
        if(forgot_pass_swich.getTag()!=null) {
            if ("0".equals(forgot_pass_swich.getTag().toString())) {
                //设置为明文显示
                forgot_pass_new_word.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                forgot_pass_swich.setImageResource(R.drawable.pass_show_gray_icon);
                forgot_pass_swich.setTag("1");
            } else {
                //设置为秘文显示
                forgot_pass_new_word.setTransformationMethod(PasswordTransformationMethod.getInstance());
                forgot_pass_swich.setImageResource(R.drawable.pass_hide_gray_icon);
                forgot_pass_swich.setTag("0");
            }
        }
    }

    @Click
    void forgot_pass_finish(){
        if(StringUtils.isNotEmpty(forgot_pass_new_word.getText()) && forgot_pass_new_word.getText().length()>5){
            if(forgotUser!=null){
                Http.forgotResetPwd(forgot_pass_new_word.getText().toString(),forgotUser.token,new Http.Callback<JSONObject>(){
                    @Override
                    public void onSuccess(JSONObject obj) {
                        super.onSuccess(obj);
                        UIShowUtil.toast(ForgotPassThreeActivity.this, "重置密码成功");
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        }else{
            UIShowUtil.toast(ForgotPassThreeActivity.this, "请输入6~18位密码");
        }
    }

}
