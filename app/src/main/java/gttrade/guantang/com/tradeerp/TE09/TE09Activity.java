package gttrade.guantang.com.tradeerp.TE09;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.RecyclerViewDemo.DividerItemDecoration;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE09.EventBusBean.RefreshBean;
import gttrade.guantang.com.tradeerp.TE09.TE0901.TE0901Activity;
import gttrade.guantang.com.tradeerp.TE09.TE0902.TE0902Activity;
import gttrade.guantang.com.tradeerp.TE09.TE0903.TE0903Activity;
import gttrade.guantang.com.tradeerp.ZXing.ScanCaptureActivity;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.database.DataBaseHelper;
import gttrade.guantang.com.tradeerp.database.DataBaseObject;
import gttrade.guantang.com.tradeerp.database.DataBaseOperate;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE09Activity extends BaseActivity implements PanDianAdapter.IUpdateShopingCarNum,TextWatcher{

    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.searchDelBtn)
    ImageView searchDelBtn;
    @BindView(R.id.commitTextView)
    TextView commitTextView;
    @BindView(R.id.tablayout)
    LinearLayout tablayout;
    @BindView(R.id.numbershow)
    TextView numbershow;
    @BindView(R.id.shoppingLayout)
    FrameLayout shoppingLayout;
    @BindView(R.id.myRecyclerView)
    RecyclerView myRecyclerView;

    private PanDianAdapter mPanDianAdapter;

    private ExecutorService cacheThreadPool = Executors.newCachedThreadPool();

    private PanDianDatabaseOperation panDianDatabaseOperation;

    private GetWaitCheckAsyncTask getWaitCheckAsyncTask;

    private CommitDataAsyncTask commitDataAsyncTask;

    private AtomicBoolean onClickFlag = new AtomicBoolean(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te09);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (getWaitCheckAsyncTask != null){
            getWaitCheckAsyncTask.cancel();
        }
        if (commitDataAsyncTask != null){
            commitDataAsyncTask.cancel();
        }
    }

    private void initView(){
        editText.addTextChangedListener(this);
    }

    private void init() {
        panDianDatabaseOperation = PanDianDatabaseOperation.getInstance(getApplicationContext());
        mPanDianAdapter = new PanDianAdapter(this,panDianDatabaseOperation);
        mPanDianAdapter.setIUpdataShopingCarNum(this);
        myRecyclerView.setAdapter(mPanDianAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());

        myRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL,R.color.themeBackgroundGrey,1));

        //1.判断当窗体加载完毕的时候,立马再加载真正的布局进来
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                if(panDianDatabaseOperation.tb_PanDianList_IsEmpty()){
                    Intent intent = new Intent(TE09Activity.this,TE0901Activity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

        new ShowDataAsyncTask().execute();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshBean refreshBean){
        new ShowSearchDataAsyncTask().execute(editText.getText().toString().trim());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                showProgressDialog("正在获取盘点货品",false,false);
                Map<String,String> requestBodyMap = new HashMap<String,String>();
                requestBodyMap.put("jsonc",data.getStringExtra("CheckItem"));
                getWaitCheckAsyncTask = new GetWaitCheckAsyncTask();
                getWaitCheckAsyncTask.startWork(requestBodyMap);
            }
        }else if(requestCode == 2){
            if (resultCode == 1){
                editText.setText(data.getStringExtra("result"));
            }
        }
    }



    @OnClick({R.id.back, R.id.filterImgBtn, R.id.scanImgBtn, R.id.searchDelBtn, R.id.shoppingLayout,R.id.commitTextView})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.filterImgBtn:
                intent.setClass(this, TE0901Activity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.scanImgBtn:
                intent.setClass(this, ScanCaptureActivity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.searchDelBtn:
                editText.setText("");
                break;
            case R.id.shoppingLayout:
                intent.setClass(this, TE0902Activity.class);
                startActivity(intent);
                break;
            case R.id.commitTextView:
                if (onClickFlag.compareAndSet(true,false)){
                    showProgressDialog("正在提交");
                    List<Map<String,Object>> list = panDianDatabaseOperation.getCommitCheckedHPList();
                    if (list.isEmpty()){
                        showToast("没有可提交的货品");
                        dismissDialog();
                        onClickFlag.compareAndSet(false,true);
                        return;
                    }
                    JSONArray jsonArray = new JSONArray(list);
                    Map<String,String> requestBodyMap = new HashMap<>();
                    requestBodyMap.put("jonc",jsonArray.toString());
                    commitDataAsyncTask = new CommitDataAsyncTask();
                    commitDataAsyncTask.startWork(requestBodyMap);
                }

                break;
        }
    }



    @Override
    public void updateShopingCarNum() {
        int num = panDianDatabaseOperation.getCheckHPCount();
        if (num > 9) {
            numbershow.setBackgroundResource(R.mipmap.numtip2);
        } else if (num > 99) {
            numbershow.setBackgroundResource(R.mipmap.numtip3);
        } else {
            numbershow.setBackgroundResource(R.mipmap.numtip1);
        }
        if (num > 999) {
            numbershow.setText("999");
        } else {
            numbershow.setText(String.valueOf(num));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            searchDelBtn.setVisibility(View.VISIBLE);
        } else {
            searchDelBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            new ShowSearchDataAsyncTask().execute(s.toString().trim());
        }else{
            new ShowDataAsyncTask().execute();
        }

    }




    class GetWaitCheckAsyncTask extends MyOkHttpExecute {


        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetWaitCheck,getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            if (TextUtils.isEmpty(JsonString)) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        JSONArray dataJSONArray = jsonObject.getJSONArray("data");
                        new SaveAndShowDataAsyncTask().executeOnExecutor(cacheThreadPool,dataJSONArray);
                        break;
                    case -1:
                        dismissDialog();
                        Intent intent = new Intent(TE09Activity.this, TE01Activity.class);
                        startActivity(intent);
                        showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        dismissDialog();
                        showToast(jsonObject.getString("Message"));
                        break;
                    default:
                        dismissDialog();
                        showToast(jsonObject.getString("Message"));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dismissDialog();
            }
        }

        @Override
        public void faild(String error) {
            dismissDialog();
            showToast(error);
        }
    }

    class SaveAndShowDataAsyncTask extends AsyncTask<JSONArray,Void,List<Map<String,Object>>>{

        @Override
        protected List<Map<String, Object>> doInBackground(JSONArray... params) {
            panDianDatabaseOperation.deleteTable(DataBaseHelper.tb_PanDianList);
            panDianDatabaseOperation.savePandianList(params[0]);
            return panDianDatabaseOperation.getPandianList();
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            super.onPostExecute(maps);
            mPanDianAdapter.setData(maps);
            updateShopingCarNum();
            dismissDialog();
        }
    }

    class ShowDataAsyncTask extends AsyncTask<Void,Void,List<Map<String,Object>>>{

        @Override
        protected List<Map<String, Object>> doInBackground(Void... params) {

            return panDianDatabaseOperation.getPandianList();
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            super.onPostExecute(maps);
            mPanDianAdapter.setData(maps);
            updateShopingCarNum();
        }
    }

    class ShowSearchDataAsyncTask extends AsyncTask<String,Void,List<Map<String,Object>>>{

        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {

            return panDianDatabaseOperation.searchUnCheckedHPList_bySKU(params[0]);
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            super.onPostExecute(maps);
            mPanDianAdapter.setData(maps);
            updateShopingCarNum();
        }
    }

    class CommitDataAsyncTask extends MyOkHttpExecute{

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.ConfirmCheck,getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            onClickFlag.compareAndSet(false,true);
            if (JsonString.isEmpty()){
                return;
            }
            Intent intent = new Intent();
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch(jsonObject.getInt("Status")){
                    case 1:
                        showToast(jsonObject.getString("Message"));
                        panDianDatabaseOperation.deleteTable(DataBaseHelper.tb_PanDianList);
                        finish();
                        break;
                    case 2:
                        JSONArray jsonArray = jsonObject.getJSONArray("errdata");
                        if (jsonArray.length()==0){
                            showToast(jsonObject.getString("Message"));
                            panDianDatabaseOperation.deleteTable(DataBaseHelper.tb_PanDianList);
                            finish();
                        }
                        deleteHP_exceptErrorHP(jsonArray);
                        intent.setClass(TE09Activity.this, TE0903Activity.class);
                        intent.putExtra("ErrorCheckHP",jsonArray.toString());
                        startActivity(intent);

                        break;
                    case -1:
                        intent.setClass(TE09Activity.this, TE01Activity.class);
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
                dismissDialog();
            }
        }

        @Override
        public void faild(String error) {
            onClickFlag.compareAndSet(false,true);
            dismissDialog();
            showToast(error);
        }
    }

    /*
    * 删除服务端账面数量和本地账面数量一致的货品，有错误的货品需要用户确认并重新提交之后再删除
    * */
    public void deleteHP_exceptErrorHP(JSONArray jsonArray){
        panDianDatabaseOperation.deleteHP_exceptErrorHP(jsonArray);
    }

}

