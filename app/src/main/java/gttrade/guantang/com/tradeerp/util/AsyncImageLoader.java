package gttrade.guantang.com.tradeerp.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import gttrade.guantang.com.tradeerp.R;

public class AsyncImageLoader {
	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private LruCache<String, Bitmap> mMemoryCache;

	public AsyncImageLoader() {
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
	}
	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}
	
	public Bitmap loadBitmap(final String id, final String imageUrl, final ImageView imgView) {
		Log.v("tag","loadBitmap "+imageUrl);
		if (imageUrl == null || imageUrl.equals("") || id.equals("") || id == null) {
			return null;
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
//				callback.imageLoaded(id,imageUrl,(ImgBeanHolder) msg.obj);
				ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
				if (holder.imageView.getTag()!=null && holder.imageView.getTag().equals(id)) {
					holder.imageView.setImageBitmap(holder.bitmap);
				}
			}
		};
		
		Bitmap bitmap = getBitmapFromMemoryCache(id);
		if( bitmap != null){
			Log.v("tag","从内存取图片 "+imageUrl);
			return bitmap;
		}
		
		new Thread() {
			public void run() {
				Log.v("tag","从网络取图片 "+imageUrl);
				Bitmap bitmap = loadImageBitmapFromUrl(imageUrl);
				addBitmapToMemoryCache(id,bitmap);
				ImgBeanHolder holder = new ImgBeanHolder();
				holder.bitmap = bitmap;
				holder.imageView = imgView;
				if (bitmap != null) {
					handler.sendMessage(handler.obtainMessage(0, holder));
				}
			}
		}.start();

		return null;
	}


	protected Bitmap loadImageBitmapFromUrl(String imageUrl) {

		URL url = null;
		HttpURLConnection connection = null;
		InputStream is = null;
		Bitmap bitmap = null;
		try {
			url = new URL(imageUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			is = connection.getInputStream();

			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			option.inPreferredConfig = Bitmap.Config.ARGB_8888;
			option.inSampleSize = 1;
			BitmapFactory.decodeStream(is,null,option);
			while ((option.outHeight / option.inSampleSize) > 360
					&& (option.outWidth / option.inSampleSize) > 360) {
				option.inSampleSize *= 2;
			}
			option.inJustDecodeBounds = false;

            connection.disconnect();
            is.close();

            url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            is = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(is,null,option);
			if (bitmap != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
				int options = 100;
				while (baos.toByteArray().length / 1024 > 20) { // 循环判断如果压缩后图片是否大于20kb,大于继续压缩
					baos.reset();// 重置baos即清空baos
					bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
					options -= 20;// 每次都减少20%
				}
				ByteArrayInputStream isBm = new ByteArrayInputStream(
						baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
				bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection!=null){
				connection.disconnect();
			}
			if (is!=null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	public class ImgBeanHolder {
		public Bitmap bitmap;
		public ImageView imageView;
	}

//	public interface ImageCallbackForBitmap {
//		public void imageLoaded(String id, String imageUrl,ImgBeanHolder imgHolder);
//	}
}
