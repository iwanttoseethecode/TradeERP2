package gttrade.guantang.com.tradeerp.TE02;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by luoling on 2016/10/11.
 * 自定义柱状图
 */
public class HistogramView extends View {

    private Paint paint;
    private int width=0,height=0;
    private Bitmap myBitmap;
    private Canvas myCanvas;

    private float progress =0f;
    private float percent = 0f;
    private int myBitmapWidth = 0;

    public HistogramView(Context context) {
        this(context,null);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#fdd9b2"));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
    }




//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int mWidth,mHeight;
//
//        //在没有约束条件下确定的内容大小
//        int contentWidth = 1000;
//        int contentHeight = 100;
//
//        mWidth = getMeasurement(widthMeasureSpec,contentWidth);
//        mHeight = getMeasurement(heightMeasureSpec,contentHeight);
//
//        //测量完毕必须将值赋给此方法
//        setMeasuredDimension(mWidth,mHeight);
//
//    }
//
//    private int getMeasurement(int measureSpec,int contentSize){
//        int specSize = MeasureSpec.getSize(measureSpec);
//        switch(MeasureSpec.getMode(measureSpec)){
//            case MeasureSpec.AT_MOST:
//                return Math.min(specSize,contentSize);
//            case MeasureSpec.UNSPECIFIED:
//                return contentSize;
//            case MeasureSpec.EXACTLY:
//                return specSize;
//            default:
//                return 0;
//        }
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        myBitmapWidth = (int) (width*percent);
        if(myBitmapWidth!=0 && height!=0){
            myBitmap = Bitmap.createBitmap(myBitmapWidth,height, Bitmap.Config.ARGB_8888);
            myCanvas = new Canvas(myBitmap);
        }
        super.onSizeChanged(width, height, oldw, oldh);
    }


    public void setPercent(float percent){
        this.percent = percent;
        progress = 0f;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(progress<=1f){
                    progress=progress+0.01f;
                    postInvalidate();
                }else {
                    return;
                }
            }
        },50,10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(myBitmap!=null){
            int nn= (int) (myBitmapWidth*progress);
            myCanvas.drawRect(0,0,nn,height,paint);
            canvas.drawBitmap(myBitmap,0,0,null);
        }
    }
}
