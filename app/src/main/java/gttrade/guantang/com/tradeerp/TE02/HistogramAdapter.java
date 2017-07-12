package gttrade.guantang.com.tradeerp.TE02;


import android.content.Context;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.commondapter.CommonAdapter;
import gttrade.guantang.com.tradeerp.commondapter.ViewHolder;
import gttrade.guantang.com.tradeerp.util.DecimalHelper;
import gttrade.guantang.com.tradeerp.util.StringIsNumber;

/**
 * Created by luoling on 2016/10/8.
 */
public class HistogramAdapter extends CommonAdapter<JSONObject>{

    //0 代表按销售额统计，1 代表按订单数统计
    private int type = 0;

    public HistogramAdapter(Context mContext, List mList, int LayoutId) {
        super(mContext, mList, LayoutId);
    }

    @Override
    public void convert(ViewHolder holder, JSONObject item) {
        HistogramView histogramView = holder.getView(R.id.histogramView);
        TextView percentTxtView = holder.getView(R.id.percentTxtView);
        TextView storeTxtView = holder.getView(R.id.storeTxtView);
        TextView moneyTxtView = holder.getView(R.id.moneyTxtView);

        float percent = 0;
        try {
            storeTxtView.setText(item.getString("eShopName"));
            if(type==0){
                moneyTxtView.setText(getMoneyString(item.getString("Amount"))+"元");
                percent = Float.parseFloat(item.getString("amountrate"));
                percentTxtView.setText(getPercentString(item.getString("amountrate")));
            }else if(type==1){
                moneyTxtView.setText(getMoneyString(item.getString("OrderNum"))+"笔");
                percent = Float.parseFloat(item.getString("numrate"));
                percentTxtView.setText(getPercentString(item.getString("numrate")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        histogramView.setPercent(percent);
    }



    /**
     * @params type 0 代表按销售额统计，1 代表按订单数统计
     * */
    public void setType(int type){
        this.type = type;
    }

    public String getPercentString(String NumberStr) {
        if (StringIsNumber.stringIsNumBer(NumberStr)) {
            return DecimalHelper.percentDecimalFormat(Double.parseDouble(NumberStr));
        } else {
            return "0";
        }
    }

    public String getMoneyString(String NumberStr) {
        if (StringIsNumber.stringIsNumBer(NumberStr)) {
            return DecimalHelper.moneyDecimalFormat(Double.parseDouble(NumberStr));
        } else {
            return "";
        }
    }

}
