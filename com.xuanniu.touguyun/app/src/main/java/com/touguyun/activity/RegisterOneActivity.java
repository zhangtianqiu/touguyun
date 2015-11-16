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
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/8/25.
 */
@EActivity(R.layout.activity_register_one)
public class RegisterOneActivity extends BaseActivity{

    private User registerUser;

    @ViewById
    EditText register_one_edit;

    @ViewById
    ImageView register_one_edit_clear;

    @ViewById
    CircleAngleTitleView regisger_next;

    @ViewById
    TitleBar touguyun_titleBar;

    @AfterViews
    void initView(){
        registerUser = new User();
        registerUser.roleType = getIntent().getIntExtra("type",User.USER_TYPE_PUTONG);
        touguyun_titleBar.showTitle(registerUser.roleType==User.USER_TYPE_TOUGU?R.string.register_tougu_title:R.string.register_title);
    }

    @Click
    void register_one_edit_clear(){
        if(register_one_edit!=null){
            register_one_edit.setText("");
        }
    }

    @Click
    void regisger_agreement_link(){
        ActivityUtil.goAgreement(RegisterOneActivity.this,registerUser!=null?registerUser.roleType:User.USER_TYPE_PUTONG);
    }
    @Click
    void regisger_next(){
        if(register_one_edit!=null && register_one_edit.getText()!=null && register_one_edit.getText().length()==11 && ProvingUtil.isMobileNO(register_one_edit.getText().toString())){
            Http.register(register_one_edit.getText().toString(),"",0, new Http.Callback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject obj) {
                    super.onSuccess(obj);
                    registerUser.token= obj.getString("token");
                    registerUser.mobile = register_one_edit.getText().toString();
                    if(StringUtils.isNotEmpty(registerUser.token)){
                        ActivityUtil.goUserRegisterTwo(RegisterOneActivity.this,registerUser.toString(),13);
                    }
                }
                @Override
                public void onBusinessError(int errorCode, String errorMessage) {
                    UIShowUtil.showErrorDialog(RegisterOneActivity.this, errorMessage);
                }
            });
        }else{
            UIShowUtil.toast(RegisterOneActivity.this, "手机号有误，请查正后重试");
            return;
        }
    }

    @AfterTextChange(R.id.register_one_edit)
    void editAfterTextChanged(TextView phone) {
        if(phone!=null){
            if(register_one_edit_clear!=null){
                register_one_edit_clear.setVisibility(phone.length() == 0? View.GONE:View.VISIBLE);
            }
            if(regisger_next!=null){
                regisger_next.setBackAndFrameColor(getResources().getColor(phone.length() == 11 ? R.color.red_F65066 : R.color.red_EB7D8C));
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
