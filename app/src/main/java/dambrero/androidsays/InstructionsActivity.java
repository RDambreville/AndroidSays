package dambrero.androidsays;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;


public class InstructionsActivity extends Activity {

    int diffNum = 0; // initial difficulty value

    ViewPager viewPager;
    PagerAdapter adapter;

    ArrayList<String> listOfInstructions = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        Resources resource = getResources();
        listOfInstructions.clear();
        listOfInstructions.trimToSize();
        listOfInstructions.add(resource.getString(R.string.page1));
        listOfInstructions.add(resource.getString(R.string.page2));

        viewPager = (ViewPager)findViewById(R.id.pager);
        adapter = new InstructionPagerAdapter(InstructionsActivity.this, listOfInstructions);
        viewPager.setAdapter(adapter);

    }


    public void onBackButtonClicked(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onStartButtonClicked(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", diffNum);
        startActivity(intent);
    }
}

