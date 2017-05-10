package com.example.msapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.umd.cmsc436.sheets.Sheets;

public class VibrateActivity extends AppCompatActivity implements Sheets.Host{
    private static final int VIBRATION_START = 0;
    private static final int VIBRATION_DURATION = 2000;

    public static final int LIB_ACCOUNT_NAME_REQUEST_CODE = 1001;
    public static final int LIB_AUTHORIZATION_REQUEST_CODE = 1002;
    public static final int LIB_PERMISSION_REQUEST_CODE = 1003;
    public static final int LIB_PLAY_SERVICES_REQUEST_CODE = 1004;
    public static final int LIB_CONNECTION_REQUEST_CODE = 1005;

    private Sheets sheet;

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

        sheet = new Sheets(this, this, getString(R.string.app_name), getString(R.string.CMSC436_testing_spreadsheet), getString(R.string.CMSC436_private_test_spreadsheet));
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
            touch.setText("Stop");
        }else{
            testComplete = true;
            vibrator.cancel();
            int timeToComplete = silentSpots/10;
            int letterGrade = -1;
            if(silentSpots < 40){
                letterGrade = 1;
            }else if(silentSpots <80){
                letterGrade = 2;
            }else if(silentSpots < 100){
                letterGrade = 3;
            }else if(silentSpots < 120){
                letterGrade = 4;
            }else{
                letterGrade = 5;
            }

            touch.setVisibility(View.INVISIBLE);
            scoreText.setText(scoreText.getText() + "\nTime until no vibration felt: " +timeToComplete +" seconds\n"+ "You got a " + letterGrade + "\nNote, 1 indicates very poor, 2 indicates poor, 3 indicates fair, 4 indicates good, 5 indicates excellent.");
            sendToSheets((float) letterGrade);
            scoreText.setVisibility(View.VISIBLE);
        }



        if(!testComplete){
            runTest();
        }


    }

    private void runTest() {
        if(!testComplete) {
            long[] pattern = {silentSpots, dot, silentSpots, dot};
            vibrator.vibrate(pattern, 0);
            new CountDownTimer(2000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    if (!testComplete) {
                        silentSpots += 10;
                        vibrator.cancel();
                        runTest();
                    }
                }
            }.start();
        }
    }

    //i dont know the metrics for this method
    private void sendToSheets(float letterGrade){
        //the last field should be an answer of sorts
        //Sheets.TestType.OUTDOOR_WALKING is there because this test hasn't been
        //added to the source code
        sheet.writeData(Sheets.TestType.LH_LEVEL, getString(R.string.username), 100 * letterGrade);
        System.out.println("written");
    }


    public int getRequestCode(Sheets.Action action){
        //this is space holding code, should be looked at more in depth in the future
        switch (action){
            case REQUEST_PERMISSIONS:
                return LIB_PERMISSION_REQUEST_CODE;

            case REQUEST_ACCOUNT_NAME:
                return LIB_ACCOUNT_NAME_REQUEST_CODE;

            case REQUEST_PLAY_SERVICES:
                return LIB_PLAY_SERVICES_REQUEST_CODE;

            case REQUEST_AUTHORIZATION:
                return LIB_AUTHORIZATION_REQUEST_CODE;

            case REQUEST_CONNECTION_RESOLUTION:
                return LIB_CONNECTION_REQUEST_CODE;

            default:
                return -1;
        }

    }

    @Override
    public void notifyFinished(Exception e) {
        if (e != null) {
            throw new RuntimeException(e);
        }
        Log.i(getClass().getSimpleName(), "Done");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
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

        this.sheet.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        this.sheet.onActivityResult(requestCode, resultCode, data);
    }




}
