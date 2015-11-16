package com.touguyun.activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.Consultant;
import com.touguyun.module.User;
import com.touguyun.net.Http;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.CircleImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/8/29.
 */
@EActivity(R.layout.activity_user_page_info)
public class UserPageInfoActivity extends BaseActivity{

    private long uid;

    @ViewById
    CircleImageView user_page_info_header;

    @ViewById
    TextView user_page_info_name;
    @ViewById
    TextView user_page_info_tag;

    @ViewById
    RelativeLayout user_page_info_sex_layout;
    @ViewById
    TextView user_page_info_sex;

    @ViewById
    RelativeLayout user_page_info_certification_layout;
    @ViewById
    ImageView user_page_info_certification_v;
    @ViewById
    TextView user_page_info_certification;

    @ViewById
    RelativeLayout user_page_info_experience_layout;
    @ViewById
    TextView user_page_info_experience;


    @ViewById
    RelativeLayout user_page_info_context_layout;
    @ViewById
    TextView user_page_info_context;

    @AfterViews
    void initViews(){
        uid = getIntent().getLongExtra("uid",0);
        UIShowUtil.showDialog(UserPageInfoActivity.this, true);
        Http.consultInfoFromUser(uid,new Http.Callback<Consultant>(){
            @Override
            public void onSuccess(Consultant obj) {
                super.onSuccess(obj);
                if(obj!=null){
                    if(StringUtils.startWithHttp(obj.userImg)){
                        ImageLoader.getInstance().showImage(obj.userImg,user_page_info_header);
                    }else{
                        user_page_info_header.setImageResource(R.drawable.default_header);
                    }
                    user_page_info_name.setText(StringUtils.returnStr(obj.name));
                    user_page_info_name.setCompoundDrawablesWithIntrinsicBounds(0,0,obj.authState == User.USER_TYPE_TOUGU?R.drawable.tougu_v_icon:0,0);
                    user_page_info_tag.setText(StringUtils.returnStr(obj.yearsEmployment));
                    //性别
                    if(StringUtils.isNotEmpty(obj.sexStr)){
                        user_page_info_sex.setText(obj.sexStr);
                        user_page_info_sex_layout.setVisibility(View.VISIBLE);
                    }else{
                        user_page_info_sex_layout.setVisibility(View.GONE);
                    }
                    //证书
                    if(StringUtils.isNotEmpty(obj.certificate)){
                        user_page_info_certification.setText(obj.certificate);
                        user_page_info_certification_layout.setVisibility(View.VISIBLE);
                    }else{
                        user_page_info_certification_layout.setVisibility(View.GONE);
                    }
                    //经历
                    if(StringUtils.isNotEmpty(obj.workExperience)){
                        user_page_info_experience.setText(obj.workExperience);
                        user_page_info_experience_layout.setVisibility(View.VISIBLE);
                    }else{
                        user_page_info_experience_layout.setVisibility(View.GONE);
                    }
                    if(StringUtils.isNotEmpty(obj.personalProfile)){
                        user_page_info_context.setText(obj.personalProfile);
                        user_page_info_context_layout.setVisibility(View.VISIBLE);
                    }else{
                        user_page_info_context_layout.setVisibility(View.GONE);
                    }
                }
            }
        });
    }



}
