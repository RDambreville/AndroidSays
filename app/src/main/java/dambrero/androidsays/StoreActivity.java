package dambrero.androidsays;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StoreActivity extends Activity {

    private ListView storeListView;
    ArrayList<String> mUpgradesAndPowerUps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        mUpgradesAndPowerUps = new ArrayList<>();
        mUpgradesAndPowerUps.add("Half Memory");
        mUpgradesAndPowerUps.add("First Gesture");
        mUpgradesAndPowerUps.add("Elephant Memory");
        mUpgradesAndPowerUps.add("Intern");
        mUpgradesAndPowerUps.add("Free Pass");
        mUpgradesAndPowerUps.add("Skip");

        ListAdapter adapter = new CustomAdapter(this, mUpgradesAndPowerUps);
        storeListView = (ListView) findViewById(R.id.storeListView);
        storeListView.setAdapter(adapter);
        storeListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String upgradeName = String.valueOf(adapterView.getItemAtPosition(position));
                Toast.makeText(StoreActivity.this, upgradeName, Toast.LENGTH_SHORT).show();
            }
        });

    }


private final class CustomAdapter extends ArrayAdapter<String>{
    private final LayoutInflater inflater;

    public CustomAdapter(Context context, ArrayList upgradesAndPowerUps){
        super(context, R.layout.custom_row, upgradesAndPowerUps);
        inflater = LayoutInflater.from(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = inflater.inflate(R.layout.custom_row, parent, false);

        String itemName = getItem(position);
        TextView itemNameText = (TextView) customView.findViewById(R.id.itemNameText);
        itemNameText.setText(itemName);
        //itemNameText.setTextColor(Color.WHITE);
        ImageView upgradeIcon = (ImageView) customView.findViewById(R.id.upgradeIcon);
        upgradeIcon.setImageResource(R.drawable.score);
        return customView;
    }
}



}
