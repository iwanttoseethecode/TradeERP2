package gttrade.guantang.com.tradeerp.TE09.TE0902;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.RecyclerViewDemo.DividerItemDecoration;
import gttrade.guantang.com.tradeerp.TE09.EventBusBean.RefreshBean;
import gttrade.guantang.com.tradeerp.TE09.PanDianDatabaseOperation;
import gttrade.guantang.com.tradeerp.base.BaseActivity;

public class TE0902Activity extends BaseActivity implements CheckedAdapter.IRefreshList,TextWatcher {

    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.delStrImgView)
    ImageView delStrImgView;
    @BindView(R.id.searchLayout)
    LinearLayout searchLayout;
    @BindView(R.id.hpRecyclerView)
    RecyclerView hpRecyclerView;

    private PanDianDatabaseOperation panDianDatabaseOperation;
    private CheckedAdapter mCheckedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te0902);
        ButterKnife.bind(this);
        initView();
        init();
    }

    private void initView(){
        editText.addTextChangedListener(this);
    }

    private void init() {
        panDianDatabaseOperation = PanDianDatabaseOperation.getInstance(getApplicationContext());

        mCheckedAdapter = new CheckedAdapter(this,panDianDatabaseOperation);
        mCheckedAdapter.setIRefreshList(this);
        hpRecyclerView.setAdapter(mCheckedAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        hpRecyclerView.setLayoutManager(linearLayoutManager);
        hpRecyclerView.setItemAnimator(new DefaultItemAnimator());

        hpRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL,R.color.themeBackgroundGrey,1));
    }

    @Override
    protected void onStart() {
        super.onStart();
        new ShowDataAsyncTask().execute();
    }

    @OnClick({R.id.back, R.id.deleteTxtView, R.id.delStrImgView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.deleteTxtView:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("您确定要清空已盘点货品？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        panDianDatabaseOperation.clearCheckedHPList();
                        EventBus.getDefault().post(new RefreshBean());
                        new ShowDataAsyncTask().execute();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();

                break;
            case R.id.delStrImgView:
                editText.setText("");
                break;
        }
    }

    @Override
    public void refreshList() {
        new ShowDataAsyncTask().execute();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            delStrImgView.setVisibility(View.VISIBLE);
        } else {
            delStrImgView.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            mCheckedAdapter.setData(panDianDatabaseOperation.searchCheckedHPList_bySKU(s.toString()));
        }else{
            new ShowDataAsyncTask().execute();
        }
    }

    class ShowDataAsyncTask extends AsyncTask<Void,Void,List<Map<String,Object>>>{

        @Override
        protected List<Map<String, Object>> doInBackground(Void... params) {

            return panDianDatabaseOperation.getCheckedHPList();
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            super.onPostExecute(maps);
            mCheckedAdapter.setData(maps);
        }
    }
}
