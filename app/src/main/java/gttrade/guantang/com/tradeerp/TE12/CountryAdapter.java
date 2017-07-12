package gttrade.guantang.com.tradeerp.TE12;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gttrade.guantang.com.tradeerp.R;
import gttrade.guantang.com.tradeerp.sortlistview.SortModel;

/**
 * Created by luoling on 2017/3/10.
 */

public class CountryAdapter extends BaseAdapter implements SectionIndexer {

    private List<SortModel> mList = new ArrayList<SortModel>();
    private LayoutInflater inflater;
    private Context context;

    public CountryAdapter(Context mContext){
        this.context = mContext;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<SortModel> list){
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.lbchoseitem,null);
            textView = (TextView) convertView.findViewById(R.id.lbitem);
            convertView.setTag(textView);
        }else{
            textView = (TextView) convertView.getTag();
        }

        textView.setText(mList.get(position).getCountry());

        return convertView;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @Override
    public int getPositionForSection(int arg0) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = ((SortModel) mList.get(i)).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == arg0) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    @Override
    public int getSectionForPosition(int position) {
        // TODO Auto-generated method stub
        return ((SortModel) mList.get(position)).getSortLetters().charAt(0);
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        // TODO 自动生成的方法存根
        return null;
    }

}
