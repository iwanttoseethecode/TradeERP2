package gttrade.guantang.com.tradeerp.TE11Activty.TE1101;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.RecyclerViewDemo.DividerItemDecoration;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE11Activty.SendDingdanAdapter;
import gttrade.guantang.com.tradeerp.TE11Activty.TE11Activity;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.util.ActivityAnimUtil;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;


public class TE1101Activity extends BaseActivity implements SendDingdanAdapter.DeleteItemInterface {

    @BindView(R.id.dingdanRecyclerView)
    RecyclerView dingdanRecyclerView;

    LocalSendDingDanDatabaseOperate mLocalSendDingDanDatabaseOperate;
    @BindView(R.id.titleTxtView)
    TextView titleTxtView;
    @BindView(R.id.totalTextView)
    TextView totalTextView;

    private SendDingdanAdapter sendDingdanAdapter;

    private SendItemAsyncTask sendItemAsyncTask;

    private AtomicBoolean onClickFlag = new AtomicBoolean(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te1101);
        ButterKnife.bind(this);
        mLocalSendDingDanDatabaseOperate = LocalSendDingDanDatabaseOperate.getInstance(this);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                if (mLocalSendDingDanDatabaseOperate.tb_fahuoInfo_IsEmpty()) {
                    Intent intent = new Intent(TE1101Activity.this, TE11Activity.class);
                    startActivity(intent);
                }
            }
        });

        init();
    }



    private void init() {
        sendDingdanAdapter = new SendDingdanAdapter(this);
        sendDingdanAdapter.setDeleteItemInterface(this);
        dingdanRecyclerView.setAdapter(sendDingdanAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        dingdanRecyclerView.setLayoutManager(linearLayoutManager);
        dingdanRecyclerView.setItemAnimator(new DefaultItemAnimator());

        dingdanRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.color.themeBackgroundGrey, 1));

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sendItemAsyncTask != null){
            sendItemAsyncTask.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mLocalSendDingDanDatabaseOperate.tb_fahuoInfo_IsEmpty()) {
            new ShowAsyncTask().execute();
        }
    }

    @OnClick({R.id.back, R.id.scanImgBtn,R.id.commitTextView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.scanImgBtn:
                Intent intent = new Intent(TE1101Activity.this, TE11Activity.class);
                startActivity(intent);
                break;
            case R.id.commitTextView:
                if (onClickFlag.compareAndSet(true,false)){
                    showProgressDialog("正在提交");
                    List<Map<String,Object>> list =mLocalSendDingDanDatabaseOperate.search_tb_fahuoInfo_little();
                    if (list.isEmpty()){
                        showToast("没有可提交的订单");
                        onClickFlag.compareAndSet(false,true);
                        dismissDialog();
                        return;
                    }
                    JSONArray SendItemJSONArray = new JSONArray();
                    for (Iterator<Map<String,Object>> i = list.iterator(); i.hasNext() ;) {
                        Map<String,Object> map = i.next();
                        JSONObject jsonObject = new JSONObject(map);
                        SendItemJSONArray.put(jsonObject);
                    }
                    Map<String,String> requestBodyMap = new HashMap<String,String>();
                    requestBodyMap.put("sendjson",SendItemJSONArray.toString());
                    sendItemAsyncTask = new SendItemAsyncTask();
                    sendItemAsyncTask.startWork(requestBodyMap);
                }

                break;
        }
    }

    @Override
    public void deleteItem(final String OrderNo) {
        if (!OrderNo.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("确定删除发货订单？");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mLocalSendDingDanDatabaseOperate.deleteOrderItem(OrderNo);
                    new ShowAsyncTask().execute();
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }


    class SendItemAsyncTask extends MyOkHttpExecute{

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.SendItem,getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            dismissDialog();
            onClickFlag.compareAndSet(false,true);
            if (JsonString.isEmpty())
            {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch(jsonObject.getInt("Status")){
                    case 1:
                        showToast(jsonObject.getString("Message"));
                        mLocalSendDingDanDatabaseOperate.deleteTable();
                        finish();
                        break;
                    case 2:
                        JSONArray jsonArray = jsonObject.getJSONArray("Nos");
                        showToast(jsonObject.getString("Message"));
                        mLocalSendDingDanDatabaseOperate.deleteNotThisOrederNo(jsonArray);
                        new ShowAsyncTask().execute();
                        break;
                    case -1:
                        Intent intent = new Intent(TE1101Activity.this, TE01Activity.class);
                        startActivity(intent);
                        showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        showToast(jsonObject.getString("Message"));
                        break;
                    default:
                        showToast(jsonObject.getString("Message"));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void faild(String error) {
            onClickFlag.compareAndSet(false,true);
            dismissDialog();
            showToast(error);
        }
    }

    class ShowAsyncTask extends AsyncTask<Void, Void, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(Void... params) {
            return mLocalSendDingDanDatabaseOperate.searchDingdan();
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            super.onPostExecute(maps);
            sendDingdanAdapter.setData(maps);
            totalTextView.setText("总共发货订单：" + maps.size());
        }
    }

    @Override
    public void finish() {
        super.finish();
        ActivityAnimUtil.setRightOut(this);
    }

}
