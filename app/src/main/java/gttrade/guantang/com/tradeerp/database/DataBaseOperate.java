package gttrade.guantang.com.tradeerp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import gttrade.guantang.com.tradeerp.TE11Activty.SendDingdanType;

/**
 * Created by luoling on 2016/10/19.
 */
public  class DataBaseOperate {

    private Context context;
    private DataBaseManager dataBaseManager;

    public DataBaseOperate(Context mContext) {
        this.context = mContext.getApplicationContext();
        dataBaseManager = DataBaseManager.getInstance(this.context);
    }

    public void deleteTable(String tablename){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        db.delete(tablename,null,null);
        dataBaseManager.closeDataBase();
    }

    /**
     * 判断货品是否存在，存在就更新，不存在就插入
     * */
    public void Update_insert_hp(JSONArray jsonArray) throws JSONException {
        SQLiteDatabase db = dataBaseManager.openDataBase();
        StringBuffer sb = new StringBuffer();
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (sb.toString().isEmpty()){
                sb.append(jsonObject.getInt("ItemID"));
            }else{
                sb.append(","+jsonObject.getInt("ItemID"));
            }
            db.execSQL("update "+DataBaseHelper.tb_Item+" set PickNum = "+jsonObject.getString("PickNum")+",SKU = '"+jsonObject.getString("SKU")+
                    "',PicUrl_Small = '"+jsonObject.getString("PicUrl_Small")+"',StorageName = '"+jsonObject.getString("StorageName")+
                    "',StoragePosition = '"+jsonObject.getString("StoragePosition")+"',ItemName = '"+jsonObject.getString("ItemName")+"' where ID = "+jsonObject.getInt("ItemID"));
            db.execSQL("insert into "+DataBaseHelper.tb_Item+"(ID,SKU,PicUrl_Small,StorageName,PickNum,StoragePosition,ItemName) select "+jsonObject.getInt("ItemID")+",'"+jsonObject.getString("SKU")+"','"+
                    jsonObject.getString("PicUrl_Small")+"','"+jsonObject.getString("StorageName")+"',"+jsonObject.getString("PickNum")+",'"+jsonObject.getString("StoragePosition")+"','"+
                    jsonObject.getString("ItemName")+"' where not exists (select * from "+DataBaseHelper.tb_Item+" where ID = "+jsonObject.getInt("ItemID")+")");
        }
        db.execSQL("delete from "+DataBaseHelper.tb_Item+" where id not in("+sb.toString()+")");
        dataBaseManager.closeDataBase();
    }

    public List<Map<String,Object>> selectHp_bySKU(String sku){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        List<Map<String, Object>> mlist = new LinkedList<Map<String, Object>>();
        Cursor cursor = null;
        if (sku.isEmpty()){
            cursor = db.rawQuery("Select ID,ItemName,SKU,PicUrl_Small,StorageName,StoragePosition,PickNum,PickedNum from "+DataBaseHelper.tb_Item,null);
        }else{
            cursor = db.rawQuery("Select ID,ItemName,SKU,PicUrl_Small,StorageName,StoragePosition,PickNum,PickedNum from "+DataBaseHelper.tb_Item+
                    " where SKU like '%"+sku+"%'",null);
        }
        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("ID",cursor.getString(cursor.getColumnIndex("ID")));
            map.put("ItemName",cursor.getString(cursor.getColumnIndex("ItemName")));
            map.put("PicUrl_Small",cursor.getString(cursor.getColumnIndex("PicUrl_Small")));
            map.put("SKU",cursor.getString(cursor.getColumnIndex("SKU")));
            map.put("StorageName",cursor.getString(cursor.getColumnIndex("StorageName")));
            map.put("StoragePosition",cursor.getString(cursor.getColumnIndex("StoragePosition")));
            map.put("PickNum",cursor.getString(cursor.getColumnIndex("PickNum")));
            map.put("PickedNum",cursor.getString(cursor.getColumnIndex("PickedNum")));
            mlist.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return mlist;
    }

    //搜索没拣过的货品
    public List<Map<String,Object>> selectUncompletehp__bySKU(String sku){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        List<Map<String, Object>> mlist = new LinkedList<Map<String, Object>>();
        Cursor cursor = null;
        if (sku.isEmpty()){
            cursor = db.rawQuery("Select ID,ItemName,SKU,PicUrl_Small,StorageName,StoragePosition,PickNum,PickedNum from "+DataBaseHelper.tb_Item+" where PickNum <> PickedNum ",null);
        }else{
            cursor = db.rawQuery("Select ID,ItemName,SKU,PicUrl_Small,StorageName,StoragePosition,PickNum,PickedNum from "+DataBaseHelper.tb_Item+
                    " where PickNum <> PickedNum and SKU like '%"+sku+"%'",null);
        }
        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("ID",cursor.getString(cursor.getColumnIndex("ID")));
            map.put("ItemName",cursor.getString(cursor.getColumnIndex("ItemName")));
            map.put("PicUrl_Small",cursor.getString(cursor.getColumnIndex("PicUrl_Small")));
            map.put("SKU",cursor.getString(cursor.getColumnIndex("SKU")));
            map.put("StorageName",cursor.getString(cursor.getColumnIndex("StorageName")));
            map.put("StoragePosition",cursor.getString(cursor.getColumnIndex("StoragePosition")));
            map.put("PickNum",cursor.getString(cursor.getColumnIndex("PickNum")));
            map.put("PickedNum",cursor.getString(cursor.getColumnIndex("PickedNum")));
            mlist.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return mlist;
    }

    //搜索拣过的货品
    public List<Map<String,Object>> selectCompletehp_bySKU(String sku){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        List<Map<String,Object>> mlist = new LinkedList<Map<String,Object>>();
        Cursor cursor = null;
        if (sku.isEmpty()){
            cursor = db.rawQuery("Select ID,ItemName,SKU,PicUrl_Small,StorageName,StoragePosition,PickNum,PickedNum from "+DataBaseHelper.tb_Item+" where PickedNum > 0 ",null);
        }else{
            cursor = db.rawQuery("Select ID,ItemName,SKU,PicUrl_Small,StorageName,StoragePosition,PickNum,PickedNum from "+DataBaseHelper.tb_Item+
                    " where PickedNum > 0 and SKU like '%"+sku+"%'",null);
        }
        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("ID",cursor.getString(cursor.getColumnIndex("ID")));
            map.put("ItemName",cursor.getString(cursor.getColumnIndex("ItemName")));
            map.put("PicUrl_Small",cursor.getString(cursor.getColumnIndex("PicUrl_Small")));
            map.put("SKU",cursor.getString(cursor.getColumnIndex("SKU")));
            map.put("StorageName",cursor.getString(cursor.getColumnIndex("StorageName")));
            map.put("StoragePosition",cursor.getString(cursor.getColumnIndex("StoragePosition")));
            map.put("PickNum",cursor.getString(cursor.getColumnIndex("PickNum")));
            map.put("PickedNum",cursor.getString(cursor.getColumnIndex("PickedNum")));
            mlist.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return mlist;
    }

    public void update_PickedNum(String hpid,String PickedNum){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        db.execSQL("update "+DataBaseHelper.tb_Item+" set PickedNum = "+PickedNum+" where ID = "+hpid );
        dataBaseManager.closeDataBase();
    }

    //拣过的货品数量
    public int getNum_PickFinish(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery(" select count(*) as num from "+DataBaseHelper.tb_Item+" where PickedNum > 0 ",null);
        cursor.moveToFirst();
        int num = cursor.getInt(cursor.getColumnIndex("num"));
        cursor.close();
        dataBaseManager.closeDataBase();
        return num;
    }

    public Double getPickedNum_byID(String ID){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery(" select PickedNum from "+DataBaseHelper.tb_Item+" where ID = '"+ID+"'",null);
        cursor.moveToFirst();
        Double num = cursor.getDouble(cursor.getColumnIndex("PickedNum"));
        cursor.close();
        dataBaseManager.closeDataBase();
        return num;
    }

    public void setZeroPicked(String ID){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        db.execSQL("update "+DataBaseHelper.tb_Item+" set PickedNum = 0 where ID = '"+ID+"'");
        dataBaseManager.closeDataBase();
    }

    //获取已拣货品的id 和 数量
    public List<Map<String,Object>> getCompleteHp(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();
        Cursor cursor = db.rawQuery(" select ID,PickedNum from "+DataBaseHelper.tb_Item+" where PickedNum > 0 ",null);
        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("Itemid",cursor.getInt(cursor.getColumnIndex("ID")));
            map.put("PickNum",cursor.getDouble(cursor.getColumnIndex("PickedNum")));
            mList.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return mList;
    }


    public boolean havePickedNum(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor=db.rawQuery("select count(*) as num from "+DataBaseHelper.tb_Item+" where PickedNum > 0 ",null);
        int num = 0;
        if (cursor.moveToFirst()){
            num = cursor.getInt(cursor.getColumnIndex("num"));

        }
        cursor.close();
        dataBaseManager.closeDataBase();
        if(num >0){
            return true;
        }else{
            return false;
        }
    }

    /*
    * 插入一行的货品目录
    * */
    public static void saveRawCatalogue(SQLiteDatabase db,ContentValues contentValues){
        db.insert(DataBaseHelper.tb_HpCatalogue,null,contentValues);
    }

    public List<Map<String,Object>> getCatalogueLevelList(String pid){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select * from "+DataBaseHelper.tb_HpCatalogue+" where PID = '"+pid+"' ORDER BY ord " ,null);
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

        String[] columnNames =cursor.getColumnNames();
        int columnSize = columnNames.length;
        while (cursor.moveToNext()){
            Map<String,Object> map = new HashMap<String,Object>();
            for (int i = 0; i < columnSize; i++){
                map.put(columnNames[i],cursor.getString(cursor.getColumnIndex(columnNames[i])));
            }
            list.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return list;
    }

    /*
    * 插入一行的盘点货品
    * */
    public static void savePandianList(SQLiteDatabase db,ContentValues contentValues){
        db.insert(DataBaseHelper.tb_PanDianList,null,contentValues);
    }

    public static void upDatePandianList(SQLiteDatabase db,ContentValues contentValues,String[] ItemID){
        db.update(DataBaseHelper.tb_PanDianList,contentValues,"ItemID = ?",ItemID);
    }

    public List<Map<String,Object>> getPandianList(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor=db.rawQuery("select * from "+DataBaseHelper.tb_PanDianList+" where CheckNum is null ",null);
        List<Map<String,Object>> list = new LinkedList<Map<String,Object>>();
        while(cursor.moveToNext()){
            String[] columnName = cursor.getColumnNames();
            int length = columnName.length;
            Map<String,Object> map = new HashMap<String,Object>();
            for (int i = 0;i < length;i++){
                map.put(columnName[i],cursor.getString(cursor.getColumnIndex(columnName[i])));
            }
            list.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return list;
    }

    /*
    * 查询 tb_PanDianList 表 是否为空，true 是数据表为空，false 是数据表不为空
    * */
    public boolean tb_PanDianList_IsEmpty(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select count(1) as num from "+DataBaseHelper.tb_PanDianList,null);

        int num = 0;
        if (cursor.moveToFirst()){
            num = cursor.getInt(cursor.getColumnIndex("num"));
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return num == 0 ? true : false;
    }

    /*
    * 获取盘点货品的盘点数量
    * */
    public String getHPCheckNum(String id){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select CheckNum from "+DataBaseHelper.tb_PanDianList+" where ItemID = '"+id+"'",null);
        String num = null;
        if(cursor.moveToFirst()){
            num = cursor.getString(cursor.getColumnIndex("CheckNum"));
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return num;
    }

    /*
    * 更新盘点货品的盘点数量
    * */
    public void updateHPCheckNum(String id,String num){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        db.execSQL("update "+DataBaseHelper.tb_PanDianList+" set CheckNum = '"+num+"' where ItemID = '"+id+"'");
        dataBaseManager.closeDataBase();
    }

    public int getCheckHPCount(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select count(1) as num from "+DataBaseHelper.tb_PanDianList+" where CheckNum is not null",null);

        int num = 0;
        if(cursor.moveToFirst()){
            num = cursor.getInt(cursor.getColumnIndex("num"));
        }
        return num;
    }

    public List<Map<String,Object>> getCheckedHPList(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select * from "+DataBaseHelper.tb_PanDianList+" where CheckNum is not null",null);
        List<Map<String,Object>> list = new LinkedList<>();

        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<>();
            String[] colunmName = cursor.getColumnNames();
            int length = colunmName.length;
            for (int i=0;i<length;i++){
                map.put(colunmName[i],cursor.getString(cursor.getColumnIndex(colunmName[i])));
            }
            list.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return list;
    }

    public List<Map<String,Object>> searchUnCheckedHPList(String searchString){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select * from "+DataBaseHelper.tb_PanDianList+" where CheckNum is null and ItemSKU like '%"+searchString+"%'",null);
        List<Map<String,Object>> list = new LinkedList<>();

        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<>();
            String[] colunmName = cursor.getColumnNames();
            int length = colunmName.length;
            for (int i = 0;i<length;i++){
                map.put(colunmName[i],cursor.getString(cursor.getColumnIndex(colunmName[i])));
            }
            list.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return list;
    }

    public List<Map<String,Object>> searchCheckedHPList(String searchString){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select * from "+DataBaseHelper.tb_PanDianList+" where CheckNum is not null and ItemSKU like '%"+searchString+"%'",null);
        List<Map<String,Object>> list = new LinkedList<>();

        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<>();
            String[] colunmName = cursor.getColumnNames();
            int length = colunmName.length;
            for (int i = 0;i<length;i++){
                map.put(colunmName[i],cursor.getString(cursor.getColumnIndex(colunmName[i])));
            }
            list.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return list;
    }

    public void clearCheckedHPList(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        db.execSQL("update "+DataBaseHelper.tb_PanDianList+" set CheckNum = null where CheckNum is not null" );
        dataBaseManager.closeDataBase();
    }

    public List<Map<String,Object>> getCommitCheckedHPList(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select ItemID,CheckNum,Stock,StorageID from "+ DataBaseHelper.tb_PanDianList+" where CheckNum is not null ",null);
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<String,Object>();
            String[] colunmName = cursor.getColumnNames();
            int length = colunmName.length;
            for(int i=0;i<length;i++){
                map.put(colunmName[i],cursor.getString(cursor.getColumnIndex(colunmName[i])));
            }
            list.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return list;
    }

    public void deleteHP_exceptErrorHP(String ItemIDString){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        db.execSQL("delete from "+DataBaseHelper.tb_PanDianList+" where ItemID not in ("+ItemIDString+")");
        dataBaseManager.closeDataBase();
    }

    public LinkedList<Map<String,Object>> getPanDianErrorHPList(String ItemIDString){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select * from "+DataBaseHelper.tb_PanDianList+" where ItemID in ("+ItemIDString+")",null);

        LinkedList<Map<String,Object>> mList = new LinkedList<>();
        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<String,Object>();
            String[] columnNames = cursor.getColumnNames();
            int length = columnNames.length;
            for (int i = 0; i < length; i++) {
                map.put(columnNames[i],cursor.getString(cursor.getColumnIndex(columnNames[i])));
            }
            mList.offer(map);
        }
        return mList;
    }

    /**
     * 检查发货订单是否有数据
     * @return
     */
    public boolean tb_fahuoInfo_IsEmpty(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select count(1) as num from "+DataBaseHelper.tb_fahuoInfo,null);

        int num = 0;
        if(cursor.moveToFirst()){
            num = cursor.getInt(cursor.getColumnIndex("num"));
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return num == 0 ? true:false;
    }


    /**
     *
     * 向发货订单添加数据，如果订单有则修改，没有就插入
     * @param contentValues
     * @param db
     */
    public void insertfahuodingdan(ContentValues contentValues,SQLiteDatabase db){
        if (db.update(DataBaseHelper.tb_fahuoInfo,contentValues,"OrderNo = ?",new String[]{ contentValues.get("OrderNo").toString()})==0){
            db.insert(DataBaseHelper.tb_fahuoInfo,null,contentValues);
        }
    }

    public List<Map<String,Object>> search_tb_fahuoInfo(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select * from "+DataBaseHelper.tb_fahuoInfo,null);

        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<String,Object>();
            String[] columnNames=cursor.getColumnNames();
            int length = columnNames.length;
            for (int i = 0; i < length; i++) {
                map.put(columnNames[i],cursor.getString(cursor.getColumnIndex(columnNames[i])));
            }
            list.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return list;
    }


    /**
     *
     * 获取发货订单，仅按订单号和运单号返回
     * @return
     */
    public List<Map<String,Object>> search_tb_fahuoInfo_little(){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        Cursor cursor = db.rawQuery("select OrderNo,TrackingNo from "+DataBaseHelper.tb_fahuoInfo,null);
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        while(cursor.moveToNext()){
            Map<String,Object> map = new HashMap<>();
            String[] columnNames = cursor.getColumnNames();
            int length = columnNames.length;
            for (int i = 0; i < length; i++) {
                map.put(columnNames[i],cursor.getString(cursor.getColumnIndex(columnNames[i])));
            }
            list.add(map);
        }
        cursor.close();
        dataBaseManager.closeDataBase();
        return list;
    }

    public void deleteNotThisOrederNo(String condition){
        SQLiteDatabase db = dataBaseManager.openDataBase();
        db.execSQL("delete from "+DataBaseHelper.tb_fahuoInfo+" where OrderNo not in ("+condition+")");
        ContentValues contentValues = new ContentValues();
        contentValues.put("Status", SendDingdanType.Fail.getValues());
        db.update(DataBaseHelper.tb_fahuoInfo,contentValues,null,null);
        dataBaseManager.closeDataBase();
    }

    public void deleteOrderItem(String OrderNo){
        SQLiteDatabase db = dataBaseManager.openDataBase();

        db.delete(DataBaseHelper.tb_fahuoInfo," OrderNo = ?",new String[]{OrderNo});
        dataBaseManager.closeDataBase();
    }

}
