package gttrade.guantang.com.tradeerp.TE02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.RecyclerViewDemo.DividerItemDecoration;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE13.TE13Activity;
import gttrade.guantang.com.tradeerp.TE13.bean.FilterItemJson;
import gttrade.guantang.com.tradeerp.base.MyApplication;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE0203Fragment extends Fragment {
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.searchTxtView)
    TextView searchTxtView;
    @BindView(R.id.myRecyclerView)
    RecyclerView myRecyclerView;
    @BindView(R.id.mySwipeRefreshLayout)
    SwipeRefreshLayout mySwipeRefreshLayout;
    @BindView(R.id.searchDelBtn)
    ImageView searchDelBtn;

    private List<JSONObject> mList = new ArrayList<JSONObject>();
    private Context context;
    private HpRecyclerAdapter myAdapter;
    private LinearLayoutManager linearLayoutManager;

    private AtomicBoolean onClickFlag = new AtomicBoolean(true); //控制是否执行子线程，true 执行子线程，false 不执行

    private GetItemsAsyncTask getItemsAsyncTask;
    private SearchItemsAsyncTask searchItemsAsyncTask;

    private FilterItemJson filterItemJson;

    private FilterItemAsyncTask filterItemAsyncTask;

    public TE0203Fragment() {
        // Required empty public constructor
    }

    public static TE0203Fragment newInstance() {
        TE0203Fragment fragment = new TE0203Fragment();
        Bundle args = new Bundle();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_te0203, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        EditorSearchAction();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getItemsAsyncTask != null) {
            getItemsAsyncTask.cancel();
        }
        if (searchItemsAsyncTask != null) {
            searchItemsAsyncTask.cancel();
        }
    }

    public void init() {

        myAdapter = new HpRecyclerAdapter(context);
        linearLayoutManager = new LinearLayoutManager(MyApplication.getContextObject());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setAdapter(myAdapter);
        //设置增加或删除条目的动画
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerView.addItemDecoration(new DividerItemDecoration(MyApplication.getContextObject(), LinearLayoutManager.VERTICAL, R.color.themeBackgroundGrey, 1));

        mySwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        //进入页面的时候显示加载进度条
        mySwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        myRecyclerView.addOnScrollListener(new OnScrollListener() {
            int lastVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == myAdapter.getItemCount()) {
                    if (NetWorkTool.checkNetworkState(context)) {
                        mySwipeRefreshLayout.setRefreshing(true);
                        if (filterItemJson == null){
                            if (mList.isEmpty()) {
                                Map<String, String> requestBodyMap = new HashMap<String, String>();
                                requestBodyMap.put("topnum", "10");
                                requestBodyMap.put("sku", editText.getText().toString().trim());
                                requestBodyMap.put("itemid", "0");
                                getItemsAsyncTask = new GetItemsAsyncTask();
                                getItemsAsyncTask.startWork(requestBodyMap);
                            } else {
                                try {
                                    Map<String, String> requestBodyMap = new HashMap<String, String>();
                                    requestBodyMap.put("topnum", "10");
                                    requestBodyMap.put("sku", editText.getText().toString().trim());
                                    requestBodyMap.put("itemid", mList.get(mList.size() - 1).getString("ID"));
                                    getItemsAsyncTask = new GetItemsAsyncTask();
                                    getItemsAsyncTask.startWork(requestBodyMap);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            if (mList.isEmpty()){
                                Map<String, String> requestBodyMap = new HashMap<String, String>();
                                requestBodyMap.put("topnum", "10");
                                requestBodyMap.put("itemid", "0");
                                requestBodyMap.put("itemjson", JSON.toJSONString(filterItemJson));
                                filterItemAsyncTask=new FilterItemAsyncTask();
                                filterItemAsyncTask.startWork(requestBodyMap);
                            }else{
                                try {
                                    Map<String, String> requestBodyMap = new HashMap<String, String>();
                                    requestBodyMap.put("topnum", "10");
                                    requestBodyMap.put("itemid", mList.get(mList.size() - 1).getString("ID"));
                                    requestBodyMap.put("itemjson", JSON.toJSONString(filterItemJson));
                                    filterItemAsyncTask=new FilterItemAsyncTask();
                                    filterItemAsyncTask.startWork(requestBodyMap);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } else {
                        ((TE02Activity) context).showToast("网络未连接");
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
                if (NetWorkTool.checkNetworkState(context)) {
                    mySwipeRefreshLayout.setRefreshing(true);
                    mList.clear();
                    Map<String, String> requestBodyMap = new HashMap<String, String>();
                    requestBodyMap.put("topnum", "10");
                    requestBodyMap.put("sku", editText.getText().toString().trim());
                    requestBodyMap.put("itemid", "0");
                    getItemsAsyncTask = new GetItemsAsyncTask();
                    getItemsAsyncTask.startWork(requestBodyMap);
                } else {
                    ((TE02Activity) context).showToast("网络未连接");
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    searchDelBtn.setVisibility(View.VISIBLE);
                } else {
                    searchDelBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
//                mList.clear();
//                myAdapter.notifyDataSetChanged();
            }
        });

        if (NetWorkTool.checkNetworkState(context)) {
            mySwipeRefreshLayout.setRefreshing(true);
            Map<String, String> requestBodyMap = new HashMap<String, String>();
            requestBodyMap.put("topnum", "10");
            requestBodyMap.put("sku", editText.getText().toString().trim());
            requestBodyMap.put("itemid", "0");
            getItemsAsyncTask = new GetItemsAsyncTask();
            getItemsAsyncTask.startWork(requestBodyMap);
        } else {
            ((TE02Activity) context).showToast("网络未连接");
        }
    }

    public void EditorSearchAction() {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (NetWorkTool.checkNetworkState(context)) {
                        mList.clear();
                        filterItemJson = null;
                        mySwipeRefreshLayout.setRefreshing(true);
                        Map<String, String> requestBodyMap = new HashMap<String, String>();
                        requestBodyMap.put("topnum", "10");
                        requestBodyMap.put("sku", editText.getText().toString().trim());
                        requestBodyMap.put("itemid", "0");
                        searchItemsAsyncTask = new SearchItemsAsyncTask();
                        searchItemsAsyncTask.startWork(requestBodyMap);
                    } else {
                        ((TE02Activity) context).showToast("网络未连接");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.searchTxtView, R.id.searchDelBtn,R.id.filterImgBtn})
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.searchTxtView:
                if (onClickFlag.compareAndSet(true, false)) {
                    if (NetWorkTool.checkNetworkState(context)) {
                        mList.clear();
                        filterItemJson = null;
                        mySwipeRefreshLayout.setRefreshing(true);
                        Map<String, String> requestBodyMap = new HashMap<String, String>();
                        requestBodyMap.put("topnum", "10");
                        requestBodyMap.put("sku", editText.getText().toString().trim());
                        requestBodyMap.put("itemid", "0");
                        searchItemsAsyncTask = new SearchItemsAsyncTask();
                        searchItemsAsyncTask.startWork(requestBodyMap);
                    } else {
                        ((TE02Activity) context).showToast("网络未连接");
                    }
                }

                break;
            case R.id.searchDelBtn:
                editText.setText("");
                break;
            case R.id.filterImgBtn:
                intent.setClass(context,TE13Activity.class);
                startActivityForResult(intent,1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == 1){
                mList.clear();
                mySwipeRefreshLayout.setRefreshing(true);
                filterItemJson = data.getParcelableExtra("filterItemJson");
                String ss = JSON.toJSONString(filterItemJson);
                Map<String, String> requestBodyMap = new HashMap<String, String>();
                requestBodyMap.put("topnum", "10");
                requestBodyMap.put("itemid", "0");
                requestBodyMap.put("itemjson", JSON.toJSONString(filterItemJson));
                filterItemAsyncTask=new FilterItemAsyncTask();
                filterItemAsyncTask.startWork(requestBodyMap);
            }
        }

    }

    class SearchItemsAsyncTask extends MyOkHttpExecute {

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetItems, context.getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            mySwipeRefreshLayout.setRefreshing(false);
            onClickFlag.compareAndSet(false, true);
            if (null == JsonString || JsonString.equals("")) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        parseJSON(jsonObject);
                        break;
                    case 2:
                        parseJSON(jsonObject);
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    case -1:
                        Intent intent = new Intent(context, TE01Activity.class);
                        startActivity(intent);
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    default:
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void faild(String error) {
            mySwipeRefreshLayout.setRefreshing(false);
            onClickFlag.compareAndSet(false, true);
            ((TE02Activity) context).showToast(error);
        }
    }


    class GetItemsAsyncTask extends MyOkHttpExecute {

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetItems, context.getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            mySwipeRefreshLayout.setRefreshing(false);
            if (null == JsonString || JsonString.equals("")) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        parseJSON(jsonObject);
                        break;
                    case 2:
                        parseJSON(jsonObject);
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    case -1:
                        Intent intent = new Intent(context, TE01Activity.class);
                        startActivity(intent);
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    default:
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void faild(String error) {
            ((TE02Activity) context).showToast(error);
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseJSON(JSONObject jsonObject) throws JSONException {
        JSONArray dateJSONObject = jsonObject.getJSONArray("Data");
        int length = dateJSONObject.length();
        for (int i = 0; i < length; i++) {
            JSONObject myJSONObject = dateJSONObject.getJSONObject(i);
            mList.add(myJSONObject);
        }
        myAdapter.setData(mList);
    }

    class FilterItemAsyncTask extends MyOkHttpExecute{

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.FilterItem, Looper.getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            mySwipeRefreshLayout.setRefreshing(false);
            if (null == JsonString || JsonString.equals("")) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        parseJSON(jsonObject);
                        break;
                    case 2:
                        parseJSON(jsonObject);
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    case -1:
                        Intent intent = new Intent(context, TE01Activity.class);
                        startActivity(intent);
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    default:
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
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

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
