package gttrade.guantang.com.tradeerp.TE06;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gttrade.guantang.com.tradeerp.R;

/**
 * Created by luoling on 2016/11/22.
 */
public class ErrorHpRecycleAdapter extends RecyclerView.Adapter<ErrorHpRecycleAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private JSONArray mJSONArray;

    public ErrorHpRecycleAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    public void setData(JSONArray mJSONArray){
        this.mJSONArray = mJSONArray;
        notifyDataSetChanged();
    }

    @Override
    public ErrorHpRecycleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.pickhperror_layout,null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(ErrorHpRecycleAdapter.MyViewHolder holder, int position) {
        try {
            JSONObject item = (JSONObject) mJSONArray.get(position);
            holder.hpnameTxtView.setText(item.getString("ItemName"));
            holder.SKUTxtView.setText(item.getString("SKU"));
            holder.messageTxtView.setText(item.getString("Info"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mJSONArray.length();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView hpnameTxtView,SKUTxtView,messageTxtView;

        public MyViewHolder(View itemView) {
            super(itemView);
            hpnameTxtView = (TextView) itemView.findViewById(R.id.hpnameTxtView);
            SKUTxtView = (TextView) itemView.findViewById(R.id.SKUcontentTxtView);
            messageTxtView = (TextView) itemView.findViewById(R.id.messageTxtView);
        }
    }
}
