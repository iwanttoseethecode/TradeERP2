package gttrade.guantang.com.tradeerp.TE11Activty;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import gttrade.guantang.com.tradeerp.ZXing.View.ViewfinderView;

/**
 * Created by luoling on 2017/2/5.
 */

public class NewViewfinderView extends ViewfinderView {

    private ScanInformation mScanInformation;

    public NewViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private CallBackData mCallBackData;
    /*
    * 回调接口，获取网络订单号和运单号对应的详细数据
    * */
    public interface CallBackData{
        void gainData();
    }

    public void setCallBackData(CallBackData mCallBackData){
        this.mCallBackData = mCallBackData;
    }

    @Override
    protected void drawText(Canvas canvas, Rect frame) {


        //画扫描框上面的字
        paint.setColor(Color.WHITE);
        paint.setTextSize(TEXT_SIZE * density);

        if (mScanInformation ==null){
            initDingdanHaoDraw(canvas,frame);
        }else{
            if (mScanInformation.isInit()){
                initDraw(canvas, frame);
            }else{
                ScanTypeDraw(canvas, frame);
            }
        }

    }

    private void initDraw(Canvas canvas, Rect frame){
        switch(mScanInformation.getmScanType().getValue()){
            case 0:
                initDingdanHaoDraw(canvas, frame);
                break;
            case 1:
                initYunDanHaoDraw(canvas,frame);
                break;
            case 2:
                initDingAndYunHaoDraw(canvas,frame);
                break;
        }
    }

    private void initDingdanHaoDraw(Canvas canvas, Rect frame){
        canvas.drawText("请扫描订单号：",frame.left, (float) (frame.top - 16*density), paint);
        canvas.drawText("订单号：",frame.left,frame.bottom + 30*density,paint);
    }

    private void initYunDanHaoDraw(Canvas canvas, Rect frame){
        canvas.drawText("请扫描运单号：",frame.left, (float) (frame.top - 16*density), paint);
        canvas.drawText("运单号：",frame.left,frame.bottom + 30*density,paint);
    }

    private void initDingAndYunHaoDraw(Canvas canvas, Rect frame){
        Rect rect = new Rect();
        paint.getTextBounds("运单号",0,3,rect);
        canvas.drawText("请扫描订单号：",frame.left, (float) (frame.top - 16*density), paint);
        canvas.drawText("订单号：",frame.left,frame.bottom + 30*density,paint);
        canvas.drawText("运单号：",frame.left,frame.bottom + 30*density + rect.height() + 10*density,paint);
    }

    private void ScanTypeDraw(Canvas canvas, Rect frame){
        Rect rect = new Rect();
        String OrderMessage = mScanInformation.getOrderMessage();
        String TrackMessage = mScanInformation.getTrackMessage();
        //绘制货品信息的y轴的起始点
        float top = 0;
        switch (mScanInformation.getmScanType().getValue()){
            case 0:
                canvas.drawText("请扫描订单号：",frame.left, (float) (frame.top - 16*density), paint);

                String dText = "订单号："+mScanInformation.getDingdanHao();
                canvas.drawText(dText,frame.left,frame.bottom + 20*density,paint);

                paint.getTextBounds(dText,0,dText.length(),rect);

                top = frame.bottom + 20*density + rect.height() + 13*density;

                if (!mScanInformation.isGetServiceData()){
                    return;
                }
                if (OrderMessage==null || OrderMessage.equals("null")){
                    ItemDraw(canvas,frame,top,rect);
                }else {
                    ErrorDraw(canvas, frame,top,TrackMessage,OrderMessage);
                }

                break;
            case 1:
                canvas.drawText("请扫描运单号：",frame.left, (float) (frame.top - 16*density), paint);

                String yText = "运单号："+mScanInformation.getYundanHao();
                canvas.drawText(yText,frame.left,frame.bottom + 20*density,paint);

                paint.getTextBounds(yText,0,yText.length(),rect);

                top = frame.bottom + 20*density + rect.height() + 13*density;

                if (!mScanInformation.isGetServiceData()){
                    return;
                }

                if (TrackMessage==null || TrackMessage.equals("null")){
                    ItemDraw(canvas, frame,top,rect);
                }else{
                    ErrorDraw(canvas, frame,top,TrackMessage,OrderMessage);
                }

                break;
            case 2:
                if(TextUtils.isEmpty(mScanInformation.getDingdanHao()) || (!TextUtils.isEmpty(mScanInformation.getDingdanHao()) && !TextUtils.isEmpty(mScanInformation.getYundanHao()))){
                    canvas.drawText("请扫描订单号：",frame.left, (float) (frame.top - 16*density), paint);
                }

                String adText = "订单号："+mScanInformation.getDingdanHao();
                paint.getTextBounds(adText,0,adText.length(),rect);
                canvas.drawText(adText,frame.left,frame.bottom + 20*density,paint);

                if(!TextUtils.isEmpty(mScanInformation.getDingdanHao()) && TextUtils.isEmpty(mScanInformation.getYundanHao())){
                    canvas.drawText("请扫描运单号：",frame.left, (float) (frame.top - 16*density), paint);
                }

                String yundanhao = mScanInformation.getYundanHao();
                yundanhao = TextUtils.isEmpty(yundanhao) ? "" :yundanhao;
                canvas.drawText("运单号："+yundanhao,frame.left,frame.bottom + 20*density + rect.height() + 10*density,paint);

                top = frame.bottom + 20*density + rect.height()+ rect.height() + 10*density + 13*density;

                if (!mScanInformation.isGetServiceData()){
                    return;
                }

                if ((TrackMessage==null || TrackMessage.equals("null")) && (OrderMessage==null || OrderMessage.equals("null"))){
                    ItemDraw(canvas, frame,top,rect);
                }else{
                    ErrorDraw(canvas, frame,top,TrackMessage,OrderMessage);
                }

                break;

        }
    }

    /**
     * 画货品信息
     * */
    private void ItemDraw(Canvas canvas, Rect frame,float top,Rect rect){
        ArrayList<ItemMessage> ItemMessageList=mScanInformation.getItemMessage();


        Rect rect1 = new Rect();
        int maxWidth = 0;
        int length = ItemMessageList.size();
        String[] textArray = new String[length*2+1];
        int sub = 0;
        for (int i = 0;i<length;i++){
            String ItemSKU = ItemMessageList.get(i).getItemSKU();
            paint.getTextBounds(ItemSKU,0,ItemSKU.length(),rect1);
            textArray[sub++] = ItemSKU;
            maxWidth = Math.max(maxWidth,rect1.height());

            String ItemName = ItemMessageList.get(i).getItemName();
            paint.getTextBounds(ItemName,0,ItemName.length(),rect1);
            textArray[sub++] = ItemName;
            maxWidth = Math.max(maxWidth,rect1.height());
        }

        String ItemSunMessage=mScanInformation.getItemSumMessage();
        paint.getTextBounds(ItemSunMessage,0,ItemSunMessage.length(),rect1);
        textArray[sub] = ItemSunMessage;
        maxWidth = Math.max(maxWidth,rect1.height());

        int drawLeft = 0;
        if (maxWidth<=frame.width()){
            drawLeft = frame.left;
        }else{
            drawLeft = (getMeasuredWidth()-maxWidth)/2;
        }

        int textArrayLength = textArray.length;
        for (int i = 0; i<textArrayLength-1; i++){
            canvas.drawText(textArray[i],drawLeft,top,paint);
            top += rect.height() + 10*density;
        }
        paint.setColor(Color.RED);
        canvas.drawText(textArray[textArrayLength-1],drawLeft,top+5*density,paint);

    }

    /**
     * 画订单错误信息信息
     * */
    private void ErrorDraw(Canvas canvas, Rect frame,float top,String TrackMessage,String OrderMessage){
        StringBuilder sb = new StringBuilder();
        if ((TrackMessage==null || TrackMessage.equals("null")) && !(OrderMessage==null || OrderMessage.equals("null"))){
            sb.append(OrderMessage);

        }else if (!(TrackMessage==null || TrackMessage.equals("null")) && (OrderMessage==null || OrderMessage.equals("null"))){
            sb.append(TrackMessage);
        }else if (!(TrackMessage==null || TrackMessage.equals("null")) && !(OrderMessage==null || OrderMessage.equals("null"))){
            sb.append(OrderMessage+"\r\n");
            sb.append(TrackMessage);
        }
        String hpInfo = sb.toString();
        Rect rect = new Rect();
        paint.getTextBounds(hpInfo,0,hpInfo.length(),rect);
        int dViewWidth = getMeasuredWidth();
        int dx = (dViewWidth-rect.width())/2;
        paint.setColor(Color.RED);
        canvas.drawText(hpInfo,dx,top+10*density,paint);
    }


    public void setScanInformation(ScanInformation scanInformation){
        try {
            mScanInformation = (ScanInformation) scanInformation.deepClone();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (mScanInformation!=null && mScanInformation.isFinish()){
            mCallBackData.gainData();
        }
    }



}
