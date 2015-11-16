package com.touguyun.activity;
import android.content.Intent;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.touguyun.R;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UploaderUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/9/3.
 */
@EActivity(R.layout.activity_create_zuhe_second)
public class CreateZuheSecondActivity extends BaseActivity implements UploaderUtil.FileUpdateListener {

    private boolean titleRightCanClick = false;
    private String name,imgPath;

    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    EditText create_zuhe_info_txt;

    @AfterViews
    void initView(){
        name = getIntent().getStringExtra("name");
        imgPath = getIntent().getStringExtra("imgPath");
        if(StringUtils.isEmpty(name)){
            UIShowUtil.toast(CreateZuheSecondActivity.this, "数据异常，请重试");
            finish();
        }
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
    }

    @AfterTextChange(R.id.create_zuhe_info_txt)
    void nameAfterTextChange(){
        titleRightCanClick = create_zuhe_info_txt.length()>0;
        updateTitleBar();
    }

    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
                onBackPressed();
            }else if(titleRightCanClick){
                if(StringUtils.isNotEmpty(create_zuhe_info_txt.getText())){
                    UIShowUtil.showDialog(CreateZuheSecondActivity.this, true);
                    if(StringUtils.isNotEmpty(imgPath)){
                        UploaderUtil.getInstance().asyncUpload(imgPath,UploaderUtil.STYLE_PORTFOLIO, UserUtils.getToken(),CreateZuheSecondActivity.this);
                    }else{
                        onUpdateFinish(true,"");
                    }
                }
            }
        }
    };

    private void updateTitleBar(){
        if(titleRightCanClick){
            touguyun_titleBar.setRightTextColor(getResources().getColor(titleRightCanClick?R.color.black_323232:R.color.gray_969696));
        }
    }
    @Override
    public void onUpdateFinish(boolean success, String url) {
        if(success){
            Http.createPortfolio(name,url,create_zuhe_info_txt.getText().toString(),new Http.Callback<Integer>(){
                @Override
                public void onSuccess(Integer obj) {
                    super.onSuccess(obj);
                    setResult(RESULT_OK, new Intent());
                    ActivityUtil.goCreateZuhe3(CreateZuheSecondActivity.this, obj.longValue());
                    finish();
                }
                @Override
                public void onNetworkError(VolleyError error) {
                    super.onNetworkError(error);
                }
                @Override
                public void onBusinessError(int errorCode, String errorMessage) {
                    super.onBusinessError(errorCode, errorMessage);
                }
            });
        }else{
            UIShowUtil.toast(CreateZuheSecondActivity.this, "图片上传失败，请稍后重试");
        }
    }
}
