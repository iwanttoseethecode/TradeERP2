package gttrade.guantang.com.tradeerp.TE02;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.TE04.TE04Activity;
import gttrade.guantang.com.tradeerp.TE06.TE06Activity;
import gttrade.guantang.com.tradeerp.TE09.TE09Activity;
import gttrade.guantang.com.tradeerp.TE11Activty.TE1101.TE1101Activity;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.util.StringIsNumber;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;


public class TE0201Fragment extends Fragment implements AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.SalesAmoundataRadioBtn)
    RadioButton SalesAmoundataRadioBtn;
    @BindView(R.id.SalesOrderNumdataRadioBtn)
    RadioButton SalesOrderNumdataRadioBtn;
    @BindView(R.id.todayOrderNumTextView)
    TextView todayOrderNumTextView;
    @BindView(R.id.remainOrderNumTextView)
    TextView remainOrderNumTextView;
    @BindView(R.id.todayDisposeNumTextView)
    TextView todayDisposeNumTextView;
    @BindView(R.id.NotDisposeNumTextView)
    TextView NotDisposeNumTextView;
    @BindView(R.id.mySpinner)
    Spinner mySpinner;
    @BindView(R.id.allmoneyTextView)
    TextView allmoneyTextView;
    @BindView(R.id.chartListView)
    ListView chartListView;
    @BindView(R.id.OrderRadioGroup)
    RadioGroup OrderRadioGroup;
    @BindView(R.id.totalNameTextView)
    TextView totalNameTextView;
    @BindView(R.id.myScrollView)
    PullToRefreshScrollView myScrollView;
    @BindView(R.id.myLayout)
    LinearLayout myLayout;
    @BindView(R.id.title_layout)
    LinearLayout titleLayout;

    private Context context;
    private List<JSONObject> mList = new ArrayList<JSONObject>();
    private JSONObject HistogramJSONObject = new JSONObject();
    private HistogramAdapter mHistogramAdapter;

    private int statisticsFlag = 0;//0 今天 ，1本周，2本月

    private int position = 0;

    private Itogglelistener mTogglelistener;

    private GetStatisticsAsyncTask getStatisticsAsyncTask;

    private GetPageIndexOrderAsyncTask getPageIndexOrderAsyncTask;


    public interface Itogglelistener {
        void toggleExecute();
    }

    public TE0201Fragment() {
        // Required empty public constructor
    }


    public static TE0201Fragment newInstance() {
        TE0201Fragment fragment = new TE0201Fragment();
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
    public void onDestroy() {
        super.onDestroy();

        if (getStatisticsAsyncTask != null){
            getStatisticsAsyncTask.cancel();
        }
        if (getPageIndexOrderAsyncTask != null){
            getPageIndexOrderAsyncTask.cancel();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTogglelistener = (TE02Activity) context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mySpinner.setOnItemSelectedListener(this);
        OrderRadioGroup.setOnCheckedChangeListener(this);
        myScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                init();
            }
        });
//        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    public void init() {
        if (NetWorkTool.checkNetworkState(getActivity())) {

            getPageIndexOrderAsyncTask=new GetPageIndexOrderAsyncTask();
            getPageIndexOrderAsyncTask.startWork(null);

            Map<String,String> requestBodyMap = new HashMap<String,String>();
            requestBodyMap.put("statisticstype",String.valueOf(statisticsFlag));
            getStatisticsAsyncTask=new GetStatisticsAsyncTask();
            getStatisticsAsyncTask.startWork(requestBodyMap);
        } else {
            ((BaseActivity) context).showToast("网络未连接");
        }
    }

    @OnClick({R.id.todayOrderNumLayout, R.id.remainOrderNumLayout, R.id.todayDisposeNumLayout, R.id.NotDisposeNumLayout,R.id.toggleImgView,R.id.packUpLayout,
            R.id.CheckLayout,R.id.scantogoLayout})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.toggleImgView:
                mTogglelistener.toggleExecute();
                break;
            case R.id.todayOrderNumLayout:
                intent.setClass(context, TE04Activity.class);
                intent.putExtra("ordertype", 1);
                startActivity(intent);
                break;
            case R.id.remainOrderNumLayout:
                intent.setClass(context, TE04Activity.class);
                intent.putExtra("ordertype", 3);
                startActivity(intent);
                break;
            case R.id.todayDisposeNumLayout:
                intent.setClass(context, TE04Activity.class);
                intent.putExtra("ordertype", 2);
                startActivity(intent);
                break;
            case R.id.NotDisposeNumLayout:
                intent.setClass(context, TE04Activity.class);
                intent.putExtra("ordertype", 4);
                startActivity(intent);
                break;
            case R.id.packUpLayout:
                intent.setClass(context, TE06Activity.class);
                startActivity(intent);
                break;
            case R.id.CheckLayout:
                intent.setClass(context,TE09Activity.class);
                startActivity(intent);
                break;
            case R.id.scantogoLayout:
                intent.setClass(context, TE1101Activity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String result = adapterView.getItemAtPosition(i).toString();
        if (NetWorkTool.checkNetworkState(context)) {
            if (result.equals("今天")) {
                ((TE02Activity) context).showProgressDialog("正在加载", true,true);
                statisticsFlag = 0;

            } else if (result.equals("本周")) {
                ((TE02Activity) context).showProgressDialog("正在加载", true,true);
                statisticsFlag = 1;
            } else if (result.equals("本月")) {
                ((TE02Activity) context).showProgressDialog("正在加载", true,true);
                statisticsFlag = 2;
            }
            Map<String,String> requestBodyMap = new HashMap<String,String>();
            requestBodyMap.put("statisticstype",String.valueOf(statisticsFlag));
            getStatisticsAsyncTask=new GetStatisticsAsyncTask();
            getStatisticsAsyncTask.startWork(requestBodyMap);
        } else {
            ((TE02Activity) context).showToast("网络未连接");
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.SalesAmoundataRadioBtn:
                try {
                    allmoneyTextView.setText(StringIsNumber.getMoneyString(HistogramJSONObject.getString("TotalAmount")) + "元");
                    totalNameTextView.setText("总收入");
                    mHistogramAdapter = null;
                    dealwithHistogramData(R.id.SalesAmoundataRadioBtn);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.SalesOrderNumdataRadioBtn:
                try {
                    allmoneyTextView.setText(StringIsNumber.getMoneyString(HistogramJSONObject.getString("TotalNum")) + "笔");
                    totalNameTextView.setText("总订单数");
                    mHistogramAdapter = null;
                    dealwithHistogramData(R.id.SalesOrderNumdataRadioBtn);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    class GetPageIndexOrderAsyncTask extends MyOkHttpExecute {

        @Override
        protected MyPostOkHttp executeNetWork() {
//            return MyWebservice.doRequestService(WebserviceMethodName.GetPageIndexOrder,requestBodyMap,context.getMainLooper(),this);
            return dealwith(WebserviceMethodName.GetPageIndexOrder,context.getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            if (null == JsonString || JsonString.equals("")) {
                return;
            }
            myScrollView.onRefreshComplete();
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        JSONObject dataJSONObject = jsonObject.getJSONObject("Data");
                        todayOrderNumTextView.setText(StringIsNumber.getMoneyString(dataJSONObject.getString("TodayOrder")));
                        remainOrderNumTextView.setText(StringIsNumber.getMoneyString(dataJSONObject.getString("HistoryOrderNum")));
                        todayDisposeNumTextView.setText(StringIsNumber.getMoneyString(dataJSONObject.getString("TodaySendOrder")));
                        NotDisposeNumTextView.setText(StringIsNumber.getMoneyString(dataJSONObject.getString("NotSendOrder")));
                        break;
                    case -1:
                        Intent intent = new Intent(context, TE01Activity.class);
                        startActivity(intent);
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        todayOrderNumTextView.setText("");
                        remainOrderNumTextView.setText("");
                        todayDisposeNumTextView.setText("");
                        NotDisposeNumTextView.setText("");
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
        }
    }


    /**
     * 0 表示今天，1表示本周，2表示本月
     */
    class GetStatisticsAsyncTask extends MyOkHttpExecute {


        @Override
        protected MyPostOkHttp executeNetWork() {
//            return MyWebservice.doRequestService(WebserviceMethodName.GetStatistics,requestBodyMap,context.getMainLooper(),this);
            return dealwith(WebserviceMethodName.GetStatistics,context.getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            if (null == JsonString || JsonString.equals("")) {
                return;
            }
            ((TE02Activity) context).dismissDialog();
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        HistogramJSONObject = jsonObject.getJSONObject("Data");
                        if (SalesAmoundataRadioBtn.isChecked()) {
                            allmoneyTextView.setText(StringIsNumber.getMoneyString(HistogramJSONObject.getString("TotalAmount")) + "元");
                            totalNameTextView.setText("总收入");
                            dealwithHistogramData(R.id.SalesAmoundataRadioBtn);
                        } else {
                            allmoneyTextView.setText(StringIsNumber.getMoneyString(HistogramJSONObject.getString("TotalNum")) + "笔");
                            totalNameTextView.setText("总订单数");
                            dealwithHistogramData(R.id.SalesOrderNumdataRadioBtn);
                        }
                        break;
                    case -1:
                        //// TODO: 2016/10/10
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
                        mList.clear();
                        mHistogramAdapter = new HistogramAdapter(context, mList, R.layout.histogram_layout);
                        chartListView.setAdapter(mHistogramAdapter);
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    case -3:
                        mList.clear();
                        mHistogramAdapter = new HistogramAdapter(context, mList, R.layout.histogram_layout);
                        chartListView.setAdapter(mHistogramAdapter);
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                    default:
                        mList.clear();
                        mHistogramAdapter = new HistogramAdapter(context, mList, R.layout.histogram_layout);
                        chartListView.setAdapter(mHistogramAdapter);
                        ((TE02Activity) context).showToast(jsonObject.getString("Message"));
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                myScrollView.onRefreshComplete();
            }
        }

        @Override
        public void faild(String error) {
            ((TE02Activity) context).dismissDialog();
            ((TE02Activity) context).showToast(error);
        }
    }

    public void dealwithHistogramData(int resId) throws JSONException {

        mList.clear();
        mHistogramAdapter = new HistogramAdapter(context, mList, R.layout.histogram_layout);
        chartListView.setAdapter(mHistogramAdapter);
        JSONArray histogramJSONArray = null;
        if (resId == R.id.SalesAmoundataRadioBtn) {
            mHistogramAdapter.setType(0);
            histogramJSONArray = HistogramJSONObject.getJSONArray("SalesAmoundata");
        } else if (resId == R.id.SalesOrderNumdataRadioBtn) {
            mHistogramAdapter.setType(1);
            histogramJSONArray = HistogramJSONObject.getJSONArray("SalesOrderNumdata");
        }
        int length = histogramJSONArray.length();
        for (int i = 0; i < length; i++) {
            mList.add(histogramJSONArray.getJSONObject(i));
        }
        mHistogramAdapter.setData(mList);
        //默认情况下Android是禁止在ScrollView中放入另外的ScrollView的，它的高度是无法计算的，上面的chartListView是嵌套在ScrollView中的。
        int totalHeight = 0;
        int size = mHistogramAdapter.getCount();
        for (int i = 0; i < size; i++) {
            View ItemView = mHistogramAdapter.getView(i, null, chartListView);
            ItemView.measure(0, 0);
            totalHeight += ItemView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = chartListView.getLayoutParams();
        params.height = totalHeight + chartListView.getPaddingBottom() + chartListView.getPaddingTop() + (chartListView.getDividerHeight() * (chartListView.getCount() - 1));
        chartListView.setLayoutParams(params);
        myLayout.requestFocus();
    }

}
