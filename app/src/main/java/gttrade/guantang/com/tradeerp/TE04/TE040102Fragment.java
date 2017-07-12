package gttrade.guantang.com.tradeerp.TE04;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;


public class TE040102Fragment extends Fragment {

    @BindView(R.id.ReceiverTxtView)
    TextView ReceiverTxtView;
    @BindView(R.id.Street1TxtView)
    TextView Street1TxtView;
    @BindView(R.id.Street2TxtView)
    TextView Street2TxtView;
    @BindView(R.id.CountyTxtView)
    TextView CountyTxtView;
    @BindView(R.id.CityTxtView)
    TextView CityTxtView;
    @BindView(R.id.StateTxtView)
    TextView StateTxtView;
    @BindView(R.id.PostalCodeTxtView)
    TextView PostalCodeTxtView;
    @BindView(R.id.Phone1TxtView)
    TextView Phone1TxtView;
    @BindView(R.id.Phone2TxtView)
    TextView Phone2TxtView;
    @BindView(R.id.EmailTxtView)
    TextView EmailTxtView;

    private String orderid;
    private Context context;

    private GetRecipientsAsyncTask getRecipientsAsyncTask;

    public TE040102Fragment() {
        // Required empty public constructor
    }

    public static TE040102Fragment newInstance(String orderid) {
        TE040102Fragment fragment = new TE040102Fragment();
        Bundle args = new Bundle();
        args.putString("orderid", orderid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderid = getArguments().getString("orderid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_te040102, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (NetWorkTool.checkNetworkState(context)){
            Map<String,String> requestBodyMap = new HashMap<String,String>();
            requestBodyMap.put("orderid",orderid);
            getRecipientsAsyncTask = new GetRecipientsAsyncTask();
            getRecipientsAsyncTask.startWork(requestBodyMap);
        }else{
            ((TE0401Activity) context).showToast("网络未连接");
        }
    }

    class GetRecipientsAsyncTask extends MyOkHttpExecute{

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetRecipients,context.getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch(jsonObject.getInt("Status")){
                    case 1:
                        JSONArray dataJSONArray = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < dataJSONArray.length(); i++) {
                            JSONObject myJSONObject = dataJSONArray.getJSONObject(i);
                            ReceiverTxtView.setText(optimizeText(myJSONObject.getString("ShippingAddress_Name")));
                            Street1TxtView.setText(optimizeText(myJSONObject.getString("ShippingAddress_Street1")));
                            Street2TxtView.setText(optimizeText(myJSONObject.getString("ShippingAddress_Street2")));
                            CountyTxtView.setText(optimizeText(myJSONObject.getString("ShippingAddress_County")));
                            CityTxtView.setText(optimizeText(myJSONObject.getString("ShippingAddress_CityName")));
                            StateTxtView.setText(optimizeText(myJSONObject.getString("ShippingAddress_StateOrProvince")));
                            PostalCodeTxtView.setText(optimizeText(myJSONObject.getString("ShippingAddress_PostalCode")));
                            Phone1TxtView.setText(optimizeText(myJSONObject.getString("ShippingAddress_Phone")));
                            Phone2TxtView.setText(optimizeText(myJSONObject.getString("ShippingAddress_Phone2")));
                            EmailTxtView.setText(optimizeText(myJSONObject.getString("BuyerEmail")));
                        }
                        break;
                    case -1:
                        //// TODO: 2016/10/14
                        Intent intent = new Intent(context, TE01Activity.class);
                        startActivity(intent);
                        ((TE0401Activity)context).showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        ((TE0401Activity)context).showToast(jsonObject.getString("Message"));
                        break;
                    default:
                        ((TE0401Activity)context).showToast(jsonObject.getString("Message"));
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

    private String optimizeText(String text){
        if (TextUtils.isEmpty(text)){
            return "";
        }else{
            return text;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
