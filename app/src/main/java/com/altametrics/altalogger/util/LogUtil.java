package com.altametrics.altalogger.util;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.altametrics.altalogger.LogApplication;
import com.altametrics.altalogger.entity.LogPreference;

public class LogUtil {

    public static LogPreference preference() {
        LogPreference preference = new LogPreference();
        preference.setCaseSensitive(LogApplication.application().getBoolean(LogConstants.CASE_SENSITIVE_KEY));
        preference.setMailTo(LogApplication.application().getString(LogConstants.MAIL_TO));
        return preference;
    }

    public static void shareTaskWith(final String data) {
        LogPreference preference = preference();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, preference.getMailTo());
        intent.putExtra(Intent.EXTRA_SUBJECT, "Logger Data");

        intent.putExtra(Intent.EXTRA_TEXT, data);
        intent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(intent, "How do you want to share?");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        LogApplication.application().getActivity().startActivity(shareIntent);
    }

    public static void setSpans(TextView textView, String searchText, boolean isCaseSensitive) {

        String txtViewContent = textView.getText().toString();
        if (!isCaseSensitive) {
            txtViewContent = txtViewContent.toLowerCase();
            searchText = searchText.toLowerCase();
        }
        int searchStartIndex = txtViewContent.indexOf(searchText, 0);

        Spannable WordtoSpan = new SpannableString(textView.getText());
        for (int charPos = 0; charPos < txtViewContent.length() && searchStartIndex != -1; charPos = searchStartIndex + 1) {
            searchStartIndex = txtViewContent.indexOf(searchText, charPos);
            if (searchStartIndex == -1) {
                break;
            } else {
                WordtoSpan.setSpan(new BackgroundColorSpan(0xFFFFFF00), searchStartIndex, searchStartIndex + searchText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
            }
        }
    }

    public static void hideKeyboard(EditText editText) {
        InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
