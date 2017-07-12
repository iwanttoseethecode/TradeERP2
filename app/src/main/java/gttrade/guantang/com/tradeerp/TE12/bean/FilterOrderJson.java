package gttrade.guantang.com.tradeerp.TE12.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by luoling on 2017/3/10.
 */

public class FilterOrderJson implements Parcelable{
    public List<String> Platform;
    public int eShopID;
    public int StorageID;
    public String Country;
    public String Shipment;
    public String Attribute;
    public String PaidStartTime;
    public String PaidEndTime;
    public String ShippedStartTime;
    public String ShippedEndTime;
    public String BuyerID;
    public String ItemSKU;

    public FilterOrderJson(){}

    protected FilterOrderJson(Parcel in) {
        Platform = in.createStringArrayList();
        eShopID = in.readInt();
        StorageID = in.readInt();
        Country = in.readString();
        Shipment = in.readString();
        Attribute = in.readString();
        PaidStartTime = in.readString();
        PaidEndTime = in.readString();
        ShippedStartTime = in.readString();
        ShippedEndTime = in.readString();
        BuyerID = in.readString();
        ItemSKU = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(Platform);
        dest.writeInt(eShopID);
        dest.writeInt(StorageID);
        dest.writeString(Country);
        dest.writeString(Shipment);
        dest.writeString(Attribute);
        dest.writeString(PaidStartTime);
        dest.writeString(PaidEndTime);
        dest.writeString(ShippedStartTime);
        dest.writeString(ShippedEndTime);
        dest.writeString(BuyerID);
        dest.writeString(ItemSKU);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FilterOrderJson> CREATOR = new Creator<FilterOrderJson>() {
        @Override
        public FilterOrderJson createFromParcel(Parcel in) {
            return new FilterOrderJson(in);
        }

        @Override
        public FilterOrderJson[] newArray(int size) {
            return new FilterOrderJson[size];
        }
    };

    public List<String> getPlatform() {
        return Platform;
    }

    public void setPlatform(List<String> platform) {
        Platform = platform;
    }

    public int geteShopID() {
        return eShopID;
    }

    public void seteShopID(int eShopID) {
        this.eShopID = eShopID;
    }

    public int getStorageID() {
        return StorageID;
    }

    public void setStorageID(int storageID) {
        StorageID = storageID;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getShipment() {
        return Shipment;
    }

    public void setShipment(String shipment) {
        Shipment = shipment;
    }

    public String getAttribute() {
        return Attribute;
    }

    public void setAttribute(String attribute) {
        Attribute = attribute;
    }

    public String getPaidStartTime() {
        return PaidStartTime;
    }

    public void setPaidStartTime(String paidStartTime) {
        PaidStartTime = paidStartTime;
    }

    public String getPaidEndTime() {
        return PaidEndTime;
    }

    public void setPaidEndTime(String paidEndTime) {
        PaidEndTime = paidEndTime;
    }

    public String getShippedStartTime() {
        return ShippedStartTime;
    }

    public void setShippedStartTime(String shippedStartTime) {
        ShippedStartTime = shippedStartTime;
    }

    public String getShippedEndTime() {
        return ShippedEndTime;
    }

    public void setShippedEndTime(String shippedEndTime) {
        ShippedEndTime = shippedEndTime;
    }

    public String getBuyerID() {
        return BuyerID;
    }

    public void setBuyerID(String buyerID) {
        BuyerID = buyerID;
    }

    public String getItemSKU() {
        return ItemSKU;
    }

    public void setItemSKU(String itemSKU) {
        ItemSKU = itemSKU;
    }
}
