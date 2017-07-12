package gttrade.guantang.com.tradeerp.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 设置viewpager 不可滑动
 * */
public class ControlScrollViewPager extends ViewPager {
	
	private boolean scrollable = true;  
	
	public ControlScrollViewPager(Context context) {
		super(context);
	}
	public ControlScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollable(boolean enable) {  
        scrollable = enable;  
    }  
  
	
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent event) {  
        if (scrollable) {  
            return super.onInterceptTouchEvent(event);  
        } else {  
            return false;  
        }  
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        /* return false;//super.onTouchEvent(arg0); */
        if (scrollable){
        	return super.onTouchEvent(arg0);
        }else{
        	return false;
        }
    }
    
      
    
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }
 
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
    
}
