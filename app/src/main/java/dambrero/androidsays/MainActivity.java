package dambrero.androidsays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

public class MainActivity extends Activity {

    int diffNum = 0; // initial difficulty number
    int currentHighScore;
    boolean soundsOn;
    boolean vibrationsOn;
    String difficulty;

    private TextView highScoreText;
    private TextView difficultyText;
    private TextView soundsText;
    private TextView vibrationsText;

    private static String TAG = "SavedScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putInt("highScore", currentHighScore);

        soundsText = (TextView) findViewById(R.id.soundsText);

        if(sharedPref.getBoolean("soundsOn", soundsOn)){
            soundsText.setText("Sounds: On");
        }
        else{
            soundsText.setText("Sounds: Off");
        }

        //editor.putBoolean("soundsOn", true);
        //editor.putString("difficulty", difficulty);

        Map sharedPrefMap = sharedPref.getAll();
        String sharedPrefString = sharedPrefMap.toString();
        Log.i(TAG, "sharePrefString: " + sharedPrefString);

        //Log.i(TAG, "sharedPref.getString = " + sharedPref.getString("difficulty", difficulty));
        if(sharedPref.getString("difficulty", difficulty) == null){
            difficulty = "Novice";
            editor.putString("difficulty", difficulty);
            editor.apply();
        }
        else{
        }

        //Log.i(TAG, "Best: " + sharedPref.getInt("highScore", currentHighScore));


        if (sharedPref.getInt("highScore", currentHighScore) == -1){// if the game has never been played and no high score has been generated
            currentHighScore = 0; // set the high score to 0
            editor.putInt("highScore", currentHighScore); // store that score in the SharedPreference file
            editor.apply();// apply the changes
        }
        else // otherwise if the game has been played and a high score has already been generated
        {

            currentHighScore = sharedPref.getInt("highScore", currentHighScore); // set the current high score equal to that generated score
            editor.putInt("highScore", currentHighScore);// store the current high score in the shared preferences file
            editor.apply();// apply the changes

        }


        vibrationsText = (TextView) findViewById(R.id.vibrationsText);
        if (sharedPref.getBoolean("vibrationsOn", vibrationsOn)){
            vibrationsText.setText("Vibrations: On");
        }
        else{
            vibrationsText.setText("Vibrations: Off");
        }

        highScoreText = (TextView) findViewById(R.id.highScoreText);
        difficultyText = (TextView) findViewById(R.id.difficultyText);

        highScoreText.setText("Best: " + sharedPref.getInt("highScore", currentHighScore));
        difficultyText.setText("Level: " + sharedPref.getString("difficulty", difficulty));


    }

    public void onClickStart(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", diffNum); // start game at easiest level by default
        startActivity(intent);
    }


    public void onClickInstructions(View view){
        Intent intent = new Intent(this, InstructionsActivity.class);
        startActivity(intent);
    }

    public void onClickSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onClickStore(View view){
        Intent intent = new Intent(this, StoreActivity.class);
        startActivity(intent);
    }

}
