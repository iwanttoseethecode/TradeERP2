package gttrade.guantang.com.tradeerp.TE06;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.RecyclerViewDemo.DividerItemDecoration;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE04.TE0401Activity;
import gttrade.guantang.com.tradeerp.TE09.EventBusBean.RefreshBean;
import gttrade.guantang.com.tradeerp.ZXing.ScanCaptureActivity;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.base.MyApplication;
import gttrade.guantang.com.tradeerp.commondapter.CommonAdapter;
import gttrade.guantang.com.tradeerp.commondapter.ViewHolder;
import gttrade.guantang.com.tradeerp.custom.CommonDialog;
import gttrade.guantang.com.tradeerp.database.DataBaseHelper;
import gttrade.guantang.com.tradeerp.database.DataBaseOperate;
import gttrade.guantang.com.tradeerp.permission.AfterPermissionGranted;
import gttrade.guantang.com.tradeerp.permission.EasyPermissions;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.util.StringIsNumber;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE06Activity extends BaseActivity implements TextWatcher, EasyPermissions.PermissionCallbacks {


    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.scanImgBtn)
    ImageButton scanImgBtn;
    @BindView(R.id.refreshImgBtn)
    ImageButton refreshImgBtn;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.searchTxtView)
    TextView searchTxtView;
    @BindView(R.id.hpListView)
    ListView hpListView;
    @BindView(R.id.numbershow)
    TextView numbershow;
    @BindView(R.id.commitTextView)
    TextView commitTextView;
    @BindView(R.id.shoppingLayout)
    FrameLayout shoppingLayout;
    @BindView(R.id.delStrImgView)
    ImageView delStrImgView;

    private int requstsize = 100;//每次请求货品量的长度
    private int count;//还需要请求借口的次数
    private int totalcount;//总共请求接口多少次
    private DataBaseOperate dbo;
    private CommonDialog progressCommonDialog;
    private MyAdapter myAdapter;
    private List<Map<String, Object>> mList = new LinkedList<Map<String, Object>>();

    private GetPickItemsAsyncTask getPickItemsAsyncTask;
    private SubmitPickItemsAsyncTask submitPickItemsAsyncTask;

    private static final int REQUECT_CODE_SDCARD = 4;//请求码
    private String []permission={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,Manifest.permission.WRITE_SETTINGS};

    private AtomicBoolean onClickFlag = new AtomicBoolean(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te06);
        ButterKnife.bind(this);
        getPermiss();
        dbo = new DataBaseOperate(MyApplication.getContextObject());
        EventBus.getDefault().register(this);
        editText.addTextChangedListener(this);
        myAdapter = new MyAdapter(this, mList, R.layout.pick_hpitem);
        hpListView.setAdapter(myAdapter);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (getPickItemsAsyncTask != null){
            getPickItemsAsyncTask.cancel();
        }
        if (submitPickItemsAsyncTask != null){
            submitPickItemsAsyncTask.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setNumbershowStatus();

    }

    public void init() {
        if (!dbo.havePickedNum()){ // 如果拣货筐里面没有已拣货品，则从服务端同步数据下来
            if (NetWorkTool.checkNetworkState(this)) {
                dbo.deleteTable(DataBaseHelper.tb_Item);
                progressCommonDialog = new CommonDialog(this, R.layout.custom_progressbar_layout);
                progressCommonDialog.setDialogContentListener(new CommonDialog.DialogContentListener() {
                    @Override
                    public void contentExecute(View parent, Dialog dialog, Object[] objs) {
                        TextView titleTextView = progressCommonDialog.getView(R.id.title);
                        TextView cancelTextView = progressCommonDialog.getView(R.id.cancel);
                        TextView percentTextView = progressCommonDialog.getView(R.id.percentTextView);
                        ProgressBar pregressbar = progressCommonDialog.getView(R.id.mybar);
                        cancelTextView.setVisibility(View.GONE);
                        percentTextView.setVisibility(View.GONE);
                        pregressbar.setMax(100);
                        pregressbar.setProgress(0);
                    }
                });
                progressCommonDialog.setCancelable(false);
                progressCommonDialog.show();

                Map<String,String> requestBodyMap = new HashMap<>();
                requestBodyMap.put("topnum",String.valueOf(requstsize));
                requestBodyMap.put("itemid","0");
                getPickItemsAsyncTask = new GetPickItemsAsyncTask();
                getPickItemsAsyncTask.startWork(requestBodyMap);
            } else {
                showToast("网络未连接");
            }
        }else{// 如果拣货筐里面有已拣货品，则不从服务端同步数据下来，显示本地需拣货品就行了
            mList = dbo.selectUncompletehp__bySKU("");
            myAdapter.setData(mList);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ContainMapObject containMapObject) {
        if (containMapObject.isDeleteFlag()) {
            mList.remove(containMapObject.getMap());
        }
        setNumbershowStatus();
        mList = dbo.selectUncompletehp__bySKU(editText.getText().toString().trim());
        myAdapter.setData(mList);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshBean refreshBean){
        setNumbershowStatus();
        mList = dbo.selectUncompletehp__bySKU(editText.getText().toString().trim());
        myAdapter.setData(mList);
    }

    public void setNumbershowStatus() {
        int num = dbo.getNum_PickFinish();
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

    @OnClick({R.id.back, R.id.scanImgBtn, R.id.refreshImgBtn, R.id.delStrImgView, R.id.shoppingLayout, R.id.commitTextView})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.scanImgBtn:
                intent.setClass(this, ScanCaptureActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.refreshImgBtn:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("你确定要更新数据吗，已拣货品数量会被清空？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbo.deleteTable(DataBaseHelper.tb_Item);
                        init();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.delStrImgView:
                editText.setText("");
                break;
            case R.id.shoppingLayout:
//                导出数据库的操作
//                copyDataBase();

                intent.setClass(this, TE0601Activity.class);
                startActivity(intent);
                break;
            case R.id.commitTextView:
                if (onClickFlag.compareAndSet(true,false)){
                    showProgressDialog("正在提交数据");
                    List<Map<String, Object>> list = dbo.getCompleteHp();
                    if (list.isEmpty()){
                        showToast("没有可提交货品");
                        dismissDialog();
                        onClickFlag.compareAndSet(false,true);
                        return;
                    }
                    JSONArray myJSONArray = new JSONArray(list);
                    if (NetWorkTool.checkNetworkState(this)) {
                        Map<String,String> requestBodyMap = new HashMap<>();
                        requestBodyMap.put("returndata",myJSONArray.toString());
                        submitPickItemsAsyncTask = new SubmitPickItemsAsyncTask();
                        submitPickItemsAsyncTask.startWork(requestBodyMap);
                    } else {
                        showToast("网络未连接");
                    }
                }

                break;
        }
    }

    @AfterPermissionGranted(REQUECT_CODE_SDCARD)
    private void getPermiss(){
        //检查是否已经赋予了所有权限
        if (EasyPermissions.hasPermissions(this, permission)) {
            // 成功
            //自己的逻辑（比如调用相机）

        } else {
            //有权限没有被赋予去请求权限
            EasyPermissions.requestPermissions(this, "需要你的SD卡读写权限", REQUECT_CODE_SDCARD, permission);
        }
    }

    private void copyDataBase() {
        InputStream is = null;
        OutputStream os = null;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + File.separator + DataBaseHelper.DBNAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            is = new FileInputStream(getDatabasePath(DataBaseHelper.DBNAME));
            os = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int c;
            while ((c = is.read(b)) > -1) {
                os.write(b, 0, c);
            }
            os.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                editText.setText(data.getStringExtra("result"));
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 0) {
            delStrImgView.setVisibility(View.VISIBLE);
        } else {
            delStrImgView.setVisibility(View.GONE);
        }
    }

    @Override

    public void afterTextChanged(Editable editable) {
        mList = dbo.selectUncompletehp__bySKU(editable.toString().trim());
        myAdapter.setData(mList);
    }

    //重写onRequestPermissionsResult将值传递给EasyPermissions.onRequestPermissionsResult();
    //固定写法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    //请求后的回掉，成功
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //判断请求成功的权限数量和需要请求的权限数量是否相同，相同表示全部权限都请求成功
        if (perms.size()==permission.length){
            //获得或有权限后的逻辑
        }
    }
    //失败
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //这里返回请求失败的权限
        //这里的逻辑可写可不写。请随意。
    }



    class GetPickItemsAsyncTask extends MyOkHttpExecute {

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetPickItems,getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        JSONObject dataJsonObject = jsonObject.getJSONObject("Data");
                        JSONArray jsonArray = dataJsonObject.getJSONArray("griddata");
                        int hpsize = Integer.parseInt(StringIsNumber.getNumberString(dataJsonObject.getString("totalnum")));
                        if (hpsize % requstsize == 0) {
                            totalcount = hpsize / requstsize;
                            count = totalcount;
                        } else {
                            totalcount = hpsize / requstsize + 1;
                            count = totalcount;
                        }
                        dbo.Update_insert_hp(jsonArray);
                        count--;
                        ((ProgressBar) progressCommonDialog.getView(R.id.mybar)).setMax(totalcount);
                        ((ProgressBar) progressCommonDialog.getView(R.id.mybar)).setProgress(totalcount - count);
                        if (count > 0) {
                            Map<String,String> requestBodyMap = new HashMap<>();
                            requestBodyMap.put("topnum",String.valueOf(requstsize));
                            requestBodyMap.put("itemid",jsonArray.getJSONObject(jsonArray.length() - 1).getString("ItemID"));
                            getPickItemsAsyncTask = new GetPickItemsAsyncTask();
                            getPickItemsAsyncTask.startWork(requestBodyMap);
                        } else {
                            progressCommonDialog.dismiss();
                            setNumbershowStatus();
                            mList = dbo.selectUncompletehp__bySKU(editText.getText().toString().trim());
                            myAdapter.setData(mList);
                        }
                        break;
                    case 2:
                        JSONObject dataJsonObject1 = jsonObject.getJSONObject("Data");
                        JSONArray jsonArray1 = dataJsonObject1.getJSONArray("griddata");
                        dbo.Update_insert_hp(jsonArray1);
                        count--;
                        ((ProgressBar) progressCommonDialog.getView(R.id.mybar)).setProgress(totalcount - count);
                        if (count > 0) {
                            Map<String,String> requestBodyMap = new HashMap<>();
                            requestBodyMap.put("topnum",String.valueOf(requstsize));
                            requestBodyMap.put("itemid",jsonArray1.getJSONObject(jsonArray1.length() - 1).getString("ItemID"));
                            getPickItemsAsyncTask = new GetPickItemsAsyncTask();
                            getPickItemsAsyncTask.startWork(requestBodyMap);
                        } else {
                            progressCommonDialog.dismiss();
                            setNumbershowStatus();
                            mList = dbo.selectUncompletehp__bySKU(editText.getText().toString().trim());
                            myAdapter.setData(mList);
                        }
                        break;
                    case -1:
                        progressCommonDialog.dismiss();
                        Intent intent = new Intent(TE06Activity.this, TE01Activity.class);
                        startActivity(intent);
                        showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        showToast(jsonObject.getString("Message"));
                        progressCommonDialog.dismiss();
                        break;
                    default:
                        progressCommonDialog.dismiss();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progressCommonDialog.dismiss();
            }
        }

        @Override
        public void faild(String error) {
            progressCommonDialog.dismiss();
            showToast(error);
        }
    }

    class SubmitPickItemsAsyncTask extends MyOkHttpExecute {


        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.SubmitPickItems,getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                onClickFlag.compareAndSet(false,true);
                dismissDialog();
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        showToast(jsonObject.getString("Message"));
                        dbo.deleteTable(DataBaseHelper.tb_Item);
                        finish();
//                        setNumbershowStatus();
                        break;
                    case 3:
                        showToast(jsonObject.getString("Message"));
                        JSONObject dataJsonObject1 = jsonObject.getJSONObject("Data");
                        JSONArray jsonArray1 = dataJsonObject1.getJSONArray("griddata");
                        CommonDialog mCommonDialog = new CommonDialog(TE06Activity.this,R.layout.listview_dialog_layout,R.style.yuanjiao_dialog);
                        mCommonDialog.setDialogContentListener(new CommonDialog.DialogContentListener() {
                            @Override
                            public void contentExecute(View parent, final Dialog dialog, Object[] objs) {
                                TextView titleTxtView = (TextView) parent.findViewById(R.id.titleTxtView);
                                RecyclerView errorlistview = (RecyclerView) parent.findViewById(R.id.errorlistview);
                                Button cancelBtn = (Button) parent.findViewById(R.id.cancelBtn);
                                cancelBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        finish();
                                    }

                                });
                                ErrorHpRecycleAdapter errorHpRecycleAdapter = new ErrorHpRecycleAdapter(MyApplication.getContextObject());
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.getContextObject());
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                errorlistview.setLayoutManager(linearLayoutManager);
                                errorlistview.setAdapter(errorHpRecycleAdapter);
                                errorlistview.setItemAnimator(new DefaultItemAnimator());
                                errorlistview.addItemDecoration(new DividerItemDecoration(MyApplication.getContextObject(), LinearLayoutManager.VERTICAL, R.color.themeBackgroundGrey, 1));
                                errorHpRecycleAdapter.setData((JSONArray) objs[0]);
                            }
                        },jsonArray1);
                        mCommonDialog.show();
                        dbo.deleteTable(DataBaseHelper.tb_Item);
                        break;
                    case -1:
                        //// TODO: 2016/10/25
                        Intent intent = new Intent(TE06Activity.this, TE01Activity.class);
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


    class MyAdapter extends CommonAdapter<Map<String, Object>> {

//        private AsyncImageLoader myImageLoader = new AsyncImageLoader();

        public MyAdapter(Context mContext, List<Map<String, Object>> mList, int LayoutId) {
            super(mContext, mList, LayoutId);
        }

        @Override
        public void convert(ViewHolder holder, final Map<String, Object> item) {
            ImageView picImgView = holder.getView(R.id.picImgView);
            TextView SKUcontentTxtView = holder.getView(R.id.SKUcontentTxtView);
            TextView PickNumTxtView = holder.getView(R.id.PickNumTxtView);
            TextView CKcontentTxtView = holder.getView(R.id.CKcontentTxtView);
            TextView KWcontentTxtView = holder.getView(R.id.KWcontentTxtView);
//            TextView pickTxtView = holder.getView(R.id.pickTxtView);
            TextView PickedNumTxtView = holder.getView(R.id.PickedNumTxtView);
            TextView xiegangTextView = holder.getView(R.id.xiegangTextView);
            TextView NameTxtView = holder.getView(R.id.NameTxtView);
            LinearLayout contentLayout = holder.getView(R.id.contentLayout);


            SKUcontentTxtView.setText(item.get("SKU") == null || item.get("SKU").equals("null") ? "" : item.get("SKU").toString());
            NameTxtView.setText(item.get("ItemName")==null || item.get("ItemName").equals("null")?"":item.get("ItemName").toString());
            String pickNum = item.get("PickNum").toString();
            if (Double.parseDouble(item.get("PickedNum").toString()) > 0) {
                PickedNumTxtView.setText(item.get("PickedNum").toString());
                xiegangTextView.setText("/");
                PickNumTxtView.setText(StringIsNumber.getNumberString(pickNum));
            } else {
                PickedNumTxtView.setText("");
                xiegangTextView.setText("");
                PickNumTxtView.setText(StringIsNumber.getNumberString(pickNum));
            }

            CKcontentTxtView.setText(item.get("StorageName") == null || item.get("StorageName").equals("null") ? "" : item.get("StorageName").toString());
            KWcontentTxtView.setText(item.get("StoragePosition") == null || item.get("StoragePosition").equals("null") ? "" : item.get("StoragePosition").toString());

//            String ID = item.get("ID").toString();
//            picImgView.setTag(ID);
//            myImageLoader.loadBitmap(ID.equals("null") ? "" : ID,
//                    item.get("PicUrl_Small").toString()==null ? "" : item.get("PicUrl_Small").toString(),
//                    picImgView);
            Object picurl =item.get("PicUrl_Small");
            Glide.with(context).load(picurl.toString()).centerCrop().placeholder(R.mipmap.pic_defalut)
                    .crossFade().into(picImgView);
//            if (!picurl.toString().equals("") && picurl!=null && !picurl.toString().equals("null")){
//                Picasso.with(context).load().into(picImgView);
//            }else{
//                picImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.pic_defalut));
//            }
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PickDialog mPickDialog = new PickDialog(TE06Activity.this, R.style.ButtonDialogStyle, item, dbo);
                    mPickDialog.setCanceledOnTouchOutside(false);
                    Window win = mPickDialog.getWindow();
                    win.getDecorView().setPadding(0, 0, 0, 0);
                    win.setGravity(Gravity.BOTTOM);
                    WindowManager.LayoutParams lp = win.getAttributes();
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    win.setAttributes(lp);
                    mPickDialog.show();
                }
            });
        }
    }

}
