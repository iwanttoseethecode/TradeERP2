package gttrade.guantang.com.tradeerp.TE12;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE12.bean.FilterOrderJson;
import gttrade.guantang.com.tradeerp.TE12.bean.FilterOrdersInitialDataBean;
import gttrade.guantang.com.tradeerp.TE12.dialog.AbstractListDialog;
import gttrade.guantang.com.tradeerp.TE12.dialog.EShopDialog;
import gttrade.guantang.com.tradeerp.TE12.dialog.ShipmentDialog;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE12Activity extends BaseActivity {


    @BindView(R.id.Num1ImgView)
    CheckBox Num1ImgView;
    @BindView(R.id.Num2ImgView)
    CheckBox Num2ImgView;
    @BindView(R.id.Num3ImgView)
    CheckBox Num3ImgView;
    @BindView(R.id.Num4ImgView)
    CheckBox Num4ImgView;
    @BindView(R.id.Num5ImgView)
    CheckBox Num5ImgView;
    @BindView(R.id.Num6ImgView)
    CheckBox Num6ImgView;
    @BindView(R.id.dianpuEdit)
    EditText dianpuEdit;
    @BindView(R.id.countryTextView)
    TextView countryTextView;
    @BindView(R.id.wuliuEdit)
    EditText wuliuEdit;
    @BindView(R.id.fahuocangEdit)
    EditText fahuocangEdit;
    @BindView(R.id.attributeEdit)
    EditText attributeEdit;
    @BindView(R.id.fukuanfromTimeTextView)
    TextView fukuanfromTimeTextView;
    @BindView(R.id.fukuantoTimeTextView)
    TextView fukuantoTimeTextView;
    @BindView(R.id.fahuofromTimeTextView)
    TextView fahuofromTimeTextView;
    @BindView(R.id.fahuotoTimeTextView)
    TextView fahuotoTimeTextView;
    @BindView(R.id.maijiaEdit)
    EditText maijiaEdit;
    @BindView(R.id.SKUEdit)
    EditText SKUEdit;
    @BindView(R.id.laterLayout)
    LinearLayout laterLayout;

    private FilterOrdersInitialDataAsyncTask filterOrdersInitialDataAsyncTask;

    private FilterOrdersInitialDataBean filterOrdersInitialDataBean;

    private int eShopID,fahuocangID;

    private LinkedList<CheckBox> CheckBoxList = new LinkedList<>();

    private int fukuanshijianNum = 0,fahuoshijianNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te12);
        ButterKnife.bind(this);

        init();
    }

    public void init() {
        showProgressDialog("加载数据");
        FilterOrdersInitialDataAsyncTask filtrateOrdersInitialDataAsyncTask = new FilterOrdersInitialDataAsyncTask();
        filtrateOrdersInitialDataAsyncTask.startWork(null);

        CheckBoxList.offerLast(Num1ImgView);
        CheckBoxList.offerLast(Num2ImgView);
        CheckBoxList.offerLast(Num3ImgView);
        CheckBoxList.offerLast(Num4ImgView);
        CheckBoxList.offerLast(Num5ImgView);
        CheckBoxList.offerLast(Num6ImgView);

//        checkboxLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (filterOrdersInitialDataAsyncTask != null) {
            filterOrdersInitialDataAsyncTask.cancel();
        }
        filterOrdersInitialDataBean = null;
    }

//    private void checkboxLayout(){
//        ScreenMeasure screenMeasure = getScreenMeasure();
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(screenMeasure.width/3-100,(screenMeasure.width/3-100)/2);
//        lp.setMargins(10,10,10,10);
//        Num1ImgView.setLayoutParams(lp);
//        Num2ImgView.setLayoutParams(lp);
//        Num3ImgView.setLayoutParams(lp);
//        Num4ImgView.setLayoutParams(lp);
//        Num5ImgView.setLayoutParams(lp);
//        Num6ImgView.setLayoutParams(lp);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (requestCode == 1){
                if (!TextUtils.isEmpty(data.getStringExtra("country"))){
                    countryTextView.setText(data.getStringExtra("country"));
                }
            }
        }
    }

    @OnClick({R.id.back, R.id.dianpuDownImgView, R.id.countryLayout, R.id.wuliuDownImgView, R.id.fahuocangDownImgView, R.id.attributeDownImgView, R.id.fukuanfromTimeLayout, R.id.fukuantoTimeLayout, R.id.fahuofromTimeLayout, R.id.fahuotoTimeLayout, R.id.cancelTxtView, R.id.confirmTxtView,R.id.showallLayout})
    public void onClick(View view) {
        Calendar calendar = Calendar.getInstance();
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.dianpuDownImgView:
                EShopDialog eShopDialog = new EShopDialog(this, filterOrdersInitialDataBean.getData().getEShop(), new AbstractListDialog.IOnItemClickDataReturn<FilterOrdersInitialDataBean.DataBean.EShopBean>() {
                    @Override
                    public String datareturn(FilterOrdersInitialDataBean.DataBean.EShopBean data, AlertDialog dialog) {
                        eShopID = data.getID();
                        dianpuEdit.setText(data.getName());
                        dialog.dismiss();
                        return null;
                    }
                });
                eShopDialog.show();
                break;
            case R.id.countryLayout:
                intent.setClass(this,TE1201Activity.class);
                intent.putExtra("countrys", (Serializable) filterOrdersInitialDataBean.getData().getCountry());
                startActivityForResult(intent,1);
                break;
            case R.id.wuliuDownImgView:
                ShipmentDialog shipmentDialog = new ShipmentDialog(this, filterOrdersInitialDataBean.getData().getShipment(), new AbstractListDialog.IOnItemClickDataReturn<String>() {
                    @Override
                    public String datareturn(String data, AlertDialog dialog) {
                        wuliuEdit.setText(data);
                        dialog.dismiss();
                        return null;
                    }
                });
                shipmentDialog.show();
                break;
            case R.id.fahuocangDownImgView:
                EShopDialog fahuocangDialog = new EShopDialog(this, filterOrdersInitialDataBean.getData().getEShop(), new AbstractListDialog.IOnItemClickDataReturn<FilterOrdersInitialDataBean.DataBean.EShopBean>() {
                    @Override
                    public String datareturn(FilterOrdersInitialDataBean.DataBean.EShopBean data, AlertDialog dialog) {
                        fahuocangID = data.getID();
                        fahuocangEdit.setText(data.getName());
                        dialog.dismiss();
                        return null;
                    }
                });
                fahuocangDialog.show();
                break;
            case R.id.attributeDownImgView:
                final ShipmentDialog attributeDialog = new ShipmentDialog(this, filterOrdersInitialDataBean.getData().getAttribute(), new AbstractListDialog.IOnItemClickDataReturn<String>() {
                    @Override
                    public String datareturn(String data, AlertDialog dialog) {
                        attributeEdit.setText(data);
                        dialog.dismiss();
                        return null;
                    }
                });
                attributeDialog.show();
                break;
            case R.id.fukuanfromTimeLayout:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fukuanfromTimeTextView.setText(year + "-" + new DecimalFormat("00").format(month + 1) + "-" + new DecimalFormat("00").format(dayOfMonth));
                        fukuanshijianNum++;
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            case R.id.fukuantoTimeLayout:
                DatePickerDialog datePickerDialog1 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fukuantoTimeTextView.setText(year + "-" + new DecimalFormat("00").format(month + 1) + "-" + new DecimalFormat("00").format(dayOfMonth));
                        fukuanshijianNum++;
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog1.show();
                break;
            case R.id.fahuofromTimeLayout:
                DatePickerDialog datePickerDialog2 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        fahuofromTimeTextView.setText(year + "-" + new DecimalFormat("00").format(month + 1) + "-" + new DecimalFormat("00").format(dayOfMonth));
                        fahuoshijianNum++;
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog2.show();
                break;
            case R.id.fahuotoTimeLayout:
                DatePickerDialog datePickerDialog3 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fahuotoTimeTextView.setText(year + "-" + new DecimalFormat("00").format(month + 1) + "-" + new DecimalFormat("00").format(dayOfMonth));
                        fahuoshijianNum++;
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog3.show();
                break;
            case R.id.showallLayout:
                if (laterLayout.isShown()){
                    laterLayout.setVisibility(View.GONE);
                }else{
                    laterLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.cancelTxtView:
                finish();
                break;
            case R.id.confirmTxtView:

                if (fahuoshijianNum % 2 == 1){
                    showToast("请完整匹配发货时间");
                    return;
                }
                if (fukuanshijianNum % 2 == 1){
                    showToast("请完整匹配付款时间时间");
                    return;
                }

                FilterOrderJson filterOrderJson = new FilterOrderJson();
                List<String> list = new ArrayList<String>();
                if(Num1ImgView.isChecked()){
                    list.add(Num1ImgView.getTag().toString());
                }
                if(Num2ImgView.isChecked()){
                    list.add(Num2ImgView.getTag().toString());
                }
                if(Num3ImgView.isChecked()){
                    list.add(Num3ImgView.getTag().toString());
                }
                if(Num4ImgView.isChecked()){
                    list.add(Num4ImgView.getTag().toString());
                }
                if(Num5ImgView.isChecked()){
                    list.add(Num5ImgView.getTag().toString());
                }
                if(Num6ImgView.isChecked()){
                    list.add(Num6ImgView.getTag().toString());
                }
                filterOrderJson.setPlatform(list);
                filterOrderJson.seteShopID(eShopID);
                filterOrderJson.setCountry(countryTextView.getText().toString());
                filterOrderJson.setShipment(wuliuEdit.getText().toString());
                filterOrderJson.setAttribute(attributeEdit.getText().toString());
                filterOrderJson.setPaidStartTime(fukuanfromTimeTextView.getText().toString());
                filterOrderJson.setPaidEndTime(fukuantoTimeTextView.getText().toString());
                filterOrderJson.setShippedStartTime(fahuofromTimeTextView.getText().toString());
                filterOrderJson.setShippedEndTime(fahuotoTimeTextView.getText().toString());
                filterOrderJson.setStorageID(fahuocangID);
                filterOrderJson.setBuyerID(maijiaEdit.getText().toString());
                filterOrderJson.setItemSKU(SKUEdit.getText().toString());
                intent.putExtra("filterOrderJson",filterOrderJson);
                setResult(1,intent);
                finish();
                break;
        }
    }



    class FilterOrdersInitialDataAsyncTask extends MyOkHttpExecute {

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.FiltrateOrdersInitialData, getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            dismissDialog();
            if (JsonString.isEmpty()) {
                return;
            }
            filterOrdersInitialDataBean = JSON.parseObject(JsonString, FilterOrdersInitialDataBean.class);

            switch (filterOrdersInitialDataBean.getStatus()) {
                case 1:
                    showPlatform();
                    break;
                case -1:
                    Toast.makeText(TE12Activity.this, filterOrdersInitialDataBean.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TE12Activity.this, TE01Activity.class);
                    startActivity(intent);
                    break;
                default:
                    Toast.makeText(TE12Activity.this, filterOrdersInitialDataBean.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void faild(String error) {
            dismissDialog();
        }
    }

    private void showPlatform() {
        List<String> platformList = filterOrdersInitialDataBean.getData().getPlatform();
        Iterator<String> it = platformList.iterator();

        while (it.hasNext()) {
            String platformName = it.next();
            CheckBox checkBox = CheckBoxList.pollFirst();
            checkBox.setVisibility(View.VISIBLE);
            if (platformName.equals("淘宝")) {
                checkBox.setBackground(getResources().getDrawable(R.drawable.taobao_selector_checkbutton));
                checkBox.setTag("淘宝");
            } else if (platformName.equals("eBay")) {
                checkBox.setBackground(getResources().getDrawable(R.drawable.ebay_selector_checkbutton));
                checkBox.setTag("eBay");
            } else if (platformName.equals("AliExpress")) {
                checkBox.setBackground(getResources().getDrawable(R.drawable.aliexpress_selector_chackbutton));
                checkBox.setTag("AliExpress");
            } else if (platformName.equals("Amazon")) {
                checkBox.setBackground(getResources().getDrawable(R.drawable.amazon_selector_checkbutton));
                checkBox.setTag("Amazon");
            } else if (platformName.equals("阿里巴巴")) {
                checkBox.setBackground(getResources().getDrawable(R.drawable.alibaba_selector_checkbutton));
                checkBox.setTag("阿里巴巴");
            } else if (platformName.equals("其他")) {
                checkBox.setBackground(getResources().getDrawable(R.drawable.other_selector_checkbutton));
                checkBox.setTag("其他");
            }
        }
    }
}
