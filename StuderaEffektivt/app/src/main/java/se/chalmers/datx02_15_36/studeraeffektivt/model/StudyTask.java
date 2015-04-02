package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.activity.StudyTaskActivity;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

/**
 * Created by jesper on 2015-03-23.
 */
public class StudyTask extends CheckBox{

    public int chapter;
    public String taskString;
    public String courseCode;
    private int startPage;
    private int endPage;
    private int id;
    DBAdapter dbAdapter;

    AssignmentType type;
    AssignmentStatus status;


    public AssignmentStatus getStatus() {
        return status;
    }

   /* public StudyTask(Context context,String courseCode,int chapter, String taskString, DBAdapter dbAdapter, AssignmentType type, AssignmentStatus status) {
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

    }*/

    public StudyTask(Context context,int id, String courseCode,int chapter, String taskString, int startPage, int endPage, DBAdapter dbAdapter, AssignmentType type, AssignmentStatus status) {
        super(context);
        this.id = id;
        this.courseCode = courseCode;
        this.chapter = chapter;
        this.taskString = taskString;
        this.startPage = startPage;
        this.endPage = endPage;
        this.dbAdapter = dbAdapter;
        this.status = status;
        this.type = type;

        if(status==AssignmentStatus.DONE) {
            this.setChecked(true);
        }

        if(type == AssignmentType.READ){
            setText(startPage + "-" + endPage);
        }
        else{
            setText(taskString);
        }

        initCheckbox();
    }

    public void initCheckbox(){
        this.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (buttonView.isChecked()) {
                    dbAdapter.setDone(getStudyTask().getIdNr());
                } else {
                    dbAdapter.setUndone(getStudyTask().getIdNr());
                }
            }
        });

        getStudyTask().setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(StudyTask.this.getContext(), getStudyTask());
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_remove_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(getStudyTask().getType() == AssignmentType.READ)
                            ((FlowLayout)getParent()).removeView(getStudyTask());
                        else
                            ((FlowLayout)getParent()).removeView(getStudyTask());

                        dbAdapter.deleteAssignment(getStudyTask().getIdNr());
                        Toast.makeText(StudyTask.this.getContext(),"Uppgift borttagen",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();//showing popup menu
                return true;
            }
        });

        getStudyTask().setLongClickable(true);
        getStudyTask().setClickable(true);
    }




    public int getIdNr() {
        return id;
    }

    public void setIdNr(int id) {
        this.id = id;
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

    public StudyTask getStudyTask(){ return this; }

}
