package gttrade.guantang.com.tradeerp.TE09.TE0903;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.RecyclerViewDemo.DividerItemDecoration;
import gttrade.guantang.com.tradeerp.TE09.EventBusBean.RefreshBean;
import gttrade.guantang.com.tradeerp.TE09.PanDianDatabaseOperation;
import gttrade.guantang.com.tradeerp.util.SnackbarUtil;

public class TE0903Activity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.myRecyclerView)
    RecyclerView myRecyclerView;
    @BindView(R.id.myCoordinatorLayout)
    CoordinatorLayout myCoordinatorLayout;
    @BindView(R.id.modfiyTxtView)
    TextView modfiyTxtView;

    private List<Map<String, Object>> mList = new ArrayList<>();

    private PanDianDatabaseOperation panDianDatabaseOperation;

    private ErrorCheckHPAdapter mErrorCheckHPAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te0903);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        String ErrorCheckHP = intent.getStringExtra("ErrorCheckHP");
        panDianDatabaseOperation = PanDianDatabaseOperation.getInstance(getApplicationContext());


        mErrorCheckHPAdapter = new ErrorCheckHPAdapter(this);
        myRecyclerView.setAdapter(mErrorCheckHPAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.color.themeBackgroundGrey, 1));

        new MakeupDataAsyncTask().execute(ErrorCheckHP);
    }


    private void modfiyStockList() {
        if (mList.isEmpty()) {
            return ;
        }
        for (ListIterator<Map<String, Object>> it = mList.listIterator(); it.hasNext(); ) {
            Map<String, Object> map = it.next();
            map.put("Stock", map.get("netStock"));
            it.set(map);
        }
    }

    private void upData() {
        panDianDatabaseOperation.upDatePandianList(mList);
    }

    @OnClick({R.id.back, R.id.modfiyTxtView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.modfiyTxtView:
                new ModifyDataAsyncTask().execute();
                break;
        }
    }

    class MakeupDataAsyncTask extends AsyncTask<String, Void, List<Map<String, Object>>> {

        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {
            MakeUpErrorPanDianList makeUpErrorPanDianList = new MakeUpErrorPanDianList(params[0], panDianDatabaseOperation);
            return makeUpErrorPanDianList.makeUpList();
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> list) {
            super.onPostExecute(list);
            if (list.isEmpty())
                return;
            mList = list;
            mErrorCheckHPAdapter.setData(list);

            Snackbar snackbar = SnackbarUtil.IndefiniteSnackbar(myCoordinatorLayout, "对不起，服务端的账面数量已经发生改变", 5000,
                    Color.WHITE, Color.parseColor("#FFE5E181"));
            myCoordinatorLayout.setVisibility(View.VISIBLE);
            snackbar.show();
        }
    }

    class ModifyDataAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            modfiyStockList();
            upData();
            return null;
        }

        @Override
        protected void onPostExecute(Void list) {
            super.onPostExecute(list);

            mErrorCheckHPAdapter.setData(mList);
            modfiyTxtView.setVisibility(View.GONE);
            EventBus.getDefault().post(new RefreshBean());

            SnackbarUtil.IndefiniteSnackbar(myCoordinatorLayout, "账面数量已经修改了", 5000,
                    Color.WHITE,Color.parseColor("#FFE5E181"), "返回提交", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).show();
            myCoordinatorLayout.setVisibility(View.VISIBLE);
        }
    }

}
