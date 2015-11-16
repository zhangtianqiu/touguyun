package com.touguyun.activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.R;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ProvingUtil;
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
@EActivity(R.layout.activity_forgot_pass_two)
public class ForgotPassTwoActivity extends BaseActivity{

    private User forgotUser;

    int codeTime;
    private Runnable runnable;
    private Handler handler=new Handler();

    @ViewById
    EditText forgot_pass_code_txt;

    @ViewById
    ImageView forgot_pass_code_txt_clear;

    @ViewById
    CircleAngleTitleView forgot_pass_two_next;

    @ViewById
    TextView forgot_pass_two_phone;

    @ViewById
    TextView forgot_pass_code_time;

    @AfterViews
    void initViews(){
        String userStr = getIntent().getStringExtra("user");
        if(StringUtils.isEmpty(userStr)){
            finish();
        }
        forgotUser = TouguJsonObject.parseObject(userStr, User.class);
        if(forgotUser==null || StringUtils.isEmpty(forgotUser.mobile )){
            finish();
        }
        forgot_pass_two_phone.setText(StringUtils.returnStr(forgotUser.mobile ));
        startTime();
    }

    public void startTime(){
        if (null!=runnable){
            handler.removeCallbacks(runnable);
        }
        runnable=new Runnable() {
            @Override
            public void run() {
                codeTime+=1;
                if (codeTime<60){
                    forgot_pass_code_time.setText((60 - codeTime) + "秒后重新发送");
                    handler.postDelayed(runnable, 1000);
                }else{
                    handler.removeCallbacks(this);
                    forgot_pass_code_time.setText("重新发送验证码");
                    codeTime=0;
                }
            }
        };
        handler.post(runnable);
    }

    @Click
    void forgot_pass_code_time(){
        if(codeTime == 0){
            Http.forgotReqSms("", forgotUser.token,1, new Http.Callback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject obj) {
                    super.onSuccess(obj);
                    startTime();
                }
            });
        }
    }

    @Click
    void forgot_pass_code_txt_clear(){
        forgot_pass_code_txt.setText("");
    }

    @Click
    void forgot_pass_two_next(){
        if(forgot_pass_code_txt!=null && forgot_pass_code_txt.getText()!=null && forgot_pass_code_txt.getText().length()>5 && ProvingUtil.isNumber(forgot_pass_code_txt.getText().toString())){
            forgotUser.code = forgot_pass_code_txt.getText().toString();
            Http.forgotVerifySms(forgotUser.code,forgotUser.token,1,new Http.Callback<JSONObject>(){
                @Override
                public void onSuccess(JSONObject obj) {
                    super.onSuccess(obj);
                    ActivityUtil.goForgotPass3(ForgotPassTwoActivity.this, forgotUser.toString(),14);
                }
            });

        }else{
            UIShowUtil.showErrorDialog(ForgotPassTwoActivity.this, "验证码错误，请重新输入。");
            return;
        }

    }

    @AfterTextChange(R.id.forgot_pass_code_txt)
    void editAfterTextChanged(TextView code) {
        if(code!=null){
            if(forgot_pass_code_txt_clear!=null){
                forgot_pass_code_txt_clear.setVisibility(code.length() == 0? View.GONE:View.VISIBLE);
            }
            if(forgot_pass_two_next!=null){
                forgot_pass_two_next.setBackAndFrameColor(getResources().getColor(code.length() >5 ? R.color.red_F65066 : R.color.red_EB7D8C));
            }
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
