package dambrero.androidsays;

import android.app.Fragment;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;


public class InstructionPagerAdapter extends PagerAdapter {

    Context context;

    ArrayList<String> listOfInstructions = new ArrayList<>();
    ArrayList<Fragment> listOfFragments = new ArrayList<>();
    LayoutInflater inflater;

    public InstructionPagerAdapter(Context context, ArrayList<String> listOfInstructions){
        this.context = context;
        this.listOfInstructions = listOfInstructions;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TextView instructionText;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.pager_item, container, false);

        instructionText = (TextView) itemView.findViewById(R.id.instructionText);
        instructionText.setText(listOfInstructions.get(position));

        // Add pager_item layout as the current page to the ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;

    }

    @Override
    public int getCount() {
        return listOfInstructions.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //Remove pager_item from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
}


