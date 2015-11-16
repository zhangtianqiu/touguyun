package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touguyun.R;

/**
 * Created by zhengyonghui on 15/8/26.
 */
public class BaseDataView extends LinearLayout{

    public enum ACTION
    {
        NET_WORTH, DAILY_INCOME, POSITION, BETA_VALUE
    }

    public BaseDataView(Context context) {
        super(context);
        initView(context);
    }
    public BaseDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public BaseDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private TextView view_base_net_worth,view_base_daily_income,view_base_position,view_base_beta_value;
    private void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_basedata, this);
        view_base_net_worth = ((TextView) findViewById(R.id.view_base_net_worth));
        view_base_daily_income = ((TextView) findViewById(R.id.view_base_daily_income));
        view_base_position = ((TextView) findViewById(R.id.view_base_position));
        view_base_beta_value = ((TextView) findViewById(R.id.view_base_beta_value));
    }

    public void setData(String netWorth,String dailyIncome,String position,String betaValue){
         if(view_base_net_worth!=null){
             view_base_net_worth.setText(netWorth);
         }
         if(view_base_daily_income!=null){
             view_base_daily_income.setText(dailyIncome);
         }
         if(view_base_position!=null){
             view_base_position.setText(position);
         }
         if(view_base_beta_value!=null){
             view_base_beta_value.setText(betaValue);
         }
    }

    public String getData(ACTION action){
        if(action == ACTION.NET_WORTH){
            return view_base_net_worth!=null?view_base_net_worth.getText().toString():"";
        }else if(action == ACTION.DAILY_INCOME){
            return view_base_daily_income!=null?view_base_daily_income.getText().toString():"";
        }else if(action == ACTION.POSITION){
            return view_base_position!=null?view_base_position.getText().toString():"";
        }else if(action == ACTION.BETA_VALUE){
            return view_base_beta_value!=null?view_base_beta_value.getText().toString():"";
        }else{
            return "";
        }
    }

}
