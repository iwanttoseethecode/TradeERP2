package gttrade.guantang.com.tradeerp.TE12.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import gttrade.guantang.com.tradeerp.TE12.bean.FilterOrdersInitialDataBean;


/**
 * Created by luoling on 2017/3/9.
 */

public class EShopDialog extends AbstractListDialog<FilterOrdersInitialDataBean.DataBean.EShopBean> {


    public EShopDialog(@NonNull Context context, List<FilterOrdersInitialDataBean.DataBean.EShopBean> mList, IOnItemClickDataReturn iOnItemClickDataReturn) {
        super(context, mList, iOnItemClickDataReturn);
    }


    @Override
    public void onMyItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        FilterOrdersInitialDataBean.DataBean.EShopBean itemdata = (FilterOrdersInitialDataBean.DataBean.EShopBean) adapterView.getAdapter().getItem(i);
        iOnItemClickDataReturn.datareturn(itemdata,this);
    }

    @Override
    public void listViewItemsetData(TextView textView, FilterOrdersInitialDataBean.DataBean.EShopBean itemData) {
        textView.setText(((FilterOrdersInitialDataBean.DataBean.EShopBean) itemData).getName());
    }


}
