package com.touguyun.activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.R;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ProvingUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.CircleAngleTitleView;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/9/6.
 */
@EActivity(R.layout.activity_forgot_pass_one)
public class ForgotPassOneActivity extends BaseActivity{
    private User forgotUser;


    @ViewById
    EditText forgot_pass_phone;

    @ViewById
    ImageView forgot_pass_phone_clear;

    @ViewById
    CircleAngleTitleView forgot_pass_phone_next;

    @Click
    void forgot_pass_phone_clear(){
        if(forgot_pass_phone!=null){
            forgot_pass_phone.setText("");
        }
    }

    @AfterTextChange(R.id.forgot_pass_phone)
    void editAfterTextChanged(TextView phone) {
        if(phone!=null){
            if(forgot_pass_phone_clear!=null){
                forgot_pass_phone_clear.setVisibility(phone.length() == 0? View.GONE:View.VISIBLE);
            }
            if(forgot_pass_phone_next!=null){
                forgot_pass_phone_next.setBackAndFrameColor(getResources().getColor(phone.length() == 11 ? R.color.red_F65066 : R.color.red_EB7D8C));
            }
        }
    }

    @Click
    void forgot_pass_phone_next(){
        if(forgot_pass_phone!=null && forgot_pass_phone.getText()!=null && forgot_pass_phone.getText().length()==11 && ProvingUtil.isMobileNO(forgot_pass_phone.getText().toString())){
            forgotUser = new User();
            forgotUser.mobile = forgot_pass_phone.getText().toString();
            Http.forgotReqSms(forgotUser.mobile, "",1, new Http.Callback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject obj) {
                    super.onSuccess(obj);
                    if (obj != null) {
                        if (StringUtils.isNotEmpty(obj.get("token"))) {
                            forgotUser.token = obj.getString("token");
                            ActivityUtil.goForgotPass2(ForgotPassOneActivity.this, forgotUser.toString(),14);
                        }
                    }
                }
            });
        }else{
            UIShowUtil.showErrorDialog(ForgotPassOneActivity.this, "该手机号已经注册，请重新输入");
            return;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 14 && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }
}
