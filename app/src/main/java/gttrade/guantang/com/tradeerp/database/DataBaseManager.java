package gttrade.guantang.com.tradeerp.database;

import android.app.Activity;
import android.app.job.JobScheduler;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by luoling on 2017/2/15.
 */

public final class DataBaseManager {

    private AtomicInteger databaseOpenCount = new AtomicInteger(0);
    private static volatile DataBaseManager dataBaseManager;
    private static DataBaseObject dataBaseObject;
    private SQLiteDatabase db;

    private DataBaseManager(Context context) {
           dataBaseObject = DataBaseObject.getInstance(context.getApplicationContext());
    }

    public static synchronized DataBaseManager getInstance(Context context) {
        if (dataBaseManager == null) {
            synchronized (DataBaseManager.class) {
                if (dataBaseManager == null) {
                    dataBaseManager = new DataBaseManager(context.getApplicationContext());
                }
            }
        }
        return dataBaseManager;
    }

    public synchronized SQLiteDatabase openDataBase(){
        if (databaseOpenCount.incrementAndGet() == 1){
            db=dataBaseObject.getReadableDatabase();
        }
        return db;
    }

    public synchronized void closeDataBase(){
        if (databaseOpenCount.decrementAndGet() == 0){
            db.close();

        }
    }
}
