package gttrade.guantang.com.tradeerp.catalogue;

/*
 * 为保证目录层级可以一层一层的添加，请先声明创建该适配器对象，再添加数据。
 * 推荐方法：
 *    TagAdapter mTagAdapter = new TagAdapter(this);
        mTagFlowLayout.setAdapter(mTagAdapter);
        mTagAdapter.addData(ElementBean elementBean);
 * */

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import gttrade.guantang.com.tradeerp.R;

public class MuluAdapter
{
	private Context context;
    private ArrayList<ElementBean> mList= new ArrayList<ElementBean>();
    private OnDataChangedListener mOnDataChangedListener;
    private LayoutInflater inflater;
    private int checknum=0;

	private IListViewRefresh mIlistViewRefresh;

	public interface IListViewRefresh{
		void myListViewRefresh(ElementBean elementBean);
	}
    
    public MuluAdapter(Context context){
    	this.context = context;
    	inflater = LayoutInflater.from(context);
		mIlistViewRefresh = (IListViewRefresh) this.context;
    }
    public MuluAdapter(Context context,ArrayList<ElementBean> datas)
    {
        mList = datas;
        this.context = context;
        inflater = LayoutInflater.from(context);
		mIlistViewRefresh = (IListViewRefresh) this.context;
    }

    public MuluAdapter(Context context,ElementBean datas)
    {
    	this.context = context;
    	inflater = LayoutInflater.from(context);
        mList.add(datas);
    }
    
    public void addData(ArrayList<ElementBean> datas){
    	mList = datas;
    	notifyDataChanged();
    }
    
    public void addData(ElementBean datas){
    	mList.add(datas);
    	notifyDataChanged();
    }
    
    public void removeLastElementBean(){
    	mList.remove(mList.size()-1);
    	notifyDataChanged();
    }
    
    public ElementBean getLastElementBean(){
    	return mList.get(mList.size()-1);
    }
    
    static interface OnDataChangedListener
    {
        void onChanged();
    }
    
    void setOnDataChangedListener(OnDataChangedListener listener)
    {
        mOnDataChangedListener = listener;
    }
    
    
    public int getCount()
    {
        return mList == null ? 0 : mList.size();
    }
    
    public void notifyDataChanged()
    {
        mOnDataChangedListener.onChanged();
    }

    public ElementBean getItem(int position)
    {
        return mList.get(position);
    }

    public int getChecknum(){
    	return checknum;
    }
    
    public String getListString(){
    	StringBuilder strBuilder = new StringBuilder();
    	Iterator<ElementBean> it = mList.iterator();
    	while(it.hasNext()){
    		if(strBuilder.length()<1){
    			strBuilder.append(it.next().getName());
    		}else{
    			strBuilder.append("\\"+it.next().getName());
    		}
    	}
    	return strBuilder.toString();
    }
    
    public View getView(MuLuFlowLayout parent,View convertView, final int position){
    	ViewHolder holder = null;
    	if(convertView == null){
    		holder = new ViewHolder();
    		convertView =inflater.inflate(R.layout.mululayout,parent, false);
    		holder.textview = (TextView) convertView.findViewById(R.id.txtview);
    		holder.imageView = (ImageView) convertView.findViewById(R.id.arrow);
    		holder.arrow_yellowBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.advance_yellow_right);
    		holder.arrow_greyBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.advanced_grey);
    		convertView.setTag(holder);
    	}else{
    		holder=(ViewHolder) convertView.getTag();
    	}
    	
    	final ElementBean elementBean = getItem(position);
    	if(position == mList.size()-1){
    		holder.imageView.setImageBitmap(holder.arrow_yellowBitmap);
    		holder.textview.setTextColor(Color.parseColor("#f5b53a"));
    	}else{
    		holder.imageView.setImageBitmap(holder.arrow_greyBitmap);
    		holder.textview.setTextColor(Color.parseColor("#333333"));
    	}
    	holder.textview.setText(elementBean.getName());
		
    	holder.textview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				checknum = position;
				//点击某一级目录，如果有子目录就清理掉子目录。
				Iterator<ElementBean> it=mList.iterator();
				while(it.hasNext()){
					ElementBean mElementBean = it.next();
					if(elementBean.getSindex().length()<mElementBean.getSindex().length()){
						it.remove();
					}
				}
				notifyDataChanged();
				mIlistViewRefresh.myListViewRefresh(elementBean);
			}
		});
		
		return convertView;
    };
    
    class ViewHolder{
    	TextView textview;
    	ImageView imageView;
    	Bitmap arrow_yellowBitmap,arrow_greyBitmap;
    }
}