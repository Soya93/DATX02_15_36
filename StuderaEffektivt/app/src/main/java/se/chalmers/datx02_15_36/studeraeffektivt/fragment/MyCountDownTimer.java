package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.widget.Toast;

import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;

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
    private DBAdapter dbAdapter;
    private String ccode;
    private long studyTimePassed=0;


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
        ccode= intent.getStringExtra("CCODE");
        dbAdapter = new DBAdapter(getBaseContext());
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
        if(count==0 ){
            insertIntoDataBase(studyTimePassed);
        }
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
                timePassed+=100;
                timeUntilFinished=millisUntilFinished;
                sendMessage((int)millisUntilFinished);
                if(count==0){
                    studyTimePassed+=100;

                }


            }

            @Override
            public void onFinish() {
                Vibrator vi = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                vi.vibrate(1000);
                sendMessage(0);
                if(timePassed<totalTime) {
                    count = (count + 1) % 2;

                    if (count == 1) {
                        studyTimer = timerFunction(pauseTime, 100);
                        sendMessage(pauseTime);
                        mHandler.sendEmptyMessage(1);
                    } else if (count == 0) {
                        studyTimer = timerFunction(studyTime, 100);
                        sendMessage(studyTime);
                        mHandler.sendEmptyMessage(2);
                    }
                    studyTimer.start();
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

    private void insertIntoDataBase(long millisPassed) {
        long inserted = dbAdapter.insertSession(ccode, milliSecondsToMin(millisPassed));
        if (inserted > 0 && getBaseContext() != null) {
            Toast toast = Toast.makeText(getBaseContext(), "Session:" + milliSecondsToMin(millisPassed)
                    + "minutes added to " + ccode, Toast.LENGTH_SHORT);
            toast.show();
        } else if (getBaseContext() != null) {
            Toast toast = Toast.makeText(getBaseContext(), "Failed to add a Session", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
    private int milliSecondsToMin(long milliSeconds) {
        return ((int) milliSeconds / 1000 / 60);
    }


}