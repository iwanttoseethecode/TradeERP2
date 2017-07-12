package gttrade.guantang.com.tradeerp.updateVersionutil;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gttrade.guantang.com.tradeerp.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class UpdateVersion_2 {

	public Context context;

	public NotificationCompat.Builder builder;
	public String downLoadUrl,apk_name;
	
	public UpdateVersion_2(Context mContext, String apk_name, String downLoadUrl){
		context = mContext;
		this.downLoadUrl = downLoadUrl;
		this.apk_name = apk_name;
		new ConfirmUrl().execute();
	}

	public String CheckHttp(String str) {
		if (str != null && str.length() > 7) {
			if (!str.substring(0, 6).equals("http://")) {
				return "http://"+str;
			} else {
				return str;
			}
		} else {
			return "http://"+str;
		}
	}
	
	class ConfirmUrl extends AsyncTask<Void, Void, Integer>{

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO 自动生成的方法存根
			URL url;
			int state = 0;
			try {
				url = new URL(CheckHttp(downLoadUrl));
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.connect();

				state = con.getResponseCode();
				con.disconnect();
			} catch (MalformedURLException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			return state;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO 自动生成的方法存根
			super.onPostExecute(result);
			if(result == 200){
				showNotification();
			}else{
				showUpdateUrlFeildDialog();
			}
		}
	}
	
	/**
	 * 下载的提示框
	 */
	public void showNotification(){

		Intent intent = new Intent(context.getApplicationContext(),UpdateVersionService.class);
		intent.putExtra("downLoadUrl",downLoadUrl);
		intent.putExtra("apk_name",apk_name);
		context.getApplicationContext().startService(intent);
	}
	
	/**
	 * 地址错误提示框
	 */
	public void showUpdateUrlFeildDialog(){
		Builder builder = new Builder(context);
		builder.setTitle("软件更新");
		builder.setMessage("下载地址链接错误！您可以到应用市场下载");
		// 更新
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 取消对话框
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

}
