package gttrade.guantang.com.tradeerp.TE04;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import gttrade.guantang.com.tradeerp.util.StringIsNumber;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE040101Fragment extends Fragment {

    @BindView(R.id.NoTxtView)
    TextView NoTxtView;
    @BindView(R.id.No_PlatformTxtView)
    TextView NoPlatformTxtView;
    @BindView(R.id.eShopNameTxtView)
    TextView eShopNameTxtView;
    @BindView(R.id.PlatformTxtView)
    TextView PlatformTxtView;
    @BindView(R.id.ShippingAddress_CountryNameTxtView)
    TextView ShippingAddressCountryNameTxtView;
    @BindView(R.id.StatusTxtView)
    TextView StatusTxtView;
    @BindView(R.id.AmountPaid_RMBTxtView)
    TextView AmountPaidRMBTxtView;
    @BindView(R.id.ShippingMethodsTxtView)
    TextView ShippingMethodsTxtView;
    @BindView(R.id.TrackingNoTxtView)
    TextView TrackingNoTxtView;
    @BindView(R.id.PaymentNoTxtView)
    TextView PaymentNoTxtView;
    @BindView(R.id.SellerIDTxtView)
    TextView SellerIDTxtView;
    @BindView(R.id.PlatformMessageTxtView)
    TextView PlatformMessageTxtView;
    @BindView(R.id.OrderNotesTxtView)
    TextView OrderNotesTxtView;
    @BindView(R.id.AttributeTxtView)
    TextView AttributeTxtView;

    private Context context;
    private String orderid;

    private GetOrderDetailAsyncTask getOrderDetailAsyncTask;

    public TE040101Fragment() {
        // Required empty public constructor
    }

    public static TE040101Fragment newInstance(String orderid) {
        TE040101Fragment fragment = new TE040101Fragment();
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
    public void onDestroy() {
        super.onDestroy();
        if (getOrderDetailAsyncTask != null){
            getOrderDetailAsyncTask.cancel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_te040101, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    public void init() {
        if (NetWorkTool.checkNetworkState(context)) {
            Map<String,String> requestBodyMap = new HashMap<String,String>();
            requestBodyMap.put("orderid",orderid);
            getOrderDetailAsyncTask = new GetOrderDetailAsyncTask();
            getOrderDetailAsyncTask.startWork(requestBodyMap);
        } else {
            ((TE0401Activity) context).showToast("网络未连接");
        }
    }

    class GetOrderDetailAsyncTask extends MyOkHttpExecute {

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetOrderDetail,context.getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        praseJSON(jsonObject);
                        break;
                    case -1:
                        //// TODO: 2016/10/14
                        Intent intent = new Intent(context, TE01Activity.class);
                        startActivity(intent);
                        ((TE0401Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        ((TE0401Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    default:
                        ((TE0401Activity) context).showToast(jsonObject.getString("Message"));
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

    public void praseJSON(JSONObject jsonObject) throws JSONException {
        JSONArray dataJSONArray = jsonObject.getJSONArray("Data");
        int length = dataJSONArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject myJsonObject = dataJSONArray.getJSONObject(i);
            NoTxtView.setText(myJsonObject.getString("No").equals("null") ? "" : myJsonObject.getString("No"));
            NoPlatformTxtView.setText(myJsonObject.getString("No_Platform").equals("null") ? "" : myJsonObject.getString("No_Platform"));
            eShopNameTxtView.setText(myJsonObject.getString("eShopName").equals("null") ? "" : myJsonObject.getString("eShopName"));
            PlatformTxtView.setText(myJsonObject.getString("Platform").equals("null") ? "" : myJsonObject.getString("Platform"));
            ShippingAddressCountryNameTxtView.setText(myJsonObject.getString("ShippingAddress_CountryName").equals("null") ? "" : myJsonObject.getString("ShippingAddress_CountryName"));
            StatusTxtView.setText(myJsonObject.getString("Status").equals("null") ? "" : myJsonObject.getString("Status"));
            AmountPaidRMBTxtView.setText(myJsonObject.getString("AmountPaid_RMB").equals("null") ? "" : myJsonObject.getString("AmountPaid_RMB"));
            ShippingMethodsTxtView.setText(myJsonObject.getString("ShippingMethods").equals("null") ? "" : myJsonObject.getString("ShippingMethods"));
            TrackingNoTxtView.setText(myJsonObject.getString("TrackingNo").equals("null") ? "" : myJsonObject.getString("TrackingNo"));
            PaymentNoTxtView.setText(myJsonObject.getString("PaymentNo").equals("null") ? "" : myJsonObject.getString("PaymentNo"));
            SellerIDTxtView.setText(myJsonObject.getString("SellerID").equals("null") ? "" : myJsonObject.getString("SellerID"));
            PlatformMessageTxtView.setText(myJsonObject.getString("PlatformMessage").equals("null") ? "" : myJsonObject.getString("PlatformMessage"));
            OrderNotesTxtView.setText(myJsonObject.getString("OrderNotes").equals("null") ? "" : myJsonObject.getString("OrderNotes"));
            AttributeTxtView.setText(myJsonObject.getString("Attribute").equals("null")?"":myJsonObject.getString("Attribute"));
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
