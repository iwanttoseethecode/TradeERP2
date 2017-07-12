package gttrade.guantang.com.tradeerp.TE06;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.database.DataBaseOperate;
import gttrade.guantang.com.tradeerp.util.DecimalHelper;
import gttrade.guantang.com.tradeerp.util.NetWorkTool;
import gttrade.guantang.com.tradeerp.util.ScreenMeasure;
import gttrade.guantang.com.tradeerp.util.StringIsNumber;

/**
 * Created by luoling on 2016/10/20.
 */
public class PickDialog extends Dialog implements View.OnClickListener,TextWatcher{


    ImageView picImgView;
    TextView SKUTxtView;
    TextView StoragePositionTxtView;
    TextView ItemNameTextView;
    TextView PickNumTextView;
    EditText PickedEditText;
    ImageButton minusImgView;
    ImageButton plusImgView;
    TextView cancelTxtView;
    TextView confirmTxtView;

    private Context context;
    private Map<String,Object> map;
    private ScreenMeasure sm;
    private DataBaseOperate dbo;

    public PickDialog(Context context, int themeResId, Map<String,Object> map, DataBaseOperate dbo) {
        super(context, themeResId);
        this.context = context;
        this.map = map;
        this.dbo = dbo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickdialog);
        initView();
        init();
    }

    public void initView(){
        picImgView = (ImageView) findViewById(R.id.picImgView);
        SKUTxtView = (TextView) findViewById(R.id.SKUTxtView);
        StoragePositionTxtView = (TextView) findViewById(R.id.StoragePositionTxtView);
        ItemNameTextView = (TextView) findViewById(R.id.ItemNameTextView);
        PickNumTextView = (TextView) findViewById(R.id.PickNumTextView);
        PickedEditText = (EditText) findViewById(R.id.PickedEditText);
        cancelTxtView = (TextView) findViewById(R.id.cancelTxtView);
        confirmTxtView = (TextView) findViewById(R.id.confirmTxtView);
        minusImgView = (ImageButton) findViewById(R.id.minusImgView);
        plusImgView = (ImageButton) findViewById(R.id.plusImgView);

        cancelTxtView.setOnClickListener(this);
        confirmTxtView.setOnClickListener(this);
        minusImgView.setOnClickListener(this);
        plusImgView.setOnClickListener(this);

        PickedEditText.addTextChangedListener(this);
    }

    public void init(){

        SKUTxtView.setText(map.get("SKU")==null?"":map.get("SKU").toString());
        StoragePositionTxtView.setText(map.get("StoragePosition")==null?"":map.get("StoragePosition").toString());
        ItemNameTextView.setText(map.get("ItemName")==null?"":map.get("ItemName").toString());
        PickNumTextView.setText(map.get("PickNum")==null?"":map.get("PickNum").toString());

        sm = ((TE06Activity)context).getScreenMeasure();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sm.width*5/7,sm.width*2/5);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins(0,20,0,20);
        picImgView.setLayoutParams(lp);
        PickedEditText.setText(DecimalHelper.numberDecimalFormat(dbo.getPickedNum_byID(map.get("ID").toString())));
        if (NetWorkTool.checkNetworkState(context)){
            new getPictureAnsycTask().execute(map.get("PicUrl_Small").toString());
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cancelTxtView:
                dismiss();
                break;
            case R.id.confirmTxtView:
                dbo.update_PickedNum(map.get("ID").toString(),PickedEditText.getText().toString().trim().equals("")?"0":PickedEditText.getText().toString().trim());
                if(Double.parseDouble(map.get("PickNum").toString())== Double.parseDouble(PickedEditText.getText().toString().trim())){
                    EventBus.getDefault().post(new ContainMapObject(map,true));
                }else{
                    EventBus.getDefault().post(new ContainMapObject(map,false));
                }
                dismiss();
                break;
            case R.id.minusImgView:
                if (PickedEditText.getText().toString().equals("")) {
                    PickedEditText.setText("0");
                } else {
                    double f = Double.parseDouble(PickedEditText.getText().toString());
                    if(f>=1){
                        PickedEditText.setText(String.valueOf(DecimalHelper.numberDecimalFormat(f-1)));
                    }else{
                        Toast.makeText(context, "数量不能为负数", Toast.LENGTH_LONG).show();
                    }

                }
                break;
            case R.id.plusImgView:
                if (PickedEditText.getText().toString().equals("")) {
                    PickedEditText.setText("1");
                } else {
                    double f = Double.parseDouble(PickedEditText.getText().toString());
                    PickedEditText.setText(String.valueOf(DecimalHelper.numberDecimalFormat(f+1)));
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Double d=Double.parseDouble(map.get("PickNum").toString());
        if (StringIsNumber.stringIsNumBer(charSequence.toString())){
            if (Double.parseDouble(charSequence.toString())>d){
                PickedEditText.setText(String.valueOf(DecimalHelper.numberDecimalFormat(d)));
                ((TE06Activity)context).showToast("拣货数量已达上线");
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

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
