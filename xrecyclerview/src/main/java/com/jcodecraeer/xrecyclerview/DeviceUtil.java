package com.jcodecraeer.xrecyclerview;

import android.content.Context;

/**
 * Created by kuson on 17/4/13.
 */

public class DeviceUtil {

    /**
     * 根据手机的分辨率从 dip 的单位 转成为px
     *
     * @param appContext
     * @param dpValue
     * @return
     */
    public static int dipToPx(Context appContext, float dpValue) {
        return (int) (dpValue * appContext.getResources().getDisplayMetrics().density + 0.5f);
    }
}
