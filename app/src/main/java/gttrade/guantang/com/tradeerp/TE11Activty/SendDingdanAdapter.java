package gttrade.guantang.com.tradeerp.TE11Activty;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gttrade.guantang.com.tradeerp.R;

/**
 * Created by luoling on 2017/2/14.
 */

public class SendDingdanAdapter extends RecyclerView.Adapter<SendDingdanAdapter.MyViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<Map<String,Object>> mList = new ArrayList<Map<String,Object>>();

    public SendDingdanAdapter(Context context){
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void setData(List<Map<String,Object>> list){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.senddingdan_item,null);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    private DeleteItemInterface mDeleteItemInterface;

    public interface DeleteItemInterface{
        void deleteItem(String OrderNo);
    }

    public void setDeleteItemInterface(DeleteItemInterface deleteItemInterface){
        this.mDeleteItemInterface = deleteItemInterface;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Map<String,Object> map = mList.get(position);
        holder.dingdanTextView.setText(map.get("OrderNo").toString());
        //待上传为0，上传成功为1，上传失败为-1
        if (Integer.parseInt(map.get("Status").toString())==SendDingdanType.NotUpDate.getValues()){
            holder.StatusTxtView.setText("待上传");
        }else if (Integer.parseInt(map.get("Status").toString())==SendDingdanType.Success.getValues()){
            holder.StatusTxtView.setText("上传成功");
        }else if (Integer.parseInt(map.get("Status").toString())==SendDingdanType.Fail.getValues()){
            holder.StatusTxtView.setText("上传失败");
        }

        holder.wuliuTextView.setText(map.get("ShipingMethod").toString());
        holder.yundanTxtView.setText(map.get("TrackingNo").toString());

        holder.ItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDeleteItemInterface.deleteItem(map.get("OrderNo").toString());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout ItemLayout;
        TextView dingdanTextView,StatusTxtView,wuliuTextView,yundanTxtView;
        public MyViewHolder(View itemView) {
            super(itemView);
            dingdanTextView = (TextView) itemView.findViewById(R.id.dingdanTextView);
            StatusTxtView = (TextView) itemView.findViewById(R.id.StatusTxtView);
            wuliuTextView = (TextView) itemView.findViewById(R.id.wuliuTextView);
            yundanTxtView = (TextView) itemView.findViewById(R.id.yundanTxtView);
            ItemLayout = (RelativeLayout) itemView.findViewById(R.id.ItemLayout);
        }
    }
}
