package dambrero.androidsays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.media.AudioManager;

import java.util.Collection;
import java.util.Map;


public class SettingsActivity extends Activity {

    //int diffNum = 0; // initial difficulty value
    int currentHighScore;
    int soundNum;
    int volume;
    String difficulty;
    boolean soundsOn;
    boolean vibrationsOn;

    TextView highScoreText;
    SeekBar soundsSeekBar;

    AudioManager audioManager;


    private static String TAG = "SavedScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        /*SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Toast.makeText(getApplicationContext(), "SeekBar Progress: " + i, Toast.LENGTH_SHORT).show();
                TextView difficultyNum = (TextView) findViewById(R.id.difficultyNum);
                diffNum = i;
                difficultyNum.setText("" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/

        SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        highScoreText = (TextView) findViewById(R.id.highScoreText);
        highScoreText.setText("Best:" + sharedPref.getInt("highScore", currentHighScore));


        /*editor.putInt("highScore", currentHighScore);
        editor.putBoolean("soundsOn", soundsOn);
        editor.putString("difficulty", difficulty);*/

        Map sharedPrefMap = sharedPref.getAll();
        String sharedPrefString = sharedPrefMap.toString();
        Log.i(TAG, "sharePrefString: " + sharedPrefString);

        RadioGroup diffRadioGroup = (RadioGroup) findViewById(R.id.diffRadioGroup);
        RadioGroup soundsRadioGroup = (RadioGroup) findViewById(R.id.soundsRadioGroup);
        RadioGroup vibrationsRadioGroup = (RadioGroup) findViewById(R.id.vibrationsRadioGroup);

        //bubble select logic for difficulty radio buttons
        switch(sharedPref.getString("difficulty", difficulty)){
            case "Novice":
                diffRadioGroup.check(R.id.noviceButton);
                break;
            case "Intermediate":
                diffRadioGroup.check(R.id.intermediateButton);
                break;
            case "Expert":
                diffRadioGroup.check(R.id.expertButton);
                break;
        }

        //bubble select logic for sounds radio buttons
        if (sharedPref.getBoolean("soundsOn", soundsOn)){
            soundsRadioGroup.check(R.id.soundsOnButton);
            soundsSeekBar = (SeekBar) findViewById(R.id.soundsSeekBar);
            soundsSeekBar.setEnabled(true);
        }
        else{
            soundsRadioGroup.check(R.id.soundsOffButton);
            soundsSeekBar = (SeekBar) findViewById(R.id.soundsSeekBar);
            soundsSeekBar.setEnabled(false);
        }

        //bubble select logic for vibrations radio buttons
        if (sharedPref.getBoolean("vibrationsOn", vibrationsOn)){
            vibrationsRadioGroup.check(R.id.vibrationsOnButton);
        }
        else{
            vibrationsRadioGroup.check(R.id.vibrationsOffButton);
        }


        //difficulty radio group seems to prefer switch structures instead of if/else structures
        /*if(sharedPref.getString("difficulty", difficulty) == "Novice") {
            diffRadioGroup.check(R.id.noviceButton);
        }
        else if(sharedPref.getString("difficulty", difficulty) == "Intermediate"){
            diffRadioGroup.check(R.id.intermediateButton);
        }
        else if(sharedPref.getString("difficulty", difficulty) == "Expert")
        {
            diffRadioGroup.check(R.id.expertButton);
        }*/


        diffRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.noviceButton:
                        difficulty = "Novice";
                        editor.putString("difficulty", difficulty);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Difficulty:Novice", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.intermediateButton:
                        difficulty = "Intermediate";
                        editor.putString("difficulty", difficulty);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Difficulty:Intermediate", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.expertButton:
                        difficulty = "Expert";
                        editor.putString("difficulty", difficulty);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Difficulty:Expert", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        soundsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                TextView soundsIndicatorText = (TextView) findViewById(R.id.soundsIndicatorText);
                switch (i){
                    case R.id.soundsOnButton:
                        soundsOn = true;
                        soundsSeekBar.setEnabled(soundsOn);
                        editor.putBoolean("soundsOn", soundsOn);
                        editor.apply();
                        soundsIndicatorText.setText("Volume: " + sharedPref.getInt("Volume", volume));
                        Toast.makeText(getApplicationContext(), "Sounds:On", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.soundsOffButton:
                        soundsOn = false;
                        soundsSeekBar.setEnabled(soundsOn);
                        editor.putBoolean("soundsOn", soundsOn);
                        editor.apply();
                        soundsIndicatorText.setText("Volume: " + 0);
                        Toast.makeText(getApplicationContext(), "Sounds:Off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        vibrationsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.vibrationsOnButton:
                        vibrationsOn = true;
                        editor.putBoolean("vibrationsOn", vibrationsOn);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Vibrations:On", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.vibrationsOffButton:
                        vibrationsOn = false;
                        editor.putBoolean("vibrationsOn", vibrationsOn);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Vibrations:Off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });



        soundsSeekBar = (SeekBar) findViewById(R.id.soundsSeekBar);
        if(!sharedPref.getBoolean("soundsOn", soundsOn)) {
            //This is taken care of in the soundsRadioGroup logic
        //soundsSeekBar.setEnabled(false);
        //soundsSeekBar.setClickable(false);
        }
        else
        {
            //setup variables to manage phone sound volume
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            volume = currentVolume;
            editor.putInt("Volume", volume);
            editor.apply();

            soundsSeekBar.setMax(maxVolume);
            soundsSeekBar.setProgress(volume);
            TextView soundsIndicatorText = (TextView) findViewById(R.id.soundsIndicatorText);
            soundsIndicatorText.setText("Volume: " + volume);

            soundsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int currentVolume, boolean b) {
                    //Toast.makeText(getApplicationContext(), "SeekBar Progress: " + i, Toast.LENGTH_SHORT).show();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    TextView soundsIndicatorText = (TextView) findViewById(R.id.soundsIndicatorText);
                    //soundNum = currentVolume;
                    soundsIndicatorText.setText("Volume: " + volume);
                    SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("Volume", volume);
                    editor.apply();

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }


    //Start game when Start button is clicked
    public void onStartClicked(View view){
        Intent intent = new Intent(this, GameActivity.class);
        //intent.putExtra("difficulty", diffNum);
        startActivity(intent);
    }


    //Go back to main screen
    public void onBackClicked(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Turn sounds on when On button is clicked
    /*public void onSoundsOnButtonClicked(View view){

        SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(!sharedPref.getBoolean("soundsOn", soundsOn )){
            soundsOn = true;
            editor.putBoolean("soundsOn", soundsOn);
            editor.apply();
            Toast.makeText(this, "Sounds have been turned on!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Sounds are already on!", Toast.LENGTH_SHORT).show();
        }
    }*/


    //Turn sound off when Off button is clicked
    /*public void onSoundsOffButtonClicked(View view){
        SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(sharedPref.getBoolean("soundsOn", soundsOn )){
            soundsOn = false;
            editor.putBoolean("soundsOn", soundsOn);
            editor.apply();
            Toast.makeText(this, "Sounds have been turned off!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Sounds are already off!", Toast.LENGTH_SHORT).show();
        }
    }*/


    //Reset high score when Reset button is clicked
    public void onResetButtonClick(View view){
        SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        currentHighScore = 0;
        editor.putInt("highScore", currentHighScore);
        editor.apply();
        highScoreText.setText("Best:" + sharedPref.getInt("highScore", currentHighScore));
        Toast.makeText(this, "High Score has been reset!", Toast.LENGTH_SHORT).show();

        //Troubleshooting code
        /*Log.i(TAG,"Best: " + sharedPref.getInt("highScore", currentHighScore));
        Log.i(TAG,"currentHighScore =  " + currentHighScore);
        Map sharedPrefMap = sharedPref.getAll();
        String sharedPrefString = sharedPrefMap.toString();
        Log.i(TAG, "sharePrefString: " + sharedPrefString);*/
    }




}
