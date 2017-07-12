package gttrade.guantang.com.tradeerp.catalogue;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import gttrade.guantang.com.tradeerp.R;


/**
 * Created by zhy on 15/9/10.
 */
public class MuLuFlowLayout extends OneLineFlowLayout implements MuluAdapter.OnDataChangedListener
{
    private MuluAdapter mMuluAdapter;
    private boolean mAutoSelectEffect = true;
    private static final String TAG = "TagFlowLayout";
    private MotionEvent mMotionEvent;
    
    public MuLuFlowLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        mAutoSelectEffect = ta.getBoolean(R.styleable.TagFlowLayout_auto_select_effect, true);
        ta.recycle();
        
        if (mAutoSelectEffect)
        {
            setClickable(true);
        }
    }
    
    public MuLuFlowLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }
    
    public MuLuFlowLayout(Context context)
    {
        this(context, null);
    }
    
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int cCount = getChildCount();
        
        for (int i = 0; i < cCount; i++)
        {
            TagView tagView = (TagView) getChildAt(i);
            if (tagView.getVisibility() == View.GONE) continue;
            if (tagView.getTagView().getVisibility() == View.GONE)
            {
                tagView.setVisibility(View.GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public void setAdapter(MuluAdapter adapter)
    {
        //if (mTagAdapter == adapter)
        //  return;
    	mMuluAdapter = adapter;
    	mMuluAdapter.setOnDataChangedListener(this);
        changeAdapter();
    }
    
    private void changeAdapter()
    {
        removeAllViews();
        MuluAdapter adapter = mMuluAdapter;
        TagView tagViewContainer = null;
        View myView = null;
        for (int i = 0; i < adapter.getCount(); i++)
        {
        	View tagView = adapter.getView(this,myView,i);
            tagViewContainer = new TagView(getContext());
            tagView.setDuplicateParentStateEnabled(true);
            tagViewContainer.setLayoutParams(tagView.getLayoutParams());
            tagViewContainer.addView(tagView);
            addView(tagViewContainer);
        }
        
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            mMotionEvent = MotionEvent.obtain(event);
        }
        return super.onTouchEvent(event);
    }
    
    @Override
    public boolean performClick()
    {
        if (mMotionEvent == null) return super.performClick();
        
        int x = (int) mMotionEvent.getX();
        int y = (int) mMotionEvent.getY();
        mMotionEvent = null;
        
        TagView child = findChild(x, y);
        int pos = findPosByView(child);
        return super.performClick();
    }

    private int findPosByView(View child)
    {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++)
        {
            View v = getChildAt(i);
            if (v == child) return i;
        }
        return -1;
    }

    private TagView findChild(int x, int y)
    {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++)
        {
            TagView v = (TagView) getChildAt(i);
            if (v.getVisibility() == View.GONE) continue;
            Rect outRect = new Rect();
            v.getHitRect(outRect);
            if (outRect.contains(x, y))
            {
                return v;
            }
        }
        return null;
    }

    @Override
    public void onChanged()
    {
        changeAdapter();
    }


}
