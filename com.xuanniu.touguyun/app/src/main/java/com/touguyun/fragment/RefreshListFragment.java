package com.touguyun.fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.touguyun.R;
import com.touguyun.net.Http;
import com.touguyun.utils.UIShowUtil;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhengyonghui on 15/9/1.
 */
public abstract class RefreshListFragment<T> extends BaseFragment  implements PullToRefreshBase.OnRefreshListener2<ListView>,AdapterView.OnItemClickListener {
    public int nowPage;
    public List<T> list;
    public long lastid;
    public View fragmentView;
    public ListView listView;
    public MyAdapter adapter;
    public PullToRefreshListView refreshView;
    public boolean hasMore = true, isclearList = false;
    public abstract View createFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    public abstract void initView();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != fragmentView) {
            ViewGroup group = (ViewGroup) fragmentView.getParent();
            if (group != null) {
                group.removeView(fragmentView);
            }
        } else {
            fragmentView = createFragmentView(inflater, container, savedInstanceState);
        }
        if (null == refreshView) {
            refreshView = (PullToRefreshListView) fragmentView.findViewById(R.id.refresh_list);
        }
        if (null == listView) {
            listView = refreshView.getRefreshableView();
        }
        if (null == list) {
            list = new ArrayList<T>();
        }
        refreshView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        initView();
        return fragmentView;
    }
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        hasMore = true;
        isclearList = false;
        isLoading = true;
        loadData(false);
    }
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (hasMore) {
            loadData(true);
            isclearList = false;
            isLoading = true;
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UIShowUtil.toast(getActivity(), "已无更多");
                    RefreshListFragment.this.refreshView.onRefreshComplete();
                }
            }, 1000);
        }
    }
    public void onDataSuccess(List<T> newData, List<T> headNull) {
        if (null == list) {
            list = new ArrayList<T>();
        }
        if (isclearList) {
            list.clear();
            if (null != headNull && headNull.size() > 0) {
                list.addAll(headNull);
            }
        }
        hasMore = null != newData && newData.size() >= Http.COUNT;
        if (null != newData && newData.size() > 0) {
            list.addAll(newData);
        }
        refreshView.onRefreshComplete();
        refreshView.setMode(hasMore ? PullToRefreshBase.Mode.BOTH : PullToRefreshBase.Mode.PULL_FROM_START);
        if (null == adapter) {
            adapter = new MyAdapter(list);
            refreshView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        isLoading = false;
        UIShowUtil.cancelDialog();
    }
    public abstract void loadData(boolean getMore);
    public abstract View getItemView(int position, View view, ViewGroup group);
    public int getItemViewType(int position) {
        return 0;
    }
    public int getViewTypeCount() {
        return 1;
    }
    public class MyAdapter extends BaseAdapter {
        private List<T> list;
        public MyAdapter(List<T> list) {
            this.list = list;
            if (this.list == null) {
                this.list = new ArrayList<T>();
            }
        }
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }
        @Override
        public int getItemViewType(int position) {
            return RefreshListFragment.this.getItemViewType(position);
        }
        @Override
        public int getViewTypeCount() {
            return RefreshListFragment.this.getViewTypeCount();
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View view, ViewGroup group) {
            return RefreshListFragment.this.getItemView(position, view, group);
        }
    }
    @Override
    public void onHttpError(boolean isNet, String methodName, int errorType, String msg) {
        refreshView.onRefreshComplete();
        isLoading = false;
    }
}