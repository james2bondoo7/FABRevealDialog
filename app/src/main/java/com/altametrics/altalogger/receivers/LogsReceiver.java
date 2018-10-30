package com.altametrics.altalogger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.altametrics.altalogger.adapters.LogListAdapter;
import com.altametrics.altalogger.entity.Log;
import com.altametrics.altalogger.util.LogConstants;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class LogsReceiver extends BroadcastReceiver {

    private static LogListAdapter logListAdapter;

    @Override
    public void onReceive(Context context, Intent intent) {
        String logData = intent.getStringExtra(LogConstants.LOG_DATA);
        if (TextUtils.isEmpty(logData)) {
            return;
        }
        this.addLogToAdapter(logData);
    }

    private void addLogToAdapter(String logData) {
        Log log = new Log();
        try {
            log.setJsonLog(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(logData)));
        } catch (Exception e) {
            log.setSimpleLog(logData);
        }
        logListAdapter().add(log);
    }

    public static LogListAdapter logListAdapter() {
        if (logListAdapter == null) {
            logListAdapter = new LogListAdapter(new ArrayList<Log>(), null);
        }
        return logListAdapter;
    }

    public static boolean areLogsEmpty() {
        return logListAdapter == null || logListAdapter.getCount() == 0;
    }
}