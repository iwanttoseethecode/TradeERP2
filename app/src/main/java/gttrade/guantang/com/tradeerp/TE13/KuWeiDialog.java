package gttrade.guantang.com.tradeerp.TE13;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

import gttrade.guantang.com.tradeerp.TE12.dialog.AbstractListDialog;
import gttrade.guantang.com.tradeerp.TE13.bean.KuweiListBean;

/**
 * Created by luoling on 2017/3/10.
 */

public class KuWeiDialog extends AbstractListDialog<KuweiListBean.DataBean> {
    protected KuWeiDialog(@NonNull Context context, List<KuweiListBean.DataBean> mList, IOnItemClickDataReturn<KuweiListBean.DataBean> iOnItemClickDataReturn) {
        super(context, mList, iOnItemClickDataReturn);
    }

    @Override
    public void onMyItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        KuweiListBean.DataBean data = (KuweiListBean.DataBean) adapterView.getAdapter().getItem(i);
        iOnItemClickDataReturn.datareturn(data,this);
    }

    @Override
    public void listViewItemsetData(TextView textView, KuweiListBean.DataBean itemData) {
        textView.setText(itemData.getRackName());
    }
}
