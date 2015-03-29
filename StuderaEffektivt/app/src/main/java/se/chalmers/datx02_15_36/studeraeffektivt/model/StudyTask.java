package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;

import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;

/**
 * Created by jesper on 2015-03-23.
 */
public class StudyTask extends CheckBox{

    public int chapter;
    public String taskString;
    public String courseCode;
    private int startPage;
    private int endPage;
    private String id;
    DBAdapter dbAdapter;

    AssignmentType type;
    AssignmentStatus status;


    public AssignmentStatus getStatus() {
        return status;
    }

    public StudyTask(Context context,String courseCode,int chapter, String taskString, DBAdapter dbAdapter, AssignmentType type, AssignmentStatus status) {
        super(context);
        this.courseCode = courseCode;
        this.chapter = chapter;
        this.taskString = taskString;
        this.status = status;
        this.type = type;
        this.dbAdapter = dbAdapter;

        this.id = courseCode + chapter + taskString;

        if(status==AssignmentStatus.DONE) {
            this.setChecked(true);
        }

        super.setText(taskString);
    }

    public StudyTask(Context context,String courseCode,int chapter, int startPage, int endPage, DBAdapter dbAdapter, AssignmentType type, AssignmentStatus status) {
        super(context);
        this.courseCode = courseCode;
        this.chapter = chapter;
        this.startPage = startPage;
        this.endPage = endPage;
        this.status = status;
        this.type = type;

        this.id = courseCode + chapter + startPage + endPage;

        if(status==AssignmentStatus.DONE) {
            this.setChecked(true);
        }

        setText(startPage + "-" + endPage);
    }




    public String getIdNr() {
        return id;
    }

    public void setIdNr(String id) {
        this.id = id;
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

    public AssignmentType getType(){
        return type;
    }

   public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getTaskString() {
        return taskString;
    }

    public void setTaskString(String taskString) {
        this.taskString = taskString;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

}
