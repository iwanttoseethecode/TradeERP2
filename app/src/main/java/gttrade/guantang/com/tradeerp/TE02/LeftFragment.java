package gttrade.guantang.com.tradeerp.TE02;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE07.TE07Activity;
import gttrade.guantang.com.tradeerp.base.MyApplication;
import gttrade.guantang.com.tradeerp.custom.CommonDialog;
import gttrade.guantang.com.tradeerp.shareprefence.shareprefenceBean;
import gttrade.guantang.com.tradeerp.updateVersionutil.UpdateVersion_2;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.util.UpdateVersion;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;


public class LeftFragment extends Fragment {

    @BindView(R.id.CompanyLayout)
    LinearLayout CompanyLayout;
    @BindView(R.id.getDataLayout)
    LinearLayout getDataLayout;
    @BindView(R.id.exitTxtView)
    TextView exitTxtView;
    @BindView(R.id.aboutUsLayout)
    LinearLayout aboutUsLayout;
    @BindView(R.id.feedbackLayout)
    LinearLayout feedbackLayout;
    private Context context;

    public LeftFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LeftFragment newInstance() {
        LeftFragment fragment = new LeftFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.nav_header_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @OnClick({R.id.CompanyLayout, R.id.getDataLayout, R.id.exitTxtView,R.id.aboutUsLayout, R.id.feedbackLayout})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.CompanyLayout:
                ((TE02Activity) context).closeNavView();
                break;
            case R.id.getDataLayout:
                ((TE02Activity) context).closeNavView();
                break;
            case R.id.exitTxtView:
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(0);
                intent.setClass (context, TE01Activity.class);
                startActivity(intent);
                ((TE02Activity) context).finish();
//                ((TE02Activity) context).closeNavView();
                break;
            case R.id.aboutUsLayout:
                ((TE02Activity) context).closeNavView();
                intent.setClass(context, TE07Activity.class);
                startActivity(intent);
                break;
            case R.id.feedbackLayout:
                ((TE02Activity) context).closeNavView();
                String[] permisswion =  {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                ((TE02Activity) context).getPermiss(permisswion);
                if (NetWorkTool.checkNetworkState(context)){
                    new ApkUpdateAsyncTesk().execute();
                }else{
                    ((TE02Activity) context).showToast("网络未连接");
                }
                break;
        }
    }

    class ApkUpdateAsyncTesk extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO 自动生成的方法存根
            String json = MyWebservice.ApkUpdate(MyApplication.getInstance().getVisionCode(),"ET001");
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO 自动生成的方法存根
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                switch(jsonObject.getInt("Status")){
                    case 1:
                        JSONObject DatajsonObject = new JSONObject(jsonObject.getString("Data"));
                        String apkUrlString=DatajsonObject.getString("ApkUrl");
                        String UpdateText = DatajsonObject.getString("UpdateTxt");
                        String apkName = (String) apkUrlString.subSequence(apkUrlString.lastIndexOf("/"), apkUrlString.length());

                        CommonDialog myDialog = new CommonDialog(context, R.layout.prompt_dialog_layout, R.style.yuanjiao_dialog);
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
                                        MyApplication.getInstance().getSharedPreferences().edit().putLong(shareprefenceBean.NOT_UPDATA,System.currentTimeMillis()).commit();
                                        dialog.dismiss();
                                    }
                                });
                                confirmTextView.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO 自动生成的方法存根
                                        new UpdateVersion_2(context, objs[1].toString(), objs[2].toString());
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }, new Object[]{UpdateText,apkName,apkUrlString});
                        myDialog.show();
                        break;
                    case -1:
					    Toast.makeText(context, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(context, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (JSONException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
    }

}
