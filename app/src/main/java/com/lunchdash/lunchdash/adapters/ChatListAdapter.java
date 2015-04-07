package com.lunchdash.lunchdash.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.datastore.ChatMessageTable;
import com.lunchdash.lunchdash.models.User;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends ArrayAdapter<ChatMessageTable> {
    private String mUserId;

    private static HashMap<String, User> usersCache;

    public ChatListAdapter(Context context, String userId, List<ChatMessageTable> messages) {
        super(context, 0, messages);
        this.mUserId = userId;
        usersCache = new HashMap<String, User>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.chat_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.llChatMessage = (LinearLayout) convertView.findViewById(R.id.llChatMessage);
            holder.imageLeft = (ImageView) convertView.findViewById(R.id.ivProfileLeft);
            holder.imageRight = (ImageView) convertView.findViewById(R.id.ivProfileRight);
            holder.body = (TextView) convertView.findViewById(R.id.tvBody);
            convertView.setTag(holder);
        }
        final ChatMessageTable message = (ChatMessageTable) getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();
        final boolean isMe = message.getUserId().equals(mUserId);
        // Show-hide image based on the logged-in user.
        // Display the profile image to the right for our user, left for other users.
        if (isMe) {
            holder.llChatMessage.setGravity(Gravity.RIGHT);
            holder.imageRight.setVisibility(View.VISIBLE);
            holder.imageLeft.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            holder.body.setBackgroundResource(R.drawable.bubble_green);
        } else {
            holder.llChatMessage.setGravity(Gravity.LEFT);
            holder.imageLeft.setVisibility(View.VISIBLE);
            holder.imageRight.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            holder.body.setBackgroundResource(R.drawable.bubble_yellow);
        }
        final ImageView profileView = isMe ? holder.imageRight : holder.imageLeft;

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        Picasso.with(getContext())
                .load(getProfileUrl(message.getUserId()))
                .transform(transformation)
                .into(profileView);
        holder.body.setText(message.getMessageBody());
        return convertView;
    }

    // Create a gravatar image based on the hash value obtained from userId
    private static String getProfileUrl(final String userId) {
        User u = usersCache.get(userId);
        if (u != null) {
            return u.getImageUrl();
        } else {
            User user = ParseClient.getUser(userId);
            usersCache.put(user.getUserId(), user);
            return user.getImageUrl();
        }

    }

    final class ViewHolder {
        public LinearLayout llChatMessage;
        public ImageView imageLeft;
        public ImageView imageRight;
        public TextView body;
    }

}