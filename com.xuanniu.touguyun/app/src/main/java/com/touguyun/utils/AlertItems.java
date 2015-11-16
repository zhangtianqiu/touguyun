package com.touguyun.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.module.IdName;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by onecode on 15/6/11.
 */
public class AlertItems extends Dialog implements AdapterView.OnItemClickListener{

    private ListView listView;
    private Activity activity;
    private List<IdName> list;
    private PopupWindow cameraPop;
    private AlertItemsClick itemsClick;

    public AlertItems(Activity a) {
        super(a);
        if (null==a) {
            throw new IllegalStateException("重要参数不能为空!");
        }
        this.activity=a;
        View popWindow = View.inflate(activity, R.layout.alert_items_popu, null);
        listView=(ListView)popWindow.findViewById(R.id.alert_items_list);
        listView.setOnItemClickListener(this);
        cameraPop = new PopupWindow(popWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cameraPop.setAnimationStyle(R.style.alert_items_anim);
        cameraPop.setBackgroundDrawable(new BitmapDrawable());
        cameraPop.setOutsideTouchable(true);
        cameraPop.setFocusable(true);
        cameraPop.setTouchable(true);
        cameraPop.update();
    }

    public void show(View parent,List<IdName> data,AlertItemsClick click){
        dismiss();
        if (null!=cameraPop&&!cameraPop.isShowing()) {
            list=data;
            if (null==list){
                list=new ArrayList<IdName>();
            }
            itemsClick=click;
            cameraPop.showAtLocation(parent, Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.anim_get_photo_popu_item);
            animation.setInterpolator(new OvershootInterpolator());
            LayoutAnimationController lac = new LayoutAnimationController(animation);
            lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
            lac.setDelay(1f);
            listView.setLayoutAnimation(lac);
            listView.setAdapter(new ItemsAdapter());
        }
    }

    public void dismiss(){
        if (null!=cameraPop&&cameraPop.isShowing()) {
            cameraPop.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (null!=itemsClick&&null!=list&&i>=0&&i<list.size()){
            itemsClick.onClick(list.get(i));
        }
        dismiss();
    }

    private class ItemsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return list.get(i).id;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView fb=null;
            if (null==view||(view instanceof TextView)){
                fb=new TextView(activity);
                fb.setBackgroundResource(android.R.color.transparent);
                fb.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                fb.setGravity(Gravity.CENTER);
                fb.setTextColor(activity.getResources().getColor(R.color.black_323232));
                int p=(int)(10*activity.getResources().getDisplayMetrics().density);
                fb.setPadding(0,p,0,p);
                fb.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            }else{
                fb=(TextView)view;
            }
            fb.setText(list.get(i).name);
            return fb;
        }
    }

    public interface AlertItemsClick{
        public void onClick(IdName a);
    }
}
