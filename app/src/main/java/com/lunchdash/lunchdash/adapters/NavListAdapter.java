package com.lunchdash.lunchdash.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
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
            holder.title = (TextView) convertView.findViewById(R.id.tvDrawerItemTitle);
            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        NavItem navItem = getItem(position);
        holder.title.setText(navItem.getTitle());
        holder.ivIcon.setImageResource(navItem.getImageResource());

        //Color the item depending on whether it's selected or not.
        if (navItem.isSelected()) {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.drawerBackgroundSelected));
            holder.title.setTextColor(getContext().getResources().getColor(R.color.drawerTextSelected));
            holder.ivIcon.setColorFilter(getContext().getResources().getColor(R.color.drawerTextSelected), PorterDuff.Mode.SRC_ATOP); //Set the selected color of the drawable.
        } else {
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.drawerBackground));
            holder.title.setTextColor(getContext().getResources().getColor(R.color.primaryText));
            holder.ivIcon.setColorFilter(getContext().getResources().getColor(R.color.primaryText), PorterDuff.Mode.SRC_ATOP); //Set the unselected color of the drawable.
        }
        return convertView;
    }

    final class ViewHolder {
        public ImageView ivIcon;
        public TextView title;
    }
}
