package gttrade.guantang.com.tradeerp.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by luoling on 2016/9/27.
 */
public class MyApplication extends Application{

    private static Context mContext;

    private SharedPreferences mSharedPreferences;
    private static String SharedPreferencesName = "tradeSharePreferences";
    private static MyApplication myApplication;

    public static MyApplication getInstance(){
        return myApplication;
    }

    @Override
    public void onCreate() {//程序创建时执行
        super.onCreate();
        mSharedPreferences = getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        myApplication = this;
        MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType. E_UM_NORMAL);
    }

    @Override
    public void onTerminate() {//程序终止的时候执行
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {//低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {//程序在内存清理的时候执行
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {//配置改变时触发这个方法
        super.onConfigurationChanged(newConfig);
    }

    public static Context getContextObject(){
        return mContext;
    }

    public SharedPreferences getSharedPreferences(){
        if(mSharedPreferences==null){
            mSharedPreferences = getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    /*获取app版本号
	 * */
    public int getVisionCode() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int version = info.versionCode;
        return version;
    }

    /*获取app版本名称
	 * */
    public String getVisionName() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return info.versionName;
    }

}
