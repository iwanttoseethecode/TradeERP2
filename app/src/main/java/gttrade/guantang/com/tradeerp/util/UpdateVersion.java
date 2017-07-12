package gttrade.guantang.com.tradeerp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateVersion {
	
	public Context context;
	public String downLoadUrl,apk_name;
	public ProgressDialog mProgressDialog;
	/**
	 * cancelUpdate 取消下载标志
	 * */
	public boolean cancelUpdate = false;
	
	public UpdateVersion(Context mContext,String apk_name,String downLoadUrl){
		context = mContext;
		this.downLoadUrl = downLoadUrl;
		this.apk_name = apk_name;
		new ConfirmUrl().execute();
	}

	public String CheckHttp(String str) {
		if (str != null && str.length() > 7) {
			if (!str.substring(0, 6).equals("http://")) {
				return "http://";
			} else {
				return "";
			}
		} else {
			return "http://";
		}
	}
	
	class ConfirmUrl extends AsyncTask<Void, Void, Integer>{

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO 自动生成的方法存根
			URL url;
			int state = 0;
			try {
				url = new URL(CheckHttp(downLoadUrl)+downLoadUrl);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
				showDownloadDialog();
			}else{
				showUpdateUrlFeildDialog();
			}
		}
	}
	
	/**
	 * 下载的提示框
	 */
	public void showDownloadDialog(){
		// 构造软件下载对话框
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setTitle("正在更新");
		mProgressDialog.setMax(100);
		mProgressDialog.setProgress(0);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO 自动生成的方法存根
				cancelUpdate = true;
				dialog.dismiss();
			}
		});
		mProgressDialog.show();
		new DownLoadAPK().execute();
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
	
	class DownLoadAPK extends AsyncTask<Void, Integer, Integer>{
		int apklength = 0;
		@Override
		protected Integer doInBackground(Void... params) {
			// TODO 自动生成的方法存根
			
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				//获取存储卡的路径
				URL url = null;
				HttpURLConnection con = null;
				InputStream is = null;
				FileOutputStream fos = null;
				try {
//					String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
//					fileSavePath = sdpath + "guantang_downapk";
					url = new URL(CheckHttp(downLoadUrl)+downLoadUrl);
					//创建连接
					con = (HttpURLConnection) url.openConnection();
					con.setReadTimeout(10 * 1000);// 设置超时时间
//					con.setRequestMethod("GET");
//					con.setRequestProperty("Charser","utf-8,utf-8;q=0.7,*;q=0.3");
					con.connect();
					// 获取文件大小
					apklength = con.getContentLength();
					//创建输入流
					is = con.getInputStream();

					File apkfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+File.separator+apk_name);
					if(!apkfile.exists()){
						apkfile.createNewFile();
					}
					
					fos = new FileOutputStream(apkfile);
					
					byte[] bt = new byte[1024];
					int count = 0;
//					do{
//						int readnum = is.read(bt);
//						if(readnum<=0){
//							break;
//						}
//						count += readnum;
//						fos.write(bt, 0, readnum);
//						publishProgress(count);
//					}while(!cancelUpdate);
					
					int readnum = 0;
					while(!cancelUpdate && ((readnum = is.read(bt))>0)){
						count += readnum;
						fos.write(bt, 0, readnum);
						publishProgress(count);
					}
					fos.flush();
				} catch (MalformedURLException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
					return -1;
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
					return -1;
				}finally{
					try {
						if(is!=null){
							is.close();
						}
						if(fos!=null){
							fos.close();
						}
						if(con!=null){
							con.disconnect();
						}
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}
			return 1;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO 自动生成的方法存根
			super.onProgressUpdate(values);
			mProgressDialog.setProgress(values[0]*100/apklength);
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO 自动生成的方法存根
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			if(result==1){
				Toast.makeText(context, "文件下载完成,正在安装更新", Toast.LENGTH_SHORT).show();
				// 安装新版apk
				installAPK();
			}else if(result==-1){
				Toast.makeText(context, "文件下载失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void installAPK(){
		File apkfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+File.separator+apk_name);
		if(!apkfile.exists()){
			return;
		}
		// 通过Intent安装APK文件
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");
		context.startActivity(intent);
//		apkfile.delete();
		android.os.Process.killProcess(android.os.Process.myPid());// 如果不加上这句的话在apk安装完成之后点击单开会崩溃
	}
	
}
