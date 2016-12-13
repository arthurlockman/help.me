package com.wpi.helpme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class RequestDescription extends AppCompatActivity {
    public final static String TOPIC = "com.helpme.tommy.helprequest.TOPIC";
    public final static String NOTES = "com.helpme.tommy.helprequest.NOTES";
    public final static String PHOTO = "com.helpme.tommy.helprequest.PHOTO";
    public final static String TITLE = "com.helpme.tommy.helprequest.TITLE";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    AutoCompleteTextView topicText;
    EditText notesText;
    EditText titleText;
    ImageView requestImage;
    Bitmap photo;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_request_description);
        String json = "";
        try {

            URLConnection connection = new URL("http://help.rthr.me/topics").openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                json = json + output;
            }

            br.close();

        } catch (IOException e) {

            e.printStackTrace();

        }
        ArrayList<String> topicList = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                topicList.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("RequestDescription", Arrays.toString(topicList.toArray()));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, topicList);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.requestTopic);
        textView.setAdapter(adapter);

        Intent intent = getIntent();

        topicText = (AutoCompleteTextView) findViewById(R.id.requestTopic);
        notesText = (EditText) findViewById(R.id.requestNotes);
        titleText = (EditText) findViewById(R.id.requestTitle);
        requestImage = (ImageView) findViewById(R.id.requestImage);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            requestImage.setImageBitmap(photo);
        }

        try {
            //Write file
            filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
            //photo.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void takePhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void requestTime(View view) {
        Intent myIntent = new Intent(this, RequestTime.class);
        myIntent.putExtra(TOPIC, topicText.getText().toString());
        myIntent.putExtra(NOTES, notesText.getText().toString());
        myIntent.putExtra(TITLE, titleText.getText().toString());
        myIntent.putExtra(PHOTO, filename);
        this.startActivity(myIntent);
    }

}

