package com.touguyun.utils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Created by zhengyonghui on 15/8/24.
 */
public class StringUtils {
    public static String encodeUTF(String str) {
        if (isNotEmpty(str)){
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        }else{
            return "";
        }
    }
    public static String decodeUTF(String str) {
        if (isNotEmpty(str)){
            try {
                return URLDecoder.decode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        }else{
            return "";
        }
    }
    public static boolean isEmpty(Object str) {
        return null == str || str.toString().length() == 0 || "null".equals(str.toString().toLowerCase()) || str.toString().trim().length() == 0;
    }

    public static String returnStr(Object str){
        return isNotEmpty(str)?str.toString():"";
    }

    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }
    public static boolean startWithHttp(Object str) {
        return isNotEmpty(str) && str.toString().toLowerCase().startsWith("http://") && str.toString().indexOf("(null)")==-1;
    }
    public static String Md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
