package com.touguyun.utils;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by zhengyonghui on 15/9/7.
 */
public class DateUtils {
    /**
     * 按要求格式返回当前时间
     * @param dataType  "yy-MM-dd","yy-MM-dd HH:mm:ss"等
     * @return
     */
    public static String getDateStrNow(String dataType) {
        SimpleDateFormat formatter = new SimpleDateFormat(dataType);
        return formatter.format(new Date());
    }

    public static String getDateStr(long time,String dataType) {
        SimpleDateFormat formatter = new SimpleDateFormat(dataType);
        return formatter.format(new Date(time));
    }

    public static String getSectionByTime(long secondDiff){
        long subsecond = System.currentTimeMillis()-secondDiff;
        if (subsecond >= 86400*1000) {
            return getDateStr(secondDiff,"MM-dd HH:mm");
        } else if (subsecond >= (3600*1000)) {
            int hour = (int) (subsecond / (3600*1000));
            return hour + "小时前";
        } else if (subsecond >= 60) {
            int minute = (int) (subsecond / (60*1000));
            return minute + "分钟前";
        } else if (subsecond > 0) {
            return  subsecond + "秒前";
        }
        return "";
    }


    /**
     * 返回5天23小时43分20秒
     * @param secondDiff
     * @param isContinue
     * @return
     */
    public static String getReckonByTime(long secondDiff, boolean isContinue) {
        String time = "";
        if (secondDiff >= 86400*1000) {
            int day = (int) (secondDiff / (86400*1000));
            time = day + "天";
            if (isContinue) {
                time += getReckonByTime(secondDiff % (86400*1000), isContinue);
            }
        } else if (secondDiff >= (3600*1000)) {
            int hour = (int) (secondDiff / (3600*1000));
            time = hour + "小时";
            if (isContinue) {
                time += getReckonByTime(secondDiff % (3600*1000), isContinue);
            }
        } else if (secondDiff >= 60) {
            int minute = (int) (secondDiff / (60*1000));
            time = minute + "分";
            if (isContinue) {
                time += getReckonByTime(secondDiff % (60*1000), isContinue);
            }
        } else if (secondDiff > 0) {
            time = secondDiff + "秒";
        }
        return time;
    }
}
