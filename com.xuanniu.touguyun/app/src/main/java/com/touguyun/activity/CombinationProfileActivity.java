package com.touguyun.activity;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.PortFolio;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.utils.StringUtils;
import com.touguyun.view.BaseDataView;
import com.touguyun.view.CombinationUserView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/8/26.
 */@EActivity(R.layout.activity_combination_profile)
public class CombinationProfileActivity extends BaseActivity{

    private PortFolio portFolio;

    @ViewById
    BaseDataView comb_profile_basedata; //基础数据

    @ViewById
    CombinationUserView comb_profile_userview;

    @ViewById
    TextView comb_profile_idea_title;
    @ViewById
    TextView comb_profile_idea_context;
//    @ViewById
//    TextView comb_profile_stop_set_title;
//    @ViewById
//    TextView comb_profile_stop_set_context;

    @AfterViews
    void initView(){
        String portFolioStr = getIntent().getStringExtra("portFolio");
        if(StringUtils.isEmpty(portFolioStr)){
            finish();
        }
        portFolio = TouguJsonObject.parseObject(portFolioStr,PortFolio.class);
        if(portFolio!=null){
            comb_profile_basedata.setData(portFolio.netValue, portFolio.dayProfit, portFolio.position, portFolio.betaValue);
            comb_profile_userview.setData(portFolio);
            comb_profile_idea_context.setText(StringUtils.returnStr(portFolio.remark));
        }
    }

}
