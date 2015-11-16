package com.touguyun.activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.touguyun.R;
import com.touguyun.crop.ImageCropper;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.FileUtils;
import com.touguyun.utils.Images;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.CircleAngleTitleView;
import com.touguyun.view.RoundedCornersImage;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
/**
 * Created by zhengyonghui on 15/9/3.
 */
@EActivity(R.layout.activity_create_zuhe_first)
public class CreateZuheFirstActivity extends BaseActivity implements ImageCropper.ICropListener {

    private File imageFile;
    private ImageCropper cropper;
    private boolean titleRightCanClick = false;
    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    CircleAngleTitleView create_zuhe_image_button;
    @ViewById
    RoundedCornersImage create_zuhe_image_view;

    @ViewById
    EditText create_zuhe_name_txt;

    @AfterViews
    void initViews(){
        cropper = new ImageCropper(CreateZuheFirstActivity.this);
        cropper.setCropListener(this);
        cropper.setMaxSize(600, true);
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
        updateTitleBar();
    }

    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
                onBackPressed();
            }else if(titleRightCanClick){
                if(StringUtils.isEmpty(create_zuhe_name_txt.getText())){
                    UIShowUtil.toast(CreateZuheFirstActivity.this, "请输入组合名称");
                    return;
                }
                ActivityUtil.goCreateZuhe2(CreateZuheFirstActivity.this,create_zuhe_name_txt.getText().toString(),FileUtils.isEmptyFile(imageFile)?"":imageFile.getAbsolutePath(),11);
            }
        }
    };

    @Click
    void create_zuhe_image_button(){
        cropper.show(false,1);
    }
    @Click
    void create_zuhe_image_view(){
        cropper.show(false,1);
    }
    @Override
    public void onCleanImage(int indexImage) {
    }
    @Override
    public void onFinish(String filePath, int index) {
        if(StringUtils.isNotEmpty(filePath)){
            if(index == 1){
                imageFile = new File(FileUtils.getTempPath(),filePath);
                create_zuhe_image_view.setImageBitmap(Images.decodeFile(imageFile.getAbsolutePath(), 800, true));
                create_zuhe_image_view.setVisibility(View.VISIBLE);
                create_zuhe_image_button.setVisibility(View.GONE);
//                titleRightCanClick = create_zuhe_name_txt.length()>0 && imageFile!=null;
//                updateTitleBar();
            }
        }
    }

    @AfterTextChange(R.id.create_zuhe_name_txt)
    void nameAfterTextChange(){
        titleRightCanClick = create_zuhe_name_txt.length()>0;
        updateTitleBar();
    }

    private void updateTitleBar(){
        if(titleRightCanClick){
            touguyun_titleBar.setRightTextColor(getResources().getColor(titleRightCanClick?R.color.black_323232:R.color.gray_969696));
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(cropper.isMyResult(requestCode)){
            cropper.onActivityResult(requestCode,resultCode,data);
        }else if(requestCode == 11 && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            this.finish();
        }

    }
}
