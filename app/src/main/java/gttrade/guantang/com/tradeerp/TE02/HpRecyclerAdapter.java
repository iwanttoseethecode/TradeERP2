package gttrade.guantang.com.tradeerp.TE02;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE04.TE0401Activity;
import gttrade.guantang.com.tradeerp.TE05.TE05Activity;
import gttrade.guantang.com.tradeerp.util.AsyncImageLoader;
import gttrade.guantang.com.tradeerp.util.StringIsNumber;

/**
 * Created by luoling on 2016/11/3.
 */
public class HpRecyclerAdapter extends RecyclerView.Adapter<HpRecyclerAdapter.MyViewHolder> {

    private List<JSONObject> jsonList = new ArrayList<JSONObject>();

    private LayoutInflater inflater;
    private Context context;

//    AsyncImageLoader myImageLoader = new AsyncImageLoader();

    public HpRecyclerAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    public void setData(List<JSONObject> mList) {
        jsonList = mList;
        notifyDataSetChanged();
    }

    @Override
    public HpRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.hplist_item,null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(HpRecyclerAdapter.MyViewHolder holder, int position) {

        final JSONObject item = jsonList.get(position);
        try {
            holder.SKUcontentTxtView.setText(item.getString("SKU").equals("null") ? "" : item.getString("SKU"));
            holder.numContentTxtView.setText((StringIsNumber.getNumberString(item.getString("Stock"))));
            holder.CKcontentTxtView.setText(item.getString("StorageName").equals("null") ? "" : item.getString("StorageName"));
            holder.KWcontentTxtView.setText(item.getString("StoragePosition").equals("null") ? "" : item.getString("StoragePosition"));
            holder.NameTxtView.setText(item.getString("Name").equals("null")?"":item.getString("Name"));
            holder.WeakPicImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.pic_defalut));
//                String ID = item.getString("ID");
//                holder.WeakPicImgView.get().setTag(ID);
//                myImageLoader.loadBitmap(ID.equals("null") ? "" : ID,
//                        item.getString("PicUrl_Small").equals("null") ? "" : item.getString("PicUrl_Small"),
//                        holder.WeakPicImgView.get());
            Object picurl =item.get("PicUrl_Small");

            if (!TextUtils.isEmpty(picurl.toString())){
                Glide.with(context).load(picurl.toString()).centerCrop().placeholder(R.mipmap.pic_defalut)
                        .crossFade().into(holder.WeakPicImgView);
            }else{
                holder.WeakPicImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.pic_defalut));
            }

            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(context, TE05Activity.class);
                        intent.putExtra("ID", item.getString("ID"));
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout contentLayout;
        ImageView WeakPicImgView;
        TextView SKUcontentTxtView, numContentTxtView, CKcontentTxtView, KWcontentTxtView,NameTxtView;

        public MyViewHolder(View itemView) {
            super(itemView);
            WeakPicImgView = (ImageView) itemView.findViewById(R.id.picImgView);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.contentLayout);
            SKUcontentTxtView = (TextView) itemView.findViewById(R.id.SKUcontentTxtView);
            numContentTxtView = (TextView) itemView.findViewById(R.id.numContentTxtView);
            CKcontentTxtView = (TextView) itemView.findViewById(R.id.CKcontentTxtView);
            KWcontentTxtView = (TextView) itemView.findViewById(R.id.KWcontentTxtView);
            NameTxtView = (TextView) itemView.findViewById(R.id.NameTxtView);
        }
    }

}
