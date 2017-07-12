package gttrade.guantang.com.tradeerp.RecyclerViewDemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gttrade.guantang.com.tradeerp.R;

/**
 * Created by luoling on 2016/10/31.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<String> mList = new ArrayList<String>();

    public MyRecyclerAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<String> mlist){
        this.mList = mlist;
        notifyDataSetChanged();
    }

    @Override
    public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.popupwindow_list_textview,null);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, int position) {
        holder.itemTxtView.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView itemTxtView;
        public MyViewHolder(View view) {
            super(view);
            itemTxtView = (TextView) view.findViewById(R.id.textview_popup);
        }
    }
}
