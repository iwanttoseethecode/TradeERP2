package gttrade.guantang.com.tradeerp.util;

import android.app.Activity;

import gttrade.guantang.com.tradeerp.R;

/**
 * Created by luoling on 2016/10/8.
 * 每个activity的进出动画
 */
public class ActivityAnimUtil {
    public static void setRightIn(Activity activity) {
        activity.overridePendingTransition(R.anim.push_right_in,
                R.anim.push_unchange);
    }

    public static void setRightOut(Activity activity) {
        activity.overridePendingTransition(R.anim.push_unchange,
                R.anim.push_right_out);
    }

    public static void setCenterIn_out(Activity activity){
        activity.overridePendingTransition(R.anim.center_in, R.anim.center_out);
    }
}
