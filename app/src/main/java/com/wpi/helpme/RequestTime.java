package com.wpi.helpme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import static com.wpi.helpme.RequestDescription.NOTES;
import static com.wpi.helpme.RequestDescription.PHOTO;
import static com.wpi.helpme.RequestDescription.TITLE;
import static com.wpi.helpme.RequestDescription.TOPIC;


public class RequestTime extends AppCompatActivity {
    Bundle bundle;

    public final static String TIME = "com.helpme.tommy.helprequest.TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_time);

        Intent intent = getIntent();
        bundle = intent.getExtras();
    }

    public void requestConfirm (View view) {
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.clearFocus();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        String clockTime = hour + ":" + minute;

        Intent myIntent = new Intent(this, RequestConfirmation.class);
        myIntent.putExtra(TOPIC, bundle.getString(TOPIC));
        myIntent.putExtra(NOTES, bundle.getString(NOTES));
        myIntent.putExtra(TITLE, bundle.getString(TITLE));
        myIntent.putExtra(TIME, clockTime);
        myIntent.putExtra(PHOTO, getIntent().getStringExtra(PHOTO));
        this.startActivity(myIntent);
    }
}
