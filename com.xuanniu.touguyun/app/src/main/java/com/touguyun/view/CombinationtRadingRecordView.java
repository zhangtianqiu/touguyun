package com.touguyun.view;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.CombPositionRecords;
import com.touguyun.module.CombRecords;
import com.touguyun.module.StockGroup;
import com.touguyun.module.StockInfo;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ViewUtils;

import java.util.List;
/**
 * Created by zhengyonghui on 15/8/27.
 */
public class CombinationtRadingRecordView extends LinearLayout{

    private Context mContext;


    public CombinationtRadingRecordView(Context context) {
        super(context);
        initView(context);
    }
    public CombinationtRadingRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public CombinationtRadingRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private LinearLayout item_context;
    private TextView item_next;
    private void initView(Context context){
        mContext = context;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_trading_records, this);
        item_next = (TextView)findViewById(R.id.item_next);
        item_context = (LinearLayout)findViewById(R.id.item_context);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.item_next && v.getTag()!=null){
                ActivityUtil.goTradingRecords((Activity)getContext(),Long.parseLong(v.getTag().toString()));
            }
        }
    };

    public void setData(List<StockGroup> dataList,long pid){
        if(dataList!=null && item_context!=null){
            item_context.removeAllViews();
            if(dataList!=null && dataList.size()>0){
                for (int i=0;i<dataList.size();i++){
                    if(dataList.get(i)!=null){
                        CombinedPositionTitleView titleView = new CombinedPositionTitleView(mContext);
                        titleView.setData(i,dataList.get(i).percent,dataList.get(i).plate);
                        item_context.addView(titleView);
                        if(dataList.get(i).stockInfos!=null){
                            for (StockInfo stockInfo:dataList.get(i).stockInfos){
                                if(stockInfo!=null){
                                    CombinedPositionItemView itemView = new CombinedPositionItemView(mContext);
                                    itemView.setdata(stockInfo.name,stockInfo.percent,stockInfo.createTime);
                                    item_context.addView(itemView);
                                }
                            }
                        }
                    }
                }
            }else{
                float dp = getResources().getDisplayMetrics().density;
                item_context.addView(ViewUtils.getListNullView(getContext(), R.color.white, (int) (30 * dp), 0, "暂无组合仓位"));
            }
        }
        if(pid>0){
            item_next.setTag(pid);
            item_next.setOnClickListener(clickListener);
        }
    }
}
