package com.touguyun.activity;
import com.touguyun.R;
import com.touguyun.utils.ActivityUtil;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
/**
 * Created by zhengyonghui on 15/8/28.
 */
@EActivity(R.layout.activity_combination_filter)
public class CombinationFilterActivity extends BaseActivity{
//    分页类型字典：
//    0：日收益-从高到低
//    1：周收益-从高到低
//    2：月收益-从高到低
//    3：净值-从高到低
//    4：创建时间-从晚到早
//    5：创建时间-从早到晚
//    6：交易频次-从多到少
//    7：交易频次-从少到多
//    8：订阅人数-从多到少
//    9：精选组合-按推荐时间从晚到早
//    10：激进组合-按贝塔斯值从高到低
//    11：免费组合-按id从大到小 定价为0

    @Click
    void comb_filter_item1(){
        ActivityUtil.goZuheFilterResult(CombinationFilterActivity.this,0);
        finish();
    }
    @Click
    void comb_filter_item2(){
        ActivityUtil.goZuheFilterResult(CombinationFilterActivity.this,1);
        finish();
    }
    @Click
    void comb_filter_item3(){
        ActivityUtil.goZuheFilterResult(CombinationFilterActivity.this,2);
        finish();
    }
    @Click
    void comb_filter_item4(){
        ActivityUtil.goZuheFilterResult(CombinationFilterActivity.this,3);
        finish();
    }


}
