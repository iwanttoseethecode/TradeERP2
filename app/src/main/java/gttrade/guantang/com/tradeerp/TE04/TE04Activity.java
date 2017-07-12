package gttrade.guantang.com.tradeerp.TE04;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.RecyclerViewDemo.DividerItemDecoration;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE02.OrderRecyclerAdapter;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.base.MyApplication;
import gttrade.guantang.com.tradeerp.commondapter.CommonAdapter;
import gttrade.guantang.com.tradeerp.commondapter.ViewHolder;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE04Activity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.titleTxtView)
    TextView titleTxtView;
    @BindView(R.id.titlelayout)
    LinearLayout titlelayout;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.myRecyclerView)
    RecyclerView myRecyclerView;
    @BindView(R.id.mySwipeRefreshLayout)
    SwipeRefreshLayout mySwipeRefreshLayout;
    @BindView(R.id.searchTxtView)
    TextView searchTxtView;

    private List<JSONObject> mList = new ArrayList<JSONObject>();
    private OrderRecyclerAdapter myAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int ordertype = 1;// 1表示今日订单 2表示今日发货订单 3表示历史订单 4表示未发货订单

    private AtomicBoolean onClickFlag = new AtomicBoolean(true); ////控制是否执行子线程，true 执行子线程，false 不执行

    private GetOrdersAsyncTask getOrdersAsyncTask;

    private SearchOrdersAsyncTask searchOrdersAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te04);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        ordertype = intent.getIntExtra("ordertype", 1);

        switch (ordertype) {
            case 1:
                titleTxtView.setText("今日订单");
                break;
            case 2:
                titleTxtView.setText("今日发货订单");
                break;
            case 3:
                titleTxtView.setText("历史遗留订单");
                break;
            case 4:
                titleTxtView.setText("剩余未发订单");
                break;
        }
        init();
        EditorSearchAction();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(getOrdersAsyncTask!=null){
            getOrdersAsyncTask.cancel();
        }
    }

    public void init() {

        myAdapter = new OrderRecyclerAdapter(this);
        linearLayoutManager = new LinearLayoutManager(MyApplication.getContextObject());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setAdapter(myAdapter);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.color.themeBackgroundGrey, 1));
        mySwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        //进入页面的时候显示加载进度条
        mySwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        myRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == myAdapter.getItemCount()) {
                    if (NetWorkTool.checkNetworkState(TE04Activity.this)) {
                        mySwipeRefreshLayout.setRefreshing(true);
                        if (mList.isEmpty()) {
                            Map<String,String> requestBodyMap = new HashMap<>();
                            requestBodyMap.put("topnum","10");
                            requestBodyMap.put("orderid","0");
                            requestBodyMap.put("searchno",editText.getText().toString().trim());
                            requestBodyMap.put("ordertype",String.valueOf(ordertype));
                            getOrdersAsyncTask = new GetOrdersAsyncTask();
                            getOrdersAsyncTask.startWork(requestBodyMap);
                        } else {
                            try {
                                Map<String,String> requestBodyMap = new HashMap<>();
                                requestBodyMap.put("topnum","10");
                                requestBodyMap.put("orderid",mList.get(mList.size() - 1).getString("ID"));
                                requestBodyMap.put("searchno",editText.getText().toString().trim());
                                requestBodyMap.put("ordertype",String.valueOf(ordertype));
                                getOrdersAsyncTask = new GetOrdersAsyncTask();
                                getOrdersAsyncTask.startWork(requestBodyMap);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        showToast("网络未连接");
                    }

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetWorkTool.checkNetworkState(TE04Activity.this)) {
                    mList.clear();
                    mySwipeRefreshLayout.setRefreshing(true);
                    Map<String,String> requestBodyMap = new HashMap<>();
                    requestBodyMap.put("topnum","10");
                    requestBodyMap.put("orderid","0");
                    requestBodyMap.put("searchno",editText.getText().toString().trim());
                    requestBodyMap.put("ordertype",String.valueOf(ordertype));
                    getOrdersAsyncTask = new GetOrdersAsyncTask();
                    getOrdersAsyncTask.startWork(requestBodyMap);
                } else {
                    showToast("网络未连接");
                }
            }
        });

        if (NetWorkTool.checkNetworkState(this)) {
            mySwipeRefreshLayout.setRefreshing(true);
            mList.clear();
            Map<String,String> requestBodyMap = new HashMap<>();
            requestBodyMap.put("topnum","10");
            requestBodyMap.put("orderid","0");
            requestBodyMap.put("searchno",editText.getText().toString().trim());
            requestBodyMap.put("ordertype",String.valueOf(ordertype));
            getOrdersAsyncTask = new GetOrdersAsyncTask();
            getOrdersAsyncTask.startWork(requestBodyMap);
        } else {
            showToast("网络未连接");
        }
    }

    public void EditorSearchAction(){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event!=null && event.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
                    if (NetWorkTool.checkNetworkState(TE04Activity.this)) {
                        mySwipeRefreshLayout.setRefreshing(true);
                        Map<String,String> requestBodyMap = new HashMap<String, String>();
                        requestBodyMap.put("topnum","10");
                        requestBodyMap.put("orderid","0");
                        requestBodyMap.put("searchno",editText.getText().toString().trim());
                        requestBodyMap.put("ordertype",String.valueOf(ordertype));
                        searchOrdersAsyncTask = new SearchOrdersAsyncTask();
                        searchOrdersAsyncTask.startWork(requestBodyMap);
                    } else {
                        showToast("网络未连接");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.back, R.id.searchTxtView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.searchTxtView:
                if (onClickFlag.compareAndSet(true,false)){
                    if (NetWorkTool.checkNetworkState(this)) {
                        mySwipeRefreshLayout.setRefreshing(true);
                        Map<String,String> requestBodyMap = new HashMap<String, String>();
                        requestBodyMap.put("topnum","10");
                        requestBodyMap.put("orderid","0");
                        requestBodyMap.put("searchno",editText.getText().toString().trim());
                        requestBodyMap.put("ordertype",String.valueOf(ordertype));
                        searchOrdersAsyncTask = new SearchOrdersAsyncTask();
                        searchOrdersAsyncTask.startWork(requestBodyMap);
                    } else {
                        showToast("网络未连接");
                    }
                }

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        JSONObject jsonObject = (JSONObject) adapterView.getAdapter().getItem(i);
        try {
            String orderid = jsonObject.getString("ID");
            Intent intent = new Intent(this, TE0401Activity.class);
            intent.putExtra("orderid", orderid);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class GetOrdersAsyncTask extends MyOkHttpExecute{

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetTypeOrders,TE04Activity.this.getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            mySwipeRefreshLayout.setRefreshing(false);
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        parseJSON(jsonObject);
                        break;
                    case 2:
                        parseJSON(jsonObject);
                        showToast(jsonObject.getString("Message"));
                        break;
                    case -1:
                        //// TODO: 2016/10/12
                        Intent intent = new Intent(TE04Activity.this, TE01Activity.class);
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
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    class SearchOrdersAsyncTask extends MyOkHttpExecute {

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetTypeOrders,TE04Activity.this.getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            onClickFlag.compareAndSet(false,true);
            mySwipeRefreshLayout.setRefreshing(false);
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        parseJSON(jsonObject);
                        break;
                    case 2:
                        parseJSON(jsonObject);
                        showToast(jsonObject.getString("Message"));
                        break;
                    case -1:
                        //// TODO: 2016/10/12
                        Intent intent = new Intent(TE04Activity.this, TE01Activity.class);
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
            mySwipeRefreshLayout.setRefreshing(false);
            onClickFlag.compareAndSet(false,true);
        }
    }

    public void parseJSON(JSONObject jsonObject) throws JSONException {
        JSONArray dataJSONArray = jsonObject.getJSONArray("Data");
        int length = dataJSONArray.length();
        for (int i = 0; i < length; i++) {
            mList.add(dataJSONArray.getJSONObject(i));
        }
        myAdapter.setData(mList);
    }

//    @Override
//    public void onRefresh() {
//        if (NetWorkTool.checkNetworkState(this)) {
//            onLoad();
//            mList.clear();
//            new GetOrdersAsyncTask().execute("10", "0", editText.getText().toString().trim(), String.valueOf(ordertype));
//        } else {
//            showToast("网络未连接");
//        }
//    }
//
//    @Override
//    public void onLoadMore() {
//        if (NetWorkTool.checkNetworkState(this)) {
//            onLoad();
//            if (mList.isEmpty()) {
//                new GetOrdersAsyncTask().execute("10", "0", editText.getText().toString().trim(), String.valueOf(ordertype));
//            } else {
//                try {
//                    new GetOrdersAsyncTask().execute("10", mList.get(mList.size() - 1).getString("ID"), editText.getText().toString().trim(), String.valueOf(ordertype));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            showToast("网络未连接");
//        }
//    }

//    public void onLoad() {
//        Calendar calendar = Calendar.getInstance();
//        String year = String.valueOf(calendar.get(Calendar.YEAR));
//        String month = String.valueOf(new DecimalFormat("00").format(calendar.get(Calendar.MONTH) + 1));
//        String day = String.valueOf(new DecimalFormat("00").format(calendar.get(Calendar.DAY_OF_MONTH)));
//        String hour = String.valueOf(new DecimalFormat("00").format(calendar.get(Calendar.HOUR_OF_DAY)));
//        String minute = String.valueOf(new DecimalFormat("00").format(calendar.get(Calendar.MINUTE)));
//        String refreshDate = year + "-" + month + "-" + day + " " + hour + ":" + minute;
//        orderListView.setRefreshTime(refreshDate);
//    }
//
//    public void stopLoad() {
//        orderListView.stopLoadMore();
//        orderListView.stopRefresh();
//    }

    class MyAdapter extends CommonAdapter<JSONObject> {

        public MyAdapter(Context mContext, List<JSONObject> mList, int LayoutId) {
            super(mContext, mList, LayoutId);
        }

        @Override
        public void convert(ViewHolder holder, JSONObject item) {
            ImageView picImgView = holder.getView(R.id.picImgView);
            TextView eShopNameTxtView = holder.getView(R.id.eShopNameTxtView);
            TextView BuyerIDTxtView = holder.getView(R.id.BuyerIDTxtView);
            TextView NoTxtView = holder.getView(R.id.NoTxtView);
            TextView PlatformTxtView = holder.getView(R.id.PlatformTxtView);


            try {
                if (!item.getString("PickingStaus").equals("")) {
                    //PickingStaus 拣货状态 1表示异常订单 2表示已发货 3待发货
                    if (item.getInt("PickingStaus") == 1) {
                        picImgView.setImageResource(R.mipmap.exception);
                    } else if (item.getInt("PickingStaus") == 2) {
                        picImgView.setImageResource(R.mipmap.complete);
                    } else if (item.getInt("PickingStaus") == 3) {
                        picImgView.setImageResource(R.mipmap.uncomplete);
                    }
                }

                eShopNameTxtView.setText(item.getString("eShopName"));
                BuyerIDTxtView.setText(item.getString("BuyerID"));
                NoTxtView.setText(item.getString("No"));
                PlatformTxtView.setText(item.getString("No_Platform"));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}
