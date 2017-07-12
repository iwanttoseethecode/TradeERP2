package gttrade.guantang.com.tradeerp.TE11Activty;

/**
 * Created by luoling on 2017/2/14.
 */

/*
* 订单数据表的状态字段Status 待上传为0，上传成功为1，上传失败为-1
* */

public enum SendDingdanType {
    NotUpDate(0),
    Success(1),
    Fail(-1);

    private int values = 0;

    SendDingdanType(int values) {
        this.values = values;
    }

    public int getValues() {
        return values;
    }

    public void setValues(int values) {
        this.values = values;
    }

    public SendDingdanType getInstance(int value){
        for (SendDingdanType type: SendDingdanType.values()) {
            if (type.getValues() == value){
                return type;
            }
        }
        return SendDingdanType.NotUpDate;
    }
}
