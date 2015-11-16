package com.touguyun.activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.DealHistory;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.NetErrorUtils;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.CircleAngleTitleView;
import com.touguyun.view.CircleImageView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
/**
 * Created by zhengyonghui on 15/8/28.
 *
 * 交易记录
 */
@EActivity(R.layout.activity_title_refresh_list)
public class TradingRecordsActivity extends BasePullRreshActivity<DealHistory>{

    @ViewById
    PullToRefreshListView refresh_list;
    @ViewById
    TitleBar touguyun_titleBar;

    private long pid;

    @ViewById
    View error_network;
    @ViewById
    View error_services;
    private NetErrorUtils netErrorUtils;

    @AfterViews
    void initView(){
        pid = getIntent().getLongExtra("pid",0);
        if(pid == 0){
            finish();
            return;
        }
        touguyun_titleBar.showTitle(R.string.trading_records_title);
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.color.white);
        refresh_list.setMode(PullToRefreshBase.Mode.DISABLED);
        refresh_list.setOnRefreshListener(this);
        if(list == null){
            list = new ArrayList<>();
        }
        netErrorUtils = new NetErrorUtils(error_network,error_services,errorOnclick,refresh_list);
        addLists(false);
    }
    private View.OnClickListener errorOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.error_network:
                    if(netErrorUtils!=null){
                        netErrorUtils.hideErrorView();
                    }
                    addLists(false);
                    break;
                case R.id.error_services:
                    if(netErrorUtils!=null){
                        netErrorUtils.hideErrorView();
                    }
                    addLists(false);
            }
        }
    };

    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        ViewHolder holder = null;
        if(list.get(position).id==-101){
            return ViewUtils.getListNullView(TradingRecordsActivity.this, R.color.white, (int) (120 * getDM().density), R.drawable.error_zuhe_icon, "暂无交易记录");
        }
        if(view != null && view.getTag()!=null && view.getTag() instanceof  ViewHolder){
            holder = (ViewHolder)view.getTag();
        }else{
            holder = new ViewHolder();
            view = LayoutInflater.from(TradingRecordsActivity.this).inflate(R.layout.list_item_trading_records, null);
            holder.item_title = (TextView) view.findViewById(R.id.item_title);
            holder.item_times = (TextView) view.findViewById(R.id.item_time);
            holder.item_buy_price = (TextView) view.findViewById(R.id.item_buy_price);
            holder.item_buy_amount = (TextView) view.findViewById(R.id.item_buy_amount);
            holder.item_buy_count = (TextView) view.findViewById(R.id.item_buy_count);
            holder.item_position = (TextView) view.findViewById(R.id.item_position);
            holder.item_contexts = (TextView) view.findViewById(R.id.item_context);
            holder.item_buy_price_title = (TextView) view.findViewById(R.id.item_buy_price_title);
            holder.item_buy_amount_title = (TextView) view.findViewById(R.id.item_buy_amount_title);
            holder.item_buy_count_title = (TextView) view.findViewById(R.id.item_buy_count_title);
            holder.item_tag = (CircleAngleTitleView) view.findViewById(R.id.item_tag);
            holder.item_header = (CircleImageView) view.findViewById(R.id.item_header);
            holder.item_layout = (RelativeLayout) view.findViewById(R.id.item_layout);
            holder.item_view = view.findViewById(R.id.item_view);

        }
        if(list!=null && list.get(position)!=null){
            holder.item_title.setText(StringUtils.returnStr(list.get(position).stockName));
            holder.item_times.setText(StringUtils.returnStr(list.get(position).time));
            holder.item_buy_price.setText(StringUtils.returnStr(list.get(position).price));
            holder.item_buy_amount.setText(list.get(position).count+"");
            holder.item_buy_count.setText(list.get(position).count+"股");
            holder.item_position.setText(StringUtils.returnStr(list.get(position).beforePercent)+StringUtils.returnStr(list.get(position).beforePercent));
            holder.item_contexts.setText(StringUtils.returnStr(list.get(position).remark));
            holder.item_tag.setVisibility(View.VISIBLE);
            if(list.get(position).type == 1){
                holder.item_tag.setText("买");
                holder.item_tag.setBackAndFrameColor(getResources().getColor(R.color.red_FD4E4E));
                holder.item_buy_price_title.setText(R.string.trading_records_buy_price);
                holder.item_buy_amount_title.setText(R.string.trading_records_buy_amount);
                holder.item_buy_count_title.setText(R.string.trading_records_buy_count);
            }else if(list.get(position).type == 2){
                holder.item_tag.setText("卖");
                holder.item_tag.setBackAndFrameColor(getResources().getColor(R.color.blue_107CDB));
                holder.item_buy_price_title.setText(R.string.trading_records_sell_price);
                holder.item_buy_amount_title.setText(R.string.trading_records_sell_amount);
                holder.item_buy_count_title.setText(R.string.trading_records_sell_count);
            }else{
                holder.item_tag.setVisibility(View.GONE);
            }
            holder.item_title.setText(StringUtils.returnStr(list.get(position).stockName));
            holder.item_times.setText(StringUtils.returnStr(list.get(position).time));
            holder.item_buy_price.setText(StringUtils.returnStr(list.get(position).price));
            holder.item_buy_amount.setText(StringUtils.returnStr(list.get(position).totalPrice));
            holder.item_buy_count.setText(StringUtils.returnStr(list.get(position).count));
            holder.item_position.setText(StringUtils.returnStr(list.get(position).beforePercent)+"-"+StringUtils.returnStr(list.get(position).afterPercent));

            if(StringUtils.isNotEmpty(list.get(position).remark)){
                if(StringUtils.startWithHttp(list.get(position).userImgPath)){
                    ImageLoader.getInstance().showImage(list.get(position).userImgPath,holder.item_header);
                }else{
                    holder.item_header.setImageResource(R.drawable.default_header);
                }
                holder.item_contexts.setText(StringUtils.returnStr(list.get(position).remark));
                holder.item_view.setVisibility(View.GONE);
                holder.item_layout.setVisibility(View.VISIBLE);
            }else{
                holder.item_view.setVisibility(View.VISIBLE);
                holder.item_layout.setVisibility(View.GONE);
            }

        }
        return view;
    }


    private class ViewHolder{
        public TextView item_title,item_times,item_buy_price,item_buy_amount,item_buy_count,item_position,item_contexts;
        public TextView item_buy_price_title,item_buy_amount_title,item_buy_count_title;
        public CircleAngleTitleView item_tag;
        public CircleImageView item_header;
        public RelativeLayout item_layout;
        public View item_view;
    }

    @Override
    public void addLists(boolean getMore) {
        if(!getMore){
            lastid=0;
        }
        isclearList = !getMore;
        UIShowUtil.showDialog(TradingRecordsActivity.this, true);
        Http.portfolioDealHistory(lastid,pid,new Http.Callback<JSONObject>(){
            @Override
            public void onSuccess(JSONObject obj) {
                super.onSuccess(obj);
                if(isclearList){
                    list.clear();
                }
                if(obj!=null){
                    if(StringUtils.isNotEmpty(obj.get("nextPageFlag"))){
                        lastid = obj.getLongValue("nextPageFlag");
                    }else{
                        hasMore = false;
                    }
                    if(StringUtils.isNotEmpty(obj.get("list"))){
                        list.addAll(TouguJsonObject.parseList(obj.getJSONArray("list"),DealHistory.class));
                    }
                }
                if(list.size() == 0){
                    list.add(TouguJsonObject.parseObject("{id:-101}",DealHistory.class));
                }
                if(adapter == null){
                    adapter = new RefreshAdapter();
                    refresh_list.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                onRefreshComplete();

            }
            @Override
            public void onNetworkError(VolleyError error) {
                super.onNetworkError(error);
                if(netErrorUtils!=null){
                    netErrorUtils.showNetError(true);
                }
            }
            @Override
            public void onBusinessError(int errorCode, String errorMessage) {
                super.onBusinessError(errorCode, errorMessage);
                if(netErrorUtils!=null){
                    netErrorUtils.showNetError(false);
                }
            }

        });

    }
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
    }
    @Override
    public void onRefreshComplete() {
        if(refresh_list!=null){
            refresh_list.onRefreshComplete();
        }
    }
}
