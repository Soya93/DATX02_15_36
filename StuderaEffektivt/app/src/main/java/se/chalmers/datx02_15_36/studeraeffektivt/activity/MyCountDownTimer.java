package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
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

    private final int PAUSE = 0;
    private final int RESUME = 1;
    private final int STOP = 2;


    private long studyTime,totalTime,pauseTime;
    private long timeUntilFinished;
    private int timePassed=0;
    private int count=0;

    private Bundle bundle = new Bundle();
    private final IBinder iBinder = new MCDTBinder();
    private Handler mHandler;
    private CountDownTimer studyTimer;


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
        totalTime = intent.getLongExtra("TOTAL_TIME", 100*1000);
        startCountDown();
        return START_NOT_STICKY;
    }

    public void setHandler (Handler handler){
         this.mHandler = handler;
    }

   public void startCountDown() {
       timerFunction(studyTime,100);
       studyTimer.start();

   }

    public Handler getServiceHandler () {
        return this.handler;
    }

    public void onDestroy () {
        super.onDestroy();
        studyTimer.cancel();

    }

    private void sendMessage(long countDownTime) {
        Message msg = mHandler.obtainMessage();
        bundle.putLong("timePassed", countDownTime);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    public CountDownTimer timerFunction(long millisInFuture, long countDownInterval) {

        studyTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                timeUntilFinished=millisUntilFinished;
                sendMessage((int)millisUntilFinished);


            }

            @Override
            public void onFinish() {
                Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                vi.vibrate(1000);
                sendMessage(0);
                count = (count + 1) % 2;

                if (count == 1) {
                    studyTimer = timerFunction(pauseTime, 100);
                    sendMessage(pauseTime);
                    mHandler.sendEmptyMessage(1);
                } else if (count == 0) {
                   studyTimer = timerFunction(studyTime,100);
                    sendMessage(studyTime);
                    mHandler.sendEmptyMessage(2);
                }
                studyTimer.start();

            }


        };
        return studyTimer;

    }


    public void pauseTimer() {
        studyTimer.cancel();
        mHandler.removeMessages(0);
    }


    public void resumeTimer() {
        timerFunction(timeUntilFinished,100);
        studyTimer.start();

    }


}