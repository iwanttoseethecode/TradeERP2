package gttrade.guantang.com.tradeerp.TE11Activty.TE1101;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Map;

import gttrade.guantang.com.tradeerp.database.DataBaseHelper;
import gttrade.guantang.com.tradeerp.database.DataBaseOperate;

/**
 * Created by luoling on 2017/2/13.
 */

public class LocalSendDingDanDatabaseOperate {
    private DataBaseOperate dataBaseOperate;
    private Context mContext;
    private static LocalSendDingDanDatabaseOperate mLocalSendDingDanDatabaseOperate;

    private LocalSendDingDanDatabaseOperate(Context context){
        dataBaseOperate = new DataBaseOperate(context);
        mContext = context;
    }

    public static LocalSendDingDanDatabaseOperate getInstance(Context context){
        if (mLocalSendDingDanDatabaseOperate == null){
            synchronized (LocalSendDingDanDatabaseOperate.class){
                if (mLocalSendDingDanDatabaseOperate == null){
                    mLocalSendDingDanDatabaseOperate = new LocalSendDingDanDatabaseOperate(context);
                }
            }
        }
        return mLocalSendDingDanDatabaseOperate;
    }

    public boolean tb_fahuoInfo_IsEmpty(){
        return dataBaseOperate.tb_fahuoInfo_IsEmpty();
    }


    /**
     *
     * 向发货订单数据表添加数据
     * @param contentValues
     * @param db
     */
    public void insertfahuodingdan(ContentValues contentValues, SQLiteDatabase db){
        dataBaseOperate.insertfahuodingdan(contentValues,db);
    }

    public List<Map<String,Object>> searchDingdan(){
        return dataBaseOperate.search_tb_fahuoInfo();
    }
    public List<Map<String,Object>> search_tb_fahuoInfo_little(){
        return dataBaseOperate.search_tb_fahuoInfo_little();
    }

    public void deleteTable(){
        dataBaseOperate.deleteTable(DataBaseHelper.tb_fahuoInfo);
    }

    public void deleteNotThisOrederNo(JSONArray jsonArray){
        int length = jsonArray.length();

        StringBuilder condition = new StringBuilder();
        if (length>0){
            try {
                condition.append("'"+jsonArray.getString(0)+"'");
                for (int i = 1; i < length; i++) {
                    condition.append(",'"+jsonArray.getString(i)+"'");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        dataBaseOperate.deleteNotThisOrederNo(condition.toString());
    }

    public void deleteOrderItem(String OrderNo){
        dataBaseOperate.deleteOrderItem(OrderNo);
    }

}
