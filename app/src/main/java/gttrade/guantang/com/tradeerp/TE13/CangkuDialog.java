package gttrade.guantang.com.tradeerp.TE13;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import gttrade.guantang.com.tradeerp.TE12.dialog.AbstractListDialog;
import gttrade.guantang.com.tradeerp.TE13.bean.CangkuListBean;

/**
 * Created by luoling on 2017/3/10.
 */

public class CangkuDialog extends AbstractListDialog<CangkuListBean.DataBean> {

    protected CangkuDialog(@NonNull Context context, List<CangkuListBean.DataBean> mList, IOnItemClickDataReturn<CangkuListBean.DataBean> iOnItemClickDataReturn) {
        super(context, mList, iOnItemClickDataReturn);
    }

    @Override
    public void onMyItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CangkuListBean.DataBean data = (CangkuListBean.DataBean) adapterView.getAdapter().getItem(i);
        iOnItemClickDataReturn.datareturn(data,this);
    }

    @Override
    public void listViewItemsetData(TextView textView, CangkuListBean.DataBean itemData) {
        textView.setText(itemData.getName());
    }
}
