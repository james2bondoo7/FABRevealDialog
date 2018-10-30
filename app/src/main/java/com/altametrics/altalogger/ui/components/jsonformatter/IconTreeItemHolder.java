package com.altametrics.altalogger.ui.components.jsonformatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.altametrics.altalogger.R;
import com.altametrics.altalogger.ui.components.LogFontViewField;

public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private LogFontViewField arrowView;


    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);
        arrowView = (LogFontViewField) view.findViewById(R.id.arrow_icon);
        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setText(context.getResources().getString(active ? R.string.icon_chevron_down : R.string.icon_chevron_right));
    }

    public static class IconTreeItem {
        public String text;

        public IconTreeItem(String text) {
            this.text = text;
        }

    }
}
