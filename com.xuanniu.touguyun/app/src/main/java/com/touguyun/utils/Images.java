package com.touguyun.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.opengl.GLES10;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
public class Images {

    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";

    private static final int SIZE_DEFAULT = 2048;
    private static final int SIZE_LIMIT = 4096;
    /**
     * 保存图片
     *
     * @param mBitmap
     * @param path
     * @return
     */
    public static String saveMyBitmap(Bitmap mBitmap, String path) {
        return saveMyBitmap(mBitmap, path, 95);
    }

    /**
     * 保存图片
     *
     * @param mBitmap
     * @param path
     * @return
     */
    public static String saveMyBitmap(Bitmap mBitmap, String path, int quality) {
        if (mBitmap == null) {
            return null;
        }
        File f = null;
        if (path.startsWith("/")) {
            f = new File(path);
        } else {
            f = new File(FileUtils.getTempPath(), path);
        }
        FileOutputStream fOut = null;
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError out) {
            out.printStackTrace();
        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException e) {
                }
            }
        }
        if (f != null) {
            return f.getPath();
        } else {
            return path;
        }
    }


    public static File getFromMediaUri(Context context, ContentResolver resolver, Uri uri) {
        if (uri == null) return null;

        if (SCHEME_FILE.equals(uri.getScheme())) {
            return new File(uri.getPath());
        } else if (SCHEME_CONTENT.equals(uri.getScheme())) {
            final String[] filePathColumn = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME };
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = (uri.toString().startsWith("content://com.google.android.gallery3d")) ?
                            cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) :
                            cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    // Picasa images on API 13+
                    if (columnIndex != -1) {
                        String filePath = cursor.getString(columnIndex);
                        if (!TextUtils.isEmpty(filePath)) {
                            return new File(filePath);
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                // Google Drive images
                return getFromMediaUriPfd(context, resolver, uri);
            } catch (SecurityException ignored) {
                // Nothing we can do
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        return null;
    }
    private static File getFromMediaUriPfd(Context context, ContentResolver resolver, Uri uri) {
        if (uri == null) return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(context);
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return new File(tempFilename);
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile(FileUtils.getTempPath(), "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }
    public static void closeSilently(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }
    public static Bitmap decodeImageWithUri(Context context,Uri sourceUri){
        if (sourceUri != null) {
            InputStream is = null;
            try {
                int sampleSize = calculateBitmapSampleSize(context,sourceUri);
                is = context.getContentResolver().openInputStream(sourceUri);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = sampleSize;
                return BitmapFactory.decodeStream(is, null, option);
            } catch (IOException e) {
                Log.e("Error reading image: ", e.getMessage());
            } catch (OutOfMemoryError e) {
                Log.e("OOM reading image: ", e.getMessage());
            } finally {
                closeSilently(is);
            }
        }
        return null;
    }
    private static int calculateBitmapSampleSize(Context context,Uri bitmapUri) throws IOException {
        InputStream is = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            is = context.getContentResolver().openInputStream(bitmapUri);
            BitmapFactory.decodeStream(is, null, options); // Just get image size
        } finally {
            if(is!=null){
                try {
                    is.close();
                }catch (Exception e){
                }
            }
        }

        int maxSize = getMaxImageSize();
        int sampleSize = 1;
        while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
            sampleSize = sampleSize << 1;
        }
        return sampleSize;
    }
    private static int getMaxImageSize() {
        int textureLimit = getMaxTextureSize();
        if (textureLimit == 0) {
            return SIZE_DEFAULT;
        } else {
            return Math.min(textureLimit, SIZE_LIMIT);
        }
    }
    private static int getMaxTextureSize() {
        // The OpenGL texture size is the maximum size that can be drawn in an ImageView
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    /**
     * 取图片压缩
     *
     * @param fileName
     * @param maxSize
     * @param limit    为true时，最大边为maxSize，flase时，最小边为maxSize；
     * @return
     */
    public static Bitmap decodeFile(String fileName, int maxSize, boolean limit) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        File f = null;
        if (fileName.startsWith("/")) {
            f = new File(fileName);
        } else {
            f = new File(FileUtils.getTempPath(), fileName);
        }
        int degree = readPictureDegree(f.getAbsolutePath());
        if (!f.exists()) {
            return null;
        }
        Bitmap b = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            if (maxSize == 0 || maxSize > 3000) {
                maxSize = 3000;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            int scale = 1;
            if (o.outHeight <= 0 || o.outWidth <= 0) {
                o.outHeight = o.outWidth = 3000;
            }
            int maxNumOfPixels = 1800 * 3000;
            if (Math.max(o.outWidth, o.outHeight) / Math.min(o.outWidth, o.outHeight) > 2) {
                maxNumOfPixels = 1200 * (1200 * Math.max(o.outWidth, o.outHeight) / Math.min(o.outWidth, o.outHeight));
            }
            scale = computeSampleSize(o, maxSize, maxNumOfPixels, limit);
            o2.inSampleSize = scale;
            o2.inJustDecodeBounds = false;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            if (degree > 0) {
                b = rotaingImageView(degree, b);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return b;
    }


// --Commented out by Inspection START (15/7/9 上午11:48):
    /**
     * 计算图片压缩比例
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @param limit
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels, boolean limit) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels, limit);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }
// --Commented out by Inspection STOP (15/7/9 上午11:48)

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels, boolean limit) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.round(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (limit) ? (int) Math.max(Math.round((float) w / (float) minSideLength), Math.round((float) h / (float) minSideLength)) : (int) Math.min(Math.floor((float) w / (float) minSideLength), Math.floor((float) h / (float) minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

// --Commented out by Inspection START (15/7/9 下午12:05):
//    public static int[] getImageSizeByFile(String filePath) {
//        if (StringUtils.isEmpty(filePath)) {
//            return null;
//        }
//        File f = null;
//        if (filePath.startsWith("/")) {
//            f = new File(filePath);
//        } else {
//            f = new File(FileUtils.getTempPath(), filePath);
//        }
//        return getImageSizeByFile(f);
//    }
// --Commented out by Inspection STOP (15/7/9 下午12:05)

    /**
     * 根据jpg文件取图片大小尺寸
     *
     * @param file
     * @return
     */
    public static int[] getImageSizeByFile(File file) {
        int[] size = new int[]{0, 0};
//        try{
//            ExifInterface exif = new ExifInterface(file.getName());
//            size[0] = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH));
//            size[1] = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));
//            if(size[0]>0 && size[1]>0){
//                return size;
//            }
//        } catch(Exception e){
//
//        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            size[0] = o.outWidth;
            size[1] = o.outHeight;
        } catch (Exception e) {
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e2) {
                }
            }
        }

        return size;
    }

    /**
     * 复制图片
     *
     * @param fileName
     * @param fromFile
     * @return
     */
    public static Uri copyUri(String fileName, File fromFile) {
        File toFile = copyImageFile(fileName, fromFile);
        if (toFile != null) {
            return Uri.fromFile(toFile);
        }
        return null;
    }

    /**
     * 获取图片
     *
     * @param fileName
     * @return
     */
    public static Uri getImageUri(String fileName) {
        if (StringUtils.isNotEmpty(fileName)) {
            File file = new File(fileName);
            return Uri.fromFile(file);
        } else {
            return null;
        }
    }


    /**
     * 复制图片
     *
     * @param fileName
     * @param fromFile
     * @return
     */
    public static File copyImageFile(String fileName, File fromFile) {
        File toFile = null;
        if(fileName.startsWith("/")){
            toFile = new File(fileName);
        }else{
            toFile = new File(FileUtils.getTempPath(), fileName);
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists()) {
            toFile.delete();
        }
        if (fromFile.exists() && fromFile.isFile() && fromFile.canRead()) {
            FileInputStream fosfrom = null;
            FileOutputStream fosto = null;
            try {
                fosfrom = new FileInputStream(fromFile);
                fosto = new FileOutputStream(toFile);
                byte bt[] = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0) {
                    fosto.write(bt, 0, c); //将内容写到新文件当中
                }
                fosto.flush();
            } catch (Exception e) {
                Log.e("readfile", e.getMessage());
            } finally {
                if (fosfrom != null) {
                    try {
                        fosfrom.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fosto != null) {
                    try {
                        fosto.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return toFile;
        }
        return null;
    }

    /**
     * 取图片压缩
     *
     * @param fileInAndOUt
     * @param maxSize
     * @param limit   为true时，最大边为maxSize，flase时，最小边为maxSize；
     * @return
     */
    public static void decodeImageFile(File fileInAndOUt, int maxSize, boolean limit) {
        if (fileInAndOUt == null || !fileInAndOUt.exists() || fileInAndOUt.isDirectory() || !fileInAndOUt.canRead()) {
            return;
        }
        if (maxSize == 0) {
            maxSize = 3000;
        }
        Bitmap mBitmap = decodeFile(fileInAndOUt.getName(), maxSize, limit);
        if (mBitmap == null) {
            return;
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(fileInAndOUt);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 95, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fOut != null) {
                try {
                    fOut.close();
                } catch (IOException e) {
                }
            }
            //如果没有回收
            if (mBitmap.isRecycled() == false) {
                mBitmap.recycle();
            }
        }
    }


    /**
     * 图片缩放（大小）
     *
     * @param mBitmap
     * @return
     */
    public static Bitmap formatImage(Bitmap mBitmap) {
        if (mBitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(isBm, null, newOpts);  //此时返回bimmap为null,只是为了测量原始图片高度和宽度
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//这里设置高度为1200f
        float ww = 800f;//这里设置宽度为1200f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        if (!mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        return BitmapFactory.decodeStream(isBm, null, newOpts);
    }


// --Commented out by Inspection START (15/7/9 下午12:05):
//    /**
//     * 旋转imageview的图片
//     *
//     * @param scaleAngle
//     * @param mBitmap
//     * @return
//     */
//    public static Bitmap turnImage(int scaleAngle, Bitmap mBitmap) {
//        try {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(scaleAngle, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
//            return Bitmap.createBitmap(mBitmap, 0, 0,
//                    mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
//        } catch (Exception e) {
//            return null;
//        } finally {
//            if (null != mBitmap && !mBitmap.isRecycled()) {
//                mBitmap.recycle();
//            }
//        }
//    }
// --Commented out by Inspection STOP (15/7/9 下午12:05)

    /**
     * 图片锐化处理
     *
     * @param image
     * @return
     */

    public static Bitmap contrastImage(Bitmap image) {
        try {

            Bitmap bmp = Bitmap.createBitmap(image.getWidth(), image.getHeight(),
                    Config.ARGB_8888);
            ColorMatrix cMatrix = new ColorMatrix();
            float contrast = (float) (1.5);
            cMatrix.set(new float[]{contrast, 0, 0, 0, 0, 0,
                    contrast, 0, 0, 0,// 改变对比度
                    0, 0, contrast, 0, 0, 0, 0, 0, 1, 0});

            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
            Canvas canvas = new Canvas(bmp);
            // 在Canvas上绘制一个已经存在的Bitmap。
            canvas.drawBitmap(image, 0, 0, paint);

            return bmp;
        } catch (Exception e) {
            return null;
        } finally {
            if (null != image && !image.isRecycled()) {
                image.recycle();
            }
        }
    }


    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        if (bgimage != null) {
            // 获取这个图片的宽和高
            float width = bgimage.getWidth();
            float height = bgimage.getHeight();
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 计算宽高缩放率
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                    (int) height, matrix, true);
            return bitmap;
        } else {
            return bgimage;
        }
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (bitmap == null || angle == 0) {
            return bitmap;
        }
        //旋转图片 动作  
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        // 创建新的图片  
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }


    public static String getImSmallImage(String url) {
        if (url == null) {
            return "";
        }
        return url.replaceAll("\\.([^.]+)$", ".$1!imsmall");
    }

    public static String getImPublicSmallImage(String url) {
        if (url == null) {
            return "";
        }
        return url.replaceAll("\\.([^.]+)$", ".$1!impublicsmall");
    }

    public static String getImPublicLargeImage(String url) {
        if (url == null) {
            return "";
        }
        return url.replaceAll("\\.([^.]+)$", ".$1!impubliclarge");
    }

// --Commented out by Inspection START (15/7/9 下午12:05):
//    public static String getImUserHeaderImage(String url) {
//        if (url == null) {
//            return "";
//        }
//        return url.replaceAll("\\.([^.]+)$", ".$1!impubliclarge");
//    }
// --Commented out by Inspection STOP (15/7/9 下午12:05)


    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
