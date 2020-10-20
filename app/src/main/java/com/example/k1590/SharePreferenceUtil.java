package com.example.k1590;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    private static SharedPreferences sp = null;
    public static SharedPreferences getSp(Context context) {
        if (null == sp) {
            sp = context.getSharedPreferences("app_info", Context.MODE_PRIVATE);
        }
        return sp;
    }
}
