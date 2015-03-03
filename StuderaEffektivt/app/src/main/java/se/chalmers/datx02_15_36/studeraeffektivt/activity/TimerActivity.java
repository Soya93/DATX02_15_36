package se.chalmers.datx02_15_36.studeraeffektivt.activity;



import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;




import se.chalmers.datx02_15_36.studeraeffektivt.R;



public class TimerActivity extends Fragment {


    private TimePicker t1;

    protected CountDownTimer studyTimer;
    protected CountDownTimer pauseTimer;

    private final int update_Time = 1000;

    private Button startButton;
    private Button resetButton;

    private long secondsUntilFinished;

    private int timePassed = 0;
    private int default_TotalTime =(120*60*1000);
    private int calculatedStudyTime ;
    private int default_PauseTime = (30*60*1000);
    private int default_NumberOfPauses = 2;
    private int default_StudyTime = (30*60*1000);

    private int studyTime=0;

    private TextView textView;


    private final int dialog_setPause = 1;
    private final int dialog_setStart = 0;

    private boolean studyTimerIsRunning = false;
    private SharedPreferences prefs;
    private String prefName = "WhichTimerIsRunning";
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

       rootView= inflater.inflate(R.layout.activity_timer,container,false);
        instantiate();

   return rootView; }



    private void instantiate() {
        t1 = (TimePicker) rootView.findViewById(R.id.timePicker);
        t1.setIs24HourView(true);
        t1.setEnabled(false);
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


    private void setTimePicker(long millisUntilFinished) {
        int hour = secondsToHour(millisUntilFinished);
        int minute = secondsToMin(millisUntilFinished);
        t1.setCurrentHour(hour);
        t1.setCurrentMinute(minute);
    }

    private void calculateStudySession() {
        this.calculatedStudyTime = default_TotalTime - ((default_NumberOfPauses*default_PauseTime)/default_NumberOfPauses);

    }

    public CountDownTimer studyTimerFunction(long millisInFuture, long countDownInterval) {

        studyTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                studyTimerIsRunning=true;

                textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                setTimePicker(millisUntilFinished);

                secondsUntilFinished = millisUntilFinished;
                timePassed += 100;
            }

            @Override
            public void onFinish() {
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
                studyTimerIsRunning=false;

                textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                setTimePicker(millisUntilFinished);

                secondsUntilFinished = millisUntilFinished;
                timePassed += 100;
            }

            @Override
            public void onFinish() {
                if(timePassed<default_TotalTime) {
                    studyTimerFunction(default_StudyTime, update_Time);
                    studyTimer.start();
                }

            }
        };
        return pauseTimer;

    }



    public long getSecondsUntilFinished() {
        return this.secondsUntilFinished;
    }

    public void startTimer() {
        studyTimerFunction(default_StudyTime,update_Time);
        studyTimer.start();

    }



    public void resetTimer() {

    }





        /*
    public void settingsTimer (View view){
        showDialog(0);
    }

    protected Dialog onCreateDialog(int choice) {
        switch (choice) {

            case 0: {
                return new AlertDialog.Builder(this).
                        setTitle(getResources()
                                .getString(R.string.settings_label))
                        .setPositiveButton("Change study time", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int arg1) {
                            }
                        }).show();
            }

        }
        return null;
    }

*/

}

