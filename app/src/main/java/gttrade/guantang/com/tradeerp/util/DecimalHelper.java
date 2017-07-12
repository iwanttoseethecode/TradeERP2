package gttrade.guantang.com.tradeerp.util;

import java.text.DecimalFormat;

/**
 * Created by luoling on 2016/10/10.
 * 对数字进行格式化处理
 */
public class DecimalHelper {

    /*数据库定义的数字类型是decimal(18, 8)，本方法是去掉数字头部和尾部多余的零*/
    public static String numberDecimalFormat(Double f){
        //#表示去除多余的零，0表示没有这么长的位数也要用零填充
        DecimalFormat df = new DecimalFormat("##########.########");
        return df.format(f);
    }

    public static String moneyDecimalFormat(Double f){
        //#表示去除多余的零，0表示没有这么长的位数也要用零填充
        DecimalFormat df = new DecimalFormat("##########.##");
        return df.format(f);
    }

    /*将小数转化为百分比格式*/
    public static String percentDecimalFormat(Double f){
        DecimalFormat df = new DecimalFormat("###.##%");
        return df.format(f);
    }
}
