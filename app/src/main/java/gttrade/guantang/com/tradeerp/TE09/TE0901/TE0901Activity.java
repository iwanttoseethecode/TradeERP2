package gttrade.guantang.com.tradeerp.TE09.TE0901;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE09.TE09Activity;
import gttrade.guantang.com.tradeerp.TE10.TE10Activity;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE0901Activity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, CangkuListDialog.ICangkuListResponse, KuWeiDialog.IKuWeiListResponse {

    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.titleTxtView)
    TextView titleTxtView;
    @BindView(R.id.titlelayout)
    LinearLayout titlelayout;
    @BindView(R.id.cangkuTextView)
    TextView cangkuTextView;
    @BindView(R.id.cangkuLayout)
    LinearLayout cangkuLayout;
    @BindView(R.id.huopinmuluTextView)
    TextView huopinmuluTextView;
    @BindView(R.id.huopinmuluLayout)
    LinearLayout huopinmuluLayout;
    @BindView(R.id.lastPandianTextView)
    TextView lastPandianTextView;
    @BindView(R.id.lastPandianLayout)
    LinearLayout lastPandianLayout;
    @BindView(R.id.pandianRadioGroup)
    RadioGroup pandianRadioGroup;
    @BindView(R.id.resetBtn)
    Button resetBtn;
    @BindView(R.id.confirmBtn)
    Button confirmBtn;
    @BindView(R.id.gudingRadBtn)
    RadioButton gudingRadBtn;
    @BindView(R.id.suijiRadBtn)
    RadioButton suijiRadBtn;
    @BindView(R.id.percentEditView)
    EditText percentEditView;
    @BindView(R.id.kuweiEdit)
    EditText kuweiEdit;

    private String ckid;

    private String storageID;

    private JSONArray CangkuJsonArray;
    private JSONArray kuweiJsonArray;

    private GetStorage getStorage;
    private GetStorageRackAsynctask getStorageRackAsynctask;

    /*
    * 是否是固定盘点
    * */
    private boolean iSFixedCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te0901);
        ButterKnife.bind(this);
        showProgressDialog("");
        getStorage = new GetStorage();
        getStorage.startWork(null);
        pandianRadioGroup.setOnCheckedChangeListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getStorage!=null){
            getStorage.cancel();
        }
        if (getStorageRackAsynctask != null){
            getStorage.cancel();
        }
    }

    @Override
    public void setCangkuValue(String id, String name) {
        cangkuTextView.setText(name);
        kuweiEdit.setText("");
        ckid = id;
        if (ckid == null)
            return;
        Map<String,String> requestBodyMap = new HashMap<String,String>();
        requestBodyMap.put("storageid",ckid);
        getStorageRackAsynctask = new GetStorageRackAsynctask();
        getStorageRackAsynctask.startWork(requestBodyMap);
    }

    @Override
    public void setKuWeiValue(String id, String name) {
        storageID = id;
        kuweiEdit.setText(name);
    }

    @OnClick({R.id.cangkuLayout, R.id.huopinmuluLayout, R.id.lastPandianLayout, R.id.kuweiDownImgView, R.id.resetBtn, R.id.confirmBtn, R.id.back})
    public void onClick(View view) {
        Calendar calender = Calendar.getInstance();
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.cangkuLayout:
                if (CangkuJsonArray == null) {
                    showToast("没有对应仓库数据");
                    return;
                }
                CangkuListDialog cangkuListDialog = new CangkuListDialog(this, CangkuJsonArray);
                cangkuListDialog.setmICangkuListResponse(this);
                cangkuListDialog.show();
                break;
            case R.id.huopinmuluLayout:
                intent.setClass(this, TE10Activity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.lastPandianLayout:
                DatePickerDialog mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        lastPandianTextView.setText(year + "-" + new DecimalFormat("00").format(month + 1) + "-" + new DecimalFormat("00").format(dayOfMonth));
                    }
                }, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
                mDatePickerDialog.show();
                break;
            case R.id.kuweiDownImgView:
                if (kuweiJsonArray == null) {
                    showToast("没有对应库位数据");
                    return;
                }
                KuWeiDialog kuWeiDialog = new KuWeiDialog(this, kuweiJsonArray);
                kuWeiDialog.setIKuWeiListResponse(this);
                kuWeiDialog.show();
                break;
            case R.id.resetBtn:
                setEmpty();
                break;
            case R.id.confirmBtn:
                if (TextUtils.isEmpty(cangkuTextView.getText().toString())) {
                    showToast("请选择仓库");
                    return;
                }

                if (!iSFixedCheck && percentEditView.getText().toString().trim().equals("")) {
                    showToast("请输入百分比");
                }

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("Storage", cangkuTextView.getText().toString().trim());
                map.put("Catalogue", huopinmuluTextView.getText().toString().trim());
                map.put("BeforeCheckTime", lastPandianTextView.getText().toString().trim());
                map.put("StoragePosition", kuweiEdit.getText().toString().trim());
                map.put("iSFixedCheck", iSFixedCheck);
                map.put("Percent", percentEditView.getText().toString().trim());
                JSONObject jsonObject = new JSONObject(map);
                intent.setClass(this, TE09Activity.class);
                intent.putExtra("CheckItem", jsonObject.toString());
                setResult(1, intent);
                finish();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void setEmpty() {
        cangkuTextView.setText("");
        huopinmuluTextView.setText("");
        lastPandianTextView.setText("");
        kuweiEdit.setText("");

        percentEditView.setText("");
        iSFixedCheck = true;
        gudingRadBtn.setChecked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == 1) {
                huopinmuluTextView.setText(data.getStringExtra("name"));
            }
        }
    }

    class GetStorage extends MyOkHttpExecute{

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetStorage,getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            dismissDialog();
            if (TextUtils.isEmpty(JsonString)) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        CangkuJsonArray = jsonObject.getJSONArray("data");
                        break;
                    case -1:
                        Intent intent = new Intent(TE0901Activity.this, TE01Activity.class);
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
            dismissDialog();
            showToast(error);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.gudingRadBtn:
                iSFixedCheck = true;
                percentEditView.setText("");
                percentEditView.setEnabled(false);
                break;
            case R.id.suijiRadBtn:
                iSFixedCheck = false;
                percentEditView.setEnabled(true);
                break;
        }
    }


    class GetStorageRackAsynctask extends MyOkHttpExecute {


        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetStorageRack,getMainLooper());
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
                        kuweiJsonArray = jsonObject.getJSONArray("data");
                        break;
                    case -1:
                        Intent intent = new Intent(TE0901Activity.this, TE01Activity.class);
                        startActivity(intent);
                        showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void faild(String error) {

        }
    }
}
