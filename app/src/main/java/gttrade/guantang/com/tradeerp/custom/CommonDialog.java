package gttrade.guantang.com.tradeerp.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

public class CommonDialog extends Dialog{
	
	private Context context;
	private LayoutInflater inflater;
	private int layout;
	private View parentView;
	private SparseArray<View> ViewSparseArray;
	private Dialog dialog;
	
	private Object[] objs;
	public interface DialogContentListener{
		/**
		 * 自定义dialog的方法，该方法可以写自己的自定义操作
		 * 
		 * @param parent 自定义dialog的布局
		 * @param dialog 自定义dialog本身实例
		 * @param objs 外部传进dialog数据；外部传入的参数为null,objs也为null,引用objs时请谨慎
		 * */
		void contentExecute(View parent, Dialog dialog, Object[] objs);
	};
	public DialogContentListener mDialogContentListener;
	
	/**
	 * 设置dialog的内容监听
	 * @param mDialogContentListener 监听实例对象
	 * @param objects 将需要的参数传递进dialog内部
	 * */
	public void setDialogContentListener(DialogContentListener mDialogContentListener, Object... objects){
		this.mDialogContentListener = mDialogContentListener;
		objs = objects;
	}
	
	/**
	 * 设置dialog的内容监听
	 * @param mDialogContentListener 监听实例对象
	 * */
	public void setDialogContentListener(DialogContentListener mDialogContentListener){
		setDialogContentListener(mDialogContentListener,(Object[]) null);
	} 
	
	public CommonDialog(Context context) {
		super(context);
		// TODO 自动生成的构造函数存根
		this.context = context;
	}
	
	public CommonDialog(Context context,int layout){
		super(context);
		this.layout = layout;
		this.context = context;
	}
	
	public CommonDialog(Context context,int layout,int style){
		super(context,style);
		this.layout = layout;
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(context);
		parentView = inflater.inflate(layout, null);
		setContentView(parentView);
		dialog = this;
		ViewSparseArray = new SparseArray<View>();
		mDialogContentListener.contentExecute(parentView,dialog,objs);
	}
	
	/**
	 * 通过控件的Id获取对于的控件，如果没有则加入views
	 * 
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId)
	{
		View view = ViewSparseArray.get(viewId);
		if (view == null)
		{
			view = parentView.findViewById(viewId);
			ViewSparseArray.put(viewId, view);
		}
		return (T) view;
	}
	
}
