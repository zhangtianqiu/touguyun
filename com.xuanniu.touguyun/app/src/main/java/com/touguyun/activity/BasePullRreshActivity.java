package com.touguyun.activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.touguyun.utils.UIShowUtil;

import java.util.List;
/**
 * Created by zhengyonghui on 15/8/27.
 */
public abstract class BasePullRreshActivity<T> extends BaseActivity implements PullToRefreshBase.OnRefreshListener2,AdapterView.OnItemClickListener {

    public List<T> list;
    public long lastid ;
    public RefreshAdapter adapter;
    public ListView mListView;
    public boolean hasMore = true,isclearList = false;


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        hasMore = true;
        addLists(false);
    }
    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        if(hasMore){
            addLists(true);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UIShowUtil.toast(BasePullRreshActivity.this, "已无更多");
                    onRefreshComplete();
                }
            }, 1000);
        }
    }
    public class RefreshAdapter extends BaseAdapter {

        public RefreshAdapter() {
        }
        @Override
        public int getCount() {
            return list!=null?list.size():0;
        }
        @Override
        public Object getItem(int position) {
            return list!=null?list.get(position):null;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItemView(position,convertView,parent);
        }

    }


    public abstract View getItemView(int position, View view, ViewGroup group);
    public abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);
    public abstract void addLists(boolean getMore);
    public abstract void onRefreshComplete();
}
