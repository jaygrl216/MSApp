package com.example.msapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class VibrateActivity extends AppCompatActivity {
    private static final int VIBRATION_START = 0;
    private static final int VIBRATION_DURATION = 5000;

    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibrate);

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void startVibrate(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE},
                    1);
        }

        int silentSpots = 0;
        int dot = 1;
        long[] pattern = {VIBRATION_START, VIBRATION_DURATION, dot, silentSpots};
        vibrator.vibrate(pattern, -1);
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
