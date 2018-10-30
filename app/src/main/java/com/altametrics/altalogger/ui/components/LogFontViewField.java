package com.altametrics.altalogger.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.altametrics.altalogger.LogApplication;

/**
 * Created by Parikshit on 06-09-2016
 */
public class LogFontViewField extends TextView {

    public LogFontViewField(Context context) {
        super(context);
        init();
    }

    public LogFontViewField(Context context, AttributeSet attribSet) {
        super(context, attribSet);
        init();
    }

    private void init() {
        if (this.isInEditMode()) {
            return;
        }
        this.setTypeface(LogApplication.application().fwTypeface());
    }
}
