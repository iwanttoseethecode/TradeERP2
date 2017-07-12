package gttrade.guantang.com.tradeerp.TE09;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.util.DecimalHelper;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.util.ScreenMeasure;

import static gttrade.guantang.com.tradeerp.R.id.PickNumTextView;

/**
 * Created by luoling on 2017/1/18.
 */

public class PanDianDialog extends Dialog {

    @BindView(R.id.picImgView)
    ImageView picImgView;
    @BindView(R.id.SKUTxtView)
    TextView SKUTxtView;
    @BindView(R.id.ItemNameTextView)
    TextView ItemNameTextView;
    @BindView(R.id.StoragePositionTxtView)
    TextView StoragePositionTxtView;
    @BindView(R.id.StockNumTextView)
    TextView StockNumTextView;
    @BindView(R.id.minusImgView)
    ImageButton minusImgView;
    @BindView(R.id.CheckNumEditText)
    EditText CheckNumEditText;
    @BindView(R.id.plusImgView)
    ImageButton plusImgView;
    @BindView(R.id.cancelTxtView)
    TextView cancelTxtView;
    @BindView(R.id.confirmTxtView)
    TextView confirmTxtView;

    private Map<String,Object> map;
    private ScreenMeasure sm;
    private Context context;
    private PanDianDatabaseOperation panDianDatabaseOperation;

    private IOnCheckDialogConfirmBtn mIOnCheckDialogConfirmBtn;

    public interface IOnCheckDialogConfirmBtn{
        void AfterDialogConfirmBtn(Map<String,Object> map);
    }

    public void setmIRemoveHPtoShopingCar(IOnCheckDialogConfirmBtn iOnCheckDialogConfirmBtn){
        this.mIOnCheckDialogConfirmBtn = iOnCheckDialogConfirmBtn;
    }

    public PanDianDialog(Context context, int themeResId, Map<String,Object> map, PanDianDatabaseOperation panDianDatabaseOperation) {
        super(context, themeResId);
        this.map = map;
        this.context = context;
        this.panDianDatabaseOperation = panDianDatabaseOperation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pandian_dialog);
        ButterKnife.bind(this);
        init();
    }

    public void init(){

        SKUTxtView.setText(map.get("ItemSKU")==null?"":map.get("ItemSKU").toString());
        StoragePositionTxtView.setText(map.get("Position")==null?"":map.get("Position").toString());
        ItemNameTextView.setText(map.get("ItemName")==null?"":map.get("ItemName").toString());

        String Stock = map.get("Stock").toString();

        StockNumTextView.setText(Stock==null?"":Stock);

        sm = ((BaseActivity)context).getScreenMeasure();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sm.width*5/7,sm.width*2/5);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins(0,20,0,20);
        picImgView.setLayoutParams(lp);

        String num = panDianDatabaseOperation.getHPCheckNum(map.get("ItemID").toString());
        if (num != null){
            if (Integer.parseInt(num)<0){
                //盘点数为负自动设置为0
                num = "0";
            }
            CheckNumEditText.setText(DecimalHelper.numberDecimalFormat(Double.parseDouble(num)));
        }else{
            if (Integer.parseInt(Stock)<0){
                //盘点数为负自动设置为0
                Stock = "0";
            }
            CheckNumEditText.setText(Stock==null?"":Stock);
        }

        if (NetWorkTool.checkNetworkState(context)){
            new getPictureAnsycTask().execute(map.get("PicUrl_Small").toString());
        }
    }


    @OnClick({R.id.minusImgView, R.id.plusImgView, R.id.cancelTxtView, R.id.confirmTxtView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.minusImgView:
                if (CheckNumEditText.getText().toString().equals("")) {
                    CheckNumEditText.setText("0");
                } else {
                    double f = Double.parseDouble(CheckNumEditText.getText().toString());
                    if(f>=1){
                        CheckNumEditText.setText(String.valueOf(DecimalHelper.numberDecimalFormat(f-1)));
                    }else{
                        Toast.makeText(context, "数量不能为负数", Toast.LENGTH_LONG).show();
                    }

                }
                break;
            case R.id.plusImgView:
                if (CheckNumEditText.getText().toString().equals("")) {
                    CheckNumEditText.setText("1");
                } else {
                    double f = Double.parseDouble(CheckNumEditText.getText().toString());
                    CheckNumEditText.setText(String.valueOf(DecimalHelper.numberDecimalFormat(f+1)));
                }
                break;
            case R.id.cancelTxtView:
                dismiss();
                break;
            case R.id.confirmTxtView:
                if (TextUtils.isEmpty(CheckNumEditText.getText().toString().trim())) {
                    Toast.makeText(context,"盘点数量不能为空",Toast.LENGTH_SHORT).show();
                }
                panDianDatabaseOperation.updateHPCheckNum(map.get("ItemID").toString(), CheckNumEditText.getText().toString().trim());
                mIOnCheckDialogConfirmBtn.AfterDialogConfirmBtn(map);
                dismiss();
                break;
        }
    }

    class getPictureAnsycTask extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            return loadImageBitmapFromUrl(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap!=null){
                picImgView.setImageBitmap(bitmap);

            }else{
                picImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.pic_defalut));
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
            while ((option.outHeight / option.inSampleSize) > sm.width*5/7
                    && (option.outWidth / option.inSampleSize) > sm.width*2/5) {
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
