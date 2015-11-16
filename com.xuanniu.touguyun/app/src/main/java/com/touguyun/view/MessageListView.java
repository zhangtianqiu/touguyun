package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.Message;
import com.touguyun.module.MessageList;
import com.touguyun.utils.DateUtils;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.StringUtils;
/**
 * Created by zhengyonghui on 15/9/3.
 */
public class MessageListView extends RelativeLayout{
    public MessageListView(Context context) {
        super(context);
        initView(context);
    }
    public MessageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public MessageListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private TextView item_title,item_context,item_time;

    private void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_message_view, this);
        item_title = (TextView)findViewById(R.id.item_title);
        item_context = (TextView)findViewById(R.id.item_context);
        item_time = (TextView)findViewById(R.id.item_time);
    }

    public void setData(Message message){
        if(message!=null && message.mid!=0 && message.body!=null){
            item_title.setText(StringUtils.returnStr(message.body.title));
            item_context.setText(StringUtils.returnStr(message.body.content));
            item_time.setText(DateUtils.getDateStr(message.postTime,"MM-dd HH:mm"));
        }
    }
}
