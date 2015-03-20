package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class TimerFrag extends Fragment {

    protected CountDownTimer studyTimer;
    protected CountDownTimer pauseTimer;

    private Button startButton;
    private Button resetButton;

    private long chosenSeconds;
    private long secondsUntilFinished;

    protected long timePassed;
    protected long default_TotalTime = (30 * 60 * 1000);
    protected long default_StudyTime;
    protected long default_PauseTime = (5 * 60 * 1000);
    protected long default_NumberOfPauses = 1;


    private final long update_Time = 1000;
    private TextView textView;

    protected boolean studyTimerIsRunning = false;
    protected boolean pauseTimerIsRunning = false;

    private View rootView;
    private TextView inputText;
    private TextView pausLengthInput;
    private TextView nbrOfPausesInput;


    private String inputTime;
    private String nbrOfPauses;
    private String pausLength;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_timer, container, false);
        instantiate();
        return rootView;
    }


    private void instantiate() {
        resetButton = (Button) rootView.findViewById(R.id.button_reset);
        startButton = (Button) rootView.findViewById(R.id.button_start_timer);
        textView = (TextView) rootView.findViewById(R.id.text_timer);

    }

    private long minToMilliSeconds(int parsedTime) {
        return ((long) parsedTime * 60 * 1000);
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
                studyTimerIsRunning = true;

                textView.setText("Plugga " + (millisUntilFinished / 1000) / 60 + ":" + (millisUntilFinished / 1000) % 60);


                secondsUntilFinished = millisUntilFinished;
                timePassed += 100;
            }

            @Override
            public void onFinish() {
                studyTimerIsRunning = false;
                if (timePassed < default_TotalTime) {
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
                pauseTimerIsRunning = true;

                textView.setText("Paus " + (millisUntilFinished / 1000) / 60 + ":" + (millisUntilFinished / 1000) % 60);
                secondsUntilFinished = millisUntilFinished;
                timePassed += 100;
            }

            @Override
            public void onFinish() {
                pauseTimerIsRunning = false;
                if (timePassed < default_TotalTime) {
                    studyTimerFunction(default_StudyTime, update_Time);
                    studyTimer.start();
                }

            }
        };
        return pauseTimer;

    }


    public void startTimer() {
        if (startButton.getText().equals("Starta Timer")) {
            calculateStudySession();
            studyTimerFunction(default_StudyTime, 100);
            studyTimer.start();
            startButton.setText("Paus");
        } else if (startButton.getText().equals("Paus")) {
            cancelOneOfTimers();
            startButton.setText("Återuppta");
        } else if (startButton.getText().equals("Återuppta")) {
            handleTimeFromService(timePassed);
            startButton.setText("Paus");
        }
    }


    protected void handleTimeFromService(long timeFromService) {
        long temp = 0;
        this.timePassed = timeFromService;
        Log.d("timeFromService", "value" + timeFromService);
        boolean isItStudy = true;
        while (temp < timeFromService) {
            if (isItStudy) {
                temp += default_StudyTime;
                isItStudy = false;
            } else {
                temp += default_PauseTime;
                isItStudy = true;
            }
        }
        Log.d("isItStudy", "value" + isItStudy);
        temp = temp - timeFromService;
        Log.d("temp", "value" + temp);
        if (isItStudy) {
            pauseTimerFunction(temp, update_Time);
            pauseTimer.start();
        } else {
            studyTimerFunction(temp, update_Time);
            studyTimer.start();

        }


    }


    public void resetTimer() {
        cancelOneOfTimers();
        startButton.setText("Starta Timer");
        textView.setText("Studera " + (default_StudyTime / 1000) / 60 + ":" + (default_StudyTime / 1000) % 60);

    }

    protected void cancelOneOfTimers() {
        if (studyTimerIsRunning) {
            studyTimer.cancel();
            studyTimerIsRunning = false;
        } else if (pauseTimerIsRunning) {
            pauseTimer.cancel();
            pauseTimerIsRunning = true;
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
                resetTimer();


            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void parseFromDialog() {
        try {
            int timeFromDialog = Integer.parseInt(inputTime);
            default_TotalTime = minToMilliSeconds(timeFromDialog);
            int pauseFromDialog = Integer.parseInt(pausLength);
            default_PauseTime = minToMilliSeconds(pauseFromDialog);
            int numberOfPauses = Integer.parseInt(nbrOfPauses);
            default_NumberOfPauses = (long) numberOfPauses;
            calculateStudySession();

        } catch (Throwable e) {
            e.printStackTrace();
        }


    }


}

