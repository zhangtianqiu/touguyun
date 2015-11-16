package com.touguyun.view;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.PortFolio;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
/**
 * Created by zhengyonghui on 15/8/26.
 */
public class CombinationUserView extends RelativeLayout{
    public CombinationUserView(Context context) {
        super(context);
        initView(context);
    }
    public CombinationUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public CombinationUserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private CircleAngleTitleView comb_user_subscribe;//已订阅
    private CircleAngleTitleView comb_user_unSubscribe;//未订阅

    private CircleImageView comb_user_header;    //用户头像
    private TextView comb_user_name,comb_user_historically_returned,comb_user_create_time;

    private void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_combination_user, this);
        comb_user_name = ((TextView) findViewById(R.id.comb_user_name));
        comb_user_historically_returned = ((TextView) findViewById(R.id.comb_user_historically_returned));
        comb_user_create_time = ((TextView) findViewById(R.id.comb_user_create_time));
        comb_user_header = ((CircleImageView) findViewById(R.id.comb_user_header));
        comb_user_subscribe = ((CircleAngleTitleView) findViewById(R.id.comb_user_subscribe));
        comb_user_unSubscribe = ((CircleAngleTitleView) findViewById(R.id.comb_user_unSubscribe));
    }

    private PortFolio portFolio;

    public void setData(PortFolio portFolio){
        this.portFolio = portFolio;
        if(portFolio!=null){
            if(UserUtils.isLogin() && portFolio.hasSub == 1){
                comb_user_subscribe.setVisibility(VISIBLE);
                comb_user_unSubscribe.setVisibility(GONE);
            }else{
                comb_user_subscribe.setVisibility(GONE);
                comb_user_unSubscribe.setVisibility(VISIBLE);
            }
            comb_user_unSubscribe.setOnClickListener(onClickListener);
            if(comb_user_header!=null){
                if(StringUtils.startWithHttp(portFolio.userImgPath)){
                    ImageLoader.getInstance().showImage(portFolio.userImgPath,comb_user_header);
                }else{
                    comb_user_header.setImageResource(R.drawable.default_header);
                }
            }
            if(comb_user_name!=null){
                comb_user_name.setText(StringUtils.isNotEmpty(portFolio.userName)?portFolio.userName:"");
                comb_user_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, portFolio.authState==1?R.drawable.tougu_v_icon:0, 0);
            }
            if(comb_user_historically_returned!=null){
                comb_user_historically_returned.setText(Html.fromHtml("历史收益 <font color='#FB3636'>"
                        +(StringUtils.isNotEmpty(portFolio.totalProfit)?portFolio.totalProfit:"")+"</font>"));
            }
            if(comb_user_create_time!=null){
                comb_user_create_time.setText("创建时间："+(StringUtils.isNotEmpty(portFolio.createDate)?portFolio.createDate:""));
            }
        }

    }
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(UserUtils.isLogin()){
                if(portFolio!=null && portFolio.id>0){
                    UIShowUtil.showDialog(getContext(), true);
                    Http.subscribePortfolio(portFolio.id,new Http.Callback<Boolean>(){
                        @Override
                        public void onSuccess(Boolean obj) {
                            super.onSuccess(obj);
                            UIShowUtil.toast(getContext(), "订阅成功");
                            portFolio.hasSub = 1;
                            portFolio.subCount++;
                            setData(portFolio);
                        }
                    });
                }
            }else{
                ActivityUtil.goLogin((Activity)getContext());
            }
        }
    };
}
