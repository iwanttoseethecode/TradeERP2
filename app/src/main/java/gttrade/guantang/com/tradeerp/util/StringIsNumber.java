package gttrade.guantang.com.tradeerp.util;

/**
 * Created by luoling on 2016/10/8.
 * 通过正则表达式判断处理字符串是不是数字的类
 */
public class StringIsNumber {

    /*
    * 判断字符串是不是浮点数，如果是就返回true，如果不是就返回false
    * */
    public static boolean stringIsNumBer(String string){
        if(string != null){
            if(string.matches("^(-?\\d+)(\\.\\d+)?$")){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    /*获取数字型字符串，如果是数字就按照规定格式返回，如果不是则返回""*/
    public static String getMoneyString(String NumberStr) {
        if (StringIsNumber.stringIsNumBer(NumberStr)) {
            return DecimalHelper.moneyDecimalFormat(Double.parseDouble(NumberStr));
        } else {
            return "";
        }
    }

    public static String getNumberString(String NumberStr) {
        if (StringIsNumber.stringIsNumBer(NumberStr)) {
            return DecimalHelper.moneyDecimalFormat(Double.parseDouble(NumberStr));
        } else {
            return "0";
        }
    }

}
