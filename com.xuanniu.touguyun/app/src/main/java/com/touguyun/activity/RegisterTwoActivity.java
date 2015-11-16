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
@EActivity(R.layout.activity_register_two)
public class RegisterTwoActivity extends BaseActivity{

    private User registerUser;

    int codeTime;
    private Runnable runnable;
    private Handler handler=new Handler();

    @ViewById
    EditText register_two_edit;

    @ViewById
    ImageView register_two_edit_clear;

    @ViewById
    CircleAngleTitleView regisger_code_next;

    @ViewById
    TextView register_two_mobile;

    @ViewById
    TextView regisger_code_time;

    @AfterViews
    void initViews(){
        String userStr = getIntent().getStringExtra("user");
        if(StringUtils.isEmpty(userStr)){
            finish();
        }
        registerUser = TouguJsonObject.parseObject(userStr,User.class);
        if(registerUser==null || StringUtils.isEmpty(registerUser.mobile )){
            finish();
        }
        register_two_mobile.setText(StringUtils.returnStr(registerUser.mobile ));
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
                    regisger_code_time.setText((60 - codeTime) + "秒后重新发送");
                    handler.postDelayed(runnable, 1000);
                }else{
                    handler.removeCallbacks(this);
                    regisger_code_time.setText("重新发送验证码");
                    codeTime=0;
                }
            }
        };
        handler.post(runnable);
    }

    @Click
    void regisger_code_time(){
        if(codeTime == 0){
            Http.register("",registerUser.token,0,new Http.Callback<JSONObject>(){
                @Override
                public void onSuccess(JSONObject obj) {
                    super.onSuccess(obj);
                    startTime();
                }
            });
        }
    }
    @Click
    void register_two_edit_clear(){
        register_two_edit.setText("");
    }

    @Click
    void regisger_code_next(){
        if(register_two_edit!=null && register_two_edit.getText()!=null && register_two_edit.getText().length()>4 && ProvingUtil.isNumber(register_two_edit.getText().toString())){
            if(registerUser!=null){
                registerUser.code = register_two_edit.getText().toString();
                Http.registerVerifySms(registerUser.code,registerUser.token,0,new Http.Callback<JSONObject>(){
                    @Override
                    public void onSuccess(JSONObject obj) {
                        super.onSuccess(obj);
                        ActivityUtil.goUserRegisterThree(RegisterTwoActivity.this, registerUser.toString(),13);
                    }
                });

            }
        }else{
            UIShowUtil.showErrorDialog(RegisterTwoActivity.this, "验证码错误，请重新输入。");
            return;
        }

    }

    @AfterTextChange(R.id.register_two_edit)
    void editAfterTextChanged(TextView phone) {
        if(phone!=null){
            if(register_two_edit_clear!=null){
                register_two_edit_clear.setVisibility(phone.length() == 0? View.GONE:View.VISIBLE);
            }
            if(regisger_code_next!=null){
                regisger_code_next.setBackAndFrameColor(getResources().getColor(phone.length() >4 ? R.color.red_F65066 : R.color.red_EB7D8C));
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 13 && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }
}
