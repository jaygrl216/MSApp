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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseIntArray;
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
import java.util.TreeMap;

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
    private Button button9;

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
    private int timeRemaining = 15;
    private boolean startTest = false;
    private int chosen1 = 0;
    private int chosen2 = 0;
    private int chosen3 = 0;
    private int chosen4 = 0;
    private int chosen5 = 0;
    private int chosen6 = 0;
    private int chosen7 = 0;
    private int chosen8 = 0;
    private int chosen9 = 0;
    Random r = new Random();
    private HashMap<Integer, Double> hash1 = new HashMap<Integer, Double>();
    private HashMap<Integer, List<String>> validPairings = new HashMap<>();
    //number, shape name
    private TreeMap<Integer, String> hashRandomizeSymbol = new TreeMap<>();
    //number, shape image id
    private TreeMap<Integer, Integer> imagePairing = new TreeMap();
    private Button advancedStatsButton;
    private TextView statsTextview;

    private StringBuffer sb;
    int chosenSymbol = -1;
    private static final String TAG = "test";
    ArrayList<String> possibleAnswers = new ArrayList<String>(Arrays.asList("one", "two", "tell",
            "three","four","five",
            "six","seven","eight",
            "sex", "1", "2",
            "3", "4", "5",
            "6", "7", "8", "9", "nine"));

    //time
    protected long firstTime = 0;
    protected long secondTime = 0;
    protected int numOfResponses = 0;
    protected double totalTime = 0.00;
    protected double averageTime = 0.00;
    private ArrayList<Double> responseTimes = new ArrayList<Double>();
    private ArrayList<ArrayList<Double>> learnTimes = new ArrayList<ArrayList<Double>>();

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

    public int setCorrespondingShape(int currentLoop){
        switch (currentLoop) {
            case 0:
                return R.drawable.shape_circle;
            case 1:
                return R.drawable.shape_dollar;
            case 2:
                return R.drawable.shape_plus;
            case 3:
                return R.drawable.shape_pound;
            case 4:
                return R.drawable.shape_square;
            case 5:
                return R.drawable.shape_star;
            case 6:
                return R.drawable.shape_triangle;
            case 7:
                return R.drawable.shape_x;
            case 8:
                return R.drawable.crescent;

        }
        return -1;
    }
    //randomizes symbol and number pairing
    public void setRandomizeSymbol(){
        String[] shapes = new String[]{"circle", "dollar", "plus", "hashtag", "square", "star", "triangle", "x", "crescent"};
        for(int i = 0; i < shapes.length; i++){
            //number is already taken, try again so rerolling another value
            do{
                chosenSymbol = r.nextInt(9 - 1 + 1) + 1;
            } while(hashRandomizeSymbol.containsKey(chosenSymbol));
            hashRandomizeSymbol.put(chosenSymbol,shapes[i]);
            imagePairing.put(chosenSymbol, setCorrespondingShape(i));
        }

        System.out.println(hashRandomizeSymbol.toString());
    }
    public void checkResult(String voiceResult){
        numTotal += 1;
        secondTime = System.currentTimeMillis();
        totalTime = (secondTime - firstTime);
        totalTime /= 1000;
        if(validPairings.get(chosenSymbol).contains(voiceResult)){
            numCorrect += 1;
            ArrayList<Double> hold = learnTimes.get(chosenSymbol - 1);
            hold.add(totalTime);
            learnTimes.set(chosenSymbol - 1, hold);
        }else{
            numWrong += 1;
        }
        responseTimes.add(totalTime);
        numOfResponses++;
        firstTime = System.currentTimeMillis();
        randomizeSymbol();
    }

    public void checkResultNormal(int num){

        numTotal += 1;
        secondTime = System.currentTimeMillis();
        System.out.println(firstTime + " " + secondTime);
        totalTime = (secondTime - firstTime);
        totalTime /= 1000;
        if(num == chosenSymbol){
            numCorrect += 1;
            ArrayList<Double> hold = learnTimes.get(chosenSymbol - 1);
            hold.add(totalTime);
            learnTimes.set(chosenSymbol - 1, hold);
        }
        else{
            numWrong += 1;
        }
        responseTimes.add(totalTime);
        numOfResponses++;
        //initializing hash1.



        switch(chosenSymbol){
            case 1:
                chosen1++;
                if(hash1.get(chosenSymbol) == null){
                    hash1.put(chosenSymbol, totalTime);
                }else{
                    double curr = hash1.get(chosenSymbol);
                    hash1.put(chosenSymbol, curr+totalTime);
                }

                break;
            case 2:
                chosen2++;
                if(hash1.get(chosenSymbol) == null){
                    hash1.put(chosenSymbol, totalTime);
                }else{
                    double curr = hash1.get(chosenSymbol);
                    hash1.put(chosenSymbol, curr+totalTime);
                }
                break;
            case 3:
                chosen3++;
                if(hash1.get(chosenSymbol) == null){
                    hash1.put(chosenSymbol, totalTime);
                }else{
                    double curr = hash1.get(chosenSymbol);
                    hash1.put(chosenSymbol, curr+totalTime);
                }
                break;
            case 4:
                chosen4++;
                if(hash1.get(chosenSymbol) == null){
                    hash1.put(chosenSymbol, totalTime);
                }else{
                    double curr = hash1.get(chosenSymbol);
                    hash1.put(chosenSymbol, curr+totalTime);
                }
                break;
            case 5:
                chosen5++;
                if(hash1.get(chosenSymbol) == null){
                    hash1.put(chosenSymbol, totalTime);
                }else{
                    double curr = hash1.get(chosenSymbol);
                    hash1.put(chosenSymbol, curr+totalTime);
                }
                break;
            case 6:
                chosen6++;
                if(hash1.get(chosenSymbol) == null){
                    hash1.put(chosenSymbol, totalTime);
                }else{
                    double curr = hash1.get(chosenSymbol);
                    hash1.put(chosenSymbol, curr+totalTime);
                }
                break;
            case 7:
                chosen7++;
                if(hash1.get(chosenSymbol) == null){
                    hash1.put(chosenSymbol, totalTime);
                }else{
                    double curr = hash1.get(chosenSymbol);
                    hash1.put(chosenSymbol, curr+totalTime);
                }
                break;
            case 8:
                chosen8++;
                if(hash1.get(chosenSymbol) == null){
                    hash1.put(chosenSymbol, totalTime);
                }else{
                    double curr = hash1.get(chosenSymbol);
                    hash1.put(chosenSymbol, curr+totalTime);
                }
                break;
            default:
                chosen9++;
                if(hash1.get(chosenSymbol) == null){
                    hash1.put(chosenSymbol, totalTime);
                }else{
                    double curr = hash1.get(chosenSymbol);
                    hash1.put(chosenSymbol, curr+totalTime);
                }
                break;
        }
        firstTime = System.currentTimeMillis();
        randomizeSymbol();

    }

    public void randomizeSymbol(){
        chosenSymbol = r.nextInt(9 - 1 + 1) + 1;
        System.out.println("The new chosen symbol: " + chosenSymbol);
        symbol.setImageResource(imagePairing.get(chosenSymbol));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key);
        //randomizes symbol and number pairing
        setRandomizeSymbol();
        //inititating validPairings
        validPairings.put(1, Arrays.asList("one", "1"));
        validPairings.put(2, Arrays.asList("two", "2", "tell"));
        validPairings.put(3, Arrays.asList("three", "3", "tree"));
        validPairings.put(4, Arrays.asList("four", "4"));
        validPairings.put(5, Arrays.asList("five", "5"));
        validPairings.put(6, Arrays.asList("six", "6", "sex"));
        validPairings.put(7, Arrays.asList("seven", "7"));
        validPairings.put(8, Arrays.asList("eight", "8", "ate"));
        validPairings.put(9, Arrays.asList("nine", "9"));
        hash1.put(1, 0.0);
        hash1.put(2, 0.0);
        hash1.put(3, 0.0);
        hash1.put(4, 0.0);
        hash1.put(5, 0.0);
        hash1.put(6, 0.0);
        hash1.put(7, 0.0);
        hash1.put(8, 0.0);
        hash1.put(9, 0.0);


        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            instructions = (TextView) findViewById(R.id.instructions);
            instructions.setText(R.string.instructions);
        } else {
            advancedStatsButton = (Button) findViewById(R.id.advancedStatsButton);
            advancedStatsButton.setVisibility(View.INVISIBLE);
            statsTextview = (TextView) findViewById(R.id.statsTextview);
            statsTextview.setVisibility(View.INVISIBLE);
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

                        button9 = (Button) findViewById(R.id.button_9);
                        button9.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v){
                                checkResultNormal(9);
                            }
                        });
                        firstTime = System.currentTimeMillis();

                        new CountDownTimer(15000, 1000) {
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
                                    for (int b = 0;b < 9;b++){
                                        ArrayList<Double> hold = learnTimes.get(b);
                                        for (int q = 0;q < hold.size();q++){
                                            System.out.println("symbol " + b + " time: " + hold.get(q));
                                        }
                                    }
                                }
                                sb = new StringBuffer();
                                ArrayList<Double> hold;
                                int learnSize;
                                double learnScore;
                                double currentScore;
                                double lastScore;
                                for (int i = 0; i < 9; i++){
                                    switch (i){
                                        case 1:
                                            sb.append("CIRCLE chosen " + chosen1+ " times\n");
                                            sb.append("Average time for CIRCLE: " + hash1.get(i)/(double)chosen1);
                                            sb.append("\nTimes in order for CIRCLE are ");
                                            hold = learnTimes.get(0);
                                            learnSize = hold.size();
                                            learnScore = 0.00;
                                            currentScore = 0.00;
                                            lastScore = 0.00;
                                            if (learnSize == 0){
                                                sb.append("NONE");
                                            }else {
                                                for (int q = 0; q < learnSize; q++) {
                                                    currentScore = hold.get(q);
                                                    sb.append("\n" + currentScore);
                                                    if (q != learnSize - 1){
                                                        sb.append(",");
                                                    }
                                                    if (q != 0){
                                                        learnScore += lastScore - currentScore;
                                                    }
                                                    lastScore = currentScore;
                                                }
                                            }
                                            sb.append("\nLearned score for CIRCLE is " + learnScore);
                                            break;
                                        case 2:
                                            sb.append("\nDOLLAR chosen " + chosen2+ " times\n");
                                            sb.append("Average time for DOLLAR: " + hash1.get(i)/(double)chosen2);
                                            sb.append("\nTimes in order for DOLLAR are ");
                                            hold = learnTimes.get(1);
                                            learnSize = hold.size();
                                            learnScore = 0.00;
                                            currentScore = 0.00;
                                            lastScore = 0.00;
                                            if (learnSize == 0){
                                                sb.append("NONE");
                                            }else {
                                                for (int q = 0; q < learnSize; q++) {
                                                    currentScore = hold.get(q);
                                                    sb.append("\n" + currentScore);
                                                    if (q != learnSize - 1){
                                                        sb.append(",");
                                                    }
                                                    if (q != 0){
                                                        learnScore += lastScore - currentScore;
                                                    }
                                                    lastScore = currentScore;
                                                }
                                            }
                                            sb.append("\nLearned score for DOLLAR is " + learnScore);
                                        case 3:
                                            sb.append("\nPLUS chosen " + chosen3+ " times\n");
                                            sb.append("Average time for PLUS: " + hash1.get(i)/(double)chosen3);
                                            sb.append("\nTimes in order for PLUS are ");
                                            hold = learnTimes.get(2);
                                            learnSize = hold.size();
                                            learnScore = 0.00;
                                            currentScore = 0.00;
                                            lastScore = 0.00;
                                            if (learnSize == 0){
                                                sb.append("NONE");
                                            }else {
                                                for (int q = 0; q < learnSize; q++) {
                                                    currentScore = hold.get(q);
                                                    sb.append("\n" + currentScore);
                                                    if (q != learnSize - 1){
                                                        sb.append(",");
                                                    }
                                                    if (q != 0){
                                                        learnScore += lastScore - currentScore;
                                                    }
                                                    lastScore = currentScore;
                                                }
                                            }
                                            sb.append("\nLearned score for PLUS is " + learnScore);
                                            break;
                                        case 4:
                                            sb.append("\nHASHTAG chosen " + chosen4+ " times\n");
                                            sb.append("Average time for HASHTAG: " + hash1.get(i)/(double)chosen4);
                                            sb.append("\nTimes in order for HASHTAG are ");
                                            hold = learnTimes.get(3);
                                            learnSize = hold.size();
                                            learnScore = 0.00;
                                            currentScore = 0.00;
                                            lastScore = 0.00;
                                            if (learnSize == 0){
                                                sb.append("NONE");
                                            }else {
                                                for (int q = 0; q < learnSize; q++) {
                                                    currentScore = hold.get(q);
                                                    sb.append("\n" + currentScore);
                                                    if (q != learnSize - 1){
                                                        sb.append(",");
                                                    }
                                                    if (q != 0){
                                                        learnScore += lastScore - currentScore;
                                                    }
                                                    lastScore = currentScore;
                                                }
                                            }
                                            sb.append("\nLearned score for HASHTAG is " + learnScore);
                                            break;
                                        case 5:
                                            sb.append("\nSQUARE chosen " + chosen5+ " times\n");
                                            sb.append("Average time for SQUARE: " + hash1.get(i)/(double)chosen5);
                                            sb.append("\nTimes in order for SQUARE are ");
                                            hold = learnTimes.get(4);
                                            learnSize = hold.size();
                                            learnScore = 0.00;
                                            currentScore = 0.00;
                                            lastScore = 0.00;
                                            if (learnSize == 0){
                                                sb.append("NONE");
                                            }else {
                                                for (int q = 0; q < learnSize; q++) {
                                                    currentScore = hold.get(q);
                                                    sb.append("\n" + currentScore);
                                                    if (q != learnSize - 1){
                                                        sb.append(",");
                                                    }
                                                    if (q != 0){
                                                        learnScore += lastScore - currentScore;
                                                    }
                                                    lastScore = currentScore;
                                                }
                                            }
                                            sb.append("\nLearned score for NONE is " + learnScore);
                                            break;
                                        case 6:
                                            sb.append("\nSTAR chosen " + chosen6+ " times\n");
                                            sb.append("Average time for STAR: " + hash1.get(i)/(double)chosen6);
                                            sb.append("\nTimes in order for STAR are ");
                                            hold = learnTimes.get(5);
                                            learnSize = hold.size();
                                            learnScore = 0.00;
                                            currentScore = 0.00;
                                            lastScore = 0.00;
                                            if (learnSize == 0){
                                                sb.append("NONE");
                                            }else {
                                                for (int q = 0; q < learnSize; q++) {
                                                    currentScore = hold.get(q);
                                                    sb.append("\n" + currentScore);
                                                    if (q != learnSize - 1){
                                                        sb.append(",");
                                                    }
                                                    if (q != 0){
                                                        learnScore += lastScore - currentScore;
                                                    }
                                                    lastScore = currentScore;
                                                }
                                            }
                                            sb.append("\nLearned score for STAR is " + learnScore);
                                            break;
                                        case 7:
                                            sb.append("\nTRIANGLE chosen " + chosen7+ " times\n");
                                            sb.append("Average time for TRIANGLE: " + hash1.get(i)/(double)chosen7);
                                            sb.append("\nTimes in order for TRIANGLE are ");
                                            hold = learnTimes.get(6);
                                            learnSize = hold.size();
                                            learnScore = 0.00;
                                            currentScore = 0.00;
                                            lastScore = 0.00;
                                            if (learnSize == 0){
                                                sb.append("NONE");
                                            }else {
                                                for (int q = 0; q < learnSize; q++) {
                                                    currentScore = hold.get(q);
                                                    sb.append("\n" + currentScore);
                                                    if (q != learnSize - 1){
                                                        sb.append(",");
                                                    }
                                                    if (q != 0){
                                                        learnScore += lastScore - currentScore;
                                                    }
                                                    lastScore = currentScore;
                                                }
                                            }
                                            sb.append("\nLearned score for TRIANGLE is " + learnScore);
                                            break;
                                        case 8:
                                            sb.append("\nX chosen " + chosen8+ " times\n");
                                            sb.append("Average time for X: " + hash1.get(i)/(double)chosen8);
                                            sb.append("\nTimes in order for X are ");
                                            hold = learnTimes.get(7);
                                            learnSize = hold.size();
                                            learnScore = 0.00;
                                            currentScore = 0.00;
                                            lastScore = 0.00;
                                            if (learnSize == 0){
                                                sb.append("NONE");
                                            }else {
                                                for (int q = 0; q < learnSize; q++) {
                                                    currentScore = hold.get(q);
                                                    sb.append("\n" + currentScore);
                                                    if (q != learnSize - 1){
                                                        sb.append(",");
                                                    }
                                                    if (q != 0){
                                                        learnScore += lastScore - currentScore;
                                                    }
                                                    lastScore = currentScore;
                                                }
                                            }
                                            sb.append("\nLearned score for X is " + learnScore);
                                            break;
                                        case 9:
                                            sb.append("\nCrescent chosen " + chosen9+ " times\n");
                                            sb.append("Average time for X: " + hash1.get(i)/(double)chosen9);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                statsTextview.setText(sb);
                                statsTextview.setMovementMethod(new ScrollingMovementMethod());
                                numericKeypad.setVisibility(View.INVISIBLE);
                                normalTimer.setVisibility(View.INVISIBLE);
                                advancedStatsButton.setVisibility(View.VISIBLE);
                                speechInfo.setVisibility(View.VISIBLE);
                                speechInfo.setTextColor(Color.BLUE);
                                speechInfo.setText("Test is over!\nYou got " + numCorrect + " correct \n" + numWrong + " incorrect\nOut of " + numTotal + " tries" + "\nAverage Time: " + averageTime);
                            }
                        }.start();
                    }
                }
            });


            for (int i = 0;i<9;i++){
                ArrayList<Double> x = new ArrayList<Double>();
                learnTimes.add(x);
            }



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
                        for (int b = 0;b < 9;b++){
                            ArrayList<Double> hold = learnTimes.get(b);
                            for (int q = 0;q < hold.size();q++){
                                System.out.println("symbol " + b + " time: " + hold.get(q));
                            }
                        }
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

    public void showAdvancedStats(View v){
        speech.setVisibility(View.INVISIBLE);
        normal.setVisibility(View.INVISIBLE);
        instructions.setVisibility(View.INVISIBLE);
        txtTimer.setVisibility(View.INVISIBLE);

        normalStartButton.setVisibility(View.INVISIBLE);

        numericKeypad.setVisibility(View.INVISIBLE);
        normalTimer.setVisibility(View.INVISIBLE);
        key.setVisibility(View.INVISIBLE);
        numbers.setVisibility(View.INVISIBLE);
        symbol.setVisibility(View.INVISIBLE);
        speechInfo.setVisibility(View.INVISIBLE);
        advancedStatsButton.setVisibility(View.INVISIBLE);

        statsTextview.setVisibility(View.VISIBLE);

    }
}
