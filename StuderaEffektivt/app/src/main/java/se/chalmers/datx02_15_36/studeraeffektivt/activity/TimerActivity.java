package se.chalmers.datx02_15_36.studeraeffektivt.activity;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.service.MyCountDownTimer;


public class TimerActivity extends Fragment {


    private TimePicker t1;

    protected CountDownTimer studyTimer;
    protected CountDownTimer pauseTimer;

    private Button startButton;
    private Button resetButton;

    private long chosenSeconds;
    private long secondsUntilFinished;

    protected long timePassed;
    protected long default_TotalTime =(60*60*1000);
    protected long default_StudyTime = (35*60*1000);
    protected long default_PauseTime = (25*60*1000);
    protected long default_NumberOfPauses = 1;
    protected long calculatedStudyTime;

    private final long update_Time=1000;
    private TextView textView;

    private boolean studyTimerIsRunning = false;
    private boolean pauseTimerIsRunning = false;

    private View rootView;
    private TextView inputText;
    private TextView pausLengthInput;
    private TextView nbrOfPausesInput;


    private String inputTime;
    private String nbrOfPauses;
    private String pausLength;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        rootView= inflater.inflate(R.layout.activity_timer,container,false);


        instantiate();


        return rootView; }



    private void instantiate() {
        resetButton = (Button) rootView.findViewById(R.id.button_reset);
        startButton = (Button) rootView.findViewById(R.id.button_start_timer);
        textView = (TextView) rootView.findViewById(R.id.text_timer);


    }

    private int secondsToHour(long millisUntilFinished) {
        return (int) (millisUntilFinished / 1000 / 3600);
    }

    private int secondsToMin(long millisUntilFinished) {
        return ((int) ((millisUntilFinished / 1000 % 3600)) + 60) / 60;
    }

    private long minToMilliSeconds (int parsedTime){
        return ((long) parsedTime*60*1000);
    }




private void calculateStudySession() {

    long temp = (default_TotalTime - (default_NumberOfPauses * default_PauseTime));
    this.default_StudyTime = temp / (default_NumberOfPauses + 1);
}
/**
 * Set the timer.
 */
    public CountDownTimer studyTimerFunction(long millisInFuture, long countDownInterval) {

        studyTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                studyTimerIsRunning=true;

                textView.setText((millisUntilFinished / 1000)/60 + ":" + (millisUntilFinished / 1000)%60);


                secondsUntilFinished = millisUntilFinished;
                timePassed += 100;
            }

            @Override
            public void onFinish() {
                studyTimerIsRunning = false;
                if(timePassed<default_TotalTime) {
                    pauseTimerFunction(default_PauseTime, update_Time);
                    pauseTimer.start();
                }

            }
        };
        return studyTimer;

    }
    public CountDownTimer pauseTimerFunction(long millisInFuture, long countDownInterval) {

        pauseTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                pauseTimerIsRunning=true;

                textView.setText("pause seconds remaining: " + millisUntilFinished / 1000);

                secondsUntilFinished = millisUntilFinished;
                timePassed += 100;
            }

            @Override
            public void onFinish() {
                pauseTimerIsRunning = false;
                if(timePassed<default_TotalTime) {
                    studyTimerFunction(default_StudyTime, update_Time);
                    studyTimer.start();
                }

            }
        };
        return pauseTimer;

    }



    public void startTimer() {
        /*
        if(inputTime != null) {
            default_StudyTime = Long.valueOf(inputTime).longValue() * 60 * 1000;
        }*/
        if(!studyTimerIsRunning) {
            studyTimerFunction(default_StudyTime, 100);
            studyTimer.start();
            startButton.setText("Pause");
        }
        else if (startButton.getText().equals("Pause")){
            studyTimer.cancel();
            startButton.setText("Resume");}
            else if (startButton.getText().equals("Resume")){
              handleTimeFromService(timePassed);
             startButton.setText("Pause");
            }
        }


    protected void handleTimeFromService(long timeFromService){
        long temp = 0;
        this.timePassed=timeFromService;
        Log.d("timeFromService", "value" + timeFromService);
        boolean isItStudy = true;
                while (temp<timeFromService){
                    if(isItStudy){
                        temp+=default_StudyTime;
                        isItStudy=false;
                    }
                    else{
                        temp+=default_PauseTime;
                        isItStudy=true;
                    }
                }
        Log.d("isItStudy", "value" + isItStudy);
        temp=temp-timeFromService;
        Log.d("temp", "value" + temp);
        if(isItStudy){
            pauseTimerFunction(temp,update_Time);
            pauseTimer.start();
        }
        else{
            studyTimerFunction(temp,update_Time);
            studyTimer.start();

        }



    }



    public void resetTimer() {
        cancelOneOfTimers();

    }

    protected void cancelOneOfTimers() {
        if(studyTimerIsRunning){
            studyTimer.cancel();
            //startButton.setText("Resume");
        }
        else if (pauseTimerIsRunning){
            pauseTimer.cancel();
        }
    }

    public void settingsTimer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.time_picker_dialog, null);
        builder.setView(dialogView);

        builder.setPositiveButton("Nästa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {


                inputText = (TextView) dialogView.findViewById(R.id.inputTime);
                inputTime = inputText.getText().toString();
                nextDialog();

            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing
            }
        });

/*
        //inputText.setText("0h 00m 00s");
*/

        //the alternatives
        String [] alternatives = {"0", "1"}; // "2" //, "3", "4", "5", "6", "7", "8", "9"};
        builder.setTitle("Ställ in tid")
                .setItems(alternatives, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                inputText.setText("delete");
                                break;
                            case 1:
                                inputText.setText("next");
                                break;
                        }
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void nextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.time_picker_dialog2, null);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                nbrOfPausesInput = (TextView) dialogView.findViewById(R.id.nbrOfPausesInt);
                nbrOfPauses = nbrOfPausesInput.getText().toString();

                Log.d("Number of pauses", nbrOfPauses);

                pausLengthInput = (TextView) dialogView.findViewById(R.id.pausLengthInt);
                pausLength = pausLengthInput.getText().toString();
                textView.setText(inputTime + ":00");
                Log.d("PauseLengt", pausLength);
                parseFromDialog();

            }
        });

        builder.setNegativeButton("Avbryt",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void parseFromDialog () {
        try{
           int timeFromDialog= Integer.parseInt(inputTime);
            default_TotalTime = minToMilliSeconds(timeFromDialog);
            int pauseFromDialog = Integer.parseInt(pausLength);
            default_PauseTime = minToMilliSeconds(pauseFromDialog);
            int numberOfPauses = Integer.parseInt(nbrOfPauses);
            default_NumberOfPauses = (long)numberOfPauses;
            calculateStudySession();

        }
        catch(Throwable e) {
            e.printStackTrace();
        }


    }



}

