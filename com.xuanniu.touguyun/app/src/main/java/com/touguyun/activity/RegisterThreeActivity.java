package com.touguyun.activity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.MainApplication;
import com.touguyun.R;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.CircleAngleTitleView;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cn.jpush.android.api.JPushInterface;
/**
 * Created by zhengyonghui on 15/9/6.
 */
@EActivity(R.layout.activity_register_three)
public class RegisterThreeActivity extends BaseActivity{

    private User registerUser;

    @ViewById
    EditText register_three_edit;

    @ViewById
    ImageView register_three_edit_clear;

    @ViewById
    CircleAngleTitleView regisger_pass_next;


    @ViewById
    RelativeLayout register_three_tougu_layout;

    @ViewById
    EditText register_three_edit_tougu;

    @ViewById
    ImageView register_three_edit_tougu_clear;

    @ViewById
    ImageView register_three_swich;

    @AfterViews
    void initViews(){
        String userStr = getIntent().getStringExtra("user");
        if(StringUtils.isEmpty(userStr)){
            finish();
        }
        registerUser = TouguJsonObject.parseObject(userStr, User.class);
        register_three_tougu_layout.setVisibility(registerUser.roleType == User.USER_TYPE_TOUGU?View.VISIBLE:View.GONE);

    }

    @Click
    void register_three_edit_clear(){
        register_three_edit.setText("");
    }

    @Click
    void register_three_edit_tougu_clear(){
        register_three_edit_tougu.setText("");
    }

    @Click
    void register_three_swich(){
        if(register_three_swich.getTag()!=null) {
            if ("0".equals(register_three_swich.getTag().toString())) {
                //设置为明文显示
                register_three_edit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                register_three_swich.setImageResource(R.drawable.pass_show_white_icon);
                register_three_swich.setTag("1");
            } else {
                //设置为秘文显示
                register_three_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                register_three_swich.setImageResource(R.drawable.pass_hide_white_icon);
                register_three_swich.setTag("0");
            }
        }
    }

    @Click
    void regisger_pass_next(){
        if(registerUser!=null){
            if(register_three_edit!=null && StringUtils.isNotEmpty((register_three_edit.getText()))){
                if(registerUser.roleType == User.USER_TYPE_TOUGU){
                    if(register_three_edit_tougu==null || StringUtils.isEmpty((register_three_edit_tougu.getText())) || (register_three_edit_tougu.getText().length()<6)){
                        UIShowUtil.showErrorDialog(RegisterThreeActivity.this, "请输入执业证书编号");
                        return;
                    }
                }
                registerUser.password = register_three_edit.getText().toString();
                if(registerUser.roleType == User.USER_TYPE_TOUGU){
                    registerUser.certificate = register_three_edit_tougu.getText().toString();
                    Http.setUserPassword(registerUser.password,registerUser.token,registerUser.certificate,registerUser.roleType,callback);
                }else{
                    Http.setUserPassword(registerUser.password,registerUser.token,"",registerUser.roleType,callback);
                }
            }else{
                UIShowUtil.showErrorDialog(RegisterThreeActivity.this, "密码错误，请重新输入。");
            }
        }

    }

    private Http.Callback callback = new Http.Callback<JSONObject>(){
        @Override
        public void onSuccess(JSONObject obj) {
            super.onSuccess(obj);
            if(StringUtils.isNotEmpty(obj.get("token")) && registerUser!=null){
                registerUser.token = obj.getString("token");
                registerUser.roleType = obj.getIntValue("roleType");
                registerUser.registType = obj.getIntValue("registType");
                UserUtils.saveUser(registerUser.token,registerUser.roleType,registerUser.registType);
                UserUtils.saveFirstRegister(true);
                Http.updateRegId(JPushInterface.getRegistrationID(MainApplication.getInstance()),null);
                ActivityUtil.goUserRegisterFour(RegisterThreeActivity.this, registerUser.toString(),RegisterFourActivity.REGISTER_TYPE_PHONE,13);
                setResult(RESULT_OK);
                finish();
            }
        }
    };

    @AfterTextChange(R.id.register_three_edit)
    void editAfterTextChanged(TextView pass) {
        if(pass!=null){
            if(register_three_edit_clear!=null){
                register_three_edit_clear.setVisibility(pass.length() == 0? View.GONE:View.VISIBLE);
            }
            if(regisger_pass_next!=null){
                if(registerUser.roleType == User.USER_TYPE_TOUGU && register_three_edit_tougu!=null){
                    regisger_pass_next.setBackAndFrameColor(getResources().getColor(pass.length() > 4  && StringUtils.isNotEmpty(register_three_edit_tougu.getText())?R.color.red_F65066 : R.color.red_EB7D8C));
                }else{
                    regisger_pass_next.setBackAndFrameColor(getResources().getColor(pass.length() > 4 ? R.color.red_F65066 : R.color.red_EB7D8C));
                }
            }
        }
    }
    @AfterTextChange(R.id.register_three_edit_tougu)
    void touguAfterTextChanged(TextView tougu) {
        if(tougu!=null){
            if(register_three_edit_tougu_clear!=null){
                register_three_edit_tougu_clear.setVisibility(tougu.length() == 0? View.GONE:View.VISIBLE);
            }
            if(regisger_pass_next!=null&& register_three_edit!=null){
                regisger_pass_next.setBackAndFrameColor(getResources().getColor(tougu.length() > 4  && StringUtils.isNotEmpty(register_three_edit.getText())?R.color.red_F65066 : R.color.red_EB7D8C));
            }
        }
    }

}
