package com.touguyun.activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.StockInfo;
import com.touguyun.net.Http;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.SearchGuPiaoItemView;
import com.touguyun.view.TitleBar;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.BeforeTextChange;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by zhengyonghui on 15/9/4.
 */
@EActivity(R.layout.activity_search_gupiao)
public class SearchGuPiaoActivity extends BasePullRreshActivity<StockInfo> {

    private long pid;

    private Map<String,String> chooseMap;


    @ViewById
    PullToRefreshListView refresh_list;
    @ViewById
    TitleBar touguyun_titleBar;
    @AfterViews
    void initView() {
        chooseMap = new HashMap<String,String>();
        pid = getIntent().getLongExtra("pid",0);
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.color.white);
        touguyun_titleBar.showTitle(getIntent().getStringExtra("title"));
        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
        refresh_list.setMode(PullToRefreshBase.Mode.DISABLED);
        refresh_list.setOnRefreshListener(this);
    }

    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if (isLeft) {
                onBackPressed();
            } else {
                if(chooseMap!=null && chooseMap.size()>0){
                    StringBuffer sb = new StringBuffer();
                    for(String str:chooseMap.keySet()){
                        sb.append(str+",");
                    }
                    UIShowUtil.showDialog(SearchGuPiaoActivity.this, true);
                    Http.portfolioAddStock(pid,sb.toString().substring(0,sb.length()-1),new Http.Callback<Boolean>(){
                        @Override
                        public void onSuccess(Boolean obj) {
                            super.onSuccess(obj);
                            setResult(RESULT_OK, new Intent().putExtra("return", true));
                            finish();
                        }
                        @Override
                        public void onBusinessError(int errorCode, String errorMessage) {
                            super.onBusinessError(errorCode, errorMessage);
                        }
                    });
                }else{
                    UIShowUtil.toast(SearchGuPiaoActivity.this, "还没有添加股票哦");
                }

            }
        }
    };

    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        if(view!=null && view instanceof SearchGuPiaoItemView){
            ((SearchGuPiaoItemView) view).setData(list.get(position),onClickListener,chooseMap.get(list.get(position).code)!=null);
        }else{
            view = new SearchGuPiaoItemView(SearchGuPiaoActivity.this);
            ((SearchGuPiaoItemView) view).setData(list.get(position),onClickListener,chooseMap.get(list.get(position).code)!=null);
        }
        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag()!=null){
                if(v.getId() == R.id.item_add){
                    chooseMap.put(v.getTag().toString(),"");
                }else if(v.getId() == R.id.item_cancel){
                    chooseMap.remove(v.getTag().toString());
                }
                if(adapter!=null){
                    adapter.notifyDataSetChanged();
                }
            }


        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
    @Override
    public void addLists(boolean getMore) {
    }
    @Override
    public void onRefreshComplete() {
        if(refresh_list!=null){
            refresh_list.onRefreshComplete();
        }
    }

    private Timer timer;
    private boolean isEditing;
    private String searchWord;

    @EditorAction(R.id.search_gupiao_edit)
    void onEditorActionsOnSomeTextViews(TextView tv, int actionId) {
        if(actionId == EditorInfo.IME_ACTION_SEARCH){
            if(StringUtils.isNotEmpty(tv.getText())){
                isEditing = false;
                handler.sendEmptyMessage(28);
            }else{
                UIShowUtil.toast(SearchGuPiaoActivity.this, "请输入股票代码或名称");
            }
        }
    }

    @TextChange(R.id.search_gupiao_edit)
    void gupiaoOnTextChanges() {
        isEditing = true;
    }


    @BeforeTextChange(R.id.search_gupiao_edit)
    void gupiaoBeforeTextChange(){
        isEditing = true;
    }
    @AfterTextChange(R.id.search_gupiao_edit)
    void guPiaoAfterTextChange(TextView gupiaoTxt){
        if (null!=timer) {
            timer.cancel();
        }
        isEditing=false;
        timer=new Timer();
        searchWord=gupiaoTxt.getText().toString();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(28);
            }
        }, 500);
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 28:
                    if (StringUtils.isNotEmpty(searchWord)&&!isEditing) {
                        Http.searchStock(pid, searchWord, callback);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private Http.Callback callback = new Http.Callback<List<StockInfo>>(){
        @Override
        public void onSuccess(List<StockInfo> obj) {
            super.onSuccess(obj);
            if(list == null){
                list = new ArrayList<StockInfo>();
            }
            list.clear();
            list.addAll(obj);
            if (adapter == null) {
                adapter = new RefreshAdapter();
                refresh_list.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            hasMore = false;
            onRefreshComplete();
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
    }
}
