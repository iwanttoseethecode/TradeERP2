package gttrade.guantang.com.tradeerp.TE09.TE0902;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE09.PanDianAdapter;
import gttrade.guantang.com.tradeerp.TE09.PanDianDatabaseOperation;
import gttrade.guantang.com.tradeerp.TE09.PanDianDialog;

/**
 * Created by luoling on 2017/1/19.
 */

public class CheckedAdapter extends RecyclerView.Adapter<CheckedAdapter.MyViewHolder> implements PanDianDialog.IOnCheckDialogConfirmBtn{

    private LayoutInflater inflater;
    private Context context;
    private List<Map<String,Object>> mList = new LinkedList<>();
    private PanDianDatabaseOperation panDianDatabaseOperation;

    private IRefreshList mIRefreshList;



    public interface IRefreshList{
        void refreshList();
    }

    public void setIRefreshList(IRefreshList iRefreshList){
        this.mIRefreshList = iRefreshList;
    }

    public CheckedAdapter (Context mContext,PanDianDatabaseOperation panDianDatabaseOperation){
        this.context = mContext;
        inflater = LayoutInflater.from(context);
        this.panDianDatabaseOperation = panDianDatabaseOperation;
    }

    public void setData(List<Map<String,Object>> list){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public CheckedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.pandianwancheng_hpitem,null);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CheckedAdapter.MyViewHolder holder, int position) {
        final Map<String,Object> map = mList.get(position);
        holder.SKUcontentTxtView.setText(map.get("ItemSKU").toString());
        holder.NameTxtView.setText(map.get("ItemName").toString());

        holder.CKcontentTxtView.setText(map.get("Storage").toString());
        holder.KWcontentTxtView.setText(map.get("Position").toString());

        String checkNum = map.get("CheckNum").toString();
        String Stock = map.get("Stock").toString();

        holder.PandianNumTxtView.setText(checkNum);
        holder.StockTxtView.setText(Stock);

        if (!Stock.equals(checkNum)){
            holder.spritTxtView.setVisibility(View.VISIBLE);
            holder.PandianNumTxtView.setVisibility(View.VISIBLE);
        }else{
            holder.spritTxtView.setVisibility(View.GONE);
            holder.PandianNumTxtView.setVisibility(View.GONE);
        }

        String url = map.get("PicUrl_Small").toString();

        if (!TextUtils.isEmpty(url)){
            Glide.with(context).load(url).centerCrop().placeholder(R.mipmap.pic_defalut)
                    .crossFade().into(holder.picImgView);
        }else{
            holder.picImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.pic_defalut));
        }


        holder.ItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PanDianDialog panDianDialog = new PanDianDialog(context,R.style.ButtonDialogStyle,map,panDianDatabaseOperation);
                panDianDialog.setCanceledOnTouchOutside(false);
                panDianDialog.setmIRemoveHPtoShopingCar(CheckedAdapter.this);
                Window window = panDianDialog.getWindow();
                window.getDecorView().setPadding(0,0,0,0);
                window.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams lp= window.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
                panDianDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    

    @Override
    public void AfterDialogConfirmBtn(Map<String, Object> map) {
        mIRefreshList.refreshList();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView picImgView;
        TextView SKUcontentTxtView,NameTxtView,PandianNumTxtView,CKcontentTxtView,KWcontentTxtView,
                  spritTxtView,StockTxtView;
        RelativeLayout ItemLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            picImgView = (ImageView) itemView.findViewById(R.id.picImgView);
            SKUcontentTxtView = (TextView) itemView.findViewById(R.id.SKUcontentTxtView);
            NameTxtView = (TextView) itemView.findViewById(R.id.NameTxtView);
            PandianNumTxtView = (TextView) itemView.findViewById(R.id.PandianNumTxtView);
            CKcontentTxtView = (TextView) itemView.findViewById(R.id.CKcontentTxtView);
            KWcontentTxtView = (TextView) itemView.findViewById(R.id.KWcontentTxtView);
            ItemLayout = (RelativeLayout) itemView.findViewById(R.id.ItemLayout);
            spritTxtView = (TextView) itemView.findViewById(R.id.spritTxtView);
            StockTxtView = (TextView) itemView.findViewById(R.id.StockTxtView);
        }
    }
}
