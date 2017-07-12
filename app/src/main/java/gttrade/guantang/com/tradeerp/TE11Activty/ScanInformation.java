package gttrade.guantang.com.tradeerp.TE11Activty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoling on 2017/2/6.
 */

public class ScanInformation implements Serializable {
    public ScanInformation() {
        super();
    }

    private ScanType mScanType;

    private String DingdanHao ;

    private String YundanHao ;
    /**
     * 当3种扫描模式切换时，是否对界面进行初始化。 true 表示进行初始化，false 表示不进行初始化
     * */
    private boolean isInit = false;

    /*
    * 设置本次是否扫描完成，true表示扫描完成，false表示扫描未完成
    * */
    private boolean isFinish = false;

    /**
     * 是否获取到了服务端的校验结果
     * */
    private boolean isGetServiceData = false;

    public boolean isGetServiceData() {
        return isGetServiceData;
    }

    public void setGetServiceData(boolean getServiceData) {
        isGetServiceData = getServiceData;
    }

    /**
     * 服务端返回订单号的错误信息
     * */
    private String OrderMessage;

    /**
     * 服务端返回运单号的错误信息
     * */
    private String TrackMessage;


    /**
     * 服务端返回订单的货品信息
     * */
    private ArrayList<ItemMessage> ItemMessage;

    /**
     * 服务端返回订单的货品的数量信息
     * */
    private String ItemSumMessage;

    public String getOrderMessage() {
        return OrderMessage;
    }

    public void setOrderMessage(String orderMessage) {
        OrderMessage = orderMessage;
    }

    public String getTrackMessage() {
        return TrackMessage;
    }

    public void setTrackMessage(String trackMessage) {
        TrackMessage = trackMessage;
    }

    public ArrayList<ItemMessage> getItemMessage() {
        return ItemMessage;
    }

    public void setItemMessage(ArrayList<ItemMessage> itemMessage) {
        ItemMessage = itemMessage;
    }

    public String getItemSumMessage() {
        return ItemSumMessage;
    }

    public void setItemSumMessage(String itemSumMessage) {
        ItemSumMessage = itemSumMessage;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }



    public ScanType getmScanType() {
        return mScanType;
    }

    public void setmScanType(ScanType mScanType) {
        this.mScanType = mScanType;
    }

    public String getDingdanHao() {
        return DingdanHao;
    }

    public void setDingdanHao(String dingdanHao) {
        DingdanHao = dingdanHao;
    }

    public String getYundanHao() {
        return YundanHao;
    }

    public void setYundanHao(String yundanHao) {
        YundanHao = yundanHao;
    }


    public Object deepClone() throws IOException,OptionalDataException,ClassNotFoundException
    {
        //将对象写到流里
        ByteArrayOutputStream bo=new ByteArrayOutputStream();
        ObjectOutputStream oo=new ObjectOutputStream(bo);
        oo.writeObject(this);
        //从流里读出来
        ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi=new ObjectInputStream(bi);
        return(oi.readObject());
    }
}
