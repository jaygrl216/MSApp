package com.example.msapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class KeyActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView instructions;
    private TextView numbers;
    private ImageView key;
    private ImageView symbol;
    private ImageButton mic;
    private SpeechRecognizer sr;
    private Button speech;
    private Button normal;

   private class Listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            System.out.println("Ready");
        }
        public void onBeginningOfSpeech()
        {
            System.out.println("Beginning");
        }
        public void onRmsChanged(float rmsdB)
        {
            System.out.println("Changed");
        }
        public void onBufferReceived(byte[] buffer)
        {
            System.out.println("Buffer Received");
        }
        public void onEndOfSpeech()
        {
            System.out.println("Done");
        }
        public void onError(int error)
        {
        }
        public void onResults(Bundle results)
        {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            System.out.println(data.get(0));

        }
        public void onPartialResults(Bundle partialResults)
        {
        }
        public void onEvent(int eventType, Bundle params)
        {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            instructions = (TextView) findViewById(R.id.instructions);
            instructions.setText(R.string.instructions);
        } else {
            instructions = (TextView) findViewById(R.id.instructions2);
            numbers = (TextView) findViewById(R.id.numbers);
            key = (ImageView) findViewById(R.id.imageView);
            symbol = (ImageView) findViewById(R.id.symbol);
            mic = (ImageButton) findViewById(R.id.speak);
            normal = (Button) findViewById(R.id.normal);
            speech = (Button) findViewById(R.id.speech);

            key.setVisibility(View.INVISIBLE);
            numbers.setVisibility(View.INVISIBLE);
            symbol.setVisibility(View.INVISIBLE);
            mic.setVisibility(View.INVISIBLE);

            mic.setOnClickListener(this);
            System.out.println("listner set");
            sr = SpeechRecognizer.createSpeechRecognizer(this);
            sr.setRecognitionListener(new Listener());


        }

    }

    public void startSpeechTest(View v) {
        instructions.setVisibility(View.INVISIBLE);
        speech.setVisibility(View.INVISIBLE);
        normal.setVisibility(View.INVISIBLE);

        key.setVisibility(View.VISIBLE);
        numbers.setVisibility(View.VISIBLE);
        mic.setVisibility(View.VISIBLE);
        symbol.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK  && resultCode == RESULT_OK) {
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            System.out.println(thingsYouSaid.get(0));
        }
    }



    public void startNormalTest(View v) {
        instructions.setVisibility(View.INVISIBLE);
        key.setVisibility(View.VISIBLE);
        numbers.setVisibility(View.VISIBLE);

    }

    public void onClick(View v) {

        if (v.getId() == R.id.speak)
        {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
            sr.startListening(intent);
            System.out.println("here");

        }
    }
}
