package gttrade.guantang.com.tradeerp.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by luoling on 2016/12/29.
 */

public class DispatchWebView extends WebView {

    private boolean isIntercept = false;//是否拦截触摸事件

    public DispatchWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setIntercept(boolean intercept){
        isIntercept = intercept;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isIntercept){
            return true;
        }else{
            return super.onInterceptTouchEvent(ev);
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (isIntercept){
//            return true;
//        }else{
//            return super.onTouchEvent(event);
//        }
//    }
}
