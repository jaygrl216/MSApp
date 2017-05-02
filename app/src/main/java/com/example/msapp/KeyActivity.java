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
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class KeyActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView instructions;
    private TextView numbers;
    private TextView txtTimer;
    private TextView normalTimer;
    private TextView speechInfo;
    private GridLayout numericKeypad;
    private Button normalStartButton;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Button button7;
    private Button button8;
    private ImageView key;
    private ImageView symbol;
    private ImageButton mic;
    private SpeechRecognizer sr;
    private Button speech;
    private Button normal;
    private Intent intent;
    private int numCorrect = 0;
    private int numWrong = 0;
    private int numTotal = 0;
    private int timeRemaining = 90;
    private boolean startTest = false;
    private HashMap<Integer, List<String>> validPairings = new HashMap<>();
    int chosenSymbol = -1;
    private static final String TAG = "test";
    ArrayList<String> possibleAnswers = new ArrayList<String>(Arrays.asList("one", "two", "tell",
            "three","four","five",
            "six","seven","eight",
            "sex", "1", "2",
            "3", "4", "5",
            "6", "7", "8"));

    //time
    protected long firstTime = 0;
    protected long secondTime = 0;
    protected int numOfResponses = 0;
    protected double totalTime = 0.00;
    protected double averageTime = 0.00;
    private ArrayList<Double> responseTimes = new ArrayList<Double>();

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
            speechInfo.setTextColor(Color.RED);
            speechInfo.setText("We didn't quite get that!\n Please say your number again!");
        }
        public void onResults(Bundle results)
        {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if(possibleAnswers.contains(data.get(0))){
                speechInfo.setTextColor(Color.GREEN);
                System.out.println(data.get(0));

                //common misintepration of numbers
                if(data.get(0).toString().equals("sex")){
                    data.set(0, "6");
                }
                else if(data.get(0).toString().equals("tell")){
                    data.set(0, "2");
                }

                speechInfo.setText("We heard " + data.get(0)+". \nPlease say the next one!");
                checkResult(data.get(0).toString());
            }
            else{
                speechInfo.setTextColor(Color.RED);
                speechInfo.setText("We didn't quite get that! \nWe heard " + data.get(0) + " \nPlease say your number again!");
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

    public void checkResult(String voiceResult){
        if(validPairings.get(chosenSymbol).contains(voiceResult)){
            numCorrect += 1;
        }else{
            numWrong += 1;
        }
        numTotal += 1;
        secondTime = System.currentTimeMillis();
        totalTime = (secondTime - firstTime);
        totalTime /= 1000;
        responseTimes.add(totalTime);
        numOfResponses++;
        firstTime = System.currentTimeMillis();
        randomizeSymbol();
    }

    public void checkResultNormal(int num){
        if(num == chosenSymbol){
            numCorrect += 1;
        }
        else{
            numWrong += 1;
        }
        numTotal += 1;
        secondTime = System.currentTimeMillis();
        System.out.println(firstTime + " " + secondTime);
        totalTime = (secondTime - firstTime);
        totalTime /= 1000;
        responseTimes.add(totalTime);
        numOfResponses++;
        firstTime = System.currentTimeMillis();
        randomizeSymbol();

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
                System.out.println("not working");
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        //inititating validPairings
        validPairings.put(1, Arrays.asList("one", "1"));
        validPairings.put(2, Arrays.asList("two", "2", "tell"));
        validPairings.put(3, Arrays.asList("three", "3", "tree"));
        validPairings.put(4, Arrays.asList("four", "4"));
        validPairings.put(5, Arrays.asList("five", "5"));
        validPairings.put(6, Arrays.asList("six", "6", "sex"));
        validPairings.put(7, Arrays.asList("seven", "7"));
        validPairings.put(8, Arrays.asList("eight", "8", "ate"));

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            instructions = (TextView) findViewById(R.id.instructions);
            instructions.setText(R.string.instructions);
        } else {
            instructions = (TextView) findViewById(R.id.instructions2);
            numbers = (TextView) findViewById(R.id.numbers);
            txtTimer = (TextView) findViewById(R.id.timer);
            normalTimer = (TextView) findViewById(R.id.normalTimer);
            key = (ImageView) findViewById(R.id.imageView);
            symbol = (ImageView) findViewById(R.id.symbol);
            mic = (ImageButton) findViewById(R.id.speak);
            normal = (Button) findViewById(R.id.normal);
            speech = (Button) findViewById(R.id.speech);
            speechInfo = (TextView) findViewById(R.id.speakInfo);
            numericKeypad = (GridLayout) findViewById(R.id.numericKeypad);

            normalStartButton = (Button) findViewById(R.id.normalTestStart);
            normalStartButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    normalStartButton.setVisibility(View.INVISIBLE);
                    if(!startTest){
                        firstTime = System.currentTimeMillis();
                        new CountDownTimer(90000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                normalTimer.setText("" +--timeRemaining);
                            }
                            public void onFinish() {
                                if (numOfResponses != 0){
                                    totalTime = 0;
                                    for (int i = 0;i < responseTimes.size();i++){
                                        totalTime += responseTimes.get(i);
                                        System.out.println("time " + i + " is " + responseTimes.get(i));
                                    }
                                    averageTime = totalTime/numOfResponses;
                                    System.out.println("average time is " + averageTime);
                                }
                                numericKeypad.setVisibility(View.INVISIBLE);
                                normalTimer.setVisibility(View.INVISIBLE);
                                speechInfo.setVisibility(View.VISIBLE);
                                speechInfo.setTextColor(Color.BLUE);
                                speechInfo.setText("Test is over!\nYou got " + numCorrect + " correct \n" + numWrong + " incorrect\nOut of " + numTotal + " tries" + "\nAverage Time: " + averageTime);
                            }
                        }.start();
                        startTest = true;
                    }
                }
            });

            button1 = (Button) findViewById(R.id.button_1);
            button1.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    checkResultNormal(1);
                }
            });

            button2 = (Button) findViewById(R.id.button_2);
            button2.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    checkResultNormal(2);
                }
            });

            button3 = (Button) findViewById(R.id.button_3);
            button3.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    checkResultNormal(3);
                }
            });

            button4 = (Button) findViewById(R.id.button_4);
            button4.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    checkResultNormal(4);
                }
            });

            button5 = (Button) findViewById(R.id.button_5);
            button5.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    checkResultNormal(5);
                }
            });

            button6 = (Button) findViewById(R.id.button_6);
            button6.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    checkResultNormal(6);
                }
            });

            button7 = (Button) findViewById(R.id.button_7);
            button7.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    checkResultNormal(7);
                }
            });

            button8 = (Button) findViewById(R.id.button_8);
            button8.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    checkResultNormal(8);
                }
            });


            key.setVisibility(View.INVISIBLE);
            numbers.setVisibility(View.INVISIBLE);
            symbol.setVisibility(View.INVISIBLE);
            mic.setVisibility(View.INVISIBLE);
            speechInfo.setVisibility(View.INVISIBLE);
            normalTimer.setVisibility(View.INVISIBLE);
            numericKeypad.setVisibility(View.INVISIBLE);
            normalStartButton.setVisibility(View.INVISIBLE);

            mic.setOnClickListener(this);
            sr = SpeechRecognizer.createSpeechRecognizer(this);
            sr.setRecognitionListener(new Listener());
            randomizeSymbol();
        }


    }

    public void startSpeechTest(View v) {
        speechInfo.setText("Speak now!");
        speech.setVisibility(View.INVISIBLE);
        normal.setVisibility(View.INVISIBLE);
        instructions.setVisibility(View.INVISIBLE);

        key.setVisibility(View.VISIBLE);
        numbers.setVisibility(View.VISIBLE);
        mic.setVisibility(View.VISIBLE);
        symbol.setVisibility(View.VISIBLE);
        speechInfo.setVisibility(View.VISIBLE);

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
        speech.setVisibility(View.INVISIBLE);
        normal.setVisibility(View.INVISIBLE);
        instructions.setVisibility(View.INVISIBLE);
        txtTimer.setVisibility(View.INVISIBLE);

        normalStartButton.setVisibility(View.VISIBLE);

        numericKeypad.setVisibility(View.VISIBLE);
        normalTimer.setVisibility(View.VISIBLE);
        key.setVisibility(View.VISIBLE);
        numbers.setVisibility(View.VISIBLE);
        symbol.setVisibility(View.VISIBLE);
    }

    public void onClick(View v) {
        System.out.println("here");
        if(!startTest){
            firstTime = System.currentTimeMillis();
            new CountDownTimer(15000, 1000) {
                public void onTick(long millisUntilFinished) {
                    txtTimer.setText("Time Remaining: " + --timeRemaining);
                }
                public void onFinish() {
                    if (numOfResponses != 0){
                        totalTime = 0;
                        for (int i = 0;i < responseTimes.size();i++){
                            totalTime += responseTimes.get(i);
                            System.out.println("time " + i + " is " + responseTimes.get(i));
                        }
                        averageTime = totalTime/numOfResponses;
                        System.out.println("average time is " + averageTime);
                    }
                    txtTimer.setVisibility(View.INVISIBLE);
                    speechInfo.setTextColor(Color.BLUE);
                    speechInfo.setText("Test is over!\nYou got " + numCorrect + " correct \n" + numWrong + " incorrect\nOut of " + numTotal + " tries" + "\nAverage Time: " + averageTime);
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
