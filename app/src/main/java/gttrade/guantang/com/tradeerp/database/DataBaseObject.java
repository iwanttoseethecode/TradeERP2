package gttrade.guantang.com.tradeerp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by luoling on 2016/9/26.
 */
public class DataBaseObject extends SQLiteOpenHelper {



    public static DataBaseObject getInstance(Context context) {

        DataBaseObject DBHELPER = new DataBaseObject(context, DataBaseHelper.DBNAME, null, DataBaseHelper.DB_VERSION);

        return DBHELPER;
    }

    private Context context;

    private DataBaseObject(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + DataBaseHelper.tb_Orders + " ( ID INTEGER PRIMARY KEY AUTOINCREMENT not null,"
                + "No NVARCHAR(100) null,Status NVARCHAR(100) null,Platform NVARCHAR(100) null,eShopName NVARCHAR(100) null,"
                +"No_Platform NVARCHAR(100) null,AmountPaid_RMB decimal(18, 8) DEFAULT 0,ShippingFreight_Paid_RMB decimal(18, 8) DEFAULT 0,"
                +"ShippingAddress_CountryName NVARCHAR(100) null,BuyerID NVARCHAR(100) null,LastModifiedTime datetime null"
                +"PickingStaus INTEGER DEFAULT 0)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+ DataBaseHelper.tb_Item + " ( ID INTEGER PRIMARY KEY not null,ItemName NVARCHAR(100) null,"
                +"SKU NVARCHAR(100) null,PicUrl_Small NVARCHAR(255) null,Stock INTEGER DEFAULT 0,StorageName NVARCHAR(100) null,"
                +"StoragePosition NVARCHAR(100) null,PickNum decimal(18, 8) DEFAULT 0,PickedNum decimal(18, 8) DEFAULT 0)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+ DataBaseHelper.tb_OrderTransaction + " ( ID INTEGER PRIMARY KEY AUTOINCREMENT not null,"
                +"OrderNo NVARCHAR(100) null,ItemID INTEGER DEFAULT 0,ItemSKU NVARCHAR(100) null,QuantityPurchased INTEGER DEFAULT 0,SinglePrice_RMB decimal(18,8) null,"
                +"AlreadyPicking INTEGER DEFAULT 0,WaitPicking INTEGER DEFAULT 0)");
        sqLiteDatabase.execSQL("create table if not exists "+DataBaseHelper.tb_HpCatalogue+" ( ID integer primary key not null,name nvarchar(50) null,lev integer null, PID integer null," +
                "ord integer null, sindex nvarchar(50) null)");
        sqLiteDatabase.execSQL("create table if not exists "+ DataBaseHelper.tb_PanDianList+" ( ID integer primary key not null,ItemSKU nvarchar(500) null,ItemName nvarchar(500) null,ItemID integer null,Storage varchar(100) null" +
                ",Position varchar(100) null,Stock integer null,CheckNum integer null,PicUrl_Small varchar(255) null,StorageID integer null)");
        sqLiteDatabase.execSQL("create table if not exists "+DataBaseHelper.tb_fahuoInfo+" ( OrderNo varchar(255) PRIMARY KEY null,TrackingNo varchar(255) null,ShipingMethod varchar(255) null, Status integer default 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i<i1){
            if (i1==6){
                context.deleteDatabase(DataBaseHelper.DBNAME);
                onCreate(sqLiteDatabase);

            }
        }
    }
}
