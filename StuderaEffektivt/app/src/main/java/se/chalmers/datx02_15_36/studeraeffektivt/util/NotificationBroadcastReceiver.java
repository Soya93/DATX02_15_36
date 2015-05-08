package se.chalmers.datx02_15_36.studeraeffektivt.util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.MainActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;

/**
 * Created by SoyaPanda on 15-05-07.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver
{

    public static final String ACTION_RESP =
            "com.mamlambo.intent.action.MESSAGE_PROCESSED";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Test 2
        int resultCode = intent.getIntExtra("resultCode", Activity.RESULT_CANCELED);
        if (resultCode == Activity.RESULT_OK) {
            String resultValue = intent.getStringExtra("resultValue");
            Log.i("OnRecieve in Broadcast", resultValue);
            Toast.makeText(context, resultValue, Toast.LENGTH_SHORT).show();
        }
    }
}
