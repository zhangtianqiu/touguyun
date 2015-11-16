package com.touguyun.utils;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.view.CircleImageView;
/**
 * Created by zhengyonghui on 15/8/27.
 */
public class ViewUtils {
    public static View getListNullView(Context context, int bgColorId, int paddingTop, int icon, String text) {
        if(context == null){
            return null;
        }
        View v = View.inflate(context, R.layout.view_null_data, null);
        if(v!=null){
            v.setBackgroundResource(bgColorId==0?R.color.white:bgColorId);
            v.setPadding(0, paddingTop, 0, 0);
            ImageView iconView = (ImageView) v.findViewById(R.id.view_null_icon);
            if(icon != 0){
                iconView.setImageResource(icon);
                iconView.setVisibility(View.VISIBLE);
            }else{
                iconView.setVisibility(View.GONE);
            }
            TextView textView = (TextView) v.findViewById(R.id.view_null_text);
            textView.setText(text);
        }
        return v;
    }
    public static int getTextColorByTxt(Context context, String text){
        if(text.startsWith("-")){
            return context.getResources().getColor(R.color.green_00CC00);
        }else if("0.00%".equals(text)){
            return context.getResources().getColor(R.color.black_323232);
        }else{
            return context.getResources().getColor(R.color.red_F05050);
        }
    }

}
