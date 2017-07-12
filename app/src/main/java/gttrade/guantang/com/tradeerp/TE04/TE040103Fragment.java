package gttrade.guantang.com.tradeerp.TE04;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import gttrade.guantang.com.tradeerp.RecyclerViewDemo.DividerItemDecoration;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.base.MyApplication;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;


public class TE040103Fragment extends Fragment {

    @BindView(R.id.myRecyclerView)
    RecyclerView myRecyclerView;
    @BindView(R.id.promptTxtView)
    TextView promptTxtView;
    private String orderid;
    private Context context;
    private MyItemRecyclerViewAdapter myAdapter;

    private GetOrderItemInfoAsyncTask getOrderItemInfoAsyncTask;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TE040103Fragment() {
    }

    public static TE040103Fragment newInstance(String orderid) {
        TE040103Fragment fragment = new TE040103Fragment();
        Bundle args = new Bundle();
        args.putString("orderid", orderid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderid = getArguments().getString("orderid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getOrderItemInfoAsyncTask != null){
            getOrderItemInfoAsyncTask.cancel();
        }
    }

    public void init() {
        myAdapter = new MyItemRecyclerViewAdapter(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setAdapter(myAdapter);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL, R.color.themeBackgroundGrey, 1));

        if (NetWorkTool.checkNetworkState(MyApplication.getContextObject())) {
            Map<String,String> requestBodyMap = new HashMap<String,String>();
            requestBodyMap.put("orderid",orderid);
            getOrderItemInfoAsyncTask = new GetOrderItemInfoAsyncTask();
            getOrderItemInfoAsyncTask.startWork(requestBodyMap);
        } else {
            ((TE0401Activity) context).showToast("网络未连接");
        }
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

    class GetOrderItemInfoAsyncTask extends MyOkHttpExecute {


        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetOrderItemInfo,context.getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        JSONArray jsonArray = jsonObject.getJSONArray("Data");
                        if (jsonArray.length() > 0) {
                            myAdapter.setData(jsonArray);
                        }else{
                            promptTxtView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case -1:
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

}
