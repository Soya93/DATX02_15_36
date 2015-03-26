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
    public Course course;

    public StudyTask(Context context,Course course,int chapter, String taskString) {
        super(context);
        this.course = course;
        this.chapter = chapter;
        this.taskString = taskString;
    }




    /*public StudyTask(){
        this.id = id;
        this.chapter = chapter;
        this.taskString = inputString;
    }*/



}
