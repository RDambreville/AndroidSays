package dambrero.androidsays;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Binder;
import android.util.Log;

public class QuitService extends Service {

    private static final String TAG = "ServiceMessage";

    private final IBinder binder = new MyLocalBinder();

    Bundle quitData;
    boolean quitButtonPressed = quitData.getBoolean("Quit");

    public QuitService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand method called");

        Runnable r = new Runnable(){
            @Override
            public void run() {
                while(!quitButtonPressed){
                    quitButtonPressed = quitData.getBoolean("Quit");
                }
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
            }
        };

        Thread quit = new Thread(r);
        quit.start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy method called");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
       return binder;
    }


    public class MyLocalBinder extends Binder{
        QuitService getService(){
            return QuitService.this;
        }

    }







}
