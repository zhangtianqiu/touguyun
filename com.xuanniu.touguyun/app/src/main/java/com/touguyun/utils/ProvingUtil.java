package com.touguyun.utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by zhengyonghui on 15/8/25.
 */
public class ProvingUtil {
    /**
     * 判断输入的内容是否为数字
     *
     * @param number
     * @return
     */
    public static boolean isNumber(String number) {
        Pattern p = Pattern.compile("^(0|[0-9][0-9]*)$");
        Matcher m = p.matcher(number);
        return m.matches();
    }


    public static boolean isMobileNO(String mobiles) {
        if (isEmpty(mobiles)) {
            return false;
        }
        return Pattern.matches("^(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$", mobiles);
    }

    public static boolean isEmpty(Object str) {
        return str == null || str.toString().length() == 0;
    }



    /**************   用户提交验证使用   ****************/

}
