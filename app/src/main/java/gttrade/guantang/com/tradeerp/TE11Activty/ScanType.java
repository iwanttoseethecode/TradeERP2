package gttrade.guantang.com.tradeerp.TE11Activty;

/**
 * Created by luoling on 2017/2/6.
 */

public enum ScanType {

    DingDanHao(0), YunDanHao(1), DingAndYun(2);

    private int value;

    ScanType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ScanType getInstance(int value) {
        for (ScanType type : ScanType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return ScanType.DingDanHao;
    }
}
