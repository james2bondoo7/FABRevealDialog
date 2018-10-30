package com.altametrics.altalogger.adapters;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.altametrics.altalogger.LogApplication;
import com.altametrics.altalogger.R;
import com.altametrics.altalogger.entity.Log;
import com.altametrics.altalogger.ui.components.jsonformatter.AndroidTreeView;
import com.altametrics.altalogger.ui.components.jsonformatter.ArrowExpandSelectableHeaderHolder;
import com.altametrics.altalogger.ui.components.jsonformatter.IconTreeItemHolder;
import com.altametrics.altalogger.ui.components.jsonformatter.TreeNode;
import com.altametrics.altalogger.util.LogConstants;
import com.altametrics.altalogger.util.LogUIUtl;
import com.altametrics.altalogger.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class LogListAdapter extends ArrayAdapter<Log> {

    private ArrayList<Log> logArray;
    private String searchText;

    public LogListAdapter(ArrayList<Log> list, String searchText) {
        super(LogApplication.application().getActivity(), R.layout.log_row, list);
        this.logArray = list;
        this.searchText = searchText;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.log_row, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Log log = getItem(position);
        holder.jsonLogContainer.removeAllViews();
        holder.dateView.setText(log.getDate());
        if (!TextUtils.isEmpty(log.getSimpleLog())) {
            this.makeRowView(holder.simpleLogView, log.getSimpleLog());
            holder.simpleLogView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (v != null) {
                        LogUtil.shareTaskWith(log.getSimpleLog());
                    }
                    return true;
                }
            });
        } else {
            try {
                TreeNode root = TreeNode.root();
                if (log.getJsonLog().equalsIgnoreCase("null")) {
                    this.makeRowView(holder.simpleLogView, log.getJsonLog());
                    return convertView;
                } else {
                    holder.simpleLogView.setText("");
                    holder.simpleLogView.setVisibility(View.GONE);
                }
                Object jsonArrayOrObject;
                if (log.isArray()) {
                    jsonArrayOrObject = new JSONArray(log.getJsonLog());
                } else {
                    jsonArrayOrObject = new JSONObject(log.getJsonLog());
                }
                buildNode(root, jsonArrayOrObject, holder.simpleLogView);
                AndroidTreeView tView = new AndroidTreeView(getContext(), root);
                holder.jsonLogContainer.addView(tView.getView());
                holder.jsonLogContainer.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        LogUtil.shareTaskWith(log.getJsonLog());
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (searchText != null)
            LogUtil.setSpans(holder.simpleLogView, searchText, LogUtil.preference().isCaseSensitive());
        return convertView;
    }

    public void buildNode(TreeNode node, Object jsonArrayOrObject, TextView contentTxtView) {
        try {
            JSONObject jsonObject;
            if (jsonArrayOrObject instanceof JSONObject) {
                jsonObject = (JSONObject) jsonArrayOrObject;
            } else {
                jsonObject = new JSONObject();
                jsonObject.put("array", jsonArrayOrObject);
            }
            for (Iterator<String> itr = jsonObject.keys(); itr.hasNext(); ) {
                String key = itr.next();
                Object object = jsonObject.get(key);
                String value = resolveValue(object, key, jsonObject);

                TreeNode treeNode;

                if (object instanceof JSONObject) {
                    treeNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(key + " : {}")).setViewHolder(new ArrowExpandSelectableHeaderHolder(getContext()));
                    if (key.equalsIgnoreCase(LogConstants.QUERY_PARAM_HASH)) {
                        if (jsonObject.getJSONObject(key).getString(LogConstants.ACTION_ID) != null) {
                            makeHeaderView(contentTxtView, jsonObject.getJSONObject(key).getString(LogConstants.ACTION_ID));
                        }
                    }
                    treeNode.setSelectable(false);
                    node.addChildren(treeNode);
                    buildNode(treeNode, jsonObject.getJSONObject(key), contentTxtView);
                } else if (object instanceof JSONArray) {

                    treeNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(key + " : [" + jsonObject.getJSONArray(key).length() + "]")).setViewHolder(new ArrowExpandSelectableHeaderHolder(getContext()));
                    treeNode.setSelectable(false);
                    node.addChildren(treeNode);

                    for (int j = 0; j < jsonObject.getJSONArray(key).length(); ++j) {
                        if (jsonObject.getJSONArray(key).get(j) instanceof JSONObject) {
                            TreeNode treeNodeNew = new TreeNode(new IconTreeItemHolder.IconTreeItem("Object" + " " + j)).setViewHolder(new ArrowExpandSelectableHeaderHolder(getContext()));
                            treeNode.addChildren(treeNodeNew);
                            buildNode(treeNodeNew, jsonObject.getJSONArray(key).getJSONObject(j), contentTxtView);
                        } else if (jsonObject.getJSONArray(key).get(j) instanceof String) {
                            TreeNode treeNodeNew = new TreeNode(new IconTreeItemHolder.IconTreeItem(jsonObject.getJSONArray(key).get(j).toString())).setViewHolder(new ArrowExpandSelectableHeaderHolder(getContext()));
                            treeNode.addChildren(treeNodeNew);
                        }
                    }
                } else {
                    treeNode = new TreeNode(new IconTreeItemHolder.IconTreeItem(key + " : " + value)).setViewHolder(new ArrowExpandSelectableHeaderHolder(getContext()));
                }

                if (value != null) {
                    treeNode.setSelectable(false);
                    node.addChildren(treeNode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private String resolveValue(Object object, String key, JSONObject jsonObject) {
        try {
            if (object instanceof String) {
                return jsonObject.getString(key);
            } else if (object instanceof Long) {
                return String.valueOf(jsonObject.getLong(key));
            } else if (object instanceof Integer) {
                return String.valueOf(jsonObject.getInt(key));
            } else if (object instanceof Boolean) {
                return String.valueOf(jsonObject.getBoolean(key));
            } else if (object instanceof Double) {
                return String.valueOf(jsonObject.getDouble(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAllLogs() {
        StringBuilder logsBuilder = new StringBuilder();
        logsBuilder.append(" ");
        for (Log log : logArray) {
            if (!TextUtils.isEmpty(log.getJsonLog())) {
                logsBuilder.append(log.getJsonLog());
            } else if (!TextUtils.isEmpty(log.getSimpleLog())) {
                logsBuilder.append(log.getSimpleLog());
            }
        }
        return logsBuilder.toString();
    }

    private void makeHeaderView(TextView textView, String textToDisplay) {
        if (textView == null) {
            return;
        }
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(LogUIUtl.getColor(R.color.colorPrimaryLight));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LogApplication.application().getResources().getDimensionPixelSize(R.dimen.forteenDip));
        textView.setGravity(Gravity.CENTER);
        if (!TextUtils.isEmpty(textToDisplay)) {
            textView.setText(textToDisplay);
        }
        textView.setVisibility(View.VISIBLE);
    }

    private void makeRowView(TextView textView, String textToDisplay) {
        if (textView == null) {
            return;
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LogApplication.application().getResources().getDimensionPixelSize(R.dimen.twelveDip));
        textView.setGravity(Gravity.START);
        textView.setBackgroundColor(0);
        textView.setTextColor(Color.BLACK);
        if (!TextUtils.isEmpty(textToDisplay)) {
            textView.setText(textToDisplay);
        }
        textView.setVisibility(View.VISIBLE);
    }

    private class ViewHolder {
        TextView dateView;
        TextView simpleLogView;
        ViewGroup jsonLogContainer;

        public ViewHolder(View convertView) {
            this.dateView = (TextView) convertView.findViewById(R.id.dateView);
            this.simpleLogView = (TextView) convertView.findViewById(R.id.simpleLogView);
            this.jsonLogContainer = (ViewGroup) convertView.findViewById(R.id.jsonLogContainer);
        }
    }
}
