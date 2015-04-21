package se.chalmers.datx02_15_36.studeraeffektivt.fragment;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;


import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;


public class TimerFrag extends Fragment {

    private ImageButton startButton;

    private int buttonId = R.drawable.ic_start;
    private boolean hasBeenPaused = false;


    protected long default_TotalTime = (55 * 60*1000);
    protected long default_StudyTime = (2 * 60*1000);
    protected long default_PauseTime = (1 * 60*1000);
    protected long default_NumberOfPauses = 1;

    private TextView textView;

    private View rootView;
    private TextView inputText;
    private TextView pausLengthInput;
    private TextView nbrOfPausesInput;

    public static final int TIMER_1 = 0;
    public static final int CHANGE_COLOR_0 = 1;
    public static final int CHANGE_COLOR_1 = 2;

    private long serviceInt;

    private String inputTime;
    private String nbrOfPauses;
    private String pausLength;
    private String ccode;
    private String textViewText;
    private int phaceInt;

    private Bundle b;
    private Spinner spinner;
    private ProgressBar progressBar;


    private DBAdapter dbAdapter;
    private Handler serviceHandler;
    private SharedPreferences sharedPref;
    private String prefName = "ButtonPref";


    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIMER_1:
                    Bundle b = msg.getData();
                    serviceInt = b.getLong("timePassed", -1);
                    phaceInt = b.getInt("Phace",-1);
                    setTimerView(serviceInt);

                    break;
                case CHANGE_COLOR_0:

                    progressBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.progressbar_pause));
                    break;

                case CHANGE_COLOR_1:

                    progressBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.progressbar_study));
                    break;
            }

        }
    };
    MyCountDownTimer timerService;
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerService = ((MyCountDownTimer.MCDTBinder) service).getService();
            timerService.setHandler(handler);
            serviceHandler = timerService.getServiceHandler();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timerService = null;
        }


    };

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
            setProgressColor(Color.BLUE);
            textView.setText(textV);

        }
    }


    public void onStart() {
        super.onStart();
        startButton.setImageResource(buttonId);
        if(isMyServiceRunning(MyCountDownTimer.class)) {
            Intent i = new Intent(getActivity().getBaseContext(), MyCountDownTimer.class);

            getActivity().bindService(i, sc, Context.BIND_AUTO_CREATE);
        }

        sharedPref = getActivity().getSharedPreferences(prefName, Context.MODE_PRIVATE);


        int buttonTemp = sharedPref.getInt("buttonImage", -1);
        if (buttonTemp > 0) {
            buttonId = buttonTemp;
            startButton.setImageResource(buttonId);
        }
        hasBeenPaused = sharedPref.getBoolean("hasPaused", false);
        if(hasBeenPaused){
            long temp = sharedPref.getLong("timeLeft",-1);
            setTimerView(temp);
        }


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
        startButton = (ImageButton) rootView.findViewById(R.id.button_start_timer);
        textView = (TextView) rootView.findViewById(R.id.textView);
        spinner = (Spinner) rootView.findViewById(R.id.spinner_timer);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        setCourses();

        setTimerView(default_StudyTime);

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


    public void setSelectedCourse() {
        String temp = spinner.getSelectedItem().toString();
        String[] parts = temp.split("-");
        this.ccode = parts[0];
        Log.d("selected course", ccode);

    }


    private long minToMilliSeconds(int parsedTime) {
        return ((long) parsedTime * 60 * 1000);
    }

    private int milliSecondsToMin(long milliSeconds) {
        return ((int) milliSeconds / 1000 / 60);
    }


    /**
     * Set the timer.
     */



    private void setProgressColor(int c) {
        progressBar.getProgressDrawable().setColorFilter(c, PorterDuff.Mode.SRC_IN);
        progressBar.setProgress(0);
    }


    public void startTimer() {
        if (buttonId == R.drawable.ic_start && !hasBeenPaused) {
            spinner.setEnabled(false);
            sendDataToService();
            buttonId = R.drawable.ic_pause;
            startButton.setImageResource(buttonId);
        } else if (buttonId == R.drawable.ic_pause) {
            hasBeenPaused = true;
            serviceHandler.sendEmptyMessage(0);
            buttonId = R.drawable.ic_start;
            startButton.setImageResource(buttonId);

        } else if (buttonId == R.drawable.ic_start && hasBeenPaused) {
            serviceHandler.sendEmptyMessage(1);
            buttonId = R.drawable.ic_pause;
            startButton.setImageResource(buttonId);
        }

    }


    private void sendDataToService () {
        Intent i = new Intent(getActivity().getBaseContext(), MyCountDownTimer.class);
        i.putExtra("CCODE",ccode);
        i.putExtra("TIME_STUDY", default_StudyTime);
        i.putExtra("TIME_PAUSE", default_PauseTime);
        i.putExtra("TOTAL_TIME", default_TotalTime);
        getActivity().bindService(i, sc, Context.BIND_AUTO_CREATE);
        getActivity().startService(i);

    }


    public void setTimerView(long secUntilFinished) {
        String sec = String.format("%02d", (secUntilFinished)/1000 % 60);
        String min = String.format("%02d", (secUntilFinished) /1000/ 60);
        textViewText = (min + ":" + sec);
        textView.setText(textViewText);
        if(phaceInt == 0){
            progressBar.setProgress((int)(secUntilFinished * 1000 / default_StudyTime));}
        if(phaceInt == 1){
            progressBar.setProgress((int)(secUntilFinished * 1000 / default_PauseTime));
        }
    }

    public void resetTimer() {
        serviceHandler.sendEmptyMessage(0);
        buttonId = R.drawable.ic_start;
        hasBeenPaused = false;
        startButton.setImageResource(buttonId);


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
        resetTimer();

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


    public void parseFromDialog() {
        try {
            int timeFromDialog = Integer.parseInt(inputTime);
            default_TotalTime = minToMilliSeconds(timeFromDialog);
            int pauseFromDialog;
            if (pausLength.equals("")) {
                pauseFromDialog = 0;
            } else {
                pauseFromDialog = Integer.parseInt(pausLength);


            }
            default_PauseTime = minToMilliSeconds(pauseFromDialog);
            int numberOfPauses = Integer.parseInt(nbrOfPauses);
            default_NumberOfPauses = (long) numberOfPauses;
            //calculateStudySession();


        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

    public void onDestroyView() {
        super.onDestroyView();
        if(isMyServiceRunning(MyCountDownTimer.class)) {
            getActivity().unbindService(sc);
            handler.removeMessages(0);
            handler.removeMessages(1);
            handler.removeMessages(2);
        }

        saveFragmentState();
    }

    private void saveFragmentState() {
        b = new Bundle();
        b.putInt("buttonImage", buttonId);
        b.putString("textViewText", textViewText);
        sharedPref = getActivity().getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("buttonImage", buttonId);
        editor.putBoolean("hasPaused", hasBeenPaused);
        Log.d("buttonId", String.valueOf(buttonId));
        Log.d("hasPaused",String.valueOf(hasBeenPaused));




        if(hasBeenPaused){
            editor.putLong("timeLeft",serviceInt);
        }

        Log.d("ONDESTROY", String.valueOf(sharedPref.getInt("buttonImage", -1)));
        editor.apply();
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}