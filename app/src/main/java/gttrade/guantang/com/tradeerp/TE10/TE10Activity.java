package gttrade.guantang.com.tradeerp.TE10;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.catalogue.ElementBean;
import gttrade.guantang.com.tradeerp.catalogue.MuLuFlowLayout;
import gttrade.guantang.com.tradeerp.catalogue.MuluAdapter;
import gttrade.guantang.com.tradeerp.database.DataBaseHelper;
import gttrade.guantang.com.tradeerp.database.DataBaseManager;
import gttrade.guantang.com.tradeerp.database.DataBaseObject;
import gttrade.guantang.com.tradeerp.database.DataBaseOperate;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE10Activity extends BaseActivity implements OnItemClickListener, MuluAdapter.IListViewRefresh {


    @BindView(R.id.dingcengTxtView)
    TextView dingcengTxtView;
    @BindView(R.id.firstLagFlowLayout)
    MuLuFlowLayout firstLagFlowLayout;
    @BindView(R.id.hScrollView)
    HorizontalScrollView hScrollView;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.resetView)
    TextView resetView;
    @BindView(R.id.confirmView)
    TextView confirmView;
    @BindView(R.id.back)
    ImageButton back;

    private SimpleAdapter listItemAdapter;
    private MuluAdapter mMuluAdapter;
    private DataBaseOperate databaseOperate;

    private String pid="0";

    private String returnId;
    private String name;

    private GetCatalogueAsyncTask getCatalogueAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te10);
        ButterKnife.bind(this);

        init();

        getCatalogueAsyncTask = new GetCatalogueAsyncTask();
        getCatalogueAsyncTask.startWork(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getCatalogueAsyncTask != null){
            getCatalogueAsyncTask.cancel();
        }
    }

    private void init() {
        databaseOperate = new DataBaseOperate(this);

        mMuluAdapter = new MuluAdapter(this);
        firstLagFlowLayout.setAdapter(mMuluAdapter);
        listView.setOnItemClickListener(this);
    }

    public void setAdapter(List<Map<String, Object>> ls) {
        if (ls == null){
            return;
        }
        listItemAdapter = new SimpleAdapter(this, ls, R.layout.lbchoseitem,
                new String[]{"name"}, new int[]{R.id.lbitem});
        listView.setAdapter(listItemAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, Object> map = (Map<String, Object>) parent.getAdapter().getItem(position);
        ElementBean elementBean = new ElementBean();
        elementBean.setPID(map.get("PID").toString());

        returnId = map.get("ID").toString();
        name = map.get("name").toString();
        elementBean.setID(returnId);
        elementBean.setName(name);
        elementBean.setSindex(map.get("sindex").toString());
        elementBean.setLev(Integer.parseInt(map.get("lev").toString()));
        mMuluAdapter.addData(elementBean);

        pid = returnId;
        setAdapter(getLocalLevelCatalogue());
    }

    @Override
    public void myListViewRefresh(ElementBean elementBean) {
        pid = elementBean.getID();
        setAdapter(getLocalLevelCatalogue());
    }

    @OnClick({R.id.dingcengTxtView, R.id.resetView, R.id.confirmView,R.id.back})
    public void onClick(View view) {
        Intent intent = getIntent();
        switch (view.getId()) {
            case R.id.dingcengTxtView:
                pid = "0";
                mMuluAdapter.addData(new ArrayList<ElementBean>());
                setAdapter(getLocalLevelCatalogue());
                break;
            case R.id.resetView:
                intent.putExtra("name","");
                intent.putExtra("id","0");
                setResult(1,intent);
                finish();
                break;
            case R.id.confirmView:
                intent.putExtra("name",name);
                intent.putExtra("id",returnId);
                setResult(1,intent);
                finish();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    class GetCatalogueAsyncTask extends MyOkHttpExecute {

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetCatalogue,getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            if (TextUtils.isEmpty(JsonString)) {
                setAdapter(getLocalLevelCatalogue());
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch (jsonObject.getInt("Status")) {
                    case 1:
                        pid = "0";
                        databaseOperate.deleteTable(DataBaseHelper.tb_HpCatalogue);
                        saveCatalogue(jsonObject.getJSONArray("data"));
                        setAdapter(getLocalLevelCatalogue());
                        break;
                    case -1:
                        Intent intent = new Intent(TE10Activity.this, TE01Activity.class);
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

        }
    }

    private void saveCatalogue(JSONArray data) {
        DataBaseManager dataBaseManager = DataBaseManager.getInstance(getApplicationContext());
        SQLiteDatabase db=dataBaseManager.openDataBase();

        int length = data.length();
        try {
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = data.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put("ID", jsonObject.getInt("ID"));
                contentValues.put("name", jsonObject.getString("name"));
                contentValues.put("lev", jsonObject.getInt("lev"));
                contentValues.put("PID", jsonObject.getInt("PID"));
                contentValues.put("sindex", jsonObject.getString("sindex"));

                DataBaseOperate.saveRawCatalogue(db, contentValues);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dataBaseManager.closeDataBase();
    }

    private List<Map<String, Object>> getLocalLevelCatalogue() {
        List<Map<String, Object>> Catalogue = databaseOperate.getCatalogueLevelList(pid);

        return Catalogue;
    }
}
