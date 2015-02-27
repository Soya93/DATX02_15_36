package se.chalmers.datx02_15_36.studeraeffektivt;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by alexandraback on 27/02/15.
 */
public class MyReceiver extends BroadcastReceiver {

    public void onReceive(Context ctx, Intent i){
            long remaining = i.getLongExtra("TIME_LEFT",-1);
            Log.d("BROADCAST", "It worked");
    }
}
