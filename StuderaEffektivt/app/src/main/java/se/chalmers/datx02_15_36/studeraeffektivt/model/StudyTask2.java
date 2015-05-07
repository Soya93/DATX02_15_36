package se.chalmers.datx02_15_36.studeraeffektivt.model;


import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

/**
 * This class is for StudyTasks that when is checked is inserted in database
 */

public class StudyTask2 extends CheckBox {

    public int chapter;
    private int week;
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

    public StudyTask2(Context context,String courseCode, int chapter, int week, String taskString, int startPage, int endPage, DBAdapter dbAdapter, AssignmentType type, AssignmentStatus status) {
        super(context);
        Random rand = new Random();
        this.id = rand.nextInt((99999999 - 10000000) + 1) + 10000000;
        this.courseCode = courseCode;
        this.chapter = chapter;
        this.week = week;
        this.taskString = taskString;
        this.startPage = startPage;
        this.endPage = endPage;
        this.dbAdapter = dbAdapter;
        this.status = status;
        this.type = type;

        if (status == AssignmentStatus.DONE) {
            this.setChecked(true);
        }

        if (type == AssignmentType.READ) {
            setText(startPage + "-" + endPage);
        } else {
            setText(taskString);
        }

        initCheckbox();
    }

    public void initCheckbox() {
        this.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isChecked()) {
                    addToDatabase();

                } else {
                    deleteFromDatabase();

                }
            }
        });

    }


    public int getIdNr() {
        return id;
    }

    public AssignmentType getType() {
        return type;
    }

    public int getChapter() {
        return chapter;
    }

    public String getTaskString() {
        return taskString;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public StudyTask2 getStudyTask() {
        return this;
    }

    public int getWeek() {
        return week;
    }

    public void addToDatabase() {

        dbAdapter.insertAssignment(
                getCourseCode(),
                getIdNr(),
                getChapter(),
               getWeek(),
                getTaskString(),
                getStartPage(),
                getEndPage(),
                getType(),
                getStatus()
        );

    }

    public void deleteFromDatabase() {
        dbAdapter.deleteAssignment(getIdNr());
    }


}
