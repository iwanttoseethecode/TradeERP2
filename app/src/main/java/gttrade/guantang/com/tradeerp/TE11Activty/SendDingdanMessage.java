package gttrade.guantang.com.tradeerp.TE11Activty;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luoling on 2017/2/10.
 *
 * 扫描过后，校对正确的订单号运单号信息
 *
 */

public class SendDingdanMessage implements Parcelable{

    /**
     * 订单号
     */
    private String OrederNo;

    /**
     * 运单号
     */
    private String TrackingNo;

    /**
     * 物流方式
     */
    private String ShipingMethod;

    public  SendDingdanMessage(){

    }

    protected SendDingdanMessage(Parcel in) {
        OrederNo = in.readString();
        TrackingNo = in.readString();
        ShipingMethod = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(OrederNo);
        dest.writeString(TrackingNo);
        dest.writeString(ShipingMethod);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SendDingdanMessage> CREATOR = new Creator<SendDingdanMessage>() {
        @Override
        public SendDingdanMessage createFromParcel(Parcel in) {
            return new SendDingdanMessage(in);
        }

        @Override
        public SendDingdanMessage[] newArray(int size) {
            return new SendDingdanMessage[size];
        }
    };

    public String getOrederNo() {
        return OrederNo;
    }

    public void setOrederNo(String orederNo) {
        OrederNo = orederNo;
    }

    public String getTrackingNo() {
        return TrackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        TrackingNo = trackingNo;
    }

    public String getShipingMethod() {
        return ShipingMethod;
    }

    public void setShipingMethod(String shipingMethod) {
        ShipingMethod = shipingMethod;
    }
}
