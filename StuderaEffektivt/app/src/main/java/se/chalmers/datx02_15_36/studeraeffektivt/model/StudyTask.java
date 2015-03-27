package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;

import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;

/**
 * Created by jesper on 2015-03-23.
 */
public class StudyTask extends CheckBox{

    public int id;
    public int chapter;
    public String taskString;
    public boolean isCompleted;
    public String courseCode;
    private boolean bool;


    public StudyTask(Context context,String courseCode,int chapter, String taskString, DBAdapter dbAdapter, boolean bool) {
        super(context);
        this.courseCode = courseCode;
        this.chapter = chapter;
        this.taskString = taskString;
        this.bool = bool;

        this.setChecked(bool);

        super.setText(taskString);
    }


    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                v.setPressed(true);
                // Start action ...
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                v.setPressed(false);
                // Stop action ...
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        return true;
    }


}
