package gttrade.guantang.com.tradeerp.TE09.TE0903;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gttrade.guantang.com.tradeerp.R;

/**
 * Created by luoling on 2017/1/23.
 */

public class ErrorCheckHPAdapter extends RecyclerView.Adapter<ErrorCheckHPAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Map<String,Object>> mList = new ArrayList<>();

    public ErrorCheckHPAdapter(Context mContext){
        this.context = mContext;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<Map<String,Object>> list){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public ErrorCheckHPAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.errorpandian_hpitem,null);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ErrorCheckHPAdapter.MyViewHolder holder, int position) {
        Map<String,Object> map = mList.get(position);

        String ItemSKU = map.get("ItemSKU").toString();
        holder.SKUcontentTxtView.setText(TextUtils.isEmpty(ItemSKU)?"":ItemSKU);

        String ItemName = map.get("ItemName").toString();
        holder.NameTxtView.setText(TextUtils.isEmpty(ItemName)?"":ItemName);

        String netStock = map.get("netStock").toString();
        holder.onLinePandianNumTxtView.setText(TextUtils.isEmpty(netStock)?"":netStock);

        String Stock = map.get("Stock").toString();
        holder.localPandianNumTxtView.setText(TextUtils.isEmpty(Stock)?"":Stock);

        String CheckNum = map.get("CheckNum").toString();
        holder.PandianNumTxtView.setText(TextUtils.isEmpty(CheckNum)?"":CheckNum);

        String url = map.get("PicUrl_Small").toString();

        if (TextUtils.isEmpty(url)){
            holder.picImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.pic_defalut));
        }else{
            Glide.with(context).load(url).centerCrop().placeholder(R.mipmap.pic_defalut)
                    .crossFade().into(holder.picImgView);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView picImgView;
        TextView SKUcontentTxtView,NameTxtView,onLinePandianNumTxtView,localPandianNumTxtView,PandianNumTxtView;
        public MyViewHolder(View itemView) {
            super(itemView);
            picImgView = (ImageView) itemView.findViewById(R.id.picImgView);
            SKUcontentTxtView = (TextView) itemView.findViewById(R.id.SKUcontentTxtView);
            NameTxtView = (TextView) itemView.findViewById(R.id.NameTxtView);
            onLinePandianNumTxtView = (TextView) itemView.findViewById(R.id.onLinePandianNumTxtView);
            localPandianNumTxtView = (TextView) itemView.findViewById(R.id.localPandianNumTxtView);
            PandianNumTxtView = (TextView) itemView.findViewById(R.id.PandianNumTxtView);
        }
    }
}
