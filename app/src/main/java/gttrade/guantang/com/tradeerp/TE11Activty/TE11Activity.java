package gttrade.guantang.com.tradeerp.TE11Activty;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE11Activty.TE1101.LocalSendDingDanDatabaseOperate;
import gttrade.guantang.com.tradeerp.TE11Activty.TE1101.TE1101Activity;
import gttrade.guantang.com.tradeerp.ZXing.ParentScanActivity;
import gttrade.guantang.com.tradeerp.ZXing.View.ViewfinderView;
import gttrade.guantang.com.tradeerp.database.DataBaseManager;
import gttrade.guantang.com.tradeerp.database.DataBaseObject;
import gttrade.guantang.com.tradeerp.util.ActivityAnimUtil;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE11Activity extends ParentScanActivity implements RadioGroup.OnCheckedChangeListener,NewViewfinderView.CallBackData{

    @BindView(R.id.viewfinder_view)
    NewViewfinderView viewfinderView;
    @BindView(R.id.bottomRadioGroup)
    RadioGroup bottomRadioGroup;

    private ScanType mScanType = ScanType.DingDanHao;

    private ScanInformation scanInformation;

    private SurfaceHolder surfaceHolder;

    private ExecutorService cacheThreadPool = Executors.newCachedThreadPool();

    private Map<String,SendDingdanMessage> SendDingdanMessageMap = new HashMap<String,SendDingdanMessage>();

    private ScanSendAsyncTask scanSendAsyncTask;

    LocalSendDingDanDatabaseOperate mLocalSendDingDanDatabaseOperate;

    public ProgressDialog mDialog;
    private static final int WHAT_DELAY = 1;
    private static final long DELAY = 8500;// 超时关闭dialog;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (WHAT_DELAY == msg.what) {
                dismissDialog();
            }
        };
    };

    private Lock waitLock = new ReentrantLock();
    private Condition waitCondition = waitLock.newCondition();
//    private AtomicBoolean waitFlag = new AtomicBoolean(false);

    private AtomicInteger waitNum = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAnimUtil.setRightIn(this);
        setContentView(R.layout.activity_te11);
        ButterKnife.bind(this);

        viewfinderView.setCallBackData(this);
        bottomRadioGroup.setOnCheckedChangeListener(this);

        if (!NetWorkTool.checkNetworkState(this)){
            Toast.makeText(this, "网络未连接，扫描发货需要联网操作", Toast.LENGTH_SHORT).show();
        }

        mLocalSendDingDanDatabaseOperate = LocalSendDingDanDatabaseOperate.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }


        decodeFormats = null;
        characterSet = null;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanSendAsyncTask != null){
            scanSendAsyncTask.cancel();
        }
    }

    /**
     * @param string  文字描述
     * @param cancel  点击是否可以取消dialog
     * @param overtimeClose true 超时关闭dialog，false 不关闭；
     */
    public void showProgressDialog(String string, boolean cancel, final boolean overtimeClose) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(this,R.style.yuanjiao_dialog);
        }
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(cancel);
        mDialog.setCanceledOnTouchOutside(cancel);
        mDialog.show();
        mDialog.setContentView(R.layout.progress_dialog_layout);
        TextView msg = (TextView) mDialog.getWindow()
                .findViewById(R.id.message);
        msg.setText(string);
        new Thread(new Runnable() {

            @Override
            public void run() {
                if(overtimeClose){
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(overtimeClose){
                        mHandler.sendEmptyMessage(WHAT_DELAY);
                    }
                }
            }
        }).start();
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    @Override
    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        produceScanInformation(obj.getText());
        viewfinderView.setScanInformation(scanInformation);

    }

    private String getScanString(String text){
        text = TextUtils.isEmpty(text) ? "" : text;
        return text;
    }

    /**
     * @param text  订单号
     *
     */
    private void produceDingDanHaoScanInformation(String text){
        if (scanInformation == null){
            scanInformation = new ScanInformation();
            scanInformation.setmScanType(mScanType);
        }

        if (scanInformation.getDingdanHao()==null){
            scanInformation.setDingdanHao(getScanString(text));
            scanInformation.setFinish(true);
        }
        scanInformation.setInit(false);
    }

    /**
     * @param text 运单号
     *
     */
    private void produceYunDanHaoScanInformation(String text){
        if (scanInformation == null){
            scanInformation = new ScanInformation();
        }
        scanInformation.setmScanType(mScanType);
        if (scanInformation.getYundanHao()==null){
            scanInformation.setYundanHao(getScanString(text));
            scanInformation.setFinish(true);
        }
        scanInformation.setInit(false);
    }

    /**
     * @param text 订单号 或者 运单号
     */
    private void produceDingAndYunScanInformation(String text){
        if (scanInformation == null){
            scanInformation = new ScanInformation();
            scanInformation.setmScanType(mScanType);
        }

        if (scanInformation.getDingdanHao()==null ){
            scanInformation.setDingdanHao(getScanString(text));
            scanInformation.setFinish(false);
            restartScan();
        }else if(scanInformation.getDingdanHao()!=null && scanInformation.getYundanHao()==null) {
            scanInformation.setYundanHao(getScanString(text));
            scanInformation.setFinish(true);
        }
        scanInformation.setInit(false);
    }



    /**
     *
     * 生产一个扫描后的扫描信息
     * @param text
     */
    private void produceScanInformation(String text){

        switch(mScanType.getValue()){
            case 0:
                produceDingDanHaoScanInformation(getScanString(text));
                break;
            case 1:
                produceYunDanHaoScanInformation(getScanString(text));
                break;
            case 2:
                produceDingAndYunScanInformation(getScanString(text));

                break;
        }

    }


    /**
     * 重新初始化扫描
     */
    private void restartScan(){
//        waitFlag.compareAndSet(false,true);
        waitNum.incrementAndGet();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                waitLock.lock();
                initCamera(surfaceHolder);
                if (handler != null)
                    handler.restartPreviewAndDecode();
                //扫描之后activity要关闭，必须等扫描初始化完成才能关闭，不然会报错
                waitNum.decrementAndGet();
                waitCondition.signalAll();
                waitLock.unlock();
            }
        },3000);

//       cacheThreadPool.execute(new Runnable() {
//           @Override
//           public void run() {
//               try {
//                   Thread.sleep(3500);
//               } catch (InterruptedException e) {
//                   e.printStackTrace();
//               }
//               initCamera(surfaceHolder);
//               if (handler != null)
//                   handler.restartPreviewAndDecode();
//           }
//       });
    }

    @Override
    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    @OnClick({R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                showProgressDialog("处理数据",true,true);
                new SaveAsyncTask().execute();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId){
            case R.id.dingdanhaoRadioButton:
                mScanType = ScanType.DingDanHao;
                scanInformation = new ScanInformation();
                scanInformation.setmScanType(mScanType);
                scanInformation.setInit(true);
                viewfinderView.setScanInformation(scanInformation);
                restartScan();
                break;
            case R.id.yundanhaoRadioButton:
                mScanType = ScanType.YunDanHao;
                scanInformation = new ScanInformation();
                scanInformation.setmScanType(mScanType);
                scanInformation.setInit(true);
                viewfinderView.setScanInformation(scanInformation);
                restartScan();
                break;
            case R.id.dingAndyunRadioButton:
                mScanType = ScanType.DingAndYun;
                scanInformation = new ScanInformation();
                scanInformation.setmScanType(mScanType);
                scanInformation.setInit(true);
                viewfinderView.setScanInformation(scanInformation);
                restartScan();
                break;
        }
    }

    @Override
    public void gainData() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("ScanType",String.valueOf(mScanType.getValue()));
        map.put("OrderNo",scanInformation.getDingdanHao());
        map.put("TrackingNo",scanInformation.getYundanHao());
        JSONObject jsonObject = new JSONObject(map);
        showProgressDialog(null,false,true);

        Map<String,String> requestBodyMap = new HashMap<String,String>();
        requestBodyMap.put("scanjc",jsonObject.toString());
        scanSendAsyncTask = new ScanSendAsyncTask();
        scanSendAsyncTask.startWork(requestBodyMap);
    }

    class ScanSendAsyncTask extends MyOkHttpExecute{


        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.ScanSend,getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            dismissDialog();
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                if (jsonObject.toString().equals("")){
                    Toast.makeText(TE11Activity.this,"扫描信息不存在",Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject scanmessageJSONObject = jsonObject.getJSONObject("scanmessage");
                switch(jsonObject.getInt("Status")){
                    case 1: //1表示扫描订单号成功
                        dealSuccessMessage(scanmessageJSONObject);
                        putMap(scanmessageJSONObject);
                        Toast.makeText(TE11Activity.this,"扫描成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 2: //2 表示扫描订单号失败
                        dealSuccessMessage(scanmessageJSONObject);
                        Toast.makeText(TE11Activity.this,jsonObject.getString("Message"),Toast.LENGTH_SHORT).show();
                        break;
                    case 3: //3表示扫描运单号成功
                        dealSuccessMessage(scanmessageJSONObject);
                        putMap(scanmessageJSONObject);
                        Toast.makeText(TE11Activity.this,"扫描成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 4: //4表示扫描运单号失败
                        dealSuccessMessage(scanmessageJSONObject);
                        Toast.makeText(TE11Activity.this,jsonObject.getString("Message"),Toast.LENGTH_SHORT).show();
                        break;
                    case 5: //5表示扫描订单号和运单号成功
                        dealSuccessMessage(scanmessageJSONObject);
                        putMap(scanmessageJSONObject);
                        Toast.makeText(TE11Activity.this,"扫描成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 6: //6表示扫描订单号和运单号，订单号失败
                        dealSuccessMessage(scanmessageJSONObject);
                        Toast.makeText(TE11Activity.this,jsonObject.getString("Message"),Toast.LENGTH_SHORT).show();
                        break;
                    case 7: //7表示扫描订单号和运单号，运单号失败
                        dealSuccessMessage(scanmessageJSONObject);
                        Toast.makeText(TE11Activity.this,jsonObject.getString("Message"),Toast.LENGTH_SHORT).show();
                        break;
                    case 8: //8表示扫描订单号和运单号，订单号和运单失败
                        dealSuccessMessage(scanmessageJSONObject);
                        Toast.makeText(TE11Activity.this,jsonObject.getString("Message"),Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(TE11Activity.this,jsonObject.getString("Message"),Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                scanInformation = null;
            }finally {
                restartScan();
            }
            scanInformation = null;
        }

        @Override
        public void faild(String error) {
            scanInformation = null;
            dismissDialog();
        }
    }

    private void dealSuccessMessage(JSONObject scanmessageJSONObject) throws JSONException {
        JSONArray ItemMessageJSONArray = scanmessageJSONObject.getJSONArray("ItemMessage");
        ArrayList<ItemMessage> ItemMessageList = new ArrayList<ItemMessage>();
        int length=ItemMessageJSONArray.length();
        for (int i=0;i<length;i++){
            ItemMessage itemMessage = new ItemMessage();
            JSONObject ItemJSONObject = ItemMessageJSONArray.getJSONObject(i);
            itemMessage.setItemSKU(ItemJSONObject.getString("ItemSKU"));
            itemMessage.setItemName(ItemJSONObject.getString("ItemName"));
            ItemMessageList.add(itemMessage);
        }
        scanInformation.setItemMessage(ItemMessageList);
        scanInformation.setItemSumMessage(scanmessageJSONObject.getString("ItemSumMessage"));
        scanInformation.setOrderMessage(scanmessageJSONObject.getString("OrderMessage"));
        scanInformation.setTrackMessage(scanmessageJSONObject.getString("TrackMessage"));
        //防止再次回调去请求服务器
        scanInformation.setFinish(false);
        scanInformation.setGetServiceData(true);
        viewfinderView.setScanInformation(scanInformation);
        restartScan();
    }


    /**
     * 扫描单据完成并跟服务端校验成功后，放入map。
     */
    private void putMap(JSONObject scanmessageJSONObject) throws JSONException {

        SendDingdanMessage sendDingdanMessage = new SendDingdanMessage();
        sendDingdanMessage.setOrederNo(scanmessageJSONObject.getString("OrderNo"));
        sendDingdanMessage.setTrackingNo(scanmessageJSONObject.getString("TrackingNo"));
        sendDingdanMessage.setShipingMethod(scanmessageJSONObject.getString("ShipingMethod"));
        SendDingdanMessageMap.put(sendDingdanMessage.getOrederNo(),sendDingdanMessage);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            showProgressDialog("处理数据",true,true);
            new SaveAsyncTask().execute();
        }

        return true;
    }

    class SaveAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {

            Set<String> set = SendDingdanMessageMap.keySet();
            DataBaseManager dataBaseManager = DataBaseManager.getInstance(getApplicationContext());
            SQLiteDatabase db=dataBaseManager.openDataBase();

            for (Iterator<String> it = set.iterator(); it.hasNext();){
                ContentValues contentValues = new ContentValues();
                SendDingdanMessage sendDingdanMessage = SendDingdanMessageMap.get(it.next());
                contentValues.put("OrderNo",sendDingdanMessage.getOrederNo());
                contentValues.put("TrackingNo",sendDingdanMessage.getTrackingNo());
                contentValues.put("ShipingMethod",sendDingdanMessage.getShipingMethod());
                //待上传为0，上传成功为1，上传失败为-1
                contentValues.put("Status", SendDingdanType.NotUpDate.getValues());
                mLocalSendDingDanDatabaseOperate.insertfahuodingdan(contentValues,db);
            }
            dataBaseManager.closeDataBase();

            while (waitNum.get() != 0){
                waitLock.lock();
                try {
                    //等扫描初始化完成之后才执行activity关闭
                    waitCondition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                waitLock.unlock();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            cacheThreadPool.shutdownNow();
            dismissDialog();
            finish();
        }
    }

}
