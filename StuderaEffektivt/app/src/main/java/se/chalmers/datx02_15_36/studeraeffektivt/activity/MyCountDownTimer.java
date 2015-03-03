package se.chalmers.datx02_15_36.studeraeffektivt.activity;

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
    private final int update_Time = 1000;
    private long studyTime;
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
        startCountDown();

        return START_NOT_STICKY;}

    public long returnStudyTime() {
        return this.studyTime;
    }

    public void onDestroy () {

        super.onDestroy();
        if(timer != null)
            timer.cancel();
        stopSelf();




    }

    private void startCountDown () {

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                studyTime--;
                Log.d("timer service", "counting" + studyTime);
                if(studyTime <=0){
                    onDestroy();
                }


            }
        },0,update_Time);    }


}