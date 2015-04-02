package com.lunchdash.lunchdash.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.models.NavItem;

import java.util.List;

public class NavListAdapter extends ArrayAdapter<NavItem> {

    public NavListAdapter(Context context, List<NavItem> navItems) {
        super(context, android.R.layout.simple_list_item_1, navItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.nav_drawer_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            holder.title = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        NavItem navItem = getItem(position);
        holder.title.setText(navItem.getTitle());
        holder.ivIcon.setImageResource(navItem.getImageResource());
        return convertView;
    }

    final class ViewHolder {
        public ImageView ivIcon;
        public TextView title;
    }
}
