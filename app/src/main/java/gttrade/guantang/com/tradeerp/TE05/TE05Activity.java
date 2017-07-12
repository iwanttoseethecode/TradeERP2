package gttrade.guantang.com.tradeerp.TE05;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE01.TE01Activity;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.util.AsyncImageLoader;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.util.ScreenMeasure;
import gttrade.guantang.com.tradeerp.util.StringIsNumber;
import gttrade.guantang.com.tradeerp.webservice.MyOkHttpExecute;
import gttrade.guantang.com.tradeerp.webservice.MyPostOkHttp;
import gttrade.guantang.com.tradeerp.webservice.MyWebservice;
import gttrade.guantang.com.tradeerp.webservice.WebserviceMethodName;

public class TE05Activity extends BaseActivity {

    @BindView(R.id.titleTxtView)
    TextView titleTxtView;
    @BindView(R.id.titlelayout)
    LinearLayout titlelayout;
    @BindView(R.id.picImgView)
    ImageView picImgView;
    @BindView(R.id.SKUTxtView)
    TextView SKUTxtView;
    @BindView(R.id.SKU_ParentTxtView)
    TextView SKUParentTxtView;
    @BindView(R.id.NameTxtView)
    TextView NameTxtView;
    @BindView(R.id.StorageNameTxtView)
    TextView StorageNameTxtView;
    @BindView(R.id.StoragePositionTxtView)
    TextView StoragePositionTxtView;
    @BindView(R.id.StockTxtView)
    TextView StockTxtView;
    @BindView(R.id.Cost_PriceTxtView)
    TextView CostPriceTxtView;
    @BindView(R.id.AmountTxtView)
    TextView AmountTxtView;
    @BindView(R.id.PurchaseDaysTxtView)
    TextView PurchaseDaysTxtView;
    @BindView(R.id.CatalogueTxtView)
    TextView CatalogueTxtView;

    private String id;
    private ScreenMeasure sm;
    private GetItemsAsyncTask getItemsAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te05);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getItemsAsyncTask != null){
            getItemsAsyncTask.cancel();
        }
    }

    public void init() {
        sm = getScreenMeasure();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,sm.height*2/5);
        picImgView.setLayoutParams(lp);
        picImgView.setVisibility(View.GONE);

        if(NetWorkTool.checkNetworkState(this)){
            Map<String,String> requestBodyMap = new HashMap<String,String>();
            requestBodyMap.put("itemid",id);
            getItemsAsyncTask = new GetItemsAsyncTask();
            getItemsAsyncTask.startWork(requestBodyMap);
        }else{
            showToast("网络未连接");
        }
    }

    @OnClick(R.id.back)
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    class GetItemsAsyncTask extends MyOkHttpExecute {

        @Override
        protected MyPostOkHttp executeNetWork() {
            return dealwith(WebserviceMethodName.GetItemsDetails,getMainLooper());
        }

        @Override
        public void getNetValue(String JsonString) {
            try {
                JSONObject jsonObject = new JSONObject(JsonString);
                switch(jsonObject.getInt("Status")){
                    case 1:
                        JSONArray dataJSONArray = jsonObject.getJSONArray("Data");
                        JSONObject myJsonObject = dataJSONArray.getJSONObject(0);
                        SKUTxtView.setText(myJsonObject.getString("SKU"));
                        SKUParentTxtView.setText(myJsonObject.getString("SKU_Parent"));
                        NameTxtView.setText(myJsonObject.getString("Name"));
                        StorageNameTxtView.setText(myJsonObject.getString("StorageName"));
                        StoragePositionTxtView.setText(myJsonObject.getString("StoragePosition"));
                        StockTxtView.setText(StringIsNumber.getMoneyString(myJsonObject.getString("Stock")));
                        CostPriceTxtView.setText(StringIsNumber.getMoneyString(myJsonObject.getString("Cost_Price")));
                        AmountTxtView.setText(StringIsNumber.getNumberString(myJsonObject.getString("Amount")));
                        CatalogueTxtView.setText(myJsonObject.getString("Catalogue").equals("null")?"":myJsonObject.getString("Catalogue"));

                        if(NetWorkTool.checkNetworkState(TE05Activity.this)){
                            new getPictureAnsycTask().execute(myJsonObject.getString("PicUrl_Small"));
                        }else{
                            showToast("网络未连接");
                        }
                        break;
                    case -1:
                        //// TODO: 2016/10/18
                        Intent intent = new Intent(TE05Activity.this, TE01Activity.class);
                        startActivity(intent);
                        showToast(jsonObject.getString("Message"));
                        break;
                    case -2:
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

    class getPictureAnsycTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            return loadImageBitmapFromUrl(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null){
                picImgView.setImageBitmap(bitmap);
                picImgView.setVisibility(View.VISIBLE);
            }
        }
    }

    protected Bitmap loadImageBitmapFromUrl(String imageUrl) {

        URL url = null;
        HttpURLConnection connection = null;
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            is = connection.getInputStream();

            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inJustDecodeBounds = true;
            option.inPreferredConfig = Bitmap.Config.ARGB_8888;
            option.inSampleSize = 1;
            BitmapFactory.decodeStream(is,null,option);
            while ((option.outHeight / option.inSampleSize) > sm.height*2/5
                    && (option.outWidth / option.inSampleSize) > sm.width) {
                option.inSampleSize *= 2;
            }
            option.inJustDecodeBounds = false;

            connection.disconnect();
            is.close();

            url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is,null,option);
            if (bitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                int options = 100;
                while (baos.toByteArray().length / 1024 > 512) { // 循环判断如果压缩后图片是否大于20kb,大于继续压缩
                    baos.reset();// 重置baos即清空baos
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                    options -= 20;// 每次都减少20%
                }
                ByteArrayInputStream isBm = new ByteArrayInputStream(
                        baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
                bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection!=null){
                connection.disconnect();
            }
            if (is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

}
