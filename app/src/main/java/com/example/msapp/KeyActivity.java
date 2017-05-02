package com.example.msapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class KeyActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView instructions;
    private TextView numbers;
    private TextView txtTimer;
    private ImageView key;
    private ImageView symbol;
    private ImageButton mic;
    private SpeechRecognizer sr;
    private Button speech;
    private Button normal;
    private Intent intent;
    private int timeRemaining = 90;
    private boolean startTest = false;
    int chosenSymbol = -1;
    private static final String TAG = "test";
    ArrayList<String> possibleAnswers = new ArrayList<String>(Arrays.asList("one", "two", "tell",
            "three","four","five",
            "six","seven","eight",
            "sex", "1", "2",
            "3", "4", "5",
            "6", "7", "8"));

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
            instructions.setTextColor(Color.RED);
            instructions.setText("We didn't quite get that!\n Please say your number again!");
        }
        public void onResults(Bundle results)
        {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if(possibleAnswers.contains(data.get(0))){
                instructions.setTextColor(Color.GREEN);
                System.out.println(data.get(0));
                instructions.setText("We heard " + data.get(0)+". \nPlease say the next one!");
                randomizeSymbol();
            }
            else{
                instructions.setTextColor(Color.RED);
                instructions.setText("We didn't quite get that! \nWe heard " + data.get(0) + " \nPlease say your number again!");
                //sr.startListening(intent);
            }
        }
        public void onPartialResults(Bundle partialResults)
        {

        }
        public void onEvent(int eventType, Bundle params)
        {

        }
    }

    public void randomizeSymbol(){
        Random r = new Random();
        chosenSymbol = r.nextInt(8 - 1 + 1) + 1;
        System.out.println(chosenSymbol);
        switch (chosenSymbol){
            case 1:
                symbol.setImageResource(R.drawable.shape_circle);
                break;
            case 2:
                symbol.setImageResource(R.drawable.shape_dollar);
                break;
            case 3:
                symbol.setImageResource(R.drawable.shape_plus);
                break;
            case 4:
                symbol.setImageResource(R.drawable.shape_pound);
                break;
            case 5:
                symbol.setImageResource(R.drawable.shape_square);
                break;
            case 6:
                symbol.setImageResource(R.drawable.shape_star);
                break;
            case 7:
                symbol.setImageResource(R.drawable.shape_triangle);
                break;
            case 8:
                symbol.setImageResource(R.drawable.shape_x);
                break;
            default:
                System.out.println("not wroking");
                break;
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
            txtTimer = (TextView) findViewById(R.id.timer);
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
            sr = SpeechRecognizer.createSpeechRecognizer(this);
            sr.setRecognitionListener(new Listener());
        }
        randomizeSymbol();

    }

    public void startSpeechTest(View v) {
        instructions.setText("Speak now!");
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
        System.out.println("here");
        if(!startTest){
            new CountDownTimer(90000, 1000) {
                public void onTick(long millisUntilFinished) {
                    txtTimer.setText("Time Remaining: " + --timeRemaining);
                }
                public void onFinish() {
                    txtTimer.setTextColor(Color.BLUE);
                    txtTimer.setText("Test is over!");
                    mic.setOnClickListener(null);
                    //TODO: ADD MORE STUFF AS NEEDED SUCH AS STAT
                }
            }.start();
            startTest = true;
        }
        if (v.getId() == R.id.speak)
        {
            instructions.setText("Speak now!");
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
            sr.startListening(intent);

        }
    }
}
