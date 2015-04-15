package io.github.gdgoakdale.hangmanonfire;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Random;


public class MainActivity extends ListActivity {

    private static final String BASE_FIREBASE_URL = "YOUR_ENDPOINT_HERE";

    private String mUserId;
    private Firebase mMessagesRef;
    private ChatListAdapter mChatListAdapter;
    private ValueEventListener mConnectedListener;
    private FireHangman mFireHangman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupUsername();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Chatting as " + this.mUserId);

        this.mMessagesRef = new Firebase(this.BASE_FIREBASE_URL).child("messages");

        this.mFireHangman = new FireHangman(this.BASE_FIREBASE_URL, this.mUserId);

        EditText messageText = (EditText)findViewById(R.id.messageInput);
        messageText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        Button sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        final ListView listView = getListView();
        mChatListAdapter = new ChatListAdapter(mMessagesRef.limitToLast(20), this, R.layout.message);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        mConnectedListener = mMessagesRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(MainActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mMessagesRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    private void setupUsername() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("HangmanOnFire", 0);
        this.mUserId = sharedPreferences.getString("userId", null);

        if(this.mUserId == null) {
            Random random = new Random();
            this.mUserId = "user" + random.nextInt(1000);
            sharedPreferences.edit().putString("userId", this.mUserId).commit();
        }
    }

    private void sendMessage() {
        EditText inputText = (EditText)findViewById(R.id.messageInput);
        String message = inputText.getText().toString();

        if(message.equals("/start")){
            this.mFireHangman.StartGame();
        }
        else if(message.startsWith("/guess")){
            String letter = message.replace("/guess", "").trim();
            this.mFireHangman.Guess(letter);
        }
        else if(!message.equals("")) {
            Chat chat = new Chat(this.mUserId, message, this.mUserId);
            this.mMessagesRef.push().setValue(chat);
        }

        inputText.setText("");
    }
}
