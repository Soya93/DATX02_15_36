package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.MainActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Utils;

/**
 * Created by alexandraback on 24/02/15.
 * This service is supposed to run in background och count down time when user
 * is not in acitivy.
 */
public class MyCountDownTimer extends Service {

    private final int PAUSE = 0;
    private final int RESUME = 1;
    private final int STOP = 2;
    private final int ACTIVITY_NOT_RUNNING = 3;



    private long studyTime,pauseTime;
    private int reps;
    private long timeUntilFinished;
    private int totalcount=0;
    private int count=0;
    private int startId;

    private Bundle bundle = new Bundle();
    private final IBinder iBinder = new MCDTBinder();
    private Handler mHandler;
    private CountDownTimer studyTimer;
    private DBAdapter dbAdapter;
    private String ccode;
    private long studyTimePassed=0;
    private boolean activityIsRunning = true;

    private Utils utils;

    private  Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case PAUSE:
                       pauseTimer();
                    break;
                case RESUME:
                        resumeTimer();
                    break;
                case STOP:
                    onDestroy();
                    break;
                case ACTIVITY_NOT_RUNNING:
                    activityIsRunning = false;
                    break;
            }

        }
    };

    public class MCDTBinder extends Binder {
        MyCountDownTimer getService(){
            return  MyCountDownTimer.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        studyTime = intent.getLongExtra("TIME_STUDY", 30*1000);
        pauseTime = intent.getLongExtra("TIME_PAUSE",15*1000);
        reps= intent.getIntExtra("REPS",1);
        Log.d("Values of reps ", String.valueOf(reps));
        ccode= intent.getStringExtra("CCODE");
        this.startId = startId;
        dbAdapter = new DBAdapter(getBaseContext());
        utils = new Utils();
        startCountDown();
        return START_NOT_STICKY;

    }

    public void setHandler (Handler handler){
         this.mHandler = handler;
    }

   public void startCountDown() {
       timerFunction(studyTime,50);
       studyTimer.start();
   }

    public Handler getServiceHandler () {
        return this.handler;
    }

    public void onDestroy () {

        if(count==0 ){
            insertIntoDataBase(studyTimePassed);
        }
        studyTimer.cancel();
        super.onDestroy();
    }

    private void sendMessage(long countDownTime) {
        Message msg = mHandler.obtainMessage();
        bundle.putLong("timePassed", countDownTime);
        bundle.putInt("Phace", count);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    public CountDownTimer timerFunction(final long millisInFuture, long countDownInterval) {

        studyTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                timeUntilFinished=millisUntilFinished; // if user wants to pause timer;
                if(count==0){
                    studyTimePassed+=50;

                }
                if(activityIsRunning){
                sendMessage((int)millisUntilFinished);}


                Log.d("Time utilfinished" , String.valueOf(millisUntilFinished/1000));
                Log.d("Values of reps ", String.valueOf(reps));
            }

            @Override
            // här ska du in
            public void onFinish() {
                Log.d("Values of totC", String.valueOf(totalcount));
                Log.d("Value of reps", String.valueOf(reps));
               // Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                //vi.vibrate(2000);
                //sendMessage(0);
                if(totalcount<reps) {
                    count = (count + 1) % 2;

                    if (count == 1) { // här  vill du att en notification att studietiden är slut
                        totalcount ++;
                        if(ccode!= null)
                            Log.d("onfinish", studyTime+"");
                        insertIntoDataBase(studyTime);
                        studyTimePassed = 0;
                        studyTimer = timerFunction(pauseTime, 50);
                        showPauseNotification();

                    } else if (count == 0) {  // här  vill du att en notification att pausetiden är slut
                        studyTimer = timerFunction(studyTime, 50);
                        showStudyNotification();
                    }
                    studyTimer.start();
                }
                else {
                    if(activityIsRunning){
                        mHandler.sendEmptyMessage(1);
                    }
                    stopSelf();
                }

            }


        };
        return studyTimer;

    }


    public void pauseTimer() {
        if(count == 0 && ccode!=null){
          insertIntoDataBase(studyTimePassed);
            studyTimePassed=0;
        }
        studyTimer.cancel();
        mHandler.removeMessages(0);
    }


    public void resumeTimer() {
        timerFunction(timeUntilFinished,100);
        studyTimer.start();

    }

    public void setActivityIsRunning(){
        this.activityIsRunning = true;
    }


    private void insertIntoDataBase(long millisPassed) {
        Log.d(ccode, "ccode");
        millisPassed = millisPassed * 60; ///TODO: This is to make it in hours instead of minutes, for the show ;D
        Log.d("databas", "millispassed: "+ milliSecondsToMin(millisPassed));

        long inserted = dbAdapter.insertSession(ccode, utils.getCurrWeekNumber(), milliSecondsToMin(millisPassed));
        if (inserted > 0 && getBaseContext() != null) {

            Cursor sessions = dbAdapter.getSessions(utils.getCurrWeekNumber());
            while (sessions.moveToNext()){
                String timestamp = sessions.getString(sessions.getColumnIndex("timestamp"));
                int minutes = sessions.getInt(sessions.getColumnIndex("minutes"));
                String course = sessions.getString(sessions.getColumnIndex("_ccode"));
                Log.d("timer", "session added: "+timestamp);
                Log.d("timer", "session minutes: "+minutes);
                Log.d("timer", "session course: "+course);
            }

            //Toast toast = Toast.makeText(getBaseContext(), "Added session!", Toast.LENGTH_SHORT);
            //toast.show();
        } else if (getBaseContext() != null) {
            Toast toast = Toast.makeText(getBaseContext(), "Failed to add a Session", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
    private int milliSecondsToMin(long milliSeconds) {
        return ((int) milliSeconds / 1000 / 60);
    }



    private void showPauseNotification() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplication());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this)
                .setSmallIcon(R.drawable.ic_study_coach)
                .setContentTitle("StudieCoach")
                .setContentText("Dags för paus!"
                );

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        mBuilder.setStyle(inboxStyle);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());


    }

    private void showStudyNotification(){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);

        Notification noti = new Notification.Builder(getBaseContext())
                .setContentTitle("StudieCoach")
                .setContentText("Dags att plugga").setSmallIcon(R.drawable.ic_timer)
                .setContentIntent(pIntent).build();
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplication());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

    }


}