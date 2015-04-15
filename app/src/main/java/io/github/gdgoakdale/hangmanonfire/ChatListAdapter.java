package io.github.gdgoakdale.hangmanonfire;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatListAdapter extends BaseAdapter {
    private Query mRef;
    private int mLayout;
    private LayoutInflater mInflater;
    private ChildEventListener mListener;
    private List<Chat> mChats;
    private Map<String, Chat> mChatKeys;

    public ChatListAdapter(Query ref, Activity activity, int layout) {
        this.mRef = ref;
        this.mLayout = layout;
        this.mInflater = activity.getLayoutInflater();
        this.mChats = new ArrayList<Chat>();
        this.mChatKeys = new HashMap<String, Chat>();

        this.mListener = this.mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                mChatKeys.put(dataSnapshot.getKey(), chat);

                if(previousChildName == null) {
                    mChats.add(0, chat);
                }
                else {
                    Chat previousChat = mChatKeys.get(previousChildName);
                    int previousIndex = mChats.indexOf(previousChat);
                    int nextIndex = previousIndex + 1;
                    if(nextIndex == mChats.size()) {
                        mChats.add(chat);
                    }
                    else {
                        mChats.add(nextIndex, chat);
                    }
                }

                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // No-op
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // No-op
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // No-op
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    private void populateView(View v, Chat model) {
        String name = model.getName();
        TextView nameText = (TextView)v.findViewById(R.id.name);
        nameText.setText(name + ": ");

        String message = model.getMessage();
        TextView messageText = (TextView)v.findViewById(R.id.message);
        messageText.setText(message);
    }

    @Override
    public int getCount() {
        return this.mChats.size();
        }

    @Override
    public Object getItem(int i) {
        return this.mChats.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(this.mLayout, parent, false);
        }

        Chat chat = this.mChats.get(position);
        populateView(convertView, chat);

        return convertView;
    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        this.mRef.removeEventListener(this.mListener);
        this.mChats.clear();
        this.mChatKeys.clear();
    }
}
