package com.xiangxue.myapplication;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Date:2019-12-03
 * author:lwb
 * Desc:
 */
public class Util {

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

}
