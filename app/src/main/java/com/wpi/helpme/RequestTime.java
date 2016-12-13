package com.wpi.helpme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static com.wpi.helpme.RequestDescription.NOTES;
import static com.wpi.helpme.RequestDescription.PHOTO;
import static com.wpi.helpme.RequestDescription.TOPIC;


public class RequestTime extends AppCompatActivity {
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_time);

        Intent intent = getIntent();
        bundle = intent.getExtras();
    }

    public void requestConfirm(View view) {
        Intent myIntent = new Intent(this, RequestConfirmation.class);
        myIntent.putExtra(TOPIC, bundle.getString(TOPIC));
        myIntent.putExtra(NOTES, bundle.getString(NOTES));
        myIntent.putExtra(PHOTO, getIntent().getStringExtra(PHOTO));
        this.startActivity(myIntent);
    }
}
