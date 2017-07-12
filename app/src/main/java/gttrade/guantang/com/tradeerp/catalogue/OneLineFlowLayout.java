package gttrade.guantang.com.tradeerp.catalogue;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import gttrade.guantang.com.tradeerp.R;


public class OneLineFlowLayout extends ViewGroup {
	protected List<View> mAllViews = new ArrayList<View>();
	protected int mLineHeight=0;
	private String mGravity;
	
	public OneLineFlowLayout(Context context){
		this(context,null);
	}
	
	public OneLineFlowLayout(Context context,AttributeSet attrs){
		this(context,attrs,0);
	}
	
	public OneLineFlowLayout(Context context,AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
		mGravity = ta.getString(R.styleable.TagFlowLayout_gravity);
		if(mGravity==null){
			mGravity = getResources().getString(R.string.gravity_left);
		}
		ta.recycle();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        
        int cCount = getChildCount();
        
        int lineWidth = 0;
        int lineHeight = 0;
        
        for (int i = 0; i < cCount; i++){
        	View child = getChildAt(i);
        	if(child.getLayoutParams() instanceof MarginLayoutParams){
        		measureChild(child, widthMeasureSpec, heightMeasureSpec);
        		MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        		int childWidth = child.getMeasuredWidth() + lp.leftMargin
        				+ lp.rightMargin;
        		int childHeight = child.getMeasuredHeight() + lp.topMargin
        				+ lp.bottomMargin;
        		lineWidth += childWidth;
        		lineHeight = Math.max(lineHeight, childHeight);
        	}
        }
        
        
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth: lineWidth, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight: lineHeight);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		int cCount = getChildCount();
		int left = 0;
		
		for (int i = 0; i < cCount; i++){
			View child = getChildAt(i);
			if (child.getVisibility() == View.GONE)
			{
				continue;
			}
			
			if(child.getLayoutParams() instanceof MarginLayoutParams){
				MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
				mAllViews.add(child);
				
				//计算childView的left,top,right,bottom
				int lc = left + lp.leftMargin;
				int tc = lp.topMargin;
				int rc =lc + child.getMeasuredWidth();
				int bc = tc + child.getMeasuredHeight();
				child.layout(lc, tc, rc, bc);
				left += child.getMeasuredWidth() + lp.rightMargin+ lp.leftMargin;
				child.requestLayout();
			}
			
		}
		
	}
	
	@Override
	protected LayoutParams generateLayoutParams(
			LayoutParams p)
	{
		return new MarginLayoutParams(p);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs)
	{
		return new MarginLayoutParams(getContext(), attrs);
	}



}
