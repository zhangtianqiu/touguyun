package com.touguyun.activity;
import android.widget.EditText;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.net.Http;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.CircleAngleTitleView;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/9/1.
 */


@EActivity(R.layout.activity_feedback)
public class FeedbackActivity extends BaseActivity{

    @ViewById
    EditText feedback_txt;

    @ViewById
    CircleAngleTitleView feedback_button;


    @AfterTextChange(R.id.feedback_txt)
    void editAfterTextChanged(TextView feedbackView) {
        if(feedbackView!=null){
            if(feedback_button!=null){
                feedback_button.setBackAndFrameColor(feedbackView.length() > 0 ? getResources().getColor(R.color.red_FD4E4E) : getResources().getColor(R.color.red_EB7D8C));
            }
        }
    }

    @Click
    void feedback_button(){
        if(StringUtils.isNotEmpty(feedback_txt.getText())){
            UIShowUtil.showDialog(FeedbackActivity.this, true);
            Http.feedback(feedback_txt.getText().toString(),new Http.Callback<Boolean>(){
                @Override
                public void onSuccess(Boolean obj) {
                    super.onSuccess(obj);
                    UIShowUtil.toast(FeedbackActivity.this, "提交成功，感谢您的支持！");
                    finish();
                }
            });
        }else{
            UIShowUtil.toast(FeedbackActivity.this, "请填写您对投顾云的意见吧~");
        }
    }
}
