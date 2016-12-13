package com.wpi.helpme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wpi.helpme.database.HelpRequest;

import java.io.FileInputStream;

public class RequestConfirmation extends AppCompatActivity {
    Bundle bundle;
    ImageView locationPhoto;

    private DatabaseReference logRef;
    private String topic;
    private String notes;
    private String time;
    private String title;

    private void logRequest(String topic, String notes) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_confirmation);

        Intent intent = getIntent();
        bundle = intent.getExtras();

        TextView topicText = (TextView) findViewById(R.id.topic);
        TextView notesText = (TextView) findViewById(R.id.notes);
        TextView timeText = (TextView) findViewById(R.id.time);

        topic = bundle.getString(RequestDescription.TOPIC);
        notes = bundle.getString(RequestDescription.NOTES);
        time = bundle.getString(RequestTime.TIME);
        title = bundle.getString(RequestDescription.TITLE);

        topicText.setText(topic);
        notesText.setText(notes);
        timeText.setText(time);

        locationPhoto = (ImageView) findViewById(R.id.locationPhoto);

        Bitmap bmp = null;
        String filename = getIntent().getStringExtra(RequestDescription.PHOTO);
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        locationPhoto.setImageBitmap(bmp);

        logRef = FirebaseDatabase.getInstance().getReference();

    }

    public void submitRequest(View view) {
        HelpMeApplication.getInstance().getDatabaseReference()
                .child("requests-inbox/")
                .push()
                .setValue(new HelpRequest(topic,
                        HelpMeApplication.getInstance().getUserProfile().getEmail(),
                        title, notes,
                        HelpMeApplication.getInstance().getCurrentLocation().getLatitude(),
                        HelpMeApplication.getInstance().getCurrentLocation().getLongitude(), time));

        Intent myIntent = new Intent(this, LocationActivity.class);
        startActivity(myIntent);
    }
}

