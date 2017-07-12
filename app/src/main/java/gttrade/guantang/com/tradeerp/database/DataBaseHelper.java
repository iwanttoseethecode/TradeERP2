package gttrade.guantang.com.tradeerp.database;

/**
 * Created by luoling on 2016/9/26.
 */
public class DataBaseHelper  {
    public static int DB_VERSION=6;//数据库当前版本,升级时数字加一, 修改时间2017-01-13 10：26
    public static final String DBNAME="gttrade_database.db";

    public static final String tb_Orders = "tb_Orders";
    public static final String tb_Item = "tb_Item";
    public static final String tb_OrderTransaction = "tb_OrderTransaction";
    public static final String tb_HpCatalogue = "tb_HpCatalogue";

    public static final String tb_PanDianList = "tb_PanDianList";
    public static final String tb_fahuoInfo = "tb_fahuoInfo";
}
