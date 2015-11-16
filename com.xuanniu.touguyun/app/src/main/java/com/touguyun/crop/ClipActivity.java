/*
 * Copyright (C) 2014 zzl09
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.touguyun.crop;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.touguyun.MainApplication;
import com.touguyun.R;
import com.touguyun.activity.BaseActivity;
import com.touguyun.utils.Images;
import com.touguyun.utils.StringUtils;

import java.io.File;

public class ClipActivity extends BaseActivity implements OnClickListener {
    ClipNewLayout mClipLayout;
    //	private File imageFile;
    private boolean isFixed;
    private int width, height;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_picture);
        uri = getIntent().getData();
        isFixed = getIntent().getBooleanExtra("isFixed", false);
        width = getIntent().getIntExtra("width", 0);
        height = getIntent().getIntExtra("height", 0);
        if (StringUtils.isEmpty(uri)) {
            finish();
        }
        initView();
        initBitmap();
    }
    private void initView() {
        mClipLayout = (ClipNewLayout) findViewById(R.id.clip_layout);
        mClipLayout.setInitData(width, height, isFixed);
        findViewById(R.id.head_save).setOnClickListener(this);
        findViewById(R.id.head_cancel).setOnClickListener(this);
        findViewById(R.id.head_title).setOnClickListener(this);
    }
    private void initBitmap() {
        Window window = getWindow();
        if (StringUtils.isNotEmpty(uri) && mClipLayout != null) {
            mClipLayout.setSourceImage(uri, window);
        }
//        try{
//            Bitmap temp =Images.decodeFile(imageFile.getPath(), 1200, false);
//            if(temp != null){
//                mClipLayout.setSourceImage(temp, window);
//            }else{
//                toast(R.string.image_cropper_out_of_error);
//            }
//        }catch (OutOfMemoryError error){
//            error.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClipLayout.onDestory();
    }
    private void clipBitmap() {
        Bitmap bitmap = mClipLayout.getBitmap();
        if (bitmap != null && StringUtils.isNotEmpty(uri)) {
            Images.saveMyBitmap(bitmap, uri.getPath(), 95);
            bitmap.recycle();
            System.gc();
        }
        setResult(RESULT_OK);
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_save:
                clipBitmap();
                break;
            case R.id.head_cancel:
                finish();
                break;
            case R.id.head_title:
                if (mClipLayout != null) {
                    mClipLayout.rotate(90.0f);
                }
                break;
            default:
                break;
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
        String[] filePathColumn = {MediaColumns.DATA};
        Cursor cursor = MainApplication.getInstance().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}
