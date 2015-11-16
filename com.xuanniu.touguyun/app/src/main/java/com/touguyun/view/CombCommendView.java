package com.touguyun.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.Comment;
import com.touguyun.utils.DateUtils;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.StringUtils;

/**
 * Created by zhengyonghui on 15/8/27.
 */
public class CombCommendView extends RelativeLayout{
    public CombCommendView(Context context) {
        super(context);
        initView(context);
    }
    public CombCommendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public CombCommendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private CircleImageView item_header;
    private TextView item_name,item_time,item_context;
    private void initView(Context context){
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_comb_comment, this);
        item_name = ((TextView) findViewById(R.id.item_name));
        item_time = ((TextView) findViewById(R.id.item_time));
        item_context = ((TextView) findViewById(R.id.item_context));
        item_header = ((CircleImageView) findViewById(R.id.item_header));
    }

    public void setData(Comment comment){
        if(comment!=null){
            if(comment.user!=null){
                if(StringUtils.startWithHttp(comment.user.userImg)){
                    ImageLoader.getInstance().showImage(comment.user.userImg,item_header);
                }else{
                    item_header.setImageResource(R.drawable.default_header);
                }
                item_name.setText(StringUtils.isNotEmpty(comment.user.name)?comment.user.name:"");
            }else{
                item_header.setImageResource(R.drawable.default_header);
                item_name.setText("");
            }
            item_time.setText(DateUtils.getSectionByTime(comment.createTime));
            item_context.setText(StringUtils.isNotEmpty(comment.content)?comment.content:"");
        }
    }
}
