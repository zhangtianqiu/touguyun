package com.touguyun.utils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.touguyun.R;
import com.touguyun.view.Alert;
import com.touguyun.view.AlertTiaocang;
import com.touguyun.view.FilletBtView;
/**
 * Created by zhengyonghui on 15/8/25.
 */
public class UIShowUtil {

    private static Dialog dialog ;

    public static void showDialog(Context context, boolean cancelable){
        showDialog(context, cancelable, context.getString(R.string.waiting));
    }
    public static void showDialog(Context context, boolean cancelable, String msg){
        cancelDialog();
        try {
//            方案一
//            dialog=new Dialog(context, R.style.CustomProgressDialog);
//            linearityLoadding loadingView=new linearityLoadding(context);
//            loadingView.init(R.drawable.loading_press_icon, R.drawable.loading_bg_icon, msg);
//            dialog.setContentView(loadingView);
//            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;

//            方案二
            dialog=new Dialog(context, R.style.CustomProgressDialog);
            dialog.setContentView(R.layout.dialog_loading_view);
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;

            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(cancelable);
            if (context instanceof Activity) {
                if (!((Activity)context).isFinishing()) {
                    dialog.show();
                }
            }else {
                dialog.show();
            }
        } catch (Exception e) {
        }
    }

    public static boolean cancelDialog(){
        boolean b=false;
        try {
            if (null!=dialog&&dialog.isShowing()) {
                dialog.cancel();
                dialog=null;
                b=true;
            }
        } catch (Exception e) {
            b=false;
        }
        return b;
    }
    public static void toast(Context context,int resId){
        toast(context, context.getString(resId));
    }

    public static void toast(Context context,String msg){
        try {
            Toast toast= Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
        }
    }
    public static void toastIcon(Context context,String text,int icon,int textColor,int textSize,int bgColor,int duration){
        try{
            FilletBtView fb=new FilletBtView(context);
            int p=(int)(10*context.getResources().getDisplayMetrics().density);
            if (StringUtils.isNotEmpty(text)){
                fb.setText(text);
                fb.setTextColor(textColor);
                fb.setCompoundDrawablePadding(p);
                fb.setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize);
            }
            fb.setFillet(p/2);
            fb.setPadding(p,p,p,p);
            fb.setGravity(Gravity.CENTER);
            fb.setBgColor(bgColor, bgColor);
            fb.setCompoundDrawablesWithIntrinsicBounds(0,icon,0,0);
            Toast toast= new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(duration);
            toast.setView(fb);
            toast.show();
        }catch (Exception e){

        }
    }

    public static void toastIcon(Context context,int icon,String text){
        toastIcon(context,text,icon, Color.WHITE,16, Color.parseColor("#aa000000"), Toast.LENGTH_SHORT);
    }

    public static void closeBroads(Activity context){
        try {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm!=null && imm.isActive()){
                imm.hideSoftInputFromWindow(context.getCurrentFocus().getApplicationWindowToken(), 0 );
            }
        } catch (Exception e) {
        }
    }

    public static void showKeyboard(Activity context,TextView view){
        try {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {
        }
    }

    private static Alert alert;
    public static void showErrorDialog(Context mContext,int messRid){
        showErrorDialog(mContext,mContext.getString(messRid));
    }
        public static void showErrorDialog(Context mContext,String mess){
        if(alert!=null && alert.isShowing()){
            alert.dismiss();
        }
        if(mContext!=null && !mContext.isRestricted()){
            alert = new Alert.Builder(mContext)
                    .setMessage(mess)
                    .setRightButton(R.string.assent,null).setRightColor(mContext.getResources().getColor(R.color.blue_3E74F6)).create();
            alert.show();
        }
    }

    private static AlertTiaocang alertTC;
    public static void showTiaoCangDialog(Context context,String title,String name,String code,String price,String count,String msg){
        if(alertTC!=null && alertTC.isShowing()){
            alertTC.dismiss();
        }
        alertTC = new AlertTiaocang.Builder(context)
                .setData(title,name,code,price,count,msg)
                .setLeftButton(R.string.assent,null)
                .setLeftColor(context.getResources().getColor(R.color.blue_107CDB))
                .setRightButton(R.string.cancel,null).create();
        alertTC.show();
    }
}
