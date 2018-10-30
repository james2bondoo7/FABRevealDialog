package com.altametrics.altalogger.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.altametrics.altalogger.LogApplication;
import com.altametrics.altalogger.R;
import com.altametrics.altalogger.adapters.LogListAdapter;
import com.altametrics.altalogger.entity.Log;
import com.altametrics.altalogger.receivers.LogsReceiver;
import com.altametrics.altalogger.ui.components.LogFontViewField;
import com.altametrics.altalogger.ui.components.fab.FloatingActionButton;
import com.altametrics.altalogger.ui.components.fab.FloatingActionMenu;
import com.altametrics.altalogger.util.LogConstants;
import com.altametrics.altalogger.util.LogUtil;

import java.util.ArrayList;
import java.util.TimerTask;

public class LogMainActivity extends Activity implements OnClickListener {

    private LinearLayout searchBarLayout;
    private LogFontViewField searchButton, clearSearchBox;
    private EditText searchBox;
    private FloatingActionButton clearLogButton, settingsButton;
    private FloatingActionMenu fabMenuBtn;
    private OnClickListener oldFabMenuBtnClickListener;

    private ListView logListView;

    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log log = (Log) parent.getAdapter().getItem(position);
            if (!TextUtils.isEmpty(log.getJsonLog())) {
                LogUtil.shareTaskWith(log.getJsonLog());
            } else if (!TextUtils.isEmpty(log.getSimpleLog())) {
                LogUtil.shareTaskWith(log.getSimpleLog());
            }
            return true;
        }
    };

    private TextWatcher logSearchBarWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s) && logListView.getAdapter() != LogsReceiver.logListAdapter()) {
                logListView.setAdapter(LogsReceiver.logListAdapter());
                return;
            }
            String query = String.valueOf(s);
            ArrayList<Log> searchList = new ArrayList<>();
            for (int i = 0; i < LogsReceiver.logListAdapter().getCount(); i++) {
                Log log = LogsReceiver.logListAdapter().getItem(i);
                if (log.contains(query)) {
                    searchList.add(log);
                }
            }
            logListView.setAdapter(new LogListAdapter(searchList, query));
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        LogApplication.application().setActivity(this);

        this.loadView();
        this.setClickListeners();
        this.dataToView();
    }

    private void loadView() {
        this.searchBarLayout = (LinearLayout) findViewById(R.id.searchBarLayout);
        this.clearSearchBox = (LogFontViewField) findViewById(R.id.fwClearSearchBtn);
        this.searchBox = (EditText) findViewById(R.id.searchBox);
        this.searchButton = (LogFontViewField) findViewById(R.id.searchBtn);
        this.fabMenuBtn = (FloatingActionMenu) findViewById(R.id.fabMenuBtn);
        this.clearLogButton = (FloatingActionButton) findViewById(R.id.clearLogBtn);
        this.settingsButton = (FloatingActionButton) findViewById(R.id.settingsBtn);
        this.logListView = (ListView) this.findViewById(R.id.listView1);
        View noRecordDisplayLayout = findViewById(R.id.no_logs_display);

        this.logListView.setEmptyView(noRecordDisplayLayout);
        this.oldFabMenuBtnClickListener = this.fabMenuBtn.getOnMenuButtonClickListener();
    }

    private void setClickListeners() {
        this.logListView.setOnItemLongClickListener(this.itemLongClickListener);
        this.searchBox.addTextChangedListener(this.logSearchBarWatcher);
        this.searchButton.setOnClickListener(this);
        this.clearSearchBox.setOnClickListener(this);
        this.clearLogButton.setOnClickListener(this);
        this.settingsButton.setOnClickListener(this);
        this.fabMenuBtn.setOnMenuButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isOpened = fabMenuBtn.isOpened();
                oldFabMenuBtnClickListener.onClick(v);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fabMenuBtn.getMenuIconView().setImageResource(isOpened ? R.drawable.icon_menu : R.drawable.icon_plus);
                    }
                }, isOpened ? 300 : 0);
            }
        });
    }

    private void dataToView() {
        if (!LogsReceiver.areLogsEmpty()) {
            this.logListView.setAdapter(LogsReceiver.logListAdapter());
        }
        onLogsUpdated();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == searchButton.getId())
            this.showSearchBar();
        else if (view.getId() == clearSearchBox.getId())
            this.closeSearch();
        else if (view.getId() == clearLogButton.getId())
            this.clearLogs(view);
        else if (view.getId() == settingsButton.getId())
            this.openSettings();
    }

    private void showSearchBar() {
        this.searchBox.requestFocus();
        if (searchBarLayout.getVisibility() == View.GONE || searchBarLayout.getVisibility() == View.INVISIBLE) {
            searchBarLayout.setVisibility(View.VISIBLE);
        }
        this.fabMenuBtn.close(true);
    }

    private void closeSearch() {
        this.searchBox.getText().clear();
        this.searchBox.clearFocus();
        this.hideSearchBar();
        this.fabMenuBtn.close(true);
    }

    private void hideSearchBar() {
        if (searchBarLayout.getVisibility() == View.VISIBLE) {
            searchBarLayout.setVisibility(View.GONE);
            LogUtil.hideKeyboard(searchBox);
        }
    }

    public void clearLogs(View view) {
        if (!LogsReceiver.areLogsEmpty()) {
            LogsReceiver.logListAdapter().clear();
            onLogsUpdated();
        }
        this.fabMenuBtn.close(true);
    }

    private void openSettings() {
        this.startActivityForResult(new Intent(LogMainActivity.this, LogPreferenceActivity.class), LogConstants.PREFERENCES_REQUEST);
        this.fabMenuBtn.close(true);
    }

    private void onLogsUpdated() {
        this.searchButton.setVisibility(LogsReceiver.areLogsEmpty() ? View.GONE : View.VISIBLE);
        this.clearLogButton.setVisibility(LogsReceiver.areLogsEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LogConstants.PREFERENCES_REQUEST:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.dataToView();
    }
}
