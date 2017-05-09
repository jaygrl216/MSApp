package com.example.msapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VibrateActivity extends AppCompatActivity {
    private static final int VIBRATION_START = 0;
    private static final int VIBRATION_DURATION = 2000;

    private int silentSpots = 0;
    private int dot = 1;
    long[] pattern = {0,1,0,1};

    private boolean testComplete = false;
    private Vibrator vibrator;
    private Button touch;
    private TextView scoreText;
    private int timesPressed = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrate);

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        touch = (Button) findViewById(R.id.start);
        scoreText = (TextView) findViewById(R.id.score_text_view);
        scoreText.setVisibility(View.INVISIBLE);
    }

    public void startVibrate(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE},
                    1);
        } else {
            System.out.println("Permission Granted");
        }
        if(timesPressed == 0){
            timesPressed++;
        }else{
            testComplete = true;
        }
        touch.setText("Stop");

        runTest();
        while(testComplete == false){
            //Wait for vibration test to finish
        }
        int timeToComplete = silentSpots/10;
        String letterGrade = "";
        if(silentSpots < 40){
            letterGrade = "Very Poor";
        }else if(silentSpots <80){
            letterGrade = "Poor";
        }else if(silentSpots < 100){
            letterGrade = "Mediocre";
        }else if(silentSpots < 120){
            letterGrade = "Good";
        }else{
            letterGrade = "Great";
        }

        touch.setVisibility(View.INVISIBLE);
        scoreText.setText(scoreText.getText() + "\nTime until no vibration felt: " +timeToComplete +" seconds\n"+letterGrade);
        scoreText.setVisibility(View.VISIBLE);
    }

    private void runTest() {
        if(testComplete == false) {
            long[] pattern = {silentSpots, dot, silentSpots, dot};
            vibrator.vibrate(pattern, 0);
            new CountDownTimer(2000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    if (testComplete == false) {
                        silentSpots += 10;
                        vibrator.cancel();
                        runTest();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == 1) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Vibrate permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vibrate permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
