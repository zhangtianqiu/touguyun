package com.touguyun.utils;
import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
public class FileUtils {
    public static void deleteFile(File file) {
        if (file == null || !file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteFile(f);
            }
        } else {
            try {
                file.delete();
            } catch (Exception e) {
            }
        }
    }
    public static long getFileSize(File file) {
        long length = 0;
        if (file == null || !file.exists())
            return length;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                length += getFileSize(f);
            }
        } else {
            length += file.length();
        }
        return length;
    }
// --Commented out by Inspection START (15/7/9 上午11:49):
//    public FileUtils() {
//        super();
//    }
// --Commented out by Inspection STOP (15/7/9 上午11:49)

    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;
    /**
     * The number of bytes in a kilobyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);
    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;
    /**
     * The number of bytes in a megabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);
    /**
     * The file copy buffer size (30 MB)
     */
    private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;
    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;
    /**
     * The number of bytes in a gigabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);
    /**
     * The number of bytes in a terabyte.
     */
    public static final long ONE_TB = ONE_KB * ONE_GB;
    /**
     * The number of bytes in a terabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);
    /**
     * The number of bytes in a petabyte.
     */
    public static final long ONE_PB = ONE_KB * ONE_TB;
    /**
     * The number of bytes in a petabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);
    /**
     * The number of bytes in an exabyte.
     */
    public static final long ONE_EB = ONE_KB * ONE_PB;
    /**
     * The number of bytes in an exabyte.
     *
     * @since 2.4
     */
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);
    /**
     * The number of bytes in a zettabyte.
     */
    public static final BigInteger ONE_ZB = BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB));
    /**
     * The number of bytes in a yottabyte.
     */
    public static final BigInteger ONE_YB = ONE_KB_BI.multiply(ONE_ZB);

// --Commented out by Inspection START (15/7/9 上午11:49):
//    public static long sizeOfDirectory(File directory) {
//        long size = 0;
//        try {
//            checkDirectory(directory);
//        } catch (IllegalArgumentException e) {
//            return size;
//        }
//        final File[] files = directory.listFiles();
//        if (files == null) { // null if security restricted
//            return 0L;
//        }
//        for (final File file : files) {
//            size += sizeOf(file);
//            if (size < 0) {
//                break;
//            }
//        }
//        return size;
//    }
// --Commented out by Inspection STOP (15/7/9 上午11:49)

// --Commented out by Inspection START (15/7/9 下午12:04):
//    /**
//     * Checks that the given {@code File} exists and is a directory.
//     *
//     * @param directory The {@code File} to check.
//     * @throws IllegalArgumentException if the given {@code File} does not exist or is not a directory.
//     */
//    private static void checkDirectory(File directory) {
//        if (!directory.exists()) {
//            throw new IllegalArgumentException(directory + " does not exist");
//        }
//        if (!directory.isDirectory()) {
//            throw new IllegalArgumentException(directory + " is not a directory");
//        }
//    }
// --Commented out by Inspection STOP (15/7/9 下午12:04)

    public static String read(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuffer sb = new StringBuffer();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

// --Commented out by Inspection START (15/7/9 上午11:49):
//    public static long sizeOf(File file) {
//        if (!file.exists()) {
//            String message = file + " does not exist";
//            throw new IllegalArgumentException(message);
//        }
//        if (file.isDirectory()) {
//            return sizeOfDirectory(file);
//        } else {
//            return file.length();
//        }
//    }
// --Commented out by Inspection STOP (15/7/9 上午11:49)
    //-----------------------------------------------------------------------

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     * If the size is over 1GB, the size is returned as the number of whole GB, i.e. the size is rounded down to the
     * nearest GB boundary.
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     * @since 2.4
     */
    // See https://issues.apache.org/jira/browse/IO-226 - should the rounding be changed?
    public static String byteCountToDisplaySize(BigInteger size) {
        String displaySize;
        if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_EB_BI)) + "E";
        } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_PB_BI)) + "P";
        } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_TB_BI)) + "T";
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_GB_BI)) + "G";
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_MB_BI)) + "M";
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0 || size.longValue() == 0) {
            displaySize = String.valueOf(size.divide(ONE_KB_BI)) + "K";
        }else{
//            displaySize = String.valueOf(size) + " bytes";
              return "0K";
        }
        return displaySize;
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     * <p>
     * If the size is over 1GB, the size is returned as the number of whole GB, i.e. the size is rounded down to the
     * nearest GB boundary.
     * </p>
     * <p>
     * Similarly for the 1MB and 1KB boundaries.
     * </p>
     *
     * @param size the number of bytes
     * @return a human-readable display value (includes units - EB, PB, TB, GB, MB, KB or bytes)
     * @see <a href="https://issues.apache.org/jira/browse/IO-226">IO-226 - should the rounding be changed?</a>
     */
    // See https://issues.apache.org/jira/browse/IO-226 - should the rounding be changed?
    public static String byteCountToDisplaySize(long size) {
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }

    private static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

// --Commented out by Inspection START (15/7/9 上午11:49):
//    public static String getMIMEType(File file) {
//        String type = "*/*";
//        String fName = file.getName();
//        //获取后缀名前的分隔符"."在fName中的位置。
//        int dotIndex = fName.lastIndexOf(".");
//        if (dotIndex < 0) {
//            return type;
//        }
//        /* 获取文件的后缀名*/
//        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
//        if (end == "") return type;
//        //在MIME和文件类型的匹配表中找到对应的MIME类型。
//        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
//            if (end.equals(MIME_MapTable[i][0]))
//                type = MIME_MapTable[i][1];
//        }
//        return type;
//    }
// --Commented out by Inspection STOP (15/7/9 上午11:49)

    public static String getTempPath() {
        File defaultfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/touguyun/temp/");
        if (!defaultfile.exists()) {
            defaultfile.mkdirs();
        }
        return defaultfile.getAbsolutePath();
    }

    public static File getWebTempCache(Context context) {
        File cacheDir = new File(context.getCacheDir(), "webview_cache");
        cacheDir.mkdirs();
        return cacheDir;

    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isEmptyFile(File file){
        return file==null||file.length()==0 || !file.canRead();
    }
    public static boolean isNotEmptyFile(File file){
        return !isEmptyFile(file);
    }
}
