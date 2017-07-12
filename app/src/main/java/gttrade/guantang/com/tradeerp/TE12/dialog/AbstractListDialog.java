package gttrade.guantang.com.tradeerp.TE12.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.TE09.TE0901.CangkuListDialog;
import gttrade.guantang.com.tradeerp.TE12.bean.FilterOrdersInitialDataBean;

/**
 * Created by luoling on 2017/3/9.
 */

public abstract class AbstractListDialog<T> extends AlertDialog{

    protected ListView mListView;
    protected List<T> mList;
    protected IOnItemClickDataReturn<T> iOnItemClickDataReturn;

    public interface IOnItemClickDataReturn<V>{
        String datareturn(V data,AlertDialog dialog);
    }

    protected AbstractListDialog(@NonNull Context context,List<T> mList,IOnItemClickDataReturn<T> iOnItemClickDataReturn) {
        super(context);
        this.mList = mList;
        this.iOnItemClickDataReturn = iOnItemClickDataReturn;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_list);
        mListView = (ListView) findViewById(R.id.popuplist);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createAdapter();
    }

    private void createAdapter(){
        MyAdapter adapter = new MyAdapter(getContext());
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onMyItemClick(adapterView,view, i, l);
                dismiss();
            }
        });
    }

    public abstract void onMyItemClick(AdapterView<?> adapterView, View view, int i, long l);

    class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Context mContext;

        public MyAdapter(Context context){
            mContext = context;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public T getItem(int position) {

            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            if (convertView==null){
                convertView = inflater.inflate(R.layout.popupwindow_list_textview,null);
                textView = (TextView) convertView.findViewById(R.id.textview_popup);
                convertView.setTag(textView);
            }else{
                textView = (TextView) convertView.getTag();
            }

            listViewItemsetData(textView,getItem(position));

            return convertView;
        }
    }

    public abstract void listViewItemsetData(TextView textView,T itemData);

}
