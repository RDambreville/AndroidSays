package dambrero.androidsays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Handler;
import android.widget.Toast;
import android.media.MediaPlayer;
//import android.media.AudioManager;
import android.graphics.Color;
import android.util.Log;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    //TODO make a wait method called "waitInBackground(Handler handler#)" to replace all the wait threads throughout
    //TODO clean up all hardcoded strings
    private static final String TAG = "LoopMessage"; // Log tag

    // ------------------------- Initial Object Declarations ----------------------------------------------------------

    private TextView prompt;
    private TextView scoreCount;
    private TextView scoreText;
    private RelativeLayout gameRelativeLayout;
    private GestureDetectorCompat gestureDetector;
    private Vibrator vibrator;

    MediaPlayer mediaPlayer1; // media player for right answer sound effect
    MediaPlayer mediaPlayer2; // medial player for wrong answer sound effect

    String performedGestureName;
    String demandedGestureName;
    int demGestIndex = 0;
    int id; // auxiliary ID returned by demandGesture()
    int id1; //Demanded Gesture ID
    int id2; // Performed Gesture ID
    int repeatMultInt = 1;
    int strikes = 0; // demerits
    int score = 0; //score
    int streak = 0; //streak count
    int longestStreak;//Longest streak at game over
    int transScore = 0;// score sent to GameOver activity
    int diffNum = 0;

    long speed = 2000;

    int roundTime = 0;
    int currentIndex = 0;
    int nextIndex = 0;
    String difficulty;
    String currentBGColor = "";
    String prevBGColor = "";
    //String objects are immutable!! Use StringBuffer objects instead if you want to change strings on the fly
    //String repeatMultString =("x" + repeatMultInt); // multiplier string label for repeated gesture (i.e. Single Tap! x2)
    StringBuilder repeatMultString = new StringBuilder();
    boolean gameStarted = false;
    boolean soundsOn;
    boolean quitButtonPressed = false; // arbitrary boolean variable to pair with forceQuitGame() method
    //when true, game logic will be overridden and game will exit to main screen

    long winVibLength = 50;
    long loseVibLength = 50;
    long repeat[] = new long[2];

    ArrayList<String> demandedGestureList = new ArrayList<String>();
    ArrayList<String> performedGestureList = new ArrayList<String>();

    Random random = new Random(System.currentTimeMillis());


    //------------------------ Thread Handlers ---------------------------------------------------------------------

    // (1) Handle initial game loop after wait time
    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "Starting gameLoop()...");
           // gameLoop();// performs game loop change after 3 seconds
            newRoundStart();
        }
    };


    // (2) Handle scoring and penalty logic after check time
    @SuppressLint("HandlerLeak")
    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "Checking if user gesture matches demanded gesture");
            SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);


            if (performedGestureList.equals(demandedGestureList)){
                if(quitButtonPressed){
                    return;
                }
                else{
                    score++;
                    streak++;
                    //speed -= 100;
                    //resetColorScheme();
                    prompt.setText("Right!");
                    if(sharedPref.getBoolean("soundsOn", soundsOn)) {
                        mediaPlayer1.start(); // play correct answer sound effect
                    }
                    else{
                        //leave sounds off
                        //do nothing
                    }

                    scoreCount.setText("" + score);
                    //roundTime = 0;
                    //demGestIndex = 0;
                    currentIndex =0;
                    nextIndex = 0;
                    gameStarted = false;
                    //demandedGestureList.removeAll(demandedGestureList);
                    performedGestureList.removeAll(performedGestureList);
                    repeatMultInt = 1;
                    repeatMultString.delete(0, repeatMultString.length());
                    repeatMultString.trimToSize();

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            long futureTime = System.currentTimeMillis() + 1000;
                            while (System.currentTimeMillis() < futureTime) {
                                synchronized (this) {
                                    try {
                                        wait(futureTime - System.currentTimeMillis());
                                    } catch (Exception e) {
                                    }
                                }
                            }
                            handler7.sendEmptyMessage(0);
                        }
                    };
                    Thread waitBeforeNewRound = new Thread(runnable);
                    waitBeforeNewRound.start();

                    //Log.i(TAG, "score = " + score); ----> debugging log message
                }

            }
            else{

                    if(quitButtonPressed){// if player pressed quit button
                        return; // same as above
                    }
                    else { // otherwise initiate penalty logic
                        prompt.setText("Wrong!");
                        if(sharedPref.getBoolean("soundsOn", soundsOn)) {
                            mediaPlayer2.start(); // play wrong answer sound effect
                        }
                        else{
                            //do nothing
                        }
                       longestStreak = streak;
                        streak = 0; // reset streak count
                        //vibrator.vibrate(loseVibLength);
                        gameStarted = false;
                        //demandedGestureList.removeAll(demandedGestureList);
                        //performedGestureList.removeAll(performedGestureList);
                        repeatMultInt = 1;
                        repeatMultString.delete(0, repeatMultString.length());
                        repeatMultString.trimToSize();
                        Thread waitThread = new Thread() {
                            @Override
                            public void run() {
                                long futureTime = System.currentTimeMillis() + 500;
                                while (System.currentTimeMillis() < futureTime) {
                                    synchronized (this) {
                                        try {
                                            wait(futureTime - System.currentTimeMillis());
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                //handler2.sendEmptyMessage(0);
                                //gameOver();
                                handler9.sendEmptyMessage(0);
                            }
                        };
                        waitThread.start();
                        //gameOver(); // initiate game over logic
                }
            }
        }
    };

    // (3) Handle gameOver() logic
    Handler handler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "Quitting game");
            toGameOverActivity(); // intents can't be handled inside a handler (no pun intended),
            // so let another function (toGameOverActivity()) handle the intent

        }
    };

    //Handle quit button logic
    /*Handler handler4 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "Checking whether or not quit button has been pressed");
            while(!quitButtonPressed){
                //do nothing
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

        }
    };*/

    Handler handler6 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           continueOrStop();
        }
    };

    Handler handler7 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            newRoundStart();
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler8 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (demandedGestureList.size() == 1 || nextIndex + 1 > demandedGestureList.size()) { // If it is only the first round or if we've reached the end of the demanded gesture list
                roundCheck();
            } else if (demandedGestureList.size() > 1 && nextIndex < demandedGestureList.size()) { // If it is after the first round and there are more gestures in the gesture list
                if (demandedGestureList.get(nextIndex) == demandedGestureList.get(nextIndex - 1)) { // If the next demanded gesture is the same as the one before it
                    repeatMultInt++; // increment the repeat indicator because a repeat gesture has been found
                    clearPrintedRepeatIndicator(); // remove the current repeat indicator from the prompt because it is about to change
                    printDemandedGestureList(nextIndex);
                } else { // if the next demanded gesture is not the same as the one before it
                    resetRepeatIndicator(); // reset and remove the repeat indicator because the repeat streak has just been broken
                    printDemandedGestureList(nextIndex);
                }
            }

        }
    };

    Handler handler9 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            gameOver();
        }
    };

    Handler handler10 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            roundCheck();
        }
    };



    //------------------------------- onCreate() ----------------------------------------------------------------------
    //Setup app activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Call the service that checks to see if quit button is pressed
        //Intent intent = new Intent(this, QuitGameIntentService.class);
       /* Intent intent = new Intent(this, QuitService.class);
        intent.putExtra("Quit", quitButtonPressed);
        startService(intent);*/

        //Bundle diffData = getIntent().getExtras(); // get difficulty from settings activity
        //diffNum = diffData.getInt("difficulty");

        /*startButton = (Button) findViewById(R.id.startButton);
        quitButton = (Button) findViewById(R.id.quitButton);*/
        this.gestureDetector = new GestureDetectorCompat(this, this); // (this activity, this listener)
        gestureDetector.setOnDoubleTapListener(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("soundsOn", soundsOn )) {
            mediaPlayer1 = MediaPlayer.create(this, R.raw.right_answer1); // media player for right answer
            mediaPlayer2 = MediaPlayer.create(this, R.raw.wrong_answer_sound); // medial player for wrong answer
        }
        else{
            //Do nothing
        }

        //Set difficulty based on user settings selection
        switch(sharedPref.getString("difficulty", difficulty)){
            case "Novice":
                diffNum = 1000; // 1000 ms (1.0 s) to perform gesture
                break;
            case "Intermediate":
                diffNum = 800; // 800 ms (0.8 s) to perform gesture ---->(4/5) of normal time
                break;
            case "Expert":
                diffNum = 600; // 600 ms (0.6 s) to perform gesture ----->(3/5) of normal time
        }

        gameRelativeLayout = (RelativeLayout) findViewById(R.id.gameRelativeLayout);
        prompt = (TextView) findViewById(R.id.prompt);
        scoreCount = (TextView) findViewById(R.id.scoreCount);
        scoreText = (TextView) findViewById(R.id.scoreText);

        scoreCount.setText("" + 0);

        Log.i(TAG, "score = " + score);
        Log.i(TAG, "strikes = " + strikes);
        Log.i(TAG, "id1 = " + id1);
        Log.i(TAG, "id2 = " + id2);


       /* //Start threat that begins checking if quit button has been pressed
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                handler4.sendEmptyMessage(0);
            }
        };
        Thread quitThread = new Thread(r1);
        quitThread.start();*/

        //Start thread that begins game loop after 3 seconds
        Runnable r2 = new Runnable(){
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + 3000;
                while(System.currentTimeMillis() < futureTime){
                    synchronized (this){
                        try{
                            wait(futureTime-System.currentTimeMillis());
                        }catch (Exception e){}
                    }
                }
                handler1.sendEmptyMessage(0);
            }
        };
        Thread robsThread = new Thread(r2);
        robsThread.start();

        prompt.setText("Ready?"); // In the meantime, display "Ready?" on the screen

    }


    //-----------------------------Override Activity Methods-------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //----------------Implement Gesture Methods-----------------------------------------------------------------------

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        if(gameStarted == true){// activate gesture response if game is started
            performedGestureName = "Double Tap!";
            storeUserGesture();
            if (performedGestureList.size() > 1 && nextIndex < performedGestureList.size()){
                if (performedGestureList.get(nextIndex) == performedGestureList.get(nextIndex - 1)){
                    repeatMultInt++;
                    repeatMultString.delete(0, repeatMultString.length());
                    repeatMultString.trimToSize();
                    prompt.setText(performedGestureName + " " + repeatMultString.append("(" + repeatMultInt + ")"));
                }
                else{
                    repeatMultInt = 1;
                    prompt.setText(performedGestureName);
                }
            }
            else /*if (repeatMultInt == 1)*/ {
                prompt.setText(performedGestureName);
            }
            nextIndex++;
            //changeColorScheme();
            Log.i(TAG, "Double Tap");
            return true;
        }
        else{
            // do nothing and return
            return true;
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if(gameStarted == true){// activate gesture response if game is started
            performedGestureName = "Single Tap!";
            storeUserGesture();
            if (performedGestureList.size() > 1 && nextIndex < performedGestureList.size()){
                if (performedGestureList.get(nextIndex) == performedGestureList.get(nextIndex - 1)){
                    repeatMultInt++;
                    repeatMultString.delete(0, repeatMultString.length());
                    repeatMultString.trimToSize();
                    prompt.setText(performedGestureName + " " + repeatMultString.append("(" + repeatMultInt + ")"));
                }
                else{
                    repeatMultInt = 1;
                    prompt.setText(performedGestureName);
                }
            }
            else /*if (repeatMultInt == 1)*/ {
                prompt.setText(performedGestureName);
            }
            nextIndex++;
            //changeColorScheme();
            Log.i(TAG, "SingleTapConfirmed");
            return true;
        }
        else{
            // do nothing and return
            return true;
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent)
    {
        return false;
    }
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    //Ignore Scroll events. They're too sensitive and difficult to implement
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        if(gameStarted == true){ // activate gesture response if game is started
            storeUserGesture();
            prompt.setText("Scroll");
            changeColorScheme();
            Log.i(TAG, "Scroll");
            return true;
        }
        else{
            //do nothing and return
            return true;
        }


    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //prompt.setText("Long Press Achieved!");
        //gestureName = "Long Press";
        if(gameStarted == true  /*&& touches < performedGestureList.size()*/ /*&& roundProgress == 3*/){// activate gesture response if game is started and round has finished making progress
            //id2 = 5;
            //touches++;
            performedGestureName = "Long Press!";
            storeUserGesture();
            if (performedGestureList.size() > 1 && nextIndex < performedGestureList.size()){
                if (performedGestureList.get(nextIndex) == performedGestureList.get(nextIndex - 1)){
                    repeatMultInt++;
                    repeatMultString.delete(0, repeatMultString.length());
                    repeatMultString.trimToSize();
                    prompt.setText(performedGestureName + " " + repeatMultString.append("(" + repeatMultInt + ")"));
                }
                else{
                    repeatMultInt = 1;
                    prompt.setText(performedGestureName);
                }
            }
            else /*if (repeatMultInt == 1)*/ {
                prompt.setText(performedGestureName);
            }
            nextIndex++;
            //changeColorScheme();
            Log.i(TAG, "Long Press");
            //Log.i(TAG, "id2 = " + id2 + "\n");
        }
        else{
            // do nothing
        }

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        //prompt.setText("Fling Achieved!");
        //gestureName = "Fling";
        if(gameStarted == true  /*&& touches < performedGestureList.size()*/ /*&& roundProgress == 3*/){// activate gesture response if game is started and round has finished making progress
            //id2 = 4;
            //touches++;
            performedGestureName = "Fling!";
            storeUserGesture();
            if (performedGestureList.size() > 1 && nextIndex < performedGestureList.size()){
                if (performedGestureList.get(nextIndex) == performedGestureList.get(nextIndex - 1)){
                    repeatMultInt++;
                    repeatMultString.delete(0, repeatMultString.length());
                    repeatMultString.trimToSize();
                    prompt.setText(performedGestureName + " " + repeatMultString.append("(" + repeatMultInt + ")"));
                }
                else{
                    repeatMultInt = 1;
                    prompt.setText(performedGestureName);
                }
            }
            else /*if (repeatMultInt == 1)*/ {
                prompt.setText(performedGestureName);
            }
            nextIndex++;
            //changeColorScheme();
            Log.i(TAG, "Fling");
            //Log.i(TAG, "id2 = " + id2 + "\n");

            return true;
        }
        else{
            // do nothing and return
            return true;
        }
    }

    //Method that handles all touch events on device screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event); // determine if touch event was a special gesture and use methods above to handle appropriately. If not handle as a normal touch event
        return super.onTouchEvent(event);
    }


//-------------------- Game Logic Methods -------------------------------------------------------------------------

    //Perform game over logic
    public void gameOver(){

        if(quitButtonPressed){//check if quit button was pressed before calling
            // the game over handler
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        Runnable r3 = new Runnable() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + 1500;
                while(System.currentTimeMillis() < futureTime){
                    synchronized (this){
                        try{
                            wait(futureTime-System.currentTimeMillis());
                        }catch (Exception e){}
                    }
                }//After wait time
                if(quitButtonPressed){//check if quit button was pressed before calling
                    // the game over handler
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else {// otherwise if quit button was not pressed
                    handler3.sendEmptyMessage(0); // call the game over handler
                }
            }
        };

        Thread waitToQuit = new Thread(r3);
        waitToQuit.start();
        prompt.setText("Game over!");
        Toast.makeText(GameActivity.this,"Thanks for playing!", Toast.LENGTH_SHORT).show();
        transScore += score; // put current score in transScore, which will be sent to the GameOver activity
        score = 0;
        strikes = 0;
    }


    //Change screen to GameOverActivity
    public void toGameOverActivity(){
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra("score", transScore);
        // intent.putExtra("difficulty", diffNum);
        intent.putExtra("streak", longestStreak);
        intent.putExtra("correctGestureList", demandedGestureList);
        intent.putExtra("performedGestureList", performedGestureList);
        startActivity(intent);
    }


    //Force Quit game by button click
    public void forceQuitGame(View view){
        //prompt.setText("Thanks for Playing!");
        quitButtonPressed = true;
        Toast.makeText(GameActivity.this, "Thanks for playing!", Toast.LENGTH_SHORT).show();
        score = 0;
        strikes = 0;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    //Randomly demand gesture to be performed by player and check if demanded gesture is a repeat
    public int demandGesture() { // app demands random gesture to be performed by user
        int randNum = (random.nextInt(4) + 1);

        Log.i(TAG, "randNum = " + randNum);

        switch (randNum) {
            case 1:
                demandedGestureName = "Double Tap!";
                id = 1;
                //checkForRepeatedGestures();
                break;

            case 2:
                demandedGestureName = "Single Tap!";
                id = 2;
                //checkForRepeatedGestures();
                break;

            case 3:
                //demandGesture();
                demandedGestureName = "Fling!";
                id = 3;
                //checkForRepeatedGestures();
                break;

            case 4:
                demandedGestureName = "Long Press!";
                id = 4;
                //checkForRepeatedGestures();
                break;
        }
        //prevID = id; //store current demanded gesture id in repeatID to check for repeated gestures later
        return id;
    }



    //Display demanded gestures at set intervals
    public void continueOrStop(){
        if (demGestIndex < 3){
            //roundProgress++;
            gameLoop();
        }
        else{
            roundCheck();
        }
    }


    public void roundCheck(){
        nextIndex = 0;
        repeatMultInt = 1;
        repeatMultString.delete(0, repeatMultString.length());
        repeatMultString.trimToSize();
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + 4000 /*(roundTime)*/; // set future time according to difficulty selected
                /*while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (Exception e) {}
                    }
                }*/
                while (performedGestureList.size() < demandedGestureList.size()) { //while user has not completed gesture sequence
                    synchronized (this) {
                        // do nothing
                    }
                }
                Thread waitThread = new Thread() {
                    @Override
                    public void run() {
                        long futureTime = System.currentTimeMillis() + 500;
                        while (System.currentTimeMillis() < futureTime) {
                            synchronized (this) {
                                try {
                                    wait(futureTime - System.currentTimeMillis());
                                } catch (Exception e) {
                                }
                            }
                        }
                        handler2.sendEmptyMessage(0);
                    }
                };
                waitThread.start();
                //handler2.sendEmptyMessage(0);
            }
        };
        Thread checkThread = new Thread(r2);
        checkThread.start();
        //resetColorScheme();
        prompt.setText("Your turn!");
        gameStarted = true;
    }


    public void newRoundStart(){
        //gameStarted = true;
        // Restart game loop
        gameLoop();
    }




    //Perform game loop
    public void gameLoop() {

        if(strikes == 1){
            gameOver();
        }
        else {
            populateDemandedGestureList();
            printDemandedGestureList(nextIndex);
        }
    }

    // store user gestures in the order they are performed
    public void storeUserGesture(){
        performedGestureList.add(performedGestureName);
    }



    //Change background to random color when gesture changes
    public void changeColorScheme(){
        selectBGColor();
        if(prevBGColor != "") {
             checkForRepeatedBGColor();
        }
        switch (currentBGColor){
            case "Red":
                gameRelativeLayout.setBackgroundColor(Color.RED);
                prompt.setTextColor(Color.WHITE);
                scoreCount.setTextColor(Color.WHITE);
                scoreText.setTextColor(Color.WHITE);
                break;

            case "Blue":
                gameRelativeLayout.setBackgroundColor(Color.BLUE);
                prompt.setTextColor(Color.WHITE);
                scoreCount.setTextColor(Color.WHITE);
                scoreText.setTextColor(Color.WHITE);
                break;

            case "Green":
                gameRelativeLayout.setBackgroundColor(Color.GREEN);
                prompt.setTextColor(Color.BLACK);
                scoreCount.setTextColor(Color.BLACK);
                scoreText.setTextColor(Color.BLACK);
                break;

            case "Yellow":
                gameRelativeLayout.setBackgroundColor(Color.YELLOW);
                prompt.setTextColor(Color.BLACK);
                scoreCount.setTextColor(Color.BLACK);
                scoreText.setTextColor(Color.BLACK);
                break;

            case "Cyan":
                gameRelativeLayout.setBackgroundColor(Color.CYAN);
                prompt.setTextColor(Color.BLACK);
                scoreCount.setTextColor(Color.BLACK);
                scoreText.setTextColor(Color.BLACK);
                break;
        }

        prevBGColor = currentBGColor;
        Log.i(TAG, "getBackground = " + gameRelativeLayout.getBackground());
        //checkForRepeatedBGColor();
    }

    public void selectBGColor(){
        Random random = new Random();
        int randNum = random.nextInt(5) + 1;
        switch (randNum){
            case 1:
                currentBGColor = "Red";
                break;
            case 2:
                currentBGColor = "Blue";
                break;
            case 3:
                currentBGColor = "Green";
                break;
            case 4:
                currentBGColor = "Yellow";
                break;
            case 5:
                currentBGColor = "Cyan";
        }


    }

    public void resetColorScheme(){
        // Reset color scheme to default
        gameRelativeLayout.setBackgroundColor(Color.WHITE);
        prompt.setTextColor(Color.BLACK);
        scoreCount.setTextColor(Color.BLACK);
        scoreText.setTextColor(Color.BLACK);
    }

    public void checkForRepeatedBGColor() {
        if (currentBGColor == prevBGColor) {
                changeColorScheme();
            }
        }

    public void populateDemandedGestureList() {
        demandGesture(); //generate a gesture to be performed
        demandedGestureList.add(demandedGestureName); // add the gesture name to the list of demanded gestures
    }

    public void printDemandedGestureList(int index) {
        //checkForRepeatedGestures();
        if (repeatMultInt == 1) { // If current gesture is not a repeat of the last one
            prompt.setText(demandedGestureList.get(index)); // print the gesture without a multiplier indicator
        }
        else{ // If the current gesture is a repeat of the last one
            prompt.setText(demandedGestureList.get(index) + " " + repeatMultString.append("(" + repeatMultInt + ")")); // print the gesture with a multiplier indicator
        }

        nextIndex++;
        Thread waitThread = new Thread() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + 1000;
                //long futureTime = System.currentTimeMillis() + speed;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (Exception e) {
                        }
                    }
                }
                handler8.sendEmptyMessage(0);
            }
        };
        waitThread.start();

    }

    public void waitInBackground(Handler handler, long waitTime){
        /*Thread waitThread = new Thread() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + waitTime;
                while (System.currentTimeMillis() < futureTime) {
                    synchronized (this) {
                        try {
                            wait(futureTime - System.currentTimeMillis());
                        } catch (Exception e) {
                        }
                    }
                }
                handler.sendEmptyMessage(0);
            }
        };
        waitThread.start();*/
    }


    //Empty the repeat indicator string buffer so that it can modified and appended later,
    //but preserve the value of the repeat integer because there are presumably more repeats to come.
    public void clearPrintedRepeatIndicator(){
        repeatMultString.delete(0, (repeatMultString.length())); // clear the repeat indicator string so that it can be appended later
        repeatMultString.trimToSize(); // trim off the empty spaces in the buffer because they aren't being used
    }

    //Reset the repeat indicator and integer
    public void resetRepeatIndicator(){
        repeatMultInt = 1;// reset the repeat indicator because a unique gesture has been found and has just broken the repeat streak
        clearPrintedRepeatIndicator();

    }

}







