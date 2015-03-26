package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.content.Context;
import android.widget.CheckBox;

import java.util.ArrayList;

/**
 * Created by jesper on 2015-03-23.
 */
public class StudyTask extends CheckBox{

    public int id;
    public int chapter;
    public String taskString;
    public boolean isCompleted;
    public String courseCode;

    public StudyTask(Context context,String courseCode,int chapter, String taskString) {
        super(context);
        this.courseCode = courseCode;
        this.chapter = chapter;
        this.taskString = taskString;
    }

    /*public StudyTask(){
        this.id = id;
        this.chapter = chapter;
        this.taskString = inputString;
    }*/



}
