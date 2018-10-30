package com.altametrics.altalogger.entity;

public class LogPreference {

    private String mailTo;
    private boolean isCaseSensitive;

    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    public void setCaseSensitive(boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

}
