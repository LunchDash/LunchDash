package com.lunchdash.lunchdash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lunchdash.lunchdash.APIs.ParseClient;
import com.lunchdash.lunchdash.LunchDashApplication;
import com.lunchdash.lunchdash.R;
import com.lunchdash.lunchdash.adapters.ChatListAdapter;
import com.lunchdash.lunchdash.datastore.ChatMessageTable;
import com.lunchdash.lunchdash.fragments.MatchedMapFragment;
import com.lunchdash.lunchdash.models.Restaurant;
import com.lunchdash.lunchdash.models.User;
import com.lunchdash.lunchdash.models.UserRestaurantMatches;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends ActionBarActivity {
    public static User matchedUser;
    TextView tvContactText;
    EditText etMessage;
    ImageButton btSend;
    public static UserRestaurantMatches match;
    public static Restaurant restaurant;
    List<ChatMessageTable> messagesPrevious = new ArrayList<>();

    public static int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    // Create a handler which can run code periodically
    private Handler handler = new Handler();

    private ListView lvChat;
    private ArrayList<ChatMessageTable> mMessages;
    private ChatListAdapter mAdapter;

    // Defines a runnable which is run every 100ms
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 100);
        }
    };

    private void refreshMessages() {
        receiveMessage();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Intent i = getIntent();
        String userId = i.getStringExtra("userId");
        String restaurantId = i.getStringExtra("restaurantId");
        match = (UserRestaurantMatches) i.getSerializableExtra("match");

        ImageView ivUserImage = (ImageView) findViewById(R.id.ivUserImage);
        tvContactText = (TextView) findViewById(R.id.tvContactText);
        matchedUser = ParseClient.getUser(userId);
        restaurant = LunchDashApplication.getRestaurantById(restaurantId);

        tvContactText.setText(matchedUser.getName() + " is ready for an awesome lunch at " + restaurant.getName() + "!\nGet in touch with them!");
        ivUserImage.setImageResource(android.R.color.transparent); //clear out the old image for a recycled view

        Picasso.with(this)
                .load(matchedUser.getImageUrl())
                .into((ivUserImage));
        setupMessagePosting();
        handler.postDelayed(runnable, 100);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.flContactMap, new MatchedMapFragment(), "MATCHED_MAP");
        ft.commit();
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (ImageButton) findViewById(R.id.btSend);

        lvChat = (ListView) findViewById(R.id.lvChat);
        mMessages = new ArrayList<>();
        mAdapter = new ChatListAdapter(this, LunchDashApplication.user.getUserId(), mMessages);
        lvChat.setAdapter(mAdapter);
        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                if (data.equals("")) {
                    return;
                }
                ParseClient.saveChatMessage(match.getId(), LunchDashApplication.user.getUserId(), data);
                etMessage.setText("");
            }
        });
    }

    // Query messages from Parse so we can load them into the chat adapter
    private void receiveMessage() {
        // Construct query to execute
        List<ChatMessageTable> messages = ParseClient.getChatMessages(match.getId());

        if (messages != null && messages.size() != messagesPrevious.size()) {
            messagesPrevious = messages;
            mMessages.clear();
            mMessages.addAll(messages);
            mAdapter.notifyDataSetChanged();
            lvChat.invalidate();
        } else {
            Log.d("message", "Error: Could not get messages ");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ParseClient.deleteUserSelections(LunchDashApplication.user.getUserId());
    }
}
