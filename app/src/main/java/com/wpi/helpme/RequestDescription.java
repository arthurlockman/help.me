package com.wpi.helpme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.FileOutputStream;

public class RequestDescription extends AppCompatActivity {
    public final static String TOPIC = "com.helpme.tommy.helprequest.TOPIC";
    public final static String NOTES = "com.helpme.tommy.helprequest.NOTES";
    public final static String PHOTO = "com.helpme.tommy.helprequest.PHOTO";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    EditText topicText;
    EditText notesText;
    ImageView requestImage;
    Bitmap photo;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_description);

        Intent intent = getIntent();

        topicText = (EditText) findViewById(R.id.requestTopic);
        notesText = (EditText) findViewById(R.id.requestNotes);
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
        myIntent.putExtra(PHOTO, filename);
        this.startActivity(myIntent);
    }

}

