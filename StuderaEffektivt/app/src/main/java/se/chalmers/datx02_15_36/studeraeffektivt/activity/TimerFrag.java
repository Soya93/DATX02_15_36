package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;


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


    private final long update_Time = 100;
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
    private String buttonText = "Starta Timer";
    private String ccode;
    private String textViewText;

    private Bundle b;
    private Spinner spinner;


    private DBAdapter dbAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_timer, container, false);
        if (getActivity() != null) {
            dbAdapter = new DBAdapter(getActivity());
        }
        instantiate();


        return rootView;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (b != null) {
            textView = (TextView) rootView.findViewById(R.id.text_timer);
            String text = b.getString("buttonText");
            startButton.setText(text);
            String textV = b.getString("textViewText");
            textView.setText(textV);

        }
    }


    public void onStart() {
        super.onStart();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedCourse();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void instantiate() {
        resetButton = (Button) rootView.findViewById(R.id.button_reset);
        startButton = (Button) rootView.findViewById(R.id.button_start_timer);
        textView = (TextView) rootView.findViewById(R.id.text_timer);
        spinner = (Spinner) rootView.findViewById(R.id.spinner_timer);
        setCourses();

    }

    private void setCourses() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Cursor cursor = dbAdapter.getCourses();
        int cnameColumn = cursor.getColumnIndex("cname");
        int ccodeColumn = cursor.getColumnIndex("_ccode");
        while (cursor.moveToNext()) {
            String ccode = cursor.getString(ccodeColumn);
            String cname = cursor.getString(cnameColumn);
            adapter.add(ccode + "-" + cname);
        }

    }

    private long minToMilliSeconds(int parsedTime) {
        return ((long) parsedTime * 60 * 1000);
    }


    private void calculateStudySession() {
        long temp = (default_TotalTime - (default_NumberOfPauses * default_PauseTime));
        this.default_StudyTime = temp / (default_NumberOfPauses + 1);
    }

    public void setSelectedCourse() {
        String temp = spinner.getSelectedItem().toString();
        String[] parts = temp.split("-");
        this.ccode = parts[0];
        Log.d("selected course", ccode);

    }

    private int milliSecondsToMin(long milliSeconds) {
        return ((int) milliSeconds / 1000 / 60);
    }


    /**
     * Set the timer.
     */
    public CountDownTimer studyTimerFunction(long millisInFuture, long countDownInterval) {

        studyTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                studyTimerIsRunning = true;
                textViewText = ("Plugga " + (millisUntilFinished / 1000) / 60 + ":" + (millisUntilFinished / 1000) % 60);
                textView.setText(textViewText);
                secondsUntilFinished = millisUntilFinished;
                timePassed += 100;
            }

            @Override
            public void onFinish() {
                studyTimerIsRunning = false;
                //Log session into database.
                long inserted = dbAdapter.insertSession(ccode, milliSecondsToMin(default_StudyTime));
                if (inserted > 0 && getActivity() != null) {
                    Toast toast = Toast.makeText(getActivity(), "Session:" + milliSecondsToMin(default_StudyTime)
                            + "minutes added to " + ccode, Toast.LENGTH_SHORT);
                    toast.show();
                } else if (getActivity() != null) {
                    Toast toast = Toast.makeText(getActivity(), "Failed to add a Session", Toast.LENGTH_SHORT);
                    toast.show();
                }

                //Start pausetimer if time left.
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
            buttonText = "Paus";
            startButton.setText("Paus");
        } else if (startButton.getText().equals("Paus")) {
            cancelOneOfTimers();
            startButton.setText("Återuppta");
            buttonText = "Återuppta";
        } else if (startButton.getText().equals("Återuppta")) {
            handleTimeFromService(timePassed);
            startButton.setText("Paus");
            buttonText = "Paus";
        }
    }


    protected void handleTimeFromService(long timeFromService) {
        this.timePassed = timeFromService;
        long countOut = 0;
        boolean lastWasStudy = true;
        while (countOut <= timeFromService) {
            if (lastWasStudy) {
                countOut += default_StudyTime;
                lastWasStudy = false;
            } else {
                countOut += default_PauseTime;
                lastWasStudy = true;
            }
        }
        countOut = countOut - timeFromService;
        if (lastWasStudy) {
            pauseTimerFunction(countOut, update_Time);
            pauseTimer.start();
        } else {
            studyTimerFunction(countOut, update_Time);
            studyTimer.start();
        }

    }

    public void resetTimer() {
        timePassed = 0;
        cancelOneOfTimers();
        buttonText = "Starta Timer";
        startButton.setText(buttonText);
        textViewText = "Studera " + (default_StudyTime / 1000) / 60 + ":" + (default_StudyTime / 1000) % 60;
        textView.setText(textViewText);
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

    public void onDestroyView() {
        super.onDestroyView();
        b = new Bundle();
        b.putString("buttonText", buttonText);
        b.putString("textViewText", textViewText);
    }


}
