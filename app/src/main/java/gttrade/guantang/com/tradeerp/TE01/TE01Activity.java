package gttrade.guantang.com.tradeerp.TE01;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE02.TE02Activity;
import gttrade.guantang.com.tradeerp.TE03.TE03Activity;
import gttrade.guantang.com.tradeerp.TE06.TE06Activity;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.base.MyApplication;
import gttrade.guantang.com.tradeerp.custom.CommonDialog;
import gttrade.guantang.com.tradeerp.database.DataBaseHelper;
import gttrade.guantang.com.tradeerp.database.DataBaseManager;
import gttrade.guantang.com.tradeerp.database.DataBaseObject;
import gttrade.guantang.com.tradeerp.database.DataBaseOperate;
import gttrade.guantang.com.tradeerp.shareprefence.shareprefenceBean;
import gttrade.guantang.com.tradeerp.updateVersionutil.UpdateVersion_2;
import gttrade.guantang.com.tradeerp.util.ActivityAnimUtil;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.util.UpdateVersion;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;

public class TE01Activity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.serverTxt)
    EditText serverTxt;
    @BindView(R.id.companyEdit)
    EditText companyEdit;
    @BindView(R.id.usernameEdit)
    EditText usernameEdit;
    @BindView(R.id.passwordEdit)
    EditText passwordEdit;
    @BindView(R.id.eye)
    CheckBox eye;
    @BindView(R.id.loginBtn)
    Button loginBtn;

    private PopupWindow serverPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te01);
        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        init();

        if (System.currentTimeMillis() - MyApplication.getInstance().getSharedPreferences().getLong(shareprefenceBean.NOT_UPDATA, 0) > 3
                * 24 * 60 * 60 * 1000) {
            if (NetWorkTool.checkNetworkState(this)) {
                new ApkUpdateAsyncTesk().execute();
            }
        }
    }

    public void init() {
        eye.setOnCheckedChangeListener(this);
        if (MyApplication.getInstance().getSharedPreferences().getBoolean(shareprefenceBean.PASSWORDSHOW, true)) {
            eye.setBackgroundResource(R.mipmap.eye_org);
        } else {
            eye.setBackgroundResource(R.mipmap.eye_gray);
        }

        serverTxt.setText(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.SERVERURL, ""));
        companyEdit.setText(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.COMPANY, ""));
        usernameEdit.setText(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.USERNAME, ""));
        passwordEdit.setText(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.PASSWORD, ""));
    }

    @OnClick({R.id.downImgView, R.id.loginBtn, R.id.experienceLayout,R.id.register_Layout})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.downImgView:
                serverPopupwindow();
                break;
            case R.id.loginBtn:
                if (serverTxt.getText().toString().isEmpty()) {
                    showToast("请输入服务器地址");
                    return;
                } else if (companyEdit.getText().toString().isEmpty()) {
                    showToast("请输入公司名称");
                    return;
                } else if (usernameEdit.getText().toString().isEmpty()) {
                    showToast("请输入用户名");
                    return;
                } else if (passwordEdit.getText().toString().isEmpty()) {
                    showToast("请输入密码");
                    return;
                }
                if (NetWorkTool.checkNetworkState(this)) {
                    showProgressDialog("正在登录", false,false);
                    MyApplication.getInstance().getSharedPreferences().edit().putInt(shareprefenceBean.LOGINFLAG, 1).commit();
                    new LoginAsyncTask().execute(serverTxt.getText().toString().trim(), companyEdit.getText().toString().trim(), usernameEdit.getText().toString().trim(), passwordEdit.getText().toString().trim());
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("网络不给力，是否离线登录？");
                    builder.setPositiveButton("立即登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (usernameEdit.getText().toString().trim().equals(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.USERNAME, "")) && passwordEdit.getText().toString().trim().equals(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.PASSWORD, ""))) {
                                MyApplication.getInstance().getSharedPreferences().edit().putInt(shareprefenceBean.LOGINFLAG, 1).commit();
                                Intent intent = new Intent(TE01Activity.this, TE06Activity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                showToast("用户名或密码错误");
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setPositiveButton("暂不登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                }
                break;
            case R.id.experienceLayout:
                if (NetWorkTool.checkNetworkState(this)) {
                    showProgressDialog("正在登录", false,false);
                    MyApplication.getInstance().getSharedPreferences().edit().putInt(shareprefenceBean.LOGINFLAG, 2).commit();
                    new LoginAsyncTask().execute(MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANSERVERURL, "gttrade.cn"),
                            MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANCOMPANY, "测试账套"),
                            MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANUSERNAME, "admin"),
                            MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.TIYANPASSWORD, "admin"));
                } else {
                    showToast("体验账号需要联网使用");
                }
                break;
            case R.id.register_Layout:
                intent.setClass(this, TE03Activity.class);
                startActivity(intent);
                break;
        }
    }

    public void serverPopupwindow() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popupwindow_list, null);
        ListView mListView = (ListView) view.findViewById(R.id.popuplist);
        ArrayAdapter<String> adpter = new ArrayAdapter<String>(this, R.layout.popupwindow_list_textview, new String[]{"gttrade.cn"});
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                serverTxt.setText(adapterView.getAdapter().getItem(i).toString().trim());
                serverPopupWindow.dismiss();
            }
        });
        mListView.setAdapter(adpter);
        int width = serverTxt.getWidth();
        serverPopupWindow = new PopupWindow(view, width, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 这个是为了点击“返回Back”也能使其消失.
        serverPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 使其聚焦
        serverPopupWindow.setFocusable(true);
        // 设置允许在外点击消失
        serverPopupWindow.setOutsideTouchable(true);
        // 刷新状态
        serverPopupWindow.update();
        serverPopupWindow.showAsDropDown(serverTxt);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.eye:
                if (b) {
                    passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eye.setBackgroundResource(R.mipmap.eye_org);
                } else {
                    passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eye.setBackgroundResource(R.mipmap.eye_gray);
                }
                MyApplication.getInstance().getSharedPreferences().edit().putBoolean(shareprefenceBean.PASSWORDSHOW, b).commit();
                break;
        }
    }


    class LoginAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String jsonString = MyWebservice.Login(strings[0], strings[1], strings[2], strings[3]);
            return jsonString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissDialog();
            if (s==null || s.equals("")){
                Toast.makeText(TE01Activity.this, "登录失败", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                final JSONObject jsonObject = new JSONObject(s);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        if (!MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.SERVERURL, "").equals(serverTxt.getText().toString().trim()) ||
                                !MyApplication.getInstance().getSharedPreferences().getString(shareprefenceBean.COMPANY, "").equals(companyEdit.getText().toString().trim())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(TE01Activity.this);
                            builder.setMessage("帐套已切换，继续登陆将会清空之前的本地数据");
                            builder.setPositiveButton("继续登陆", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

//                                    deleteDatabase(DataBaseHelper.DBNAME);
                                    DataBaseOperate dataBaseOperate = new DataBaseOperate(getApplicationContext());
                                    dataBaseOperate.deleteTable(DataBaseHelper.tb_Orders);
                                    dataBaseOperate.deleteTable(DataBaseHelper.tb_Item);
                                    dataBaseOperate.deleteTable(DataBaseHelper.tb_OrderTransaction);
                                    dataBaseOperate.deleteTable(DataBaseHelper.tb_HpCatalogue);
                                    dataBaseOperate.deleteTable(DataBaseHelper.tb_PanDianList);
                                    dataBaseOperate.deleteTable(DataBaseHelper.tb_fahuoInfo);
                                    //保存 登录信息
                                    saveShareprefenceBeanAndStartActivity(jsonObject);
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("取消登录", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        } else {
                            //保存 登录信息
                            saveShareprefenceBeanAndStartActivity(jsonObject);
                        }
                        break;
                    case -1:
                        showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        showToast(jsonObject.getString("Message"));
                        break;
                    case -3:
                        showToast(jsonObject.getString("Message"));
                        break;
                    case -4:
                        showToast(jsonObject.getString("Message"));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveShareprefenceBeanAndStartActivity(JSONObject jsonObject){
        MyApplication.getInstance().getSharedPreferences().edit().putString(shareprefenceBean.SERVERURL, serverTxt.getText().toString().trim()).commit();
        MyApplication.getInstance().getSharedPreferences().edit().putString(shareprefenceBean.COMPANY, companyEdit.getText().toString().trim()).commit();
        MyApplication.getInstance().getSharedPreferences().edit().putString(shareprefenceBean.USERNAME, usernameEdit.getText().toString().trim()).commit();
        MyApplication.getInstance().getSharedPreferences().edit().putString(shareprefenceBean.PASSWORD, passwordEdit.getText().toString().trim()).commit();
        try {
            MyApplication.getInstance().getSharedPreferences().edit().putString(shareprefenceBean.CONNECTSTR, jsonObject.getString("Connectstr")).commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(TE01Activity.this, TE02Activity.class);
        startActivity(intent);
        finish();
    }

    class ApkUpdateAsyncTesk extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO 自动生成的方法存根
            String json = MyWebservice.ApkUpdate(MyApplication.getInstance().getVisionCode(), "ET001");
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO 自动生成的方法存根
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        String[] permisswion =  {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        getPermiss(permisswion);
                        JSONObject DatajsonObject = new JSONObject(jsonObject.getString("Data"));
                        String apkUrlString = DatajsonObject.getString("ApkUrl");
                        String UpdateText = DatajsonObject.getString("UpdateTxt");
                        String apkName = (String) apkUrlString.subSequence(apkUrlString.lastIndexOf("/"), apkUrlString.length());

                        CommonDialog myDialog = new CommonDialog(TE01Activity.this, R.layout.prompt_dialog_layout, R.style.yuanjiao_dialog);
                        myDialog.setDialogContentListener(new CommonDialog.DialogContentListener() {

                            @Override
                            public void contentExecute(View parent, final Dialog dialog, final Object[] objs) {
                                // TODO 自动生成的方法存根
                                TextView titleTextView = (TextView) parent.findViewById(R.id.title);
                                TextView contentTextView = (TextView) parent.findViewById(R.id.content_txtview);
                                TextView cancelTextView = (TextView) parent.findViewById(R.id.cancel);
                                TextView confirmTextView = (TextView) parent.findViewById(R.id.confirm);

                                titleTextView.setVisibility(View.VISIBLE);
                                titleTextView.setText("更新提示");
                                contentTextView.setText(objs[0].toString());
                                cancelTextView.setText("取消");
                                confirmTextView.setText("更新");

                                cancelTextView.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO 自动生成的方法存根
                                        MyApplication.getInstance().getSharedPreferences().edit().putLong(shareprefenceBean.NOT_UPDATA, System.currentTimeMillis()).commit();
                                        dialog.dismiss();
                                    }
                                });
                                confirmTextView.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO 自动生成的方法存根
                                        new UpdateVersion_2(TE01Activity.this, objs[1].toString(), objs[2].toString());
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }, new Object[]{UpdateText, apkName, apkUrlString});
                        myDialog.show();
                        break;
                    case -1:
//                        Toast.makeText(TE01Activity.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(TE01Activity.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (JSONException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }

    }

    @Override
    public void finish() {
        super.finish();
        ActivityAnimUtil.setCenterIn_out(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Process.killProcess(Process.myPid());
            System.exit(0);
        }
        return super.onKeyDown(keyCode, event);
    }
}
