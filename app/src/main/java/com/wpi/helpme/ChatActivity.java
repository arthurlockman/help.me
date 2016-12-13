package com.wpi.helpme;

import android.content.Intent;
import android.os.StrictMode;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class ChatActivity extends AppCompatActivity {


    private String group;
    private String id_code;
    private FirebaseListAdapter<ChatMessage> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Intent intent = getIntent();
        String id = "";

        group = intent.getStringExtra("ChatName");

        try {

            URLConnection connection = new URL("http://help.rthr.me/chatid?id=" + URLEncoder.encode(group, "UTF-8")).openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            InputStream response = connection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                id = id + output;
            }

            br.close();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        id_code = id;



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        displayMessages();

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
                                .getReference("chats/" + id_code + "/messages")
                                .push()
                                .setValue(new ChatMessage(input.getText().toString(),
                                        HelpMeApplication.getInstance().getUserProfile().getUserName())
                                );

                        // Clear the input
                        input.setText("");
                        displayMessages();
                    }
                });

            }
        });
    }

    private void displayMessages() {
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.messages, FirebaseDatabase.getInstance().getReference("chats/" + id_code + "/messages")) {
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
