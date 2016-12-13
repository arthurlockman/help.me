package com.wpi.helpme;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;

public class ChatListActivity extends AppCompatActivity {

    private FirebaseListAdapter<ChatObject> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        displayChats();
    }

    public void openAddChatActivity(View v) {
            Intent addChat = new Intent(this, AddChatActivity.class);
            startActivity(addChat);
    }


    private void displayChats(){
        ListView listOfChats = (ListView)findViewById(R.id.list_of_chats);

        adapter = new FirebaseListAdapter<ChatObject>(this, ChatObject.class,
                R.layout.messages, FirebaseDatabase.getInstance().getReference("chats/" )) {
            @Override
            protected void populateView(View v, ChatObject model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);

                // Set their text
                messageText.setText(model.getChatTitle());
            }
        };

        // Listener for clicking on any of the filter items
        listOfChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * @see {@link android.widget.AdapterView.OnItemClickListener#onItemClick(AdapterView, View, int, long)}
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int itemIndex, long l) {
                ChatObject chat =  (ChatObject) adapterView.getItemAtPosition(itemIndex);
                String chatname = chat.getChatTitle();

                Intent openChat = new Intent(ChatListActivity.this, ChatActivity.class);
                openChat.putExtra("ChatName", chatname);
                startActivity(openChat);
            }
        });

        listOfChats.setAdapter(adapter);
    }
}
