package com.touguyun.view;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.UserUtils;
/**
 * Created by zhengyonghui on 15/8/29.
 */
public class MainBottomToolsView extends RelativeLayout{

    private Context mContext;
    private TextView indexView,touguView,combView,opinionView,meView,lastView;

    public final static int MAIN_TOOLS_TYPE_INDEX = 1001;
    public final static int MAIN_TOOLS_TYPE_TOUGU = 1002;
    public final static int MAIN_TOOLS_TYPE_COMB = 1003;
    public final static int MAIN_TOOLS_TYPE_OPINION = 1004;
    public final static int MAIN_TOOLS_TYPE_ME = 1005;

    public int lastType;

    private MainToolsListener listener;
    public void setListener(MainToolsListener listener){
        this.listener = listener;
    }

    public MainBottomToolsView(Context context) {
        super(context);
        initView(context);
    }
    public MainBottomToolsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public MainBottomToolsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        this.mContext = context;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_main_bottom_tools, this);
        indexView = (TextView)findViewById(R.id.main_bottom_tools_index);
        touguView = (TextView)findViewById(R.id.main_bottom_tools_tougu);
        combView = (TextView)findViewById(R.id.main_bottom_tools_comb);
        opinionView = (TextView)findViewById(R.id.main_bottom_tools_opinion);
        meView = (TextView)findViewById(R.id.main_bottom_tools_me);

        indexView.setOnClickListener(myOnclickListener);
        touguView.setOnClickListener(myOnclickListener);
        combView.setOnClickListener(myOnclickListener);
        opinionView.setOnClickListener(myOnclickListener);
        meView.setOnClickListener(myOnclickListener);

        if(lastType == 0){
            updateView(MAIN_TOOLS_TYPE_INDEX);
        }
    }

    public void updateView(int type){
        if(lastView!=null){
            lastView.setTextColor(mContext.getResources().getColor(R.color.black_3B3B3B));
            switch (lastType) {
                case MAIN_TOOLS_TYPE_INDEX:
                    lastView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.main_bottom_tools_index_default,0,0);
                    break;
                case MAIN_TOOLS_TYPE_TOUGU:
                    lastView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.main_bottom_tools_tougu_default,0,0);
                    break;
                case MAIN_TOOLS_TYPE_COMB:
                    lastView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.main_bottom_tools_comb_default,0,0);
                    break;
                case MAIN_TOOLS_TYPE_OPINION:
                    lastView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.main_bottom_tools_opinion_default,0,0);
                    break;
                case MAIN_TOOLS_TYPE_ME:
                    lastView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.main_bottom_tools_me_default,0,0);
                    break;
                default:
                    break;
            }
        }

        switch (type) {
            case MAIN_TOOLS_TYPE_INDEX:
                indexView.setTextColor(mContext.getResources().getColor(R.color.red_EA1717));
                indexView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.main_bottom_tools_index_checked,0,0);
                lastView = indexView;
                break;
            case MAIN_TOOLS_TYPE_TOUGU:
                touguView.setTextColor(mContext.getResources().getColor(R.color.red_EA1717));
                touguView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.main_bottom_tools_tougu_checked,0,0);
                lastView = touguView;
                break;
            case MAIN_TOOLS_TYPE_COMB:
                combView.setTextColor(mContext.getResources().getColor(R.color.red_EA1717));
                combView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.main_bottom_tools_comb_checked,0,0);
                lastView = combView;
                break;
            case MAIN_TOOLS_TYPE_OPINION:
                opinionView.setTextColor(mContext.getResources().getColor(R.color.red_EA1717));
                opinionView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.main_bottom_tools_opinion_checked,0,0);
                lastView = opinionView;
                break;
            case MAIN_TOOLS_TYPE_ME:
                meView.setTextColor(mContext.getResources().getColor(R.color.red_EA1717));
                meView.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.main_bottom_tools_me_checked,0,0);
                lastView = meView;
                break;
        }
        lastType = type;
    }

    private OnClickListener myOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int type = 0;
            switch (v.getId()) {
                case R.id.main_bottom_tools_index:
                    type = MAIN_TOOLS_TYPE_INDEX;
                    break;
                case R.id.main_bottom_tools_tougu:
                    type = MAIN_TOOLS_TYPE_TOUGU;
                    break;
                case R.id.main_bottom_tools_comb:
                    type = MAIN_TOOLS_TYPE_COMB;
                    break;
                case R.id.main_bottom_tools_opinion:
                    type = MAIN_TOOLS_TYPE_OPINION;
                    break;
                case R.id.main_bottom_tools_me:
                    if(UserUtils.isLogin()){
                        type = MAIN_TOOLS_TYPE_ME;
                    }else{
                        type = lastType;
                        ActivityUtil.goLogin((Activity)mContext);
                    }
                    break;
                default:
                    break;
            }
            if(type>0 && type!=lastType){
                updateView(type);
                if(listener!=null){
                    listener.onToolsClick(type);
                }
            }
        }
    };


    public interface MainToolsListener{
        public abstract void onToolsClick(int type);
    }
}

