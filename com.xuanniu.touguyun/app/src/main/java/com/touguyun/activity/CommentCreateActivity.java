package com.touguyun.activity;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.net.Http;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/8/28.
 */
@EActivity(R.layout.activity_comment_create)
public class CommentCreateActivity extends BaseActivity implements TitleBar.TitleBarClickListener{

    public static final int TYPE_TOPIC = 1;
    public static final int TYPE_COMB = 2;

    private int type;
    private long targetId;

    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    TextView comment_text;

    @AfterViews
    void initView(){
        type = getIntent().getIntExtra("type",TYPE_TOPIC);
        targetId = getIntent().getLongExtra("targetId",0);
        if(targetId == 0){
            finish();
        }
        touguyun_titleBar.setTitleBarClickListener(this);
    }

    @Override
    public void onBarClick(boolean isLeft) {
        if(isLeft){
            onBackPressed();
        }else{
            sentMessage();
        }
    }

    private void sentMessage(){
        if(StringUtils.isNotEmpty(comment_text.getText())){
            UIShowUtil.showDialog(CommentCreateActivity.this, true);
            if(type == TYPE_TOPIC){
                Http.opinionSentComment(targetId,comment_text.getText().toString(),callback);
            }else{
                Http.portfolioSentComment(targetId,comment_text.getText().toString(),callback);
            }
        }else{
            UIShowUtil.toast(CommentCreateActivity.this, "请输入内容");
        }
    }

    private Http.Callback callback = new Http.Callback<Boolean>(){
        @Override
        public void onSuccess(Boolean obj) {
            super.onSuccess(obj);
            UIShowUtil.toast(CommentCreateActivity.this, "发布评论成功");
            setResult(RESULT_OK);
            finish();
        }
    };
}
