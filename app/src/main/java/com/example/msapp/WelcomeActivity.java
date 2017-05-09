package com.example.msapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void outsideTest(View v) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    public void keyTest(View v) {
        Intent i = new Intent(this, KeyActivity.class);
        startActivity(i);
    }

    public void vibrateTest(View v) {
        Intent i = new Intent(this, VibrateActivity.class);
        startActivity(i);
    }
}
