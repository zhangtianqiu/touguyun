package com.touguyun.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
public class SDCardUtils {

    /**
     * 判断存储卡是否可用
     * true为可用
     *
     * @return
     */
    public static boolean hasSDCard() {
        File path = Environment.getExternalStorageDirectory();
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                && path != null && path.canWrite() && path.canRead();
    }

    /**
     * 判断存储卡是否可用
     * true为可用
     *
     * @return
     */
    public static boolean hasSDCardWithToast(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            Toast.makeText(context, "存储卡已被拔除，将暂时无法使用语音、图片等功能", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

// --Commented out by Inspection START (15/7/9 上午11:40):
//    /**
//     * 查看SD卡的剩余空间
//     *
//     * @return
//     */
//    public long getSDFreeSize() {
//        //取得SD卡文件路径
//        File path = Environment.getExternalStorageDirectory();
//        StatFs sf = new StatFs(path.getPath());
//        //获取单个数据块的大小(Byte)
//        long blockSize = sf.getBlockSize();
//        //空闲的数据块的数量
//        long freeBlocks = sf.getAvailableBlocks();
//        //返回SD卡空闲大小
//        //return freeBlocks * blockSize;  //单位Byte
//        //return (freeBlocks * blockSize)/1024;   //单位KB
//        return (freeBlocks * blockSize) / 1024 / 1024; //单位MB
//    }
// --Commented out by Inspection STOP (15/7/9 上午11:40)

// --Commented out by Inspection START (15/7/9 上午11:40):
//    /**
//     * 查看SD卡总容量
//     *
//     * @return
//     */
//    public long getSDAllSize() {
//        // 取得SD卡文件路径
//        File path = Environment.getExternalStorageDirectory();
//        StatFs sf = new StatFs(path.getPath());
//        // 获取单个数据块的大小(Byte)
//        long blockSize = sf.getBlockSize();
//        // 获取所有数据块数
//        long allBlocks = sf.getBlockCount();
//        // 返回SD卡大小
//        // return allBlocks * blockSize; //单位Byte
//        // return (allBlocks * blockSize)/1024; //单位KB
//        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
//    }
// --Commented out by Inspection STOP (15/7/9 上午11:40)

}
