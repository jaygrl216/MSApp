package com.example.msapp;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class KeyActivity extends AppCompatActivity {
    private TextView instructions;
    private TextView numbers;
    private ImageView key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            instructions = (TextView) findViewById(R.id.instructions);
            instructions.setText("Please rotate your phone to start the test.");
        } else {
            instructions = (TextView) findViewById(R.id.instructions2);
            numbers = (TextView) findViewById(R.id.numbers);
            key = (ImageView) findViewById(R.id.imageView);

            key.setVisibility(View.INVISIBLE);
            numbers.setVisibility(View.INVISIBLE);
        }

    }
}
