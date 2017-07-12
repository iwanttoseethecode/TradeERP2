package gttrade.guantang.com.tradeerp.TE09;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gttrade.guantang.com.tradeerp.database.DataBaseManager;
import gttrade.guantang.com.tradeerp.database.DataBaseObject;
import gttrade.guantang.com.tradeerp.database.DataBaseOperate;

/**
 * Created by luoling on 2017/1/18.
 */

public class PanDianDatabaseOperation {

    private DataBaseOperate databaseOperate;
    private Context mContext;
    private DataBaseManager dataBaseManager;

    private static PanDianDatabaseOperation panDianDatabaseOperation;

    public static PanDianDatabaseOperation getInstance(Context context){
        if (panDianDatabaseOperation == null){
            panDianDatabaseOperation = new PanDianDatabaseOperation(context);
        }
        return panDianDatabaseOperation;
    }

    private PanDianDatabaseOperation(Context context){
        databaseOperate = new DataBaseOperate(context);

        if (context instanceof Activity){
            mContext = context.getApplicationContext();
        }else {
            mContext = context;
        }
        dataBaseManager = DataBaseManager.getInstance(mContext);
    }

    public void deleteTable(String table){
        databaseOperate.deleteTable(table);
    }

    public List<Map<String,Object>> getPandianList() {
        return databaseOperate.getPandianList();
    }

    public void savePandianList(JSONArray dataJSONArray) {
        int length = dataJSONArray.length();
        try {
            SQLiteDatabase db = dataBaseManager.openDataBase();


            for (int i = 0; i < length; i++) {

                JSONObject jsonObject = dataJSONArray.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put("ItemID", jsonObject.getInt("ItemID"));
                contentValues.put("ItemSKU", jsonObject.getString("ItemSKU"));
                contentValues.put("ItemName", jsonObject.getString("ItemName"));
                contentValues.put("Storage", jsonObject.getString("Storage"));
                contentValues.put("Position", jsonObject.getString("Position"));
                contentValues.put("Stock", jsonObject.getInt("Stock"));
                contentValues.put("PicUrl_Small",jsonObject.getString("PicUrl_Small"));
                contentValues.put("StorageID",jsonObject.getInt("StorageID"));

                DataBaseOperate.savePandianList(db, contentValues);
            }
            dataBaseManager.closeDataBase();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void upDatePandianList(List<Map<String,Object>> list) {

        int length = list.size();


        SQLiteDatabase db = dataBaseManager.openDataBase();


        for (int i = 0; i < length; i++) {

            Map<String,Object> map = list.get(i);

            ContentValues contentValues = new ContentValues();
            String ItemID = map.get("ItemID").toString();
            contentValues.put("ItemID", Integer.valueOf(ItemID));
            contentValues.put("ItemSKU", map.get("ItemSKU").toString());
            contentValues.put("ItemName", map.get("ItemName").toString());
            contentValues.put("Storage", map.get("Storage").toString());
            contentValues.put("Position", map.get("Position").toString());
            contentValues.put("Stock", Integer.valueOf(map.get("Stock").toString()));
            contentValues.put("PicUrl_Small",map.get("PicUrl_Small").toString());
            contentValues.put("CheckNum",Integer.valueOf(map.get("CheckNum").toString()));

            DataBaseOperate.upDatePandianList(db, contentValues,new String[] {ItemID});
        }
        dataBaseManager.closeDataBase();

    }

    public boolean tb_PanDianList_IsEmpty(){
        return databaseOperate.tb_PanDianList_IsEmpty();
    }

    public String getHPCheckNum(String id){
        return databaseOperate.getHPCheckNum(id);
    }

    /*
    * 当 num 为null的时候是不能使用下面的方法
    * */
    public void updateHPCheckNum(String id,String num){
        databaseOperate.updateHPCheckNum(id,num);
    }

    public int getCheckHPCount(){
        return databaseOperate.getCheckHPCount();
    }

    public List<Map<String,Object>> getCheckedHPList(){
        return databaseOperate.getCheckedHPList();
    }

    public List<Map<String,Object>> searchUnCheckedHPList_bySKU(String searchString){

        if (TextUtils.isEmpty(searchString)){
            return databaseOperate.getPandianList();
        }else{
            return databaseOperate.searchUnCheckedHPList(searchString);

        }
    }

    public List<Map<String,Object>> searchCheckedHPList_bySKU(String searchString){
        return databaseOperate.searchCheckedHPList(searchString);
    }

    public void clearCheckedHPList(){
        databaseOperate.clearCheckedHPList();
    }

    public List<Map<String,Object>> getCommitCheckedHPList(){
        return databaseOperate.getCommitCheckedHPList();
    }

    public void deleteHP_exceptErrorHP(JSONArray jsonArray){
        StringBuilder sb = new StringBuilder();
        int length = jsonArray.length();
        try {
            for (int i=0;i<length;i++){
                if (sb.toString().isEmpty()){
                    sb.append(jsonArray.getJSONObject(i).getString("ItemID"));
                }else{
                    sb.append(","+jsonArray.getJSONObject(i).getString("ItemID"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        databaseOperate.deleteHP_exceptErrorHP(sb.toString());
    }

    public LinkedList<Map<String,Object>> getPanDianErrorHPList(String jsonArraySstring){
        StringBuilder sb = new StringBuilder();
        try {
            JSONArray jsonArray = new JSONArray(jsonArraySstring);
            int length = jsonArray.length();


            for (int i = 0; i < length; i++) {
                if (sb.toString().isEmpty()){
                    sb.append(jsonArray.getJSONObject(i).getString("ItemID"));
                }else {
                    sb.append(","+jsonArray.getJSONObject(i).getString("ItemID"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return databaseOperate.getPanDianErrorHPList(sb.toString());
    }

}
