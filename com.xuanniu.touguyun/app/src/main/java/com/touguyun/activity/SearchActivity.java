package com.touguyun.activity;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.UserUtils;
import com.touguyun.view.Alert;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.ViewById;

import java.util.List;
/**
 * Created by zhengyonghui on 15/9/5.
 */
@EActivity(R.layout.activity_search)
public class SearchActivity extends BaseActivity{

    public static final int SEARCH_TYPE_TOUGU = 3001;       //搜索头顾
    public static final int SEARCH_TYPE_ZUHE = 3002;        //搜索组全
    public static final int SEARCH_TYPE_GUANDIAN = 3003;

    private int search_type;

    @ViewById
    EditText search_edit_txt;

    @ViewById
    ImageView search_edit_del;

    @ViewById
    LinearLayout search_history;
    @ViewById
    LinearLayout search_history_items;

    @AfterViews
    void initViews(){
        search_type = getIntent().getIntExtra("type", SEARCH_TYPE_TOUGU);
        List<String> historyList = UserUtils.getSearchHistory(search_type);
        search_edit_txt.setText("");
        if(historyList!= null && historyList.size()>0){
            search_history_items.removeAllViews();
            float dp = getDM().density;
            for (String str:historyList){
                TextView textView = new TextView(SearchActivity.this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(51.3f*dp)));
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                textView.setTextSize(16);
                textView.setTextColor(getResources().getColor(R.color.black_323232));
                textView.setPadding((int) (23 * dp), 0, (int) (23 * dp), 0);
                textView.setText(str);
                textView.setOnClickListener(clickListener);
                textView.setTag(str);
                search_history_items.addView(textView);

                View line = new View(SearchActivity.this);
                line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(0.7*dp)));
                line.setBackgroundColor(getResources().getColor(R.color.gray_D8D8D8));
                search_history_items.addView(line);
            }
            search_history.setVisibility(View.VISIBLE);
        }else{
            search_history.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(search_type == SEARCH_TYPE_TOUGU){
                ActivityUtil.goSearchTouguResult(SearchActivity.this,v.getTag().toString());
            }else if(search_type == SEARCH_TYPE_GUANDIAN){
                ActivityUtil.goSearchGuandianResult(SearchActivity.this,v.getTag().toString());
            }
        }
    };

    @Click
    void search_cancel(){
        onBackPressed();
    }

    @Click
    void search_edit_del(){
        search_edit_txt.setText("");
    }

    @Click
    void search_history_clear(){
        new Alert.Builder(SearchActivity.this)
                .setMessage("您确定要清空搜索历史吗？")
                .setLeftButton(R.string.assent,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserUtils.clearSearchHistory(search_type);
                        initViews();
                    }
                }).setLeftColor(getResources().getColor(R.color.blue_3E74F6))
                .setRightButton(R.string.cancel, null).create().show();
    }

    @EditorAction(R.id.search_edit_txt)
    void onEditorActionsOnSomeTextViews(TextView tv, int actionId) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH){
            if(search_edit_txt.length()==0){
                UIShowUtil.toast(SearchActivity.this, "请输入搜索内容");
            }else{
                UserUtils.saveSearchHistory(search_edit_txt.getText().toString(), search_type);
                if(search_type == SEARCH_TYPE_TOUGU){
                    ActivityUtil.goSearchTouguResult(SearchActivity.this,search_edit_txt.getText().toString());
                }else if(search_type == SEARCH_TYPE_GUANDIAN){
                    ActivityUtil.goSearchGuandianResult(SearchActivity.this,search_edit_txt.getText().toString());
                }
                initViews();
            }

        }
    }
    @AfterTextChange(R.id.search_edit_txt)
    void editAfterTextChange(){
        search_edit_del.setVisibility(search_edit_txt.length() > 0 ? View.VISIBLE : View.GONE);
    }
}
