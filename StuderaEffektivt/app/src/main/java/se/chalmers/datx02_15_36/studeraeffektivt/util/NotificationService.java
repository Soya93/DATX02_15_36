package se.chalmers.datx02_15_36.studeraeffektivt.util;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.MainActivity;

/**
 * Created by SoyaPanda on 15-05-07.
 */
public class NotificationService extends IntentService {
    public static final String ACTION = "com.codepath.example.servicesdemo.MyTestService";


    //public static final String PARAM_IN_MSG = "imsg";
    //public static final String PARAM_OUT_MSG = "omsg";

    private NotificationCompat.Builder mBuilder;

        public NotificationService() {
            super("NotificationService");
        }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

        @Override
        protected void onHandleIntent(Intent intent) {

            //Fetch data from the intent
            ArrayList <String> messages = intent.getStringArrayListExtra("messages");
            boolean hasCourses = intent.getBooleanExtra("hascourses", false);
            int numOfCourses = intent.getIntExtra("numOfCourses", 0);
            ArrayList <String> courses = intent.getStringArrayListExtra("courses");


            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplication());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    getApplicationContext())
                    .setSmallIcon(R.drawable.ic_study_coach)
                    .setContentTitle("StudieCoach")
                    .setContentText("Ny studievecka");
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();

            if(!messages.isEmpty()){
                for(String text: messages)
                    inboxStyle.addLine(text);
            }

            if(hasCourses) {
                String[] coursesArray = new String[numOfCourses];
                int i = 0;
                for (String course : courses) {
                    coursesArray[i] = course;
                    i++;
                }

                mBuilder.addAction(R.drawable.ic_av_loop, "Ja", resultPendingIntent);
                Bundle bundle = new Bundle();
                bundle.putBoolean("Go_To_Cal", true);
                resultIntent.putExtras(bundle);
                resultIntent.setAction("Go_To_Cal");

                inboxStyle.addLine("Vill du skapa ett repetitionspass?");

                for (int j=0; i < coursesArray.length; j++) {
                    inboxStyle.addLine(coursesArray[j]);
                }
            } else {
                resultIntent.setAction("Go_To_Main");
                mBuilder.setStyle(inboxStyle);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());
            }

            // Test 2
           /* String val = intent.getStringExtra("foo");
            // Construct an Intent tying it to the ACTION (arbitrary event namespace)
            Intent in = new Intent(ACTION);
            // Put extras into the intent as usual
            in.putExtra("resultCode", Activity.RESULT_OK);
            in.putExtra("resultValue", "My Result Value. Passed in: " + val);
            // Fire the broadcast with intent packaged
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);
            // or sendBroadcast(in) for a normal broadcast;
            /*Log.d("NotificationService", "Service Started!");*/
        }
    }

