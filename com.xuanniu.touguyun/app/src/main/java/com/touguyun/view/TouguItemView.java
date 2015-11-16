package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.Consultant;
import com.touguyun.module.User;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.ViewUtils;
/**
 * Created by zhengyonghui on 15/9/2.
 */
public class TouguItemView extends RelativeLayout{
    public TouguItemView(Context context) {
        super(context);
        initView(context);
    }
    public TouguItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public TouguItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private TextView item_title,item_name,item_fans,item_profit;
    private CircleImageView item_header;


    public void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_tougu_view, this);
        item_title = (TextView)findViewById(R.id.item_title);
        item_header = (CircleImageView)findViewById(R.id.item_header);
        item_name = (TextView)findViewById(R.id.item_name);
        item_fans = (TextView)findViewById(R.id.item_fans);
        item_profit = (TextView)findViewById(R.id.item_profit);

    }

    private Consultant consultant;

    public Consultant getData(){
        return this.consultant;
    }

    public void setData(Consultant consultant,int position){
        this.consultant = consultant;
        if(position < 0){
            item_title.setVisibility(GONE);
        }else{
            item_title.setText(position+"");
            if(position==1){
                item_title.setBackgroundColor(getResources().getColor(R.color.red_FB3636));
            }else if(position == 2){
                item_title.setBackgroundColor(getResources().getColor(R.color.orange_FF8E52));
            }else if(position == 3){
                item_title.setBackgroundColor(getResources().getColor(R.color.yellow_FFCC66));
            }else{
                item_title.setBackgroundColor(getResources().getColor(R.color.gray_D8D8D8));
            }
            item_title.setVisibility(VISIBLE);
        }
        if(consultant!=null){
             if(StringUtils.startWithHttp(consultant.userImg)){
                 ImageLoader.getInstance().showImage(consultant.userImg,item_header);
             }else{
                 item_header.setImageResource(R.drawable.default_header);
             }
            item_name.setText(StringUtils.returnStr(consultant.name));
            item_name.setCompoundDrawablesWithIntrinsicBounds(0,0,consultant.authState == User.USER_TYPE_TOUGU?R.drawable.tougu_v_icon:0,0);
            item_fans.setText(StringUtils.returnStr(consultant.fansNum));
            item_profit.setText(StringUtils.returnStr(consultant.profitSum));
            item_profit.setTextColor(ViewUtils.getTextColorByTxt(getContext(),consultant.profitSum));
        }
    }

    public void setLeftAndRightPadding(int padding){
        this.setPadding(padding,0,padding,0);
    }

}
