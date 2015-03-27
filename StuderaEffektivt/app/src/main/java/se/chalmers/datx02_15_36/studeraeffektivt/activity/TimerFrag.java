package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;


public class TimerFrag extends Fragment {

    protected CountDownTimer studyTimer;
    protected CountDownTimer pauseTimer;

    private ImageButton startButton;
    private ImageButton resetButton;
    private ImageButton pauseButton;

    private int buttonId;

    private long chosenSeconds;
    private long secondsUntilFinished;

    protected long timePassed;
    protected long default_TotalTime = (55 * 60 * 1000);
    protected long default_StudyTime = (25*60*1000);
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
    private String ccode;
    private String textViewText;

    private Bundle b;
    private Spinner spinner;
    private ProgressBar progressBar;


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
            textView = (TextView) rootView.findViewById(R.id.textView);
            String textV = b.getString("textViewText");
            textView.setText(textV);
            int buttonId= b.getInt("buttonImage");
            Log.d("Värdet", String.valueOf(buttonId));
            startButton.setImageResource(buttonId);

        }
    }


    public void onStart () {
        super.onStart();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedCourse ();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void instantiate() {
       resetButton = (ImageButton) rootView.findViewById(R.id.button_reset);
        startButton = (ImageButton) rootView.findViewById(R.id.button_start_timer);
        textView = (TextView) rootView.findViewById(R.id.textView);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
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


        public void setSelectedCourse (){
            String temp =spinner.getSelectedItem().toString();
            String [] parts = temp.split("-");
            this.ccode = parts[0];
            Log.d("selected course", ccode);

        }


        private long minToMilliSeconds ( int parsedTime){
            return ((long) parsedTime * 60 * 1000);
        }

        private int milliSecondsToMin (long milliSeconds){
            return ((int) milliSeconds/1000/60);
        }


        private void calculateStudySession () {
            long temp = (default_TotalTime - (default_NumberOfPauses * default_PauseTime));
            this.default_StudyTime = (temp / (default_NumberOfPauses + 1));
        }

        /**
         * Set the timer.
         */
        public CountDownTimer studyTimerFunction ( long millisInFuture, long countDownInterval){

            studyTimer = new CountDownTimer(millisInFuture, countDownInterval) {

                public void onTick(long millisUntilFinished) {
                    studyTimerIsRunning = true;
                    setTimerView(millisUntilFinished);
                    timePassed += 100;
                }

                @Override
                public void onFinish() {
                    timePassed +=300;

                    studyTimerIsRunning = false;
                    insertIntoDataBase();

                    setProgressColor(Color.GREEN);
                    //Start pausetimer if time left.
                    if (timePassed < default_TotalTime) {

                        pauseTimerFunction(default_PauseTime, update_Time);
                        pauseTimer.start();
                    }

                }
            };
            return studyTimer;

        }

    private void insertIntoDataBase() {
        long inserted = dbAdapter.insertSession(ccode, milliSecondsToMin(default_StudyTime));
        if (inserted > 0 && getActivity() != null) {
            Toast toast = Toast.makeText(getActivity(), "Session:" + milliSecondsToMin(default_StudyTime)
                    + "minutes added to " + ccode, Toast.LENGTH_SHORT);
            toast.show();
        } else if (getActivity() != null) {
            Toast toast = Toast.makeText(getActivity(), "Failed to add a Session", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void setProgressColor (int c) {
        progressBar.getProgressDrawable().setColorFilter(c, PorterDuff.Mode.SRC_IN);
        progressBar.setProgress(0);
    }

        public CountDownTimer pauseTimerFunction ( long millisInFuture, long countDownInterval){

            pauseTimer = new CountDownTimer(millisInFuture, countDownInterval) {

                public void onTick(long millisUntilFinished) {
                    pauseTimerIsRunning = true;
                       setTimerView(millisUntilFinished);
                    timePassed += 100;
                }

                @Override
                public void onFinish() {
                    progressBar.getProgressDrawable().setColorFilter(Color.parseColor("#ff523799"), PorterDuff.Mode.SRC_IN);
                    timePassed +=300;
                    pauseTimerIsRunning = false;
                    if (timePassed < default_TotalTime) {
                        studyTimerFunction(default_StudyTime, update_Time);
                        studyTimer.start();
                    }

                }
            };
            return pauseTimer;

        }


        public void startTimer () {
            if(!studyTimerIsRunning && !pauseTimerIsRunning) {
                if (timePassed == 0) {
                    calculateStudySession();
                    studyTimerFunction(default_StudyTime, update_Time);
                    studyTimer.start();

                }
                else {
                    handleTimeFromService(timePassed);
                }
               buttonId = R.drawable.ic_pause;
                startButton.setImageResource(buttonId);
            }
            else if(studyTimerIsRunning || pauseTimerIsRunning){
                cancelOneOfTimers();
                buttonId = R.drawable.ic_start;
                startButton.setImageResource(buttonId);

            }


            }


        protected void handleTimeFromService ( long timeFromService){
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

     public void setTimerView(long millisUntilFinished ) {
         String sec = String.format("%02d", (millisUntilFinished / 1000) % 60);
         String min = String.format("%02d", (millisUntilFinished / 1000) / 60);
         textViewText = (min + ":" + sec);
         textView.setText(textViewText);
         progressBar.setProgress((int) (millisUntilFinished * 1000 / default_StudyTime));
     }

        public void resetTimer () {
            timePassed = 0;
            cancelOneOfTimers();
            setTimerView(default_StudyTime);
            textView.setText(textViewText);
            startButton.setImageResource(R.drawable.ic_start);

        }

        protected void cancelOneOfTimers () {
            if (studyTimerIsRunning) {
                studyTimer.cancel();
                studyTimerIsRunning = false;

            } else if (pauseTimerIsRunning) {
                pauseTimer.cancel();
                pauseTimerIsRunning = true;
            }
        }

        public void settingsTimer () {
            timePassed=0;
            resetTimer();
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

        private void nextDialog () {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.time_picker_dialog2, null);
            builder.setView(dialogView);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    nbrOfPausesInput = (TextView) dialogView.findViewById(R.id.nbrOfPausesInt);
                    nbrOfPauses = nbrOfPausesInput.getText().toString();

                    pausLengthInput = (TextView) dialogView.findViewById(R.id.pausLengthInt);
                    pausLength = pausLengthInput.getText().toString();
                    textView.setText(inputTime + ":00");
                    parseFromDialog();


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


        public void parseFromDialog () {
            try {
                int timeFromDialog = Integer.parseInt(inputTime);
                default_TotalTime = minToMilliSeconds(timeFromDialog);
                int pauseFromDialog;
                if(pausLength.equals("")) {
                    pauseFromDialog = 0;
                }
                else{
                  pauseFromDialog = Integer.parseInt(pausLength);


                }
                default_PauseTime = minToMilliSeconds(pauseFromDialog);
                int numberOfPauses = Integer.parseInt(nbrOfPauses);
                default_NumberOfPauses = (long) numberOfPauses;
                calculateStudySession();


            } catch (Throwable e) {
                e.printStackTrace();
            }


        }

        public void onDestroyView () {
            super.onDestroyView();
            saveFragmentState();
        }

        private void saveFragmentState() {
            b = new Bundle();
            b.putInt("buttonImage", buttonId);
            b.putString("textViewText" ,textViewText);
        }
    }