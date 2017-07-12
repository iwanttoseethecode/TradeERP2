package gttrade.guantang.com.tradeerp.TE02;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE04.TE0401Activity;
import gttrade.guantang.com.tradeerp.base.BaseActivity;
import gttrade.guantang.com.tradeerp.util.ScreenMeasure;
import gttrade.guantang.com.tradeerp.util.StringIsNumber;

/**
 * Created by luoling on 2016/11/7.
 */
public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.MyHolderView> {

    private Context context;
    private LayoutInflater inflater;
    private List<JSONObject> mList = new ArrayList<JSONObject>();


    public OrderRecyclerAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<JSONObject> mlist){
        mList = mlist;
        notifyDataSetChanged();
    }

    @Override
    public OrderRecyclerAdapter.MyHolderView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.order_item_layout,null,false);
        ScreenMeasure sm = ((BaseActivity) context).getScreenMeasure();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(sm.width, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        MyHolderView holder = new MyHolderView(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(OrderRecyclerAdapter.MyHolderView holder, int position) {

        final JSONObject item = mList.get(position);
        try {
            if (!item.getString("PickingStaus").equals("")) {
                //PickingStaus 拣货状态 1表示异常订单 2表示已发货 3待发货
                if (item.getInt("PickingStaus") == 2) {
                    holder.picImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.complete));
                } else if (item.getInt("PickingStaus") == 3 ) {
                    holder.picImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.uncomplete));
                } else if (item.getInt("PickingStaus") == 1) {
                    holder.picImgView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.exception));
                }
            }

            holder.eShopNameTxtView.setText(item.getString("eShopName").equals("null") ? "" : item.getString("eShopName"));
            holder.BuyerIDTxtView.setText(item.getString("BuyerID").equals("null") ? "" : item.getString("BuyerID"));
            holder.NoTxtView.setText(item.getString("No").equals("null") ? "" : item.getString("No"));
            holder.PlatformTxtView.setText(item.getString("No_Platform").equals("null") ? "" : item.getString("No_Platform"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String orderid = item.getString("ID");
                    Intent intent = new Intent(context, TE0401Activity.class);
                    intent.putExtra("orderid", orderid);
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }



    class MyHolderView extends RecyclerView.ViewHolder{
        LinearLayout contentLayout;
        ImageView picImgView;
        TextView eShopNameTxtView,BuyerIDTxtView,NoTxtView,PlatformTxtView;
        public MyHolderView(View itemView) {
            super(itemView);
            picImgView = (ImageView) itemView.findViewById(R.id.picImgView);
            eShopNameTxtView = (TextView) itemView.findViewById(R.id.eShopNameTxtView);
            BuyerIDTxtView = (TextView) itemView.findViewById(R.id.BuyerIDTxtView);
            NoTxtView = (TextView) itemView.findViewById(R.id.NoTxtView);
            PlatformTxtView = (TextView) itemView.findViewById(R.id.PlatformTxtView);

            contentLayout = (LinearLayout) itemView.findViewById(R.id.contentLayout);
        }
    }

}
