package gttrade.guantang.com.tradeerp.TE13;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE10.TE10Activity;
import gttrade.guantang.com.tradeerp.TE12.dialog.AbstractListDialog;
import gttrade.guantang.com.tradeerp.TE13.bean.CangkuListBean;
import gttrade.guantang.com.tradeerp.TE13.bean.FilterItemJson;
import gttrade.guantang.com.tradeerp.TE13.bean.KuweiListBean;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE13Activity extends BaseActivity {


    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.titleTxtView)
    TextView titleTxtView;
    @BindView(R.id.titlelayout)
    LinearLayout titlelayout;
    @BindView(R.id.huopinmuluTextView)
    TextView huopinmuluTextView;
    @BindView(R.id.huopinmuluLayout)
    LinearLayout huopinmuluLayout;
    @BindView(R.id.kuweiEdit)
    EditText kuweiEdit;
    @BindView(R.id.kuweiDownImgView)
    ImageView kuweiDownImgView;
    @BindView(R.id.kuweiLayout)
    LinearLayout kuweiLayout;
    @BindView(R.id.jiagefromEdit)
    EditText jiagefromEdit;
    @BindView(R.id.jiagetoEdit)
    EditText jiagetoEdit;
    @BindView(R.id.paixuEdit)
    EditText paixuEdit;
    @BindView(R.id.paixunDownImgView)
    ImageView paixunDownImgView;
    @BindView(R.id.cancelTxtView)
    TextView cancelTxtView;
    @BindView(R.id.confirmTxtView)
    TextView confirmTxtView;
    @BindView(R.id.cangkuTextView)
    TextView cangkuTextView;
    @BindView(R.id.cangkuLayout)
    LinearLayout cangkuLayout;

    private Integer StorageID;
    private String kuwei,CatalogueID;

    private GetStorage getStorage;
    private GetStorageRackAsynctask getStorageRackAsynctask;
    private CangkuListBean cangkuListBean;
    private KuweiListBean kuweiListBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te13);
        ButterKnife.bind(this);

        showProgressDialog("");
        getStorage = new GetStorage();
        getStorage.startWork(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == 1) {
                huopinmuluTextView.setText(data.getStringExtra("name"));
                CatalogueID=data.getStringExtra("id");

            }
        }
    }

    @OnClick({R.id.back, R.id.huopinmuluLayout, R.id.cangkuLayout, R.id.kuweiDownImgView,R.id.confirmTxtView,R.id.cancelTxtView})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.huopinmuluLayout:
                intent.setClass(this, TE10Activity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.cangkuLayout:
                if (cangkuListBean.getData() == null) {
                    showToast("没有对应仓库数据");
                    return;
                }
                CangkuDialog cangkuDialog = new CangkuDialog(this, cangkuListBean.getData(), new AbstractListDialog.IOnItemClickDataReturn<CangkuListBean.DataBean>() {
                    @Override
                    public String datareturn(CangkuListBean.DataBean data, AlertDialog dialog) {
                        StorageID = data.getID();
                        cangkuTextView.setText(data.getName());
                        Map<String, String> requestBodyMap = new HashMap<String, String>();
                        requestBodyMap.put("storageid", String.valueOf(StorageID));
                        getStorageRackAsynctask = new GetStorageRackAsynctask();
                        getStorageRackAsynctask.startWork(requestBodyMap);
                        dialog.dismiss();
                        return null;
                    }
                });
                cangkuDialog.show();
                break;
            case R.id.kuweiDownImgView:
                if (kuweiListBean==null ) {
                    showToast("没有对应库位数据");
                    return;
                }
                KuWeiDialog kuWeiDialog = new KuWeiDialog(this, kuweiListBean.getData(), new AbstractListDialog.IOnItemClickDataReturn<KuweiListBean.DataBean>() {
                    @Override
                    public String datareturn(KuweiListBean.DataBean data, AlertDialog dialog) {

                        kuwei = data.getRackName();
                        kuweiEdit.setText(kuwei);
                        dialog.dismiss();
                        return null;
                    }
                });
                kuWeiDialog.show();
                break;
            case R.id.confirmTxtView:
                FilterItemJson filterItemJson = new FilterItemJson();

                if (CatalogueID!=null){
                    filterItemJson.setCatalogueID(Integer.parseInt(CatalogueID));
                }else{
                    filterItemJson.setCatalogueID(0);
                }
                if (StorageID!=null){
                    filterItemJson.setStorageID(StorageID);
                }else{
                    filterItemJson.setStorageID(0);
                }

                filterItemJson.setStoragePosition(kuwei);
                String jiagefrom = jiagefromEdit.getText().toString();
                String jiageto = jiagetoEdit.getText().toString();
                if (!TextUtils.isEmpty(jiagefrom)&&!TextUtils.isEmpty(jiageto)){
                    filterItemJson.setMinPrice(Double.valueOf(jiagefrom));
                    filterItemJson.setMaxPrice(Double.valueOf(jiageto));
                }else{
                    filterItemJson.setMinPrice(0.0);
                    filterItemJson.setMaxPrice(0.0);
                }

                intent.putExtra("filterItemJson",filterItemJson);
                setResult(1,intent);
                finish();
                break;
            case R.id.cancelTxtView:
                finish();
                break;
        }
    }

    class GetStorage extends MyOkHttpExecute {

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetStorage, getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            dismissDialog();
            if (TextUtils.isEmpty(JsonString)) {
                return;
            }
            cangkuListBean = JSON.parseObject(JsonString, CangkuListBean.class);
            switch (cangkuListBean.getStatus()) {
                case 1:

                    break;
                case -1:
                    Intent intent = new Intent(TE13Activity.this, TE01Activity.class);
                    startActivity(intent);
                    showToast(cangkuListBean.getMessage());
                    break;
                case -2:
                    showToast(cangkuListBean.getMessage());
                    break;
                default:
                    showToast(cangkuListBean.getMessage());
                    break;
            }

        }

        @Override
        public void faild(String error) {
            dismissDialog();
            showToast(error);
        }
    }

    class GetStorageRackAsynctask extends MyOkHttpExecute {


        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetStorageRack, getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            if (TextUtils.isEmpty(JsonString)) {
                return;
            }
            kuweiListBean = JSON.parseObject(JsonString,KuweiListBean.class);
            switch (kuweiListBean.getStatus()) {
                case 1:

                    break;
                case -1:
                    Intent intent = new Intent(TE13Activity.this, TE01Activity.class);
                    startActivity(intent);
                    showToast(kuweiListBean.getMessage());
                    break;
                case -2:
                    showToast(kuweiListBean.getMessage());
                    break;
                default:
                    showToast(kuweiListBean.getMessage());
                    break;
            }
        }

        @Override
        public void faild(String error) {

        }
    }

}
