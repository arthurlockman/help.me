package com.wpi.helpme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class RequestMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_main);
    }

    public void requestHelp (View view) {
        Intent myIntent = new Intent(this, RequestDescription.class);
        this.startActivity(myIntent);
    }
}
