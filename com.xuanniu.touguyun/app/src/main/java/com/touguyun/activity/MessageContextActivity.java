package com.touguyun.activity;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/9/3.
 */
@EActivity(R.layout.activity_message_context)
public class MessageContextActivity extends BaseActivity{

    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    TextView mess_cont_title;

    @ViewById
    TextView mess_cont_time;

    @ViewById
    TextView mess_cont_context;

    @AfterViews
    void initViews(){
        touguyun_titleBar.showTitle(getIntent().getStringExtra("title"));
    }

}
