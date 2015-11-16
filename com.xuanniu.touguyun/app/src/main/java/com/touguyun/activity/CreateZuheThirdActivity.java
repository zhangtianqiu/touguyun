package com.touguyun.activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.touguyun.R;
import com.touguyun.module.StockGroup;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.GupiaoZuheGroupView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;
/**
 * Created by zhengyonghui on 15/9/3.
 */
@EActivity(R.layout.activity_create_zuhe_third)
public class CreateZuheThirdActivity extends BaseActivity{


    private long pid;
    private String cashPercent;
    private List<StockGroup> stockGroup;
    @ViewById
    TitleBar touguyun_titleBar;

    @ViewById
    View create_zuhe_third_space;
    @ViewById
    ScrollView create_zuhe_third_gupiao_ScrollView;
    @ViewById
    RelativeLayout create_zuhe_bottom_tools_layout;
    @ViewById
    TextView create_zuhe_bottom_tools_txt;
    @ViewById
    ProgressBar create_zuhe_bottom_tools_progressbar;

    @AfterViews
    void initViews(){
        pid = getIntent().getLongExtra("pid",0);
        if(pid == 0){
            finish();
        }
        UIShowUtil.showDialog(CreateZuheThirdActivity.this, true);
        Http.getPortfolioStockList(pid,new Http.Callback<JSONObject>(){
            @Override
            public void onSuccess(JSONObject obj) {
                super.onSuccess(obj);
                if(obj!=null){
                    cashPercent = obj.getString("cashPercent");
                    stockGroup = TouguJsonObject.parseList(obj.getJSONArray("stockGroup"), StockGroup.class);
                    touguyun_titleBar.setRightTextColor(getResources().getColor(R.color.black_323232));
                    initLayoutView();
                }
            }
        });
        touguyun_titleBar.setTitleBarClickListener(new TitleBar.TitleBarClickListener() {
            @Override
            public void onBarClick(boolean isLeft) {
                if(isLeft){
                    onBackPressed();
                }else if(stockGroup!=null && stockGroup.size()>0){
                    onBackPressed();
                }else{
                    UIShowUtil.toast(CreateZuheThirdActivity.this, "请添加股票");
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }
    @Click
    void create_zuhe_third_add_gupiao(){
        ActivityUtil.goSearchGupiao(CreateZuheThirdActivity.this,pid,23);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 23){
            initViews();
        }
    }
    private void initLayoutView(){
        if(stockGroup!=null){
            LinearLayout layout = new LinearLayout(CreateZuheThirdActivity.this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.VERTICAL);
            for (StockGroup group:stockGroup){
                GupiaoZuheGroupView groupView = new GupiaoZuheGroupView(CreateZuheThirdActivity.this);
                groupView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                groupView.setData(group, delOnclick);
                layout.addView(groupView);
            }
            create_zuhe_third_gupiao_ScrollView.removeAllViews();
            create_zuhe_third_gupiao_ScrollView.addView(layout);
            create_zuhe_third_gupiao_ScrollView.setVisibility(View.VISIBLE);
            create_zuhe_third_space.setVisibility(View.GONE);
        }
        if(StringUtils.isNotEmpty(cashPercent)){
            create_zuhe_bottom_tools_txt.setText(cashPercent);
            create_zuhe_bottom_tools_progressbar.setProgress(getMoney());
            create_zuhe_bottom_tools_layout.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener delOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag()!=null){
                if(v.getId() == R.id.item_view){
                    ActivityUtil.goTiaocang(CreateZuheThirdActivity.this,pid,v.getTag().toString());
                }else if(v.getId() == R.id.item_button){
                    UIShowUtil.showDialog(CreateZuheThirdActivity.this, true);
                    Http.portfolioDelStock(pid,v.getTag().toString(),new Http.Callback<Boolean>(){
                        @Override
                        public void onSuccess(Boolean obj) {
                            super.onSuccess(obj);
                            initViews();
                        }
                    });
                }
            }
        }
    };
    private int getMoney(){
        int money = 0;
        if(StringUtils.isNotEmpty(cashPercent)){
            try{
                float num = Float.parseFloat(cashPercent.substring(0,cashPercent.length()-1));
                if(num>0){
                    money = (int)(num*100);
                }
            }catch (Exception e){}
        }
        return money;
    }
}
