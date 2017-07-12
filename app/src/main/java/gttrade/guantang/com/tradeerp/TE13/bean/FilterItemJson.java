package gttrade.guantang.com.tradeerp.TE13.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luoling on 2017/3/10.
 */

public class FilterItemJson implements Parcelable{
    public Integer CatalogueID;
    public Integer StorageID;
    public String StoragePosition;
    public Double MinPrice;
    public Double MaxPrice;
    public String Sort;

    public FilterItemJson() {}

    protected FilterItemJson(Parcel in) {
        CatalogueID = in.readInt();
        StorageID = in.readInt();
        StoragePosition = in.readString();
        MinPrice = in.readDouble();
        MaxPrice = in.readDouble();
        Sort = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(CatalogueID);
        dest.writeInt(StorageID);
        dest.writeString(StoragePosition);
        dest.writeDouble(MinPrice.doubleValue());
        dest.writeDouble(MaxPrice.doubleValue());
        dest.writeString(Sort);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FilterItemJson> CREATOR = new Creator<FilterItemJson>() {
        @Override
        public FilterItemJson createFromParcel(Parcel in) {
            return new FilterItemJson(in);
        }

        @Override
        public FilterItemJson[] newArray(int size) {
            return new FilterItemJson[size];
        }
    };

    public Integer getCatalogueID() {
        return CatalogueID;
    }

    public void setCatalogueID(Integer catalogueID) {
        CatalogueID = catalogueID;
    }

    public Integer getStorageID() {
        return StorageID;
    }

    public void setStorageID(Integer storageID) {
        StorageID = storageID;
    }

    public String getStoragePosition() {
        return StoragePosition;
    }

    public void setStoragePosition(String storagePosition) {
        StoragePosition = storagePosition;
    }

    public Double getMinPrice() {
        return MinPrice;
    }

    public void setMinPrice(Double minPrice) {
        MinPrice = minPrice;
    }

    public Double getMaxPrice() {
        return MaxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        MaxPrice = maxPrice;
    }

    public String getSort() {
        return Sort;
    }

    public void setSort(String sort) {
        Sort = sort;
    }
}
