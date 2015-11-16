package com.touguyun.activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityStackControlUtil;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.Alert;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/9/1.
 */
@EActivity(R.layout.activity_reset_password)
public class ResetPasswordActivity extends BaseActivity{

    @ViewById
    EditText reset_pass_old_txt;
    @ViewById
    EditText reset_pass_new_txt;
    @ViewById
    EditText reset_pass_new_again_txt;
    @ViewById
    ImageView reset_pass_old_clear;
    @ViewById
    ImageView reset_pass_new_clear;
    @ViewById
    ImageView reset_pass_new_again_clear;
    @ViewById
    TitleBar touguyun_titleBar;

    @AfterViews
    void initView(){
        touguyun_titleBar.setTitleBarClickListener(clickListener);
    }

    private TitleBar.TitleBarClickListener clickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
                onBackPressed();
            }else{
                String oldp = StringUtils.returnStr(reset_pass_old_txt.getText());
                String newp =  StringUtils.returnStr(reset_pass_new_txt.getText());
                String newpa =  StringUtils.returnStr(reset_pass_new_again_txt.getText());
                if(oldp.length()<6 || newp.length()<6){
                    UIShowUtil.showErrorDialog(ResetPasswordActivity.this, "密码太短");
                    return;
                }
                if(!newp.equals(newpa)){
                    UIShowUtil.showErrorDialog(ResetPasswordActivity.this, "两次输入不一致，请确认后重试");
                    return;
                }
                Http.resetPwd(oldp,newp,new Http.Callback<Boolean>(){
                    @Override
                    public void onSuccess(Boolean obj) {
                        super.onSuccess(obj);
                        new Alert.Builder(ResetPasswordActivity.this)
                                .setMessage("重置密码成功！请重新登录！")
                                .setRightButton(R.string.assent,new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UserUtils.saveUser("", 0,0);
                                        ActivityStackControlUtil.finishActivity();
                                        ActivityUtil.goMain(ResetPasswordActivity.this);
                                        ActivityUtil.goLogin(ResetPasswordActivity.this);
                                        finish();
                                    }
                                })
                                .setRightColor(getResources().getColor(R.color.blue_3E74F6))
                                .create().show();

                    }
                });

            }
        }
    };

    @AfterTextChange(R.id.reset_pass_old_txt)
    void editAfterTextChanged1(TextView textView) {
        if(textView!=null){
            if(reset_pass_old_clear!=null){
                reset_pass_old_clear.setVisibility(textView.length() == 0 ? View.GONE : View.VISIBLE);
            }
        }
    }
    @AfterTextChange(R.id.reset_pass_new_txt)
    void editAfterTextChanged2(TextView textView) {
        if(textView!=null){
            if(reset_pass_new_clear!=null){
                reset_pass_new_clear.setVisibility(textView.length() == 0 ? View.GONE : View.VISIBLE);
            }
        }
    }
    @AfterTextChange(R.id.reset_pass_new_again_txt)
    void editAfterTextChanged3(TextView textView) {
        if(textView!=null){
            if(reset_pass_new_again_clear!=null){
                reset_pass_new_again_clear.setVisibility(textView.length() == 0 ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Click
    void reset_pass_old_clear(){
        if(reset_pass_old_txt!=null){
            reset_pass_old_txt.setText("");
        }
    }
    @Click
    void reset_pass_new_clear(){
        if(reset_pass_new_txt!=null){
            reset_pass_new_txt.setText("");
        }
    }
    @Click
    void reset_pass_new_again_clear(){
        if(reset_pass_new_again_txt!=null){
            reset_pass_new_again_txt.setText("");
        }
    }
}
