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
import com.touguyun.activity.CommentCreateActivity;
import com.touguyun.module.Opinion;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.DateUtils;
import com.touguyun.utils.ImageLoader;
import com.touguyun.utils.ShareUtil;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.umeng.onlineconfig.OnlineConfigAgent;
/**
 * Created by zhengyonghui on 15/8/27.
 */
public class CombOpinionView extends RelativeLayout{
    private Context mContext;
    private Boolean canShowLine;
    public CombOpinionView(Context context) {
        super(context);
        initView(context);
    }
    public CombOpinionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public CombOpinionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private CircleImageView item_header;
    private TextView item_name,item_time,item_tag,item_context,item_share,item_comment,item_praise;
    private Opinion opinion;
    private View item_line;

    private String shareTitle;
    private String shareContext;

    public Opinion getData(){
        return this.opinion;
    }

    private void initView(Context context){
        this.mContext = context;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_comb_opinion, this);
        item_header = (CircleImageView)findViewById(R.id.item_header);
        item_name = (TextView)findViewById(R.id.item_name);
        item_time = (TextView)findViewById(R.id.item_time);
        item_tag = (TextView)findViewById(R.id.item_tag);
        item_context = (TextView)findViewById(R.id.item_context);
        item_share = (TextView)findViewById(R.id.item_share);
        item_comment = (TextView)findViewById(R.id.item_comment);
        item_praise = (TextView)findViewById(R.id.item_praise);
        item_line = findViewById(R.id.item_line);
        shareTitle = OnlineConfigAgent.getInstance().getConfigParams(mContext, "SHARE_OPINION_TITLE");
        shareContext = OnlineConfigAgent.getInstance().getConfigParams(mContext, "SHARE_OPINION_CONTEXT");
    }

    public void setData(Opinion opinion,boolean canShowLine){
        setData(opinion,canShowLine,null,true);
    }
    public void setData(Opinion opinion,boolean canShowLine,boolean isList){
        setData(opinion,canShowLine,null,isList);
    }
    public void setData(Opinion opinion,boolean canShowLine,OnClickListener clickListener,boolean isList){
        this.canShowLine = canShowLine;
        if(opinion!=null && opinion.id>0){
            this.opinion = opinion;
            if(opinion.user!=null){
               if(StringUtils.startWithHttp(opinion.user.userImg)){
                   ImageLoader.getInstance().showImage(opinion.user.userImg,item_header);
               }else{
                   item_header.setImageResource(R.drawable.default_header);
               }
                item_name.setText(StringUtils.isNotEmpty(opinion.user.name)?opinion.user.name:"");
                item_name.setCompoundDrawablesWithIntrinsicBounds(0,0,opinion.user.authState==1?R.drawable.tougu_v_icon:0,0);
            }else{
                item_header.setImageResource(R.drawable.default_header);
            }
            item_time.setText(StringUtils.isNotEmpty(opinion.createTime) ? DateUtils.getSectionByTime(opinion.createTime) : "");
            item_tag.setText(Html.fromHtml((StringUtils.isNotEmpty(opinion.subject) ? "<font color='#107CDB'>#" + opinion.subject + "#</font>" : "")+(StringUtils.isNotEmpty(opinion.title) ? "&nbsp;"+opinion.title : "")));
            item_context.setText(StringUtils.isNotEmpty(opinion.content) ? opinion.content : "");
            item_context.setMaxLines(isList?3:10000);
            item_share.setText("分享");
            item_comment.setText(opinion.commentNum + "");
            item_praise.setText(opinion.likeNum + "");
            item_praise.setCompoundDrawablesWithIntrinsicBounds(opinion.liked == 1 ? R.drawable.praise_red_icon : R.drawable.praise_icon, 0, 0, 0);
            if(clickListener!=null){
                item_header.setOnClickListener(clickListener);
                item_name.setOnClickListener(clickListener);
                item_share.setOnClickListener(clickListener);
                item_comment.setOnClickListener(clickListener);
                item_praise.setOnClickListener(clickListener);
            }else{
                item_header.setOnClickListener(userListener);
                item_name.setOnClickListener(userListener);
                item_share.setOnClickListener(userListener);
                item_comment.setOnClickListener(userListener);
                item_praise.setOnClickListener(userListener);
            }
            item_line.setVisibility(canShowLine?VISIBLE:GONE);


        }
    }

    private OnClickListener userListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.item_header || v.getId() == R.id.item_name){
                if(getContext()!=null && opinion!=null && opinion.user!=null&&opinion.user.uid>0){
                    ActivityUtil.goUserPage((Activity)getContext(),opinion.user.uid);
                }
            }else if(UserUtils.isLogin()){
                if(v.getId() == R.id.item_share){
                    ShareUtil.getInstance().openShare((Activity)getContext(),
                            StringUtils.isNotEmpty(shareTitle)?shareTitle:getResources().getString(R.string.share_opinion_title),
                            StringUtils.isNotEmpty(shareContext)?shareContext:getResources().getString(R.string.share_opinion_context),
                            ShareUtil.AppHost);
                }else if(v.getId() == R.id.item_comment){
                    ActivityUtil.goCommentCreate((Activity)getContext(), opinion.id, CommentCreateActivity.TYPE_TOPIC,12);
                }else if(v.getId() == R.id.item_praise){
                    if(opinion!=null && opinion.liked == 0){
                        Http.opinionLike(opinion.id, new Http.Callback<Boolean>(){
                            @Override
                            public void onSuccess(Boolean obj) {
                                super.onSuccess(obj);
                                UIShowUtil.toast(getContext(), "点赞成功");
                                if(opinion!=null){
                                    opinion.likeNum++;
                                    opinion.liked=1;
                                    setData(opinion,canShowLine);
                                }
                            }
                        });
                    }else{
                        UIShowUtil.toast(getContext(), "已赞过");
                    }
                }
            }else{
                ActivityUtil.goLogin((Activity)getContext());
            }
        }
    };
}
