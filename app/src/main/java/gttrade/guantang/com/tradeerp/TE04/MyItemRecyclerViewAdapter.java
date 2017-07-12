package gttrade.guantang.com.tradeerp.TE04;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gttrade.guantang.com.tradeerp.R;

import java.util.List;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private JSONArray mJSONArray = new JSONArray();
    private OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(JSONArray mJSONArray){
        this.mJSONArray = mJSONArray;
        notifyDataSetChanged();
    }

    public interface OnListFragmentInteractionListener{
        void onClick();
    }

    public MyItemRecyclerViewAdapter( OnListFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item,parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            JSONObject item = mJSONArray.getJSONObject(position);
            Object picurl =item.get("PicUrl_Local");

            if (!TextUtils.isEmpty(picurl.toString())){
                Glide.with(context).load(picurl.toString()).centerCrop().placeholder(R.mipmap.pic_defalut)
                        .crossFade().into(holder.picImgView);
            }else{
                holder.picImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.pic_defalut));
            }
            holder.SKUcontentTxtView.setText(item.getString("SKU").equals("null")?"":item.getString("SKU"));
            holder.CKcontentTxtView.setText(item.getString("StorageName").equals("null")?"":item.getString("StorageName"));
            holder.CurrencyIDTxtView.setText(item.getString("CurrencyID").equals("null")?"":item.getString("CurrencyID"));
            holder.NameTxtView.setText(item.getString("Name").equals("null")?"":item.getString("Name"));
            holder.StockTxtView.setText(item.getString("Stock").equals("null")?"":item.getString("Stock"));
            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onClick();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mJSONArray.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picImgView;
        LinearLayout contentLayout;
        TextView SKUcontentTxtView,NameTxtView,StockTxtView,CurrencyIDTxtView,CKcontentTxtView;
        public ViewHolder(View view) {
            super(view);
            picImgView = (ImageView) view.findViewById(R.id.picImgView);
            contentLayout = (LinearLayout) view.findViewById(R.id.contentLayout);
            SKUcontentTxtView = (TextView) view.findViewById(R.id.SKUcontentTxtView);
            NameTxtView = (TextView) view.findViewById(R.id.NameTxtView);
            StockTxtView = (TextView) view.findViewById(R.id.StockTxtView);
            CurrencyIDTxtView = (TextView) view.findViewById(R.id.CurrencyIDTxtView);
            CKcontentTxtView = (TextView) view.findViewById(R.id.CKcontentTxtView);
        }

    }
}
