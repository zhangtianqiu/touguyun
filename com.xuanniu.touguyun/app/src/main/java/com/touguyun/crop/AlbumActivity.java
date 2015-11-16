package com.touguyun.crop;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.activity.BaseActivity;
import com.touguyun.module.ImageBucket;
import com.touguyun.utils.BitmapCache;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.TitleBar;

import java.io.Serializable;
import java.util.List;
public class AlbumActivity extends BaseActivity implements OnItemClickListener {

	
	List<ImageBucket> dataList;
	GridView gridView;
	ImageBucketAdapter adapter;// 自定义的适配器
	AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static Bitmap bimap;
	public boolean mode_only = false;
	private int hasImageCount;
    private TitleBar title_bar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		helper = AlbumHelper.getNewHelper();
		helper.init(getApplicationContext());
		hasImageCount = getIntent().getIntExtra("hasCount", 0);
		mode_only = getIntent().getBooleanExtra("mode", false);
		myHandler.sendEmptyMessage(1);
	}
	
	private void initData() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					dataList = helper.getImagesBucketList(false);
				} catch (Exception e) {
				}
				myHandler.sendEmptyMessage(2);
			}
		}).start();
	}
	/**
	 * 初始化view视图
	 */
	private void initView()
	{
		gridView = (GridView) findViewById(R.id.album_gridview);
		adapter = new ImageBucketAdapter(AlbumActivity.this, dataList);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
		/**
		 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
		 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
		 */
		// if(dataList.get(position).isSelected()){
		// dataList.get(position).setSelected(false);
		// }else{
		// dataList.get(position).setSelected(true);
		// }
		/**
		 * 通知适配器，绑定的数据发生了改变，应当刷新视图
		 */
		// adapter.notifyDataSetChanged();
		Intent intent = new Intent(AlbumActivity.this,
				ImageGridActivity.class);
		intent.putExtra(AlbumActivity.EXTRA_IMAGE_LIST,
				(Serializable) dataList.get(position).imageList);
		intent.putExtra("mode", mode_only);
		intent.putExtra("hasCount", hasImageCount);
		startActivityForResult(intent, 11);
	}
	public class ImageBucketAdapter extends BaseAdapter
	{
		final String TAG = getClass().getSimpleName();

		Activity act;
		/**
		 * 图片集列表
		 */
		List<ImageBucket> dataList;
		BitmapCache cache;
		BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback()
		{
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap,
					Object... params)
			{
				if (imageView != null && bitmap != null)
				{
					String url = (String) params[0];
					if (url != null && url.equals((String) imageView.getTag()))
					{
						((ImageView) imageView).setImageBitmap(bitmap);
					} else
					{
						Log.e(TAG, "callback, bmp not match");
					}
				} else
				{
					Log.e(TAG, "callback, bmp null");
				}
			}
		};

		public ImageBucketAdapter(Activity act, List<ImageBucket> list)
		{
			this.act = act;
			dataList = list;
			cache = new BitmapCache(AlbumActivity.this);
		}

		@Override
		public int getCount()
		{
			int count = 0;
			if (dataList != null)
			{
				count = dataList.size();
			}
			return count;
		}

		@Override
		public Object getItem(int arg0)
		{
			return null;
		}

		@Override
		public long getItemId(int arg0)
		{
			return arg0;
		}

		class Holder
		{
			private ImageView iv;
			private TextView name;
			private TextView count;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2)
		{
			Holder holder;
			if (arg1 == null)
			{
				holder = new Holder();
				arg1 = View.inflate(act, R.layout.list_item_album_bucket_view, null);
				holder.iv = (ImageView) arg1.findViewById(R.id.album_bucket_image);
				holder.name = (TextView) arg1.findViewById(R.id.album_bucket_name);
				holder.count = (TextView) arg1.findViewById(R.id.album_bucket_count);
				arg1.setTag(holder);
			} else
			{
				holder = (Holder) arg1.getTag();
			}
			ImageBucket item = dataList!=null?dataList.get(arg0):null;
			holder.count.setText(item!=null?item.count+"":"0");
			holder.name.setText(item!=null ? item.bucketName:"");
			if (item!=null && item.imageList != null && item.imageList.size() > 0)
			{
				String thumbPath = item.imageList.get(0).thumbnailPath;
				String sourcePath = item.imageList.get(0).imagePath;
				holder.iv.setTag(sourcePath);
				cache.displayBmp(holder.iv, thumbPath, sourcePath, callback);
			} else
			{
				holder.iv.setImageBitmap(null);
				Log.e(TAG, "no images in bucket " + item.bucketName);
			}
			return arg1;
		}

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 11 && !this.isFinishing()){
			if(data != null){
				setResult(RESULT_OK, data);
			}
			if(resultCode == RESULT_OK){
				finish();
			}
		}
		
	}
	
	private Handler myHandler = new Handler(){
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case 1:
                UIShowUtil.showDialog(AlbumActivity.this, true);
				initData();
				break;
			case 2:
				UIShowUtil.cancelDialog();
				initView();
				break;

			default:
				break;
			}
		}
	};

}
