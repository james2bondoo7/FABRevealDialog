package com.altametrics.altalogger.entity;

import android.text.TextUtils;

import com.altametrics.altalogger.util.LogUtil;

import java.util.Date;

public class Log {

    private String date;
    private String jsonLog;
    private String simpleLog;

    public Log() {
        this.date = new Date().toString();
    }

    public String getSimpleLog() {
        return simpleLog;
    }

    public void setSimpleLog(String simpleLog) {
        this.simpleLog = simpleLog;
    }

    public String getDate() {
        return date;
    }

    public String getJsonLog() {
        return jsonLog;
    }

    public void setJsonLog(String jsonLog) {
        this.jsonLog = jsonLog;
    }

    public boolean isArray() {
        return jsonLog != null && jsonLog.trim().startsWith("[");
    }

    public boolean contains(String aString) {
        String log = getJsonLog() != null ? jsonLog : (TextUtils.isEmpty(simpleLog) ? simpleLog : "");
        return LogUtil.preference().isCaseSensitive() ? log.contains(aString) : log.toLowerCase().contains(aString.toLowerCase());
    }
}
