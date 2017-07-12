package gttrade.guantang.com.tradeerp.TE00;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE08.TE08Activity;
import gttrade.guantang.com.tradeerp.base.MyApplication;
import gttrade.guantang.com.tradeerp.database.DataBaseHelper;
import gttrade.guantang.com.tradeerp.database.DataBaseOperate;
import gttrade.guantang.com.tradeerp.shareprefence.shareprefenceBean;

public class TE00Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedprefence();
        if (MyApplication.getInstance().getSharedPreferences().getInt(shareprefenceBean.VERSIONCODE,0)!=MyApplication.getInstance().getVisionCode()){
            if (DataBaseHelper.DB_VERSION == 6){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DataBaseOperate dataBaseOperate = new DataBaseOperate(getApplicationContext());
                        dataBaseOperate.deleteTable(DataBaseHelper.tb_Orders);
                        dataBaseOperate.deleteTable(DataBaseHelper.tb_Item);
                        dataBaseOperate.deleteTable(DataBaseHelper.tb_OrderTransaction);
                        dataBaseOperate.deleteTable(DataBaseHelper.tb_HpCatalogue);
                        dataBaseOperate.deleteTable(DataBaseHelper.tb_PanDianList);
                        dataBaseOperate.deleteTable(DataBaseHelper.tb_fahuoInfo);
                    }
                }).start();
            }
            MyApplication.getInstance().getSharedPreferences().edit().putInt(shareprefenceBean.VERSIONCODE,MyApplication.getInstance().getVisionCode()).commit();
            Intent intent = new Intent(TE00Activity.this, TE08Activity.class);
            startActivity(intent);
            finish();
        }else {
            setContentView(R.layout.activity_te00);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(TE00Activity.this, TE01Activity.class);
                    startActivity(intent);
                    finish();
                }
            },2000);
        }


    }

    public void initSharedprefence(){
        MyApplication.getInstance().getSharedPreferences().edit().putString(shareprefenceBean.TIYANSERVERURL,"gttrade.cn").commit();
        MyApplication.getInstance().getSharedPreferences().edit().putString(shareprefenceBean.TIYANCOMPANY,"测试账套").commit();
        MyApplication.getInstance().getSharedPreferences().edit().putString(shareprefenceBean.TIYANUSERNAME,"admin").commit();
        MyApplication.getInstance().getSharedPreferences().edit().putString(shareprefenceBean.TIYANPASSWORD,"admin").commit();
        MyApplication.getInstance().getSharedPreferences().edit().putInt(shareprefenceBean.LOGINFLAG,-1).commit();
    }
}
