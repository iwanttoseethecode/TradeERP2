package gttrade.guantang.com.tradeerp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by luoling on 2016/10/8.
 */
public class MD5Util {
    public static String changeToMD5(String str){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5" );
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        digest.update(str.getBytes());
        byte[] b=digest.digest();
        return byte2hex(b);
    }

    public static String byte2hex(byte[] b){
        StringBuffer sb = new StringBuffer();
        int length = b.length;
        for(int i = 0;i<length;i++){
            String str = Integer.toHexString(0xff & b[i]);
            if(str.length()==1){
                sb.append("0").append(str);
            }else{
                sb.append(str);
            }
        }
        return sb.toString().toUpperCase();
    }

}
