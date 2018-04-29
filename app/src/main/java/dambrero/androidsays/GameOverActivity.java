package dambrero.androidsays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class GameOverActivity extends Activity {

    int diffNum = 0;
    int finalScore;
    int longestStreak;
    int currentHighScore;
    ArrayList<String> demandedGesturesList = new ArrayList<>();
    ArrayList<String> performedGesturesList = new ArrayList<>();

    ListView correctList;
    ListView performedList;

    //boolean isFirstGame = true;

    //int defaultHighScore = sharedPref.getInt("highScore", -1);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_over);
        TextView endGameMessage = (TextView) findViewById(R.id.endGameMessage);
        TextView scoreText = (TextView) findViewById(R.id.gameOverScore);
        //TextView longestStreakText = (TextView) findViewById(R.id.longestStreak);
        //TextView levelText = (TextView) findViewById(R.id.level);




         SharedPreferences sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPref.edit();

        //editor.putBoolean("isFirstGame", false);

        Bundle scoreData = getIntent().getExtras();
        finalScore = scoreData.getInt("score");

        //Bundle streakData = getIntent().getExtras();
        //longestStreak = streakData.getInt("streak");

        Bundle diffData = getIntent().getExtras();
        diffNum = diffData.getInt("difficulty");

        Bundle demandedGestures = getIntent().getExtras();
        demandedGesturesList = demandedGestures.getStringArrayList("correctGestureList");

        Bundle performedGestures = getIntent().getExtras();
        performedGesturesList = performedGestures.getStringArrayList("performedGestureList");

        correctList = (ListView)findViewById(R.id.correctList);
        performedList = (ListView)findViewById(R.id.performedList);

        ListAdapter adapter1 = new CustomAdapter(this, demandedGesturesList);
        correctList.setAdapter(adapter1);

        ListAdapter adapter2 = new CustomAdapter(this, performedGesturesList);
        performedList.setAdapter(adapter2);



        if (scoreData == null) {
            return;
        }
        else
        {
            if (currentHighScore == -1){
                currentHighScore = finalScore;
                editor.putInt("highScore", currentHighScore);
                editor.apply();
                endGameMessage.setText("New High Score!");
            }
            else {
                if (finalScore > sharedPref.getInt("highScore",currentHighScore)) {
                    currentHighScore = finalScore;
                    editor.putInt("highScore", currentHighScore);
                    editor.apply();
                    endGameMessage.setText("New High Score!");
                } else {
                    endGameMessage.setText("Good game!");
                }

            }

            scoreText.setText("Score: " + finalScore);
            //longestStreakText.setText("Longest Streak: " + longestStreak);
            //levelText.setText("Level: ");
        }
    }


    public void onQuitButtonClicked(View view){
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("highScore", sharedPref.getInt("highScore", currentHighScore));
        startActivity(intent);

    }

    public void onRetryButtonClicked(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", diffNum);
        startActivity(intent);
    }






    private final class CustomAdapter extends ArrayAdapter<String> {
        private final LayoutInflater inflater;

        public CustomAdapter(Context context, ArrayList gestures){
            super(context, R.layout.custom_row2, gestures);
            inflater = LayoutInflater.from(getContext());
        }

       /* public void changeColorScheme(View listItem){

        }*/

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String gestureName = getItem(position);
            View row = convertView;
            MyViewHolder holder = null;

            if (row == null){
                row = inflater.inflate(R.layout.custom_row2, parent, false);
                holder = new MyViewHolder(row);
                row.setTag(holder);
            }else{
                holder = (MyViewHolder) row.getTag();
            }

            holder.gestureName.setText(getItem(position));
            holder.numberText.setText("" + (position + 1));


            // WRONG way to compare strings: if (gestureName == demandedGesturesList.get(position))
            // This statement tests to see if gestureName is the exact same object as demandedGesturesList.get(position),
            // which it is not!
            // We want to see if the two strings have the same VALUE. We don't care if they're different objects
            // Use the .equals() method instead of ==
            if(gestureName.equals(demandedGesturesList.get(position))){
                row.setBackgroundColor(Color.GREEN);
                //row.setBackground(getDrawable(R.drawable.green_gradient_background));
                holder.gestureName.setTextColor(Color.BLACK);
                holder.numberText.setTextColor(Color.BLACK);
            }else{
                row.setBackgroundColor(Color.RED);
                //row.setBackground(getDrawable(R.drawable.red_gradient_background));
                holder.gestureName.setTextColor(Color.WHITE);
                holder.numberText.setTextColor(Color.WHITE);
            }



            /*String gestureName = getItem(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.custom_row2, parent, false);
                TextView itemNameText = (TextView) convertView.findViewById(R.id.gestureName);
                TextView numberText = (TextView) convertView.findViewById(R.id.numberText);
                itemNameText.setText(gestureName);
                numberText.setText("" + (position + 1));
            }else{
                TextView itemNameText = (TextView) convertView.findViewById(R.id.gestureName);
                TextView numberText = (TextView) convertView.findViewById(R.id.numberText);
                itemNameText.setText(gestureName);
                numberText.setText("" + (position + 1));
            }*/


            /*if(gestureName == demandedGesturesList.get(position)){
                customView.setBackgroundColor(Color.GREEN);
                itemNameText.setTextColor(Color.BLACK);
                numberText.setTextColor(Color.BLACK);
            }else{
                customView.setBackgroundColor(Color.RED);
                itemNameText.setTextColor(Color.WHITE);
                numberText.setTextColor(Color.WHITE);
            }*/
            //itemNameText.setTextColor(Color.WHITE);
            //ImageView upgradeIcon = (ImageView) customView.findViewById(R.id.upgradeIcon);
            //upgradeIcon.setImageResource(R.drawable.score);
            return row;
        }
    }


    private class MyViewHolder{

        TextView numberText;
        TextView gestureName;

        MyViewHolder(View view){
            numberText = (TextView) view.findViewById(R.id.numberText);
            gestureName = (TextView) view.findViewById(R.id.gestureName);
        }

    }



}


