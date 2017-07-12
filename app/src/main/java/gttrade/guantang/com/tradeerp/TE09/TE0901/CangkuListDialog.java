package gttrade.guantang.com.tradeerp.TE09.TE0901;

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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gttrade.guantang.com.tradeerp.R;

/**
 * Created by luoling on 2017/1/12.
 */

public class CangkuListDialog extends AlertDialog {

    private ListView mListView;
    private JSONArray jsonArray;

    private ICangkuListResponse mICangkuListResponse;

    public interface ICangkuListResponse{
        void setCangkuValue(String id,String name);
    }

    public void setmICangkuListResponse(ICangkuListResponse iCangkuListResponse){
        mICangkuListResponse = iCangkuListResponse;
    }

    public CangkuListDialog(@NonNull Context context, JSONArray jsonArray) {
        super(context);
        this.jsonArray = jsonArray;
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
                JSONObject jsonObject= (JSONObject) adapterView.getAdapter().getItem(i);
                try {
                    mICangkuListResponse.setCangkuValue(jsonObject.getString("ID"),jsonObject.getString("Name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
    }

    class MyAdapter extends BaseAdapter{
        private LayoutInflater inflater;
        private Context mContext;

        public MyAdapter(Context context){
            mContext = context;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return jsonArray.get(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
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

            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                textView.setText(jsonObject.getString("Name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

}
