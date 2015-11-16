package com.touguyun.fragment;
import android.content.DialogInterface;
import android.text.Html;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.PortfolioStock;
import com.touguyun.net.Http;
import com.touguyun.utils.ProvingUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.AlertTiaocang;
import com.touguyun.view.SellBuyTopView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
/**
 * Created by zhengyonghui on 15/9/5.
 */
@EFragment(R.layout.fragment_tiaocang_sell)
public class TiaocangSellFragment extends BaseFragment {

    @ViewById
    SellBuyTopView tiaocang_sell_top;
    @ViewById
    TextView tiaocang_sell_gupiao_name;
    @ViewById
    TextView tiaocang_sell_gupiao_price;
    @ViewById
    TextView tiaocang_sell_gupiao_dieting;
    @ViewById
    TextView tiaocang_sell_gupiao_zhangting;
    @ViewById
    TextView tiaocang_sell_gupiao_max_count;
    @ViewById
    TextView tiaocang_sell_gupiao_shizhi;
    @ViewById
    TextView tiaocang_sell_gupiao_yingkui;
    @ViewById
    TextView tiaocang_sell_gupiao_chicang;
    @ViewById
    TextView tiaocang_sell_gupiao_chengben;


    private PortfolioStock portfolioStock;
    private long pid;


    @ViewById
    TextView tiaocang_sell_gupiao_count;
    @ViewById
    TextView tiaocang_sell_gupiao_context;


    @Click
    void tiaocang_sell_gupiao_button(){
        if(StringUtils.isEmpty(tiaocang_sell_gupiao_count.getText())){
            UIShowUtil.toast(getActivity(), "请输入卖出数量");
            tiaocang_sell_gupiao_count.requestFocus();
            return ;
        }else if(!ProvingUtil.isNumber(tiaocang_sell_gupiao_count.getText().toString())) {
            UIShowUtil.toast(getActivity(), "请输入正确的卖出数量");
            tiaocang_sell_gupiao_count.requestFocus();
            return ;
        }else if(portfolioStock!=null  && Integer.parseInt(tiaocang_sell_gupiao_count.getText().toString())>portfolioStock.maxSellCount) {
            tiaocang_sell_gupiao_count.requestFocus();
            UIShowUtil.toast(getActivity(), "卖出数量超过了最大卖出量，请确认");
            return;
        }else if(Integer.parseInt(tiaocang_sell_gupiao_count.getText().toString())%100>0){
            UIShowUtil.showErrorDialog(getActivity(), "您的输入数量格式错误，必须是100的整数倍！");
            return;
        }

        if(portfolioStock!=null){
            new AlertTiaocang.Builder(getActivity())
                    .setData("卖出确认",portfolioStock.name,portfolioStock.code,portfolioStock.nowPrice,tiaocang_sell_gupiao_count.getText().toString(),"提示：成交价格以实时价格为准。")
                    .setLeftButton(R.string.assent,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UIShowUtil.showDialog(getActivity(), true);
                            Http.portfolioSellStock(pid, portfolioStock.code, Integer.parseInt(tiaocang_sell_gupiao_count.getText().toString()),
                                    StringUtils.isNotEmpty(tiaocang_sell_gupiao_context.getText()) ? tiaocang_sell_gupiao_context.getText().toString() : "", sellCallback);
                        }
                    })
                    .setLeftColor(getResources().getColor(R.color.blue_107CDB))
                    .setRightButton(R.string.cancel,null).create().show();
        }
    }

    private Http.Callback sellCallback = new Http.Callback<Boolean>(){
        @Override
        public void onSuccess(Boolean obj) {
            super.onSuccess(obj);
            getActivity().finish();
        }
    };





    @Override
    public void onHttpError(boolean isNet, String methodName, int errorType, String msg) {
    }

    public void setDate(PortfolioStock portfolioStock,long pid){
        if(portfolioStock!=null){
            this.portfolioStock = portfolioStock;
            this.pid = pid;
            tiaocang_sell_top.setData(portfolioStock.getSellAndBuy());
            tiaocang_sell_gupiao_name.setText(StringUtils.returnStr(portfolioStock.code) + "  " + StringUtils.returnStr(portfolioStock.name));
            tiaocang_sell_gupiao_price.setText(StringUtils.returnStr(portfolioStock.nowPrice));
            tiaocang_sell_gupiao_dieting.setText(Html.fromHtml("跌停 <font color='#00CC00'>" + StringUtils.returnStr(portfolioStock.lowPrice) + "</font>"));
            tiaocang_sell_gupiao_zhangting.setText(Html.fromHtml("涨停 <font color='#FD2828'>"+StringUtils.returnStr(portfolioStock.highPrice)+"</font>"));
            tiaocang_sell_gupiao_max_count.setText(Html.fromHtml("可卖<font color='#FD2828'>"+portfolioStock.maxSellCount+"</font>股"));
            tiaocang_sell_gupiao_shizhi.setText(StringUtils.returnStr(portfolioStock.asset));
            tiaocang_sell_gupiao_chicang.setText(portfolioStock.count+"");
            tiaocang_sell_gupiao_yingkui.setText(StringUtils.returnStr(portfolioStock.profit));
            tiaocang_sell_gupiao_chengben.setText(StringUtils.returnStr(portfolioStock.buyPrice)+"/"+StringUtils.returnStr(portfolioStock.nowPrice));
        }
    }
}
