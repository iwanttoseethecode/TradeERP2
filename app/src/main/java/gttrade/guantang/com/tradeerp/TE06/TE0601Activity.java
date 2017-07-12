package gttrade.guantang.com.tradeerp.TE06;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE09.EventBusBean.RefreshBean;
import gttrade.guantang.com.tradeerp.base.MyApplication;
import gttrade.guantang.com.tradeerp.commondapter.CommonAdapter;
import gttrade.guantang.com.tradeerp.commondapter.ViewHolder;
import gttrade.guantang.com.tradeerp.database.DataBaseOperate;
import gttrade.guantang.com.tradeerp.util.StringIsNumber;

public class TE0601Activity extends AppCompatActivity implements TextWatcher {

    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.deleteTxtView)
    ImageButton deleteTxtView;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.delStrImgView)
    ImageView delStrImgView;
    @BindView(R.id.hpListView)
    ListView hpListView;

    private MyAdapter myAdapter;
    private DataBaseOperate dbo = new DataBaseOperate(MyApplication.getContextObject());
    private List<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_te0601);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        editText.addTextChangedListener(this);

        mList = dbo.selectCompletehp_bySKU(editText.getText().toString().trim());
        myAdapter = new MyAdapter(this, mList, R.layout.pick_hpitem);
        hpListView.setAdapter(myAdapter);
    }

    @OnClick({R.id.back, R.id.deleteTxtView, R.id.delStrImgView,})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.deleteTxtView:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("您确定要清空已拣货品？");
                builder.setCancelable(false);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Iterator<Map<String,Object>> it=mList.iterator();
                        while(it.hasNext()){
                            dbo.setZeroPicked(it.next().get("ID").toString());
                        }
                        mList = dbo.selectCompletehp_bySKU(editText.getText().toString().trim());
                        myAdapter.setData(mList);
                        EventBus.getDefault().post(new RefreshBean());
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 0) {
            delStrImgView.setVisibility(View.VISIBLE);
        } else {
            delStrImgView.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        mList = dbo.selectCompletehp_bySKU(editable.toString().trim());
        myAdapter.setData(mList);
    }

    class MyAdapter extends CommonAdapter<Map<String, Object>> {

        public MyAdapter(Context mContext, List<Map<String, Object>> mList, int LayoutId) {
            super(mContext, mList, LayoutId);
        }

        @Override
        public void convert(ViewHolder holder, Map<String, Object> item) {
            ImageView picImgView = holder.getView(R.id.picImgView);
            TextView SKUcontentTxtView = holder.getView(R.id.SKUcontentTxtView);
            TextView PickNumTxtView = holder.getView(R.id.PickNumTxtView);
            TextView CKcontentTxtView = holder.getView(R.id.CKcontentTxtView);
            TextView KWcontentTxtView = holder.getView(R.id.KWcontentTxtView);
            TextView pickTxtView = holder.getView(R.id.pickTxtView);
            TextView PickedNumTxtView = holder.getView(R.id.PickedNumTxtView);
            TextView xiegangTextView = holder.getView(R.id.xiegangTextView);
            TextView NameTxtView = holder.getView(R.id.NameTxtView);

            pickTxtView.setVisibility(View.GONE);
            SKUcontentTxtView.setText(item.get("SKU") == null || item.get("SKU").equals("null") ? "" : item.get("SKU").toString());
            NameTxtView.setText(item.get("ItemName")==null || item.get("ItemName").equals("null")?"":item.get("ItemName").toString());
            String pickNum = item.get("PickNum").toString();
            if (Double.parseDouble(item.get("PickedNum").toString()) > 0 && !item.get("PickedNum").toString().equals(pickNum)) {
                PickedNumTxtView.setText(item.get("PickedNum").toString());
                xiegangTextView.setText("/");
                PickNumTxtView.setText(StringIsNumber.getNumberString(pickNum));
            } else {
                PickedNumTxtView.setText("");
                xiegangTextView.setText("");
                PickNumTxtView.setText(StringIsNumber.getNumberString(pickNum));
            }

            CKcontentTxtView.setText(item.get("StorageName") == null || item.get("StorageName").equals("null") ? "" : item.get("StorageName").toString());
            KWcontentTxtView.setText(item.get("StoragePosition") == null || item.get("StoragePosition").equals("null") ? "" : item.get("StoragePosition").toString());

            Object picurl =item.get("PicUrl_Small");
            Glide.with(context).load(picurl.toString()).centerCrop().placeholder(R.mipmap.pic_defalut)
                    .crossFade().into(picImgView);
//            if (!picurl.toString().equals("") && picurl!=null && !picurl.toString().equals("null")){
//                Picasso.with(context).load(picurl.toString()).into(picImgView);
//            }else{
//                picImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.pic_defalut));
//            }
        }
    }

}
