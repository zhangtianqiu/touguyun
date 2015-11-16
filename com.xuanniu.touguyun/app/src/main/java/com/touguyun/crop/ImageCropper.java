package com.touguyun.crop;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.touguyun.MainApplication;
import com.touguyun.R;
import com.touguyun.utils.AlertItem;
import com.touguyun.utils.FileUtils;
import com.touguyun.utils.Images;
import com.touguyun.utils.SDCardUtils;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;

import java.io.File;
import java.io.IOException;

public class ImageCropper {
    //生成缩略图
    private static class CompressBitmapTask extends AsyncTask<File, CompressBitmapTask.ProcessingState, Boolean> {
        public enum ProcessingState {
            STARTING, PROCESSING_LARGE, FINISHED
        }
        private int thumbnailSize;
        public CompressBitmapTask() {
            super();
        }
        @Override
        protected Boolean doInBackground(File... files) {
            if (files == null || files.length == 0 || !files[0].exists()) {
                return false;
            }
            ProcessingState[] s = new ProcessingState[1];
            if(s != null){
                s[0] = ProcessingState.PROCESSING_LARGE;
                publishProgress(s);
            }
            Images.decodeImageFile(files[0], thumbnailSize,false);
            return true;
        }
    }
    public static interface ICropListener {
        /**
         * @param requestCode
         * @param resultCode
         * @param data
         */
//		public void onActivityResult(int requestCode, int resultCode, Intent data);
        /**
         * 当外部应用
         * @param　删除掉图片数组的第几个元素
         */
        public void onCleanImage(int indexImage);
        /**
         * 拍照，或从相册选取，或截图完毕后，回调。题目
         * @param filePath
         */
        public void onFinish(String filePath,int index);

    }
    public static File getTempFile(String name) {
        if (SDCardUtils.hasSDCard()) {
            File f = new File(FileUtils.getTempPath(), name);
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return f;
        } else {
            return null;
        }
    }
    private static File getImageFile(Uri uri) {
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            return new File(uri.getPath());
        } else {
            return new File(getFilePathFromContentUri(uri));
        }
    }
    private static String getFilePathFromContentUri(Uri uri) {
        String[] filePathColumn = { MediaColumns.DATA };
        Cursor cursor = MainApplication.getInstance().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
    private ICropListener cropListener;
    private Activity activity;
    private int indexImage;
    private boolean isAhead;//是否是前置摄像头
    private ImageView imageView;
    //	private int thumbnailSize = 400;	//用于显示图片的bitmat大小
    private int width = 1024;		//剪裁长宽比
    private int height = 1024;		//剪裁长宽比
    private Uri mImageCaptureUri;
    private Uri uriFromCameraOrTakeFile;
    private static final int PICK_FROM_CAMERA = 91;
    private static final int CROP_IMAGE = 92;
    private static final int PICK_FROM_FILE = 93;
    private static String fileName;
    private boolean isFixed = false;
    private boolean cancelSave = false;
    public static String getPhotoFileName(){
        if(StringUtils.isEmpty(fileName)){
            fileName = System.currentTimeMillis()+".jpg";
        }
        return fileName;

    }


    public void setCancelSave(Boolean cancelSave){
        this.cancelSave = cancelSave;
    }

    public void setMaxSize(int MaxSize, boolean isFixed){
        this.width = this.height = width;
        this.isFixed = isFixed;
    }

    public void setWidthAndHeight(int width,int height,boolean isFixed){
        this.width = width;
        this.height = height;
        this.isFixed = isFixed;
    }
    /**
     * @param activity
     */
    public ImageCropper(Activity activity) {
        this.activity = activity;
    }

    public void doCropImage(){
        System.gc();
        Intent intent  = new Intent(this.activity, ClipActivity.class);
        if (mImageCaptureUri != null) {
            File tempFile = getImageFile(this.mImageCaptureUri);
            if(tempFile == null || !tempFile.canRead()  || !tempFile.exists()){
                UIShowUtil.toast(activity, "获取图片失败");
                return;
            }
        }
        intent.setData(this.mImageCaptureUri);
        intent.putExtra("isFixed", isFixed);
        intent.putExtra("width", width);
        intent.putExtra("height", height);
        this.activity.startActivityForResult(intent, CROP_IMAGE);
    }

    public boolean isMyResult(int requestCode){
        return requestCode==PICK_FROM_CAMERA||requestCode==PICK_FROM_FILE||requestCode==CROP_IMAGE;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if(null == mImageCaptureUri){
                    mImageCaptureUri = Uri.fromFile(new File(FileUtils.getTempPath(), getStaticFileName()));
                    uriFromCameraOrTakeFile = mImageCaptureUri;
                }
                doCropImage();
                break;
            case PICK_FROM_FILE:
                if(data == null){
                    return;
                }
                if(data.getData()!=null){
                    mImageCaptureUri = Images.copyUri(getPhotoFileName(), getImageFile(data.getData()));
                }else if(StringUtils.isNotEmpty(data.getStringExtra("data"))){
                    mImageCaptureUri = Images.getImageUri(data.getStringExtra("data"));
                }
                uriFromCameraOrTakeFile = mImageCaptureUri;
                if(uriFromCameraOrTakeFile == null){
                    UIShowUtil.toast(activity, "获取图片失败");
                    return;
                }
                setStaticFileName(getImageFile(uriFromCameraOrTakeFile).getName());
                doCropImage();
                break;
            case CROP_IMAGE:
                if(cancelSave || resultCode == -1){
                    processPhotoUpdate(getTempFile(getStaticFileName()));
//					onFinish(getTempFile(getStaticFileName()));
                }
                break;
        }
    }
    public void onCleanImage() {
        if (this.cropListener != null) {
            this.cropListener.onCleanImage(this.indexImage);
        }
    }
    private void onFinish(Boolean isSuccess) {
        if (isSuccess && this.cropListener != null) {
            this.cropListener.onFinish(getStaticFileName(),this.indexImage);
        }
    }
//	private void onFinish(File imageFile){
//		if(width>0 && height>0){
//			Images.decodeImageFile(imageFile,Math.max(width,height),true);
//		}
//		if(this.cropListener != null && imageFile!=null && imageFile.canRead()){
//			this.cropListener.onFinish(getStaticFileName(),this.indexImage);
//		}
//	}

    private void processPhotoUpdate(File tempFile) {
        CompressBitmapTask task = new CompressBitmapTask() {
            @Override
            protected void onPostExecute(Boolean result) {
                onFinish(result);
            }
        };
        task.thumbnailSize = Math.max(this.width,this.height);
        task.execute(tempFile);
    }
    public void setCropListener(ICropListener cropListener) {
        this.cropListener = cropListener;
    }

    public void show(boolean ifShowCleanImage, int indexImage) {

        this.indexImage = indexImage;
        final String[] items = ifShowCleanImage ? new String[] { "手机拍照", "从相册选取", "清除图片" } : new String[] { "手机拍照", "从相册选取" };
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.activity, android.R.layout.select_dialog_item, items);
        new AlertItem.Builder(this.activity)
                //.setTitle("选择图片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            clickCamera();
                        } else if (item == 1) {
                            clickAlbum();
                        } else if (item == 2) {
                            onCleanImage();
                        }
                    }
                }).setCanceledOnTouchOutside(true).create().show();
    }

    private PopupWindow cameraPop;
    public void showPop(int indexImage,View location){
        this.indexImage = indexImage;
        if (null==cameraPop){
            View popWindow = View.inflate(activity, R.layout.get_photo_popu, null);
            cameraPop = new PopupWindow(popWindow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popWindow.findViewById(R.id.get_photo_popu_camre).setOnClickListener(clickListener);
            popWindow.findViewById(R.id.get_photo_popu_select).setOnClickListener(clickListener);
            popWindow.findViewById(R.id.get_photo_popu_cancel).setOnClickListener(clickListener);
            cameraPop.setAnimationStyle(R.style.get_pohoto_popu_anim_style);
            cameraPop.setBackgroundDrawable(new BitmapDrawable());
            cameraPop.setOutsideTouchable(false);
            cameraPop.setFocusable(true);
            cameraPop.setTouchable(true);
            cameraPop.update();
        }
        if (null!=cameraPop&&!cameraPop.isShowing()&&!activity.isFinishing()) {
            cameraPop.showAtLocation(location, Gravity.CENTER, 0, 0);
            if (cameraPop.getContentView() instanceof RelativeLayout){
                Animation animation = AnimationUtils.loadAnimation(activity, R.anim.anim_get_photo_popu_item);
                animation.setInterpolator(new OvershootInterpolator());
                LayoutAnimationController lac = new LayoutAnimationController(animation);
                lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
                lac.setDelay(0.5f);
                ((RelativeLayout)cameraPop.getContentView()).setLayoutAnimation(lac);
            }
        }

    }

    private View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.get_photo_popu_camre:
                    clickCamera();
                    break;
                case R.id.get_photo_popu_select:
                    clickAlbum();
                    break;
            }
            dismiss();
        }
    };

    public void dismiss(){
        if (null!=cameraPop&&cameraPop.isShowing()) {
            cameraPop.dismiss();
        }
    }

    public void clickCamera(){
        if(!SDCardUtils.hasSDCard()){
            Toast.makeText(this.activity, this.activity.getString(R.string.point_card_error), Toast.LENGTH_LONG).show();
            return;
        }
//		cleanCache();
        fileName = "";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mImageCaptureUri = Uri.fromFile(new File(FileUtils.getTempPath(), getPhotoFileName()));
        uriFromCameraOrTakeFile = mImageCaptureUri;
        setStaticFileName(getImageFile(uriFromCameraOrTakeFile).getName());
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        intent.putExtra("return-data", true);
        if (isAhead) {
            intent.putExtra("camerasensortype", 2); // 调用前置摄像头
        }
        intent.putExtra("autofocus", true); // 自动对焦
        setStaticFileName(getPhotoFileName());
//		Log.e("clickCamera", uriFromCameraOrTakeFile.toString());
        try {
            ImageCropper.this.activity.startActivityForResult(intent, PICK_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void clickAlbum(){
        if(!SDCardUtils.hasSDCard()){
            Toast.makeText(this.activity, this.activity.getString(R.string.point_card_error), Toast.LENGTH_LONG).show();
            return;
        }
        fileName = "";
        Intent intent = new Intent(this.activity, AlbumActivity.class);
        intent.putExtra("mode", true);
        intent.putExtra("hasCount", 1);
        ImageCropper.this.activity.startActivityForResult(intent, PICK_FROM_FILE);
    }


    /*——————————————————-因为部分手机在跨应用时，数据并不保留，因为把图片名存本地，返回时再取——————————————————-*/
    public void setStaticFileName(String fileName){
        if(this.activity!=null){
            SharedPreferences shared = this.activity.getSharedPreferences("ImageCrop", 0);
            shared.edit().putString("filename",fileName).commit();
        }
    }
    public String getStaticFileName(){
        if(this.activity!=null){
            SharedPreferences shared = this.activity.getSharedPreferences("ImageCrop", 0);
            return shared.getString("filename",getPhotoFileName());
        }
        return getPhotoFileName();
    }



    Handler mHandler = new Handler(){
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    cancelDialog();
                    doCropImage();
                    break;

                default:
                    break;
            }


        };
    };

    private Dialog dialog;

    public void cancelDialog(){
        if(activity!=null && null!= dialog && dialog.isShowing() && !activity.isFinishing()){
            dialog.cancel();
        }
    }
}
