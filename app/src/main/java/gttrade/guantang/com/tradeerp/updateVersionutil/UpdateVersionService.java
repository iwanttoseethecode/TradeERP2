package gttrade.guantang.com.tradeerp.updateVersionutil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gttrade.guantang.com.tradeerp.R;

public class UpdateVersionService extends Service {

    /**
     * cancelUpdate 取消下载标志
     * */
    public boolean cancelUpdate = false;
    public NotificationCompat.Builder builder;
    public Notification notification;
    public String downLoadUrl,apk_name;
    public RemoteViews remoteViews;
    NotificationManager notificationManager;
    public int notification_id = 100;

    public UpdateVersionService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentText("下载通知")
                .setContentText("正在现在电商仓库ERP……")
                .setTicker("下载电商仓库ERP")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.appicon);
        remoteViews = new RemoteViews(getPackageName(),R.layout.notification_progress_layout);
        remoteViews.setProgressBar(R.id.progressbar,100,0,false);
        builder.setContent(remoteViews);
        notification = builder.build();
//        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        Intent notificationIntent = new Intent(this.getApplicationContext(),UpdateVersionService.class);
        PendingIntent contentIntent = PendingIntent.getService(
                this.getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = contentIntent;
        notificationManager.notify(notification_id,notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downLoadUrl = intent.getStringExtra("downLoadUrl");
        apk_name = intent.getStringExtra("apk_name");
        new DownLoadAPK().execute(downLoadUrl,apk_name);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class DownLoadAPK extends AsyncTask<String, Integer, Integer> {
        int apklength = 0;
        @Override
        protected Integer doInBackground(String... params) {
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
                    url = new URL(CheckHttp(params[0]));
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

                    File apkfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+File.separator+params[1]);
                    if(!apkfile.exists()){
                        apkfile.createNewFile();
                    }

                    fos = new FileOutputStream(apkfile);

                    byte[] bt = new byte[1024];
                    int count = 0;

                    int readnum = 0;
                    int progress = 0;
                    while(!cancelUpdate && ((readnum = is.read(bt))>0)){
                        count += readnum;
                        fos.write(bt, 0, readnum);

                        int m = progressPercent(count,apklength);
                        if (m>progress+4){
                            progress=m;
                            publishProgress(progress);
                        }
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

            remoteViews.setProgressBar(R.id.progressbar,100,values[0],false);
            notificationManager.notify(notification_id,notification);
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO 自动生成的方法存根
            super.onPostExecute(result);
//            remoteViews.setProgressBar(R.id.progressbar,0,0,false);
//            notificationManager.notify(notification_id,notification);
            notificationManager.cancel(notification_id);
            if(result==1){
                Toast.makeText(getApplicationContext(), "文件下载完成,正在安装更新", Toast.LENGTH_SHORT).show();
                // 安装新版apk
                installAPK();
            }else if(result==-1){
                Toast.makeText(getApplicationContext(), "文件下载失败", Toast.LENGTH_SHORT).show();
            }

            stopSelf();
            android.os.Process.killProcess(android.os.Process.myPid());// 如果不加上这句的话在apk安装完成之后点击单开会崩溃
        }
    }

    public int progressPercent(float progress,float max){
        return (int) ((progress/max)*100);
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

    public void installAPK(){
        File apkfile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+File.separator+apk_name);
        if(!apkfile.exists()){
            return;
        }
        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");
        getApplicationContext().startActivity(intent);
    }
}
