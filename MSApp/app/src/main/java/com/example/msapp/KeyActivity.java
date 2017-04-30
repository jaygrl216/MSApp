package com.example.msapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class KeyActivity extends AppCompatActivity {
    private TextView instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);

        instructions = (TextView) findViewById(R.id.instructions);
        instructions.setText("Please rotate your phone to start the test.");

    }
}
