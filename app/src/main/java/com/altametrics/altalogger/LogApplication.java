package com.altametrics.altalogger;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import com.altametrics.altalogger.util.LogConstants;

/**
 * Created by Parikshit on 06-09-2016
 */
public class LogApplication extends Application {

    private static volatile LogApplication logApplication;
    private SharedPreferences sharedPref;
    private Typeface fwTypeface;
    private Activity activity;

    public static LogApplication application() {
        return logApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logApplication = this;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public SharedPreferences sharedPref() {
        if (this.sharedPref == null) {
            this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        }
        return this.sharedPref;
    }

    public String getString(String key) {
        return sharedPref().getString(key, "");
    }

    public boolean getBoolean(String key) {
        return sharedPref().getBoolean(key, false);
    }

    public Typeface fwTypeface() {
        if (this.fwTypeface == null) {
            this.fwTypeface = Typeface.createFromAsset(getAssets(), LogConstants.FW_FILENAME);
        }
        return this.fwTypeface;
    }
}
