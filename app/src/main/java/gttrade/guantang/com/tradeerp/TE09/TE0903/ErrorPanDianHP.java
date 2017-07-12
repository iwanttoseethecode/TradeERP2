package gttrade.guantang.com.tradeerp.TE09.TE0903;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luoling on 2017/1/23.
 */

public class ErrorPanDianHP implements Parcelable {

    private String ItemID;
    private String Stock;

    public ErrorPanDianHP(String ItemID,String Stock){
        this.ItemID = ItemID;
        this.Stock = Stock;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public String getStock() {
        return Stock;
    }

    public void setStock(String stock) {
        Stock = stock;
    }

    protected ErrorPanDianHP(Parcel in) {
        ItemID = in.readString();
        Stock = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 将类的数据写入外部提供的Parcel中
        dest.writeString(ItemID);
        dest.writeString(Stock);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ErrorPanDianHP> CREATOR = new Creator<ErrorPanDianHP>() {
        @Override
        public ErrorPanDianHP createFromParcel(Parcel in) {
            // 从Parcel容器中读取传递数据值，封装成Parcelable对象返回

            return new ErrorPanDianHP(in);
        }

        @Override
        public ErrorPanDianHP[] newArray(int size) {
            return new ErrorPanDianHP[size];
        }
    };
}
