package gttrade.guantang.com.tradeerp.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import gttrade.guantang.com.tradeerp.util.NetWorkTool;

/**
 * Created by luoling on 2016/12/16.
 */

public class NetWorkStatusService extends Service {


    public NetWorkStatusService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver,mFilter);
    }

    BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //判断网络状态改变
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                if (!NetWorkTool.checkNetworkState(getApplicationContext())){
                    Toast.makeText(getApplicationContext(),"网络连接已断开",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"网络已连接",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectionReceiver);
    }
}
