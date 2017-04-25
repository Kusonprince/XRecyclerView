package com.jcodecraeer.xrecyclerview;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;

import java.util.Date;

/**
 * Created by kuson on 17/4/25.
 */

public class UpdateTimePreference {

    private static final String FILE_NAME = "time_preferences";

    private static final String UPDATE_TIME = "update_time";

    private static SharedPreferences getPreferences (Context context) {
        return context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void saveUpdataTime(Context context, long data) {
        getPreferences(context).edit().putLong(UPDATE_TIME, data).commit();
    }

    public static long getUpdateTime(Context context) {
        long time = getPreferences(context).getLong(UPDATE_TIME, System.currentTimeMillis());
        return time;
    }

}
