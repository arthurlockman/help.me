package com.wpi.helpme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;

public class RequestConfirmation extends AppCompatActivity {
    Bundle bundle;
    ImageView locationPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_confirmation);

        Intent intent = getIntent();
        bundle = intent.getExtras();

        TextView topicText = (TextView) findViewById(R.id.topic);
        TextView notesText = (TextView) findViewById(R.id.notes);

        topicText.setText(bundle.getString(RequestDescription.TOPIC));
        notesText.setText(bundle.getString(RequestDescription.NOTES));

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

    }
}

