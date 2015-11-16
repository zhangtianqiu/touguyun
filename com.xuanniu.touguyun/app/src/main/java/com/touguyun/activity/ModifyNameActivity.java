package com.touguyun.activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.net.Http;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/9/1.
 */
@EActivity(R.layout.activity_modify_name)
public class ModifyNameActivity extends BaseActivity{

    private String name;

    @ViewById
    EditText modify_name_txt;

    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    ImageView modify_clear_icon;

    @AfterViews
    void initView(){
        name = getIntent().getStringExtra("name");
        modify_name_txt.setText(StringUtils.returnStr(name));
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
    }

    @AfterTextChange(R.id.modify_name_txt)
    void editAfterTextChanged(TextView nameView) {
        if(nameView!=null){
            if(modify_clear_icon!=null){
                modify_clear_icon.setVisibility(nameView.length() == 0 ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Click
    void modify_clear_icon(){
        if(modify_name_txt!=null){
            modify_name_txt.setText("");
        }
        modify_clear_icon.setVisibility(View.GONE);
    }

    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
                onBackPressed();
            }else{
                if(StringUtils.isNotEmpty(modify_name_txt.getText())){
                    Http.updateUserInfo("",modify_name_txt.getText().toString(),new Http.Callback<Boolean>(){
                        @Override
                        public void onSuccess(Boolean obj) {
                            super.onSuccess(obj);
                            UIShowUtil.toast(ModifyNameActivity.this, "修改成功");
                            setResult(RESULT_OK,new Intent().putExtra("name",modify_name_txt.getText().toString()));
                            finish();
                        }
                    });
                }
            }
        }
    };


}
