package gttrade.guantang.com.tradeerp.TE11Activty;

import java.io.Serializable;

/**
 * Created by luoling on 2017/2/10.
 * 扫描订单之后，从服务端获取的货品信息，每个订单一般直列举两个
 */

public class ItemMessage implements Serializable {
    public String ItemSKU;

    public String ItemName;

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemSKU() {
        return ItemSKU;
    }

    public void setItemSKU(String itemSKU) {
        ItemSKU = itemSKU;
    }

}
