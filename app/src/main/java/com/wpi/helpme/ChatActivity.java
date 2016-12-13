package com.wpi.helpme;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;


public class ChatActivity extends AppCompatActivity {


    private String group;
    private String id_code;
    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        String url_string;
        String id = "";

        group = intent.getStringExtra("ChatName");
        
        //// TODO: fix REST request 

        try {
            url_string = "http://help.rthr.me/chatid?id=" + URLEncoder.encode(group, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 encoding is broken, cry now.");

        }

        try {

            URL url = new URL(url_string);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                id = id + output;
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        id_code = id;



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);

                FloatingActionButton fab =
                        (FloatingActionButton) findViewById(R.id.fab);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText input = (EditText) findViewById(R.id.input);

                        // Read the input field and push a new instance
                        // of ChatMessage to the Firebase database
                        FirebaseDatabase.getInstance()
                                .getReference("chats/" + id_code)
                                .push()
                                .setValue(new ChatMessage(input.getText().toString(),
                                        HelpMeApplication.getInstance().getUserProfile().getUserName())
                                );

                        // Clear the input
                        input.setText("");
                    }
                });

            }
        });
    }

    private void displayMessages(){
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.messages, FirebaseDatabase.getInstance().getReference("chats/" + id_code)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMsgText());
                messageUser.setText(model.getMsgUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMsgTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }
}
