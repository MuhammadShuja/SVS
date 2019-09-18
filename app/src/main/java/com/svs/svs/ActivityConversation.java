package com.svs.svs;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityConversation extends AppCompatActivity {

    private TextView txtFrom, txtMessage, txtTime;
    private Button btnBack;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_conversation);
        String from = getIntent().getExtras().getString("from");
        String message = getIntent().getExtras().getString("message");
        String time = getIntent().getExtras().getString("time");
        boolean isImg = getIntent().getExtras().getBoolean("image");



        txtFrom = (TextView) findViewById(R.id.txtFrom);
        txtFrom.setText("From: "+from);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtTime.setText(time);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtMessage.setMovementMethod(new ScrollingMovementMethod());


        image = (ImageView) findViewById(R.id.image);
        if(isImg){
            message = message.substring(4, message.length());
            Bitmap b = new Base64Converter().decode(message);
            image.setImageBitmap(b);
            txtMessage.setText("Image sent via: Smart Photo Sharing");
        }
        else{
            image.setVisibility(View.GONE);
            txtMessage.setText(message);
        }

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
        startActivity(new Intent(ActivityConversation.this, MainActivity.class));
            }
        });

    }
}
