package com.touguyun.crop;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.touguyun.R;
import com.touguyun.activity.BaseActivity;
import com.touguyun.module.ImageItem;
import com.touguyun.utils.BitmapCache;
import com.touguyun.utils.Images;
import com.touguyun.utils.StringUtils;
import com.touguyun.utils.UIShowUtil;
import com.touguyun.view.TitleBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImageGridActivity extends BaseActivity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	private List<ImageItem> dataList;
	private GridView gridView;
	private ImageGridAdapter adapter;
	private AlbumHelper helper;
	private int hasImageCount;
	private boolean mode_only;
	private final int MAX_IMAGE_MUN = 9;
	private ArrayList<String> list = new ArrayList<String>();
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
            UIShowUtil.cancelDialog();
			switch (msg.what)
			{
			case 0:
				UIShowUtil.toast(ImageGridActivity.this, R.string.album_max_select_count);
				break;
			case 1:
				setResult(RESULT_OK, new Intent().putExtra("data", list));
				finish();
				break;
			case 2:
                UIShowUtil.toast(ImageGridActivity.this, "图片加载失败");
				break;
			case 3:
				if(StringUtils.isNotEmpty(msg.obj)){
					setResult(RESULT_OK, new Intent().putExtra("data", msg.obj.toString()));
					finish();
				}else{
                    UIShowUtil.toast(ImageGridActivity.this, "图片加载失败");
				}
				break;
			case 4:
                UIShowUtil.showDialog(ImageGridActivity.this, true);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							dataList = (List<ImageItem>) getIntent().getSerializableExtra(
									EXTRA_IMAGE_LIST);
						} catch (Exception e) {
						}
						sendEmptyMessage(5);
					}
				}).start();
				break;
				
			case 5:
				initView();
				break;
			default:
				break;
			}
		}
	};

    private TitleBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_album);
        bar=(TitleBar)findViewById(R.id.touguyun_titleBar);
        bar.showTitle(R.string.album_title);
        bar.setTitleBarClickListener(barClick);
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		hasImageCount = getIntent().getIntExtra("hasCount", 0);
		mode_only = getIntent().getBooleanExtra("mode", false);
		mHandler.sendEmptyMessage(4);
		if(mode_only){
            bar.hideButton(false);
		}else{
			onListen(0);
		}
	}

    private TitleBar.TitleBarClickListener barClick=new TitleBar.TitleBarClickListener() {
        @Override
        public void onBarClick(boolean isLeft) {
            if (isLeft){
                onBackPressed();
            }else{
                UIShowUtil.showDialog(ImageGridActivity.this, true);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Collection<String> c = adapter.map.values();
                            Iterator<String> it = c.iterator();
                            for (; it.hasNext();)
                            {
                                list.add(Images.saveMyBitmap(Images.decodeFile(it.next(), 1800, false), System.currentTimeMillis() + ".jpg"));
//							try {
//								list.add(Images.copyImageFile(System.currentTimeMillis()+".jpg",new File(it.next())).getPath());
//							}catch (Exception e){
//								e.printStackTrace();
//							}
                            }
                            mHandler.sendEmptyMessage(1);
                        } catch (Exception e) {
                            mHandler.sendEmptyMessage(2);
                        }
                    }
                }).start();
            }
        }
    };

	public void onListen(int count) {
        bar.showRight("完成" + "(" + (hasImageCount + count) + "/" + MAX_IMAGE_MUN + ")", 0);
	}
	
	@Override
	public void onBackPressed() {
//		setResult(RESULT_OK);
		finish();
	}
	
	private void initView()
	{
		gridView = (GridView) findViewById(R.id.album_gridview);
		gridView.setNumColumns(3);
		gridView.setHorizontalSpacing(4);
		gridView.setVerticalSpacing(4);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList,mHandler);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				adapter.notifyDataSetChanged();
			}

		});

	}
	private class ImageGridAdapter extends BaseAdapter
	{

		final String TAG = getClass().getSimpleName();
		ImageGridActivity act;
		List<ImageItem> dataList;
		Map<String, String> map = new HashMap<String, String>();
		BitmapCache cache;
		private Handler mHandler;
		private int selectTotal = 0;
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


		public ImageGridAdapter(ImageGridActivity act, List<ImageItem> list, Handler mHandler)
		{
			this.act = act;
			dataList = list;
			cache = new BitmapCache(ImageGridActivity.this);
			this.mHandler = mHandler;
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
		public Object getItem(int position)
		{
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		class Holder
		{
			private ImageView iv;
			private ImageView selected;
			private TextView text;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			final Holder holder;

			if (convertView == null)
			{
				holder = new Holder();
				convertView = View.inflate(act, R.layout.list_item_album_image_view, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.album_bucket_image);
				holder.selected = (ImageView) convertView
						.findViewById(R.id.album_bucket_isselected);
				holder.text = (TextView) convertView
						.findViewById(R.id.album_bucket_name);
				convertView.setTag(holder);
			} else
			{
				holder = (Holder) convertView.getTag();
			}
			final ImageItem item = dataList.get(position);

			holder.iv.setTag(item.imagePath);
			cache.displayBmp(holder.iv, item.thumbnailPath, item.imagePath,
					callback);
			if (item.isSelected)
			{
				holder.selected.setImageResource(R.drawable.album_image_select_icon);
				holder.text.setBackgroundResource(R.color.black_99323232);
			} else
			{
				holder.selected.setImageResource(Color.TRANSPARENT);
				holder.text.setBackgroundColor(0x00000000);
			}
			holder.iv.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					String path = dataList.get(position).imagePath;
					if(mode_only){
						returnImage(path);
						return;
					}
					if ((hasImageCount + selectTotal) < MAX_IMAGE_MUN)
					{
						item.isSelected = !item.isSelected;
						if (item.isSelected)
						{
							holder.selected
									.setImageResource(R.drawable.album_image_select_icon);
							holder.text
									.setBackgroundResource(R.color.black_99323232);
							selectTotal++;
							if (act != null){
								act.onListen(selectTotal);
							}
							map.put(path, path);

						} else if (!item.isSelected)
						{
							holder.selected.setImageResource(Color.TRANSPARENT);
							holder.text.setBackgroundColor(0x00000000);
							selectTotal--;
							if (act != null){
								act.onListen(selectTotal);
							}
							map.remove(path);
						}
					} else if ((hasImageCount + selectTotal) >= MAX_IMAGE_MUN)
					{
						if (item.isSelected == true)
						{
							item.isSelected = !item.isSelected;
							holder.selected.setImageResource(Color.TRANSPARENT);
							holder.text.setBackgroundColor(0x00000000);
							selectTotal--;
							map.remove(path);

						} else
						{
							Message message = Message.obtain(mHandler, 0);
							message.sendToTarget();
						}
					}
				}

			});

			return convertView;
		}
	}
	
	private void returnImage(final String filePath) {
		UIShowUtil.showDialog(ImageGridActivity.this, true);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {

//					String resultFile = Images.saveMyBitmap(Images.decodeFile(filePath, 0,false), System.currentTimeMillis()+".jpg",95);
					String resultFile = Images.copyImageFile(System.currentTimeMillis() + ".jpg", new File(filePath)).getPath();
					Message msg = new Message();
					msg.what = 3;
					msg.obj = resultFile;
					mHandler.sendMessage(msg);

				} catch (Exception e) {
					mHandler.sendEmptyMessage(2);
				}
			}
		}).start();
	}
}
