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

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void setupUsername() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("HangmanOnFire", 0);
        this.mUserId = sharedPreferences.getString("userId", null);

        if(this.mUserId == null) {
            Random random = new Random();
            this.mUserId = "user" + random.nextInt(10000);
            sharedPreferences.edit().putString("userId", this.mUserId).commit();
        }
    }

    private void sendMessage() {

    }
}
