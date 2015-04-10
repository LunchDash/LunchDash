package com.lunchdash.lunchdash.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    User user;

    public static int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    // Create a handler which can run code periodically
    private Handler handler = new Handler();

    private ListView lvChat;
    private ArrayList<ChatMessageTable> mMessages;
    private ChatListAdapter mAdapter;

    // Defines a runnable which is run every 500ms
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 1000);
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
        user = LunchDashApplication.user;
        if (user == null) {
            user = LunchDashApplication.getUserFromSharedPref(this);
        }

        ImageView ivUserImage = (ImageView) findViewById(R.id.ivUserImage);
        tvContactText = (TextView) findViewById(R.id.tvContactText);
        matchedUser = ParseClient.getUser(userId);
        restaurant = LunchDashApplication.getRestaurantById(this, restaurantId);

        tvContactText.setText(Html.fromHtml(matchedUser.getName() + " is ready for an awesome lunch at<br> <b>" + restaurant.getName() + "!</b><br>Get in touch!"));
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
        startActivity(new Intent(ContactActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (ImageButton) findViewById(R.id.btSend);

        lvChat = (ListView) findViewById(R.id.lvChat);
        TextView tvEmptyList = (TextView) findViewById(R.id.tvEmptyList);
        lvChat.setEmptyView(tvEmptyList);
        mMessages = new ArrayList<>();
        mAdapter = new ChatListAdapter(this, user.getUserId(), mMessages);
        lvChat.setAdapter(mAdapter);

        btSend.setOnClickListener(new View.OnClickListener() { //Submit when the send button is clicked
            @Override
            public void onClick(View v) {
                postMessage();
            }
        });

        etMessage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) { //Submits if you press the enter key on the soft keyboard.
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    postMessage();
                    return true;
                }
                return false;
            }
        });


    }

    private void postMessage() { //create message object on Parse
        String data = etMessage.getText().toString();
        if (data.equals("")) {
            return;
        }
        ParseClient.saveChatMessage(match.getId(), user.getUserId(), data);
        etMessage.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //Hide the soft keyboard after you've sent the message.
        imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);


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
            //Log.d("message", "Error: Could not get messages ");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ParseClient.setUserStatus("None");
        ParseClient.deleteUserSelections(user.getUserId());
    }
}
