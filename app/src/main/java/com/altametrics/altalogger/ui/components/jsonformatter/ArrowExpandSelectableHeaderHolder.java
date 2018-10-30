package com.altametrics.altalogger.ui.components.jsonformatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.altametrics.altalogger.R;
import com.altametrics.altalogger.ui.components.LogFontViewField;


public class ArrowExpandSelectableHeaderHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private LogFontViewField arrowView;

    public ArrowExpandSelectableHeaderHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItemHolder.IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_selectable_header, null, false);

        tvValue = (TextView) view.findViewById(R.id.node_value);


        tvValue.setText(value.text);
        arrowView = (LogFontViewField) view.findViewById(R.id.arrow_icon);
        arrowView.setPadding(20, 10, 10, 10);
        if (node.isLeaf()) {
            arrowView.setVisibility(View.GONE);
        }
        arrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tView.toggleNode(node);
            }
        });
        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setText(context.getResources().getString(active ? R.string.icon_chevron_down : R.string.icon_chevron_right));
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {

    }

    @Override
    public int getContainerStyle() {
        return R.style.TreeNodeStyleCustom;
    }
}
