package com.touguyun.activity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.R;
import com.touguyun.module.IdName;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.AlertItems;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.MainTopToolsView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;
/**
 * Created by zhengyonghui on 15/9/2.
 */
@EActivity(R.layout.activity_create_guandian)
public class CreateGuandianActivity extends BaseActivity{

    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    MainTopToolsView touguyun_main_top_tools;

    @ViewById
    TextView create_guandian_choose_topic;

    @ViewById
    EditText create_guandian_edit_title;

    @ViewById
    EditText create_guandian_edit_context;

    private String title, summary, tags, content;
    private long osid, cid;
    private boolean isTopic = true;
    private List<IdName> topicList;
    private List<IdName> combList;
    private AlertItems alertItems;

    @AfterViews
    void initView(){
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
        Http.opinionSubjectList(new Http.Callback<JSONObject>(){
            @Override
            public void onSuccess(JSONObject obj) {
                super.onSuccess(obj);
                if(obj!=null){
                    topicList = TouguJsonObject.parseList(obj.getJSONArray("subjects"),IdName.class);
                    combList = TouguJsonObject.parseList(obj.getJSONArray("portFolios"),IdName.class);
                }
                if(combList!=null && combList.size()>0){
                    touguyun_main_top_tools.setVisibility(View.VISIBLE);
                    touguyun_main_top_tools.setData(new String[]{"关于话题","关于组合"},topToolsClickListener);
                }
            }
        });
    }



    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if(isLeft){
                onBackPressed();
            }else{
                if((osid == 0 && isTopic == true)||(cid == 0 && isTopic == false)){
                    UIShowUtil.toast(CreateGuandianActivity.this, "请选择话题");
                    return;
                }
                if(StringUtils.isNotEmpty(create_guandian_edit_title.getText())){
                    title = create_guandian_edit_title.getText().toString();
                }else{
                    UIShowUtil.toast(CreateGuandianActivity.this, "请输入标题");
                    return;
                }
                if(StringUtils.isNotEmpty(create_guandian_edit_context.getText())){
                    content = create_guandian_edit_context.getText().toString();
                }else{
                    UIShowUtil.toast(CreateGuandianActivity.this, "请输入观点内容");
                    return;
                }
                UIShowUtil.showDialog(CreateGuandianActivity.this, true);
                Http.opinionCreate(title,"","",content,isTopic?osid:0,isTopic?0:cid,new Http.Callback<Boolean>(){
                    @Override
                    public void onSuccess(Boolean obj) {
                        super.onSuccess(obj);
                        UIShowUtil.toast(CreateGuandianActivity.this, "发布观点成功");
                        finish();
                    }
                });
            }
        }
    };

    @Click
    void create_guandian_choose_topic(){
        UIShowUtil.closeBroads(CreateGuandianActivity.this);
        if(topicList==null){
            Http.opinionSubjectList(new Http.Callback<JSONObject>(){
                @Override
                public void onSuccess(JSONObject obj) {
                    super.onSuccess(obj);
                    if(obj!=null){
                        topicList = TouguJsonObject.parseList(obj.getJSONArray("subjects"),IdName.class);
                        combList = TouguJsonObject.parseList(obj.getJSONArray("portFolios"),IdName.class);
                    }
                    showAlert();
                }
            });
        }else{
            showAlert();
        }
    }

    private void showAlert(){
        if(alertItems == null){
            alertItems = new AlertItems(CreateGuandianActivity.this);
        }
        alertItems.show(touguyun_titleBar,isTopic?topicList:combList,new AlertItems.AlertItemsClick() {
            @Override
            public void onClick(IdName a) {
                if(isTopic){
                    osid = a.id;
                }else{
                    cid = a.id;
                }
                create_guandian_choose_topic.setText("#"+a.name+"#");
            }
        });
    }

    private MainTopToolsView.MainTopToolsClickListener topToolsClickListener = new MainTopToolsView.MainTopToolsClickListener() {
        @Override
        public void onTopClick(int position, View view) {
            if(position == 0){
                create_guandian_choose_topic.setText("#选择话题#");
            }else{
                create_guandian_choose_topic.setText("#选择组合#");
            }
            isTopic = position == 0;
        }
    };


}
