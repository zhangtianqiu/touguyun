package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.MessageList;
import com.touguyun.utils.DateUtils;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.StringUtils;
/**
 * Created by zhengyonghui on 15/9/3.
 */
public class MessageMainView extends RelativeLayout{
    public MessageMainView(Context context) {
        super(context);
        initView(context);
    }
    public MessageMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public MessageMainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private ImageView item_icon;
    private CircleAngleTitleView item_new_point;
    private TextView item_title,item_context,item_time;

    private void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_message_main_view, this);
        item_icon = (ImageView)findViewById(R.id.item_icon);
        item_new_point = (CircleAngleTitleView)findViewById(R.id.item_new_point);
        item_title = (TextView)findViewById(R.id.item_title);
        item_context = (TextView)findViewById(R.id.item_context);
        item_time = (TextView)findViewById(R.id.item_time);
    }

    public void setData(MessageList messageList){
        if(messageList!=null && messageList.id!=0){
            if(StringUtils.startWithHttp(messageList.imgUrl)){
                ImageLoader.getInstance().showImage(messageList.imgUrl,item_icon);
            }else{
                item_icon.setImageResource(R.drawable.default_zuhe_icon);
            }
            item_title.setText(StringUtils.returnStr(messageList.name));
            item_new_point.setVisibility(messageList.unReadNum>0?VISIBLE:GONE);
            if(messageList.lastMessage!=null){
                item_time.setText(StringUtils.returnStr(DateUtils.getDateStr(messageList.lastMessage.postTime,"yy-MM-dd")));
                if(messageList.lastMessage.body!=null && StringUtils.isNotEmpty(messageList.lastMessage.body.title)){
                    item_context.setText(StringUtils.returnStr(messageList.lastMessage.body.title));
                }else{
                    item_context.setText("");
                }
            }else{
                item_time.setText("");
                item_context.setText("");
            }
        }
    }
}
