package dambrero.androidsays;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.os.Bundle;

public class QuitGameIntentService extends IntentService {

    Bundle quitData;
    boolean quitButtonPressed = quitData.getBoolean("Quit");


    private static final String TAG ="ServiceMessage";


    public QuitGameIntentService() {
        super("QuitGameIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //This is what the service does
        Log.i(TAG, "The service has started");
        while(!quitButtonPressed){// while quit button is not pressed (i.e. the value is false)
            quitButtonPressed = quitData.getBoolean("Quit");// keep getting the boolean value until it's true
        }
        Intent intent1 = new Intent(this, MainActivity.class);
        startActivity(intent1);

    }

}

