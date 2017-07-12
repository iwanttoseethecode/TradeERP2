package gttrade.guantang.com.tradeerp.TE12.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by luoling on 2017/3/9.
 */

public class ShipmentDialog extends AbstractListDialog<String>{
    public ShipmentDialog(@NonNull Context context, List<String> mList, IOnItemClickDataReturn<String> iOnItemClickDataReturn) {
        super(context, mList, iOnItemClickDataReturn);
    }

    @Override
    public void onMyItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String data = (String) adapterView.getAdapter().getItem(i);
        iOnItemClickDataReturn.datareturn(data,this);
    }

    @Override
    public void listViewItemsetData(TextView textView, String itemData) {
        textView.setText(itemData);
    }
}
