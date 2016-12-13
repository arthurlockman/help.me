package com.wpi.helpme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        Date d = new Date();
        d.setHours(hour);
        d.setMinutes(minute); //Yes I know this is wrong but we needed it to work for demo
        d.setSeconds(0);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Intent myIntent = new Intent(this, RequestConfirmation.class);
        myIntent.putExtra(TOPIC, bundle.getString(TOPIC));
        myIntent.putExtra(NOTES, bundle.getString(NOTES));
        myIntent.putExtra(TITLE, bundle.getString(TITLE));
        myIntent.putExtra(TIME, dateFormat.format(d));
        myIntent.putExtra(PHOTO, getIntent().getStringExtra(PHOTO));
        this.startActivity(myIntent);
    }
}
