package com.touguyun.activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.module.Consultant;
import com.touguyun.module.ListModule;
import com.touguyun.module.TouguJsonObject;
import com.touguyun.net.Http;
import com.touguyun.utils.ActivityUtil;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.utils.ViewUtils;
import com.touguyun.view.Alert;
import com.touguyun.view.TitleBar;
import com.touguyun.view.TouguItemView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
/**
 * Created by zhengyonghui on 15/9/2.
 */
@EActivity(R.layout.activity_title_refresh_list)
public class MyTouguActivity extends BasePullRreshActivity<Consultant> {
    @ViewById
    PullToRefreshListView refresh_list;
    @ViewById
    TitleBar touguyun_titleBar;


    @AfterViews
    void initView() {
        mListView = refresh_list.getRefreshableView();
        mListView.setSelector(R.drawable.list_item_selector_bg);
        touguyun_titleBar.showTitle(R.string.my_tougu_title);
//        touguyun_titleBar.showRight("", R.drawable.search_black_icon);
//        touguyun_titleBar.setTitleBarClickListener(titleBarClickListener);
        refresh_list.setMode(PullToRefreshBase.Mode.BOTH);
        refresh_list.setOnRefreshListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(itemLongClickListener);
        if(list == null){
            list = new ArrayList<>();
        }
        addLists(false);
    }


//    private TitleBar.TitleBarClickListener titleBarClickListener = new TitleBar.TitleBarClickListener() {
//        @Override
//        public void onBarClick(boolean isLeft) {
//            if (isLeft) {
//                onBackPressed();
//            } else {
//                ActivityUtil.goSearch(MyTouguActivity.this,SearchActivity.SEARCH_TYPE_TOUGU);
//            }
//        }
//    };
    @Override
    public View getItemView(int position, View view, ViewGroup group) {
        if((list.get(position)).id == -101) {
            view = ViewUtils.getListNullView(MyTouguActivity.this, R.color.white, (int) (60 * getDM().density), R.drawable.error_zuhe_icon, "暂无投顾信息");
        }else if (view != null && view instanceof TouguItemView) {
            ((TouguItemView) view).setData(list.get(position), -1);
        } else {
            view = new TouguItemView(MyTouguActivity.this);
            ((TouguItemView) view).setData(list.get(position), -1);
            view.setPadding((int)(23*getDM().density),0,(int)(23*getDM().density),0);
        }
        return view;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(list!=null && list.get(position-1)!=null && list.get(position-1).id!=-101){
            ActivityUtil.goUserPage(MyTouguActivity.this, list.get(position - 1).uid);
        }
    }

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if(list!=null && list.get(position-1)!=null && list.get(position-1).id!=-101){
                longClickPosition = position - 1;
                cancelDingyue();
            }
            return true;
        }
    };

    private int longClickPosition;

    private void cancelDingyue(){
        if(list!=null && list.size()>longClickPosition){
            new Alert.Builder(MyTouguActivity.this)
                    .setMessage("是否取消关注 "+list.get(longClickPosition).name)
                    .setLeftButton(R.string.assent,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UIShowUtil.showDialog(MyTouguActivity.this, true);
                            Http.attentionCancel(list.get(longClickPosition).uid,cancelCallback);
                        }
                    })
                    .setRightButton(R.string.cancel,null)
                    .create().show();
        }
    }
    private Http.Callback cancelCallback = new Http.Callback<Boolean>(){
        @Override
        public void onSuccess(Boolean obj) {
            super.onSuccess(obj);
            if(obj){
                if(list!=null && list.size()>longClickPosition){
                    list.remove(longClickPosition);
                }
                if(adapter!=null){
                    adapter.notifyDataSetChanged();
                }

            }
        }
    };

    @Override
    public void addLists(boolean getMore) {
        isclearList = !getMore;
        UIShowUtil.showDialog(MyTouguActivity.this, true);
        Http.myConsultantList(getMore ? lastid : 0, callback);
    }

    private Http.Callback callback = new Http.Callback<ListModule>(){
        @Override
        public void onSuccess(ListModule obj) {
            super.onSuccess(obj);
            if(isclearList){
                list.clear();
            }
            if(obj!=null){
                lastid = obj.nextPageFlag;
                hasMore = obj.nextPageFlag!=0;
                if(obj.list!=null){
                    list.addAll(TouguJsonObject.parseList(obj.list,Consultant.class));
                }
            }
            if(list.size() == 0){
                list.add(TouguJsonObject.parseObject("{id:-101}", Consultant.class));
            }
            if(adapter == null){
                adapter = new RefreshAdapter();
                refresh_list.setAdapter(adapter);
            }
            adapter.notifyDataSetChanged();
            onRefreshComplete();
        }
    };

    @Override
    public void onRefreshComplete() {
        if(refresh_list!=null){
            refresh_list.onRefreshComplete();
        }
    }

}
