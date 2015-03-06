package se.chalmers.datx02_15_36.studeraeffektivt.activity;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;


import se.chalmers.datx02_15_36.studeraeffektivt.R;


public class TimerFrag extends Fragment {
    private String userName;
    private CountDownTimer cdt;
    private TimePicker t1;
    private Button startButton;
    private Button resetButton;
    private long chosenSeconds;
    private long seconds;
    private long timePassed;
   //private DbAccess dbAccess;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_timer, container, false);

        instantiate();
        t1 = (TimePicker) rootView.findViewById(R.id.timePicker);
        resetButton = (Button) rootView.findViewById(R.id.button_reset);
        startButton = (Button) rootView.findViewById(R.id.button_start_timer);


        t1.setIs24HourView(true);
        t1.clearFocus();
        setTime();
        getTimer(5000, 100);

        return rootView;
    }


    private void setTime() {
        t1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                                        @Override
                                        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                                            chosenSeconds = minute * 60 + hourOfDay * 3600;
                                            getTimer(chosenSeconds * 1000, 100);

                                        }
                                    }
        );

    }

    private void instantiate() {


    }

    /**
     * Set the timer.
     */
    public CountDownTimer getTimer(long millisInFuture, long countDownInterval) {

        cdt = new CountDownTimer(millisInFuture, countDownInterval) {

            public void onTick(long millisUntilFinished) {

                TextView textView = (TextView) getView().findViewById(R.id.text_timer);
                textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                setTimePicker(millisUntilFinished);

                seconds = millisUntilFinished;
                timePassed += 100;
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

            @Override
            public void onFinish() {
                t1.setEnabled(true);
                setTime();
                startButton.setText("Start");
                startButton.setEnabled(true);
                //dbAccess.insertValue("Alex",Long.toString(timePassed));

            }
        };
        return cdt;

    }

    /**
     * Start the timer.
     * Called when the user clicks the Start Timer button.
     */
    public void startTimer(View view) {
        t1.setEnabled(false);
        if (startButton.getText().equals("Pause")) {
            startButton.setText("Start");
            cdt.cancel();
            getTimer(seconds, 100);
        } else if (startButton.getText().equals("Start")) {
            cdt.start();
            startButton.setText("Pause");
            resetButton.setEnabled(true);

        }
    }
    /*
    If you hit pause and then reset button and then not choose another time. Timer will count down
    the seconds that are left.
    */

    public void resetTimer(View view) {
        cdt.cancel();
        startButton.setText("Start");
        startButton.setEnabled(true);
        resetButton.setEnabled(false);
        t1.setEnabled(true);
        setTime();
    }




}
