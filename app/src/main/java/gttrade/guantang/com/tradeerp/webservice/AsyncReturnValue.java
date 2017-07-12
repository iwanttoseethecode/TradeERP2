package gttrade.guantang.com.tradeerp.webservice;

/**
 * Created by luoling on 2017/3/1.
 *
 *
 */

public interface AsyncReturnValue {
    void getNetValue(String JsonString);
    void faild(String error);
}
