package se.chalmers.datx02_15_36.studeraeffektivt;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by alexandraback on 24/02/15.
 * This service is supposed to run in background och count down time when user
 * is not in acitivy.
 */
public class MyCountDownTimer extends Service {
    private Timer  timer = new Timer ();
    private TimerTask timerTask;
    private final int update_Time = 1000;
    private long studyTime;
    private Intent broadCastIntent;
    private final IBinder iBinder = new MCDTBinder();

    public class MCDTBinder extends Binder {
        MyCountDownTimer getService(){
          return  MyCountDownTimer.this;
        }
    }

      public IBinder onBind(Intent intent) {
          return iBinder;
      }

    public int onStartCommand(Intent intent, int flags, int startId) {

       studyTime = intent.getLongExtra("STUDY_TIME",100);
        broadCastIntent = new Intent();
        intent.setAction("TIME_LEFT_SERVICE_ACTION");

        startCountDown();

    return START_STICKY;}

    public long returnRemainingTime() {
        return this.studyTime;
    }

    public void onDestroy () {



        super.onDestroy();
        if(timer != null)
                    timer.cancel();



    }

    private void startCountDown () {

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
            studyTime--;
                  sendData();


            }
        },0,update_Time);    }

     private void sendData () {
                  broadCastIntent.putExtra("TIME_LEFT",studyTime);
                  sendBroadcast(broadCastIntent);
                  Log.d("SENDING SERVICE", Long.toString(studyTime));
    }


}
