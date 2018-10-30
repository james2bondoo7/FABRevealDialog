package com.altametrics.altalogger.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.altametrics.altalogger.LogApplication;

/**
 * Created by Parikshit on 19-09-2016
 */
public class LogUIUtl {

    @SuppressWarnings("deprecation")
    public static int getColor(int id) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return LogApplication.application().getColor(id);
            } else {
                return LogApplication.application().getResources().getColor(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static Drawable getDrawable(int drawableResId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return LogApplication.application().getResources().getDrawable(drawableResId);
        } else {
            return LogApplication.application().getDrawable(drawableResId);
        }
    }
}
