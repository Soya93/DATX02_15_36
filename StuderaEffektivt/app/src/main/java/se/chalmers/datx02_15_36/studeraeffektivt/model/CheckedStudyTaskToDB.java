/*
    Copyright 2015 DATX02-15-36

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific language governing permissions and   
limitations under the License.

**/

package se.chalmers.datx02_15_36.studeraeffektivt.model;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.Random;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;

/**
 * This class is for StudyTasks which are checked and is inserted in database
 */

public class CheckedStudyTaskToDB extends CheckBox {

    public int chapter;
    private int week;
    public String taskString;
    public String courseCode;
    private int startPage;
    private int endPage;
    private int id;
    private CompoundButton buttonView;
    DBAdapter dbAdapter;

    AssignmentType type;
    AssignmentStatus status;


    public AssignmentStatus getStatus() {
        return status;
    }

    public CheckedStudyTaskToDB(Context context, String courseCode, int chapter, int week, String taskString, int startPage, int endPage, DBAdapter dbAdapter, AssignmentType type, AssignmentStatus status) {
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

        if (type == AssignmentType.READ) {
            setText(startPage + "-" + endPage);
        } else {
            setText(taskString);
        }
        buttonView = (CompoundButton) this.getRootView();

        if(isChecked()){
            Drawable checked = getResources().getDrawable(R.drawable.ic_toggle_check_box);
            checked.setColorFilter(Color.parseColor(Colors.secondaryColor), PorterDuff.Mode.SRC_ATOP);
            buttonView.setButtonDrawable(checked);
        }else {
            Drawable unchecked = getResources().getDrawable(R.drawable.ic_toggle_check_box_outline_blank);
            unchecked.setColorFilter(Color.parseColor("#939393"), PorterDuff.Mode.SRC_ATOP);
            buttonView.setButtonDrawable(unchecked);
        }

        initCheckbox();
    }

    public void initCheckbox() {
        this.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isChecked()) {
                    Drawable checked = getResources().getDrawable(R.drawable.ic_toggle_check_box);
                    checked.setColorFilter(Color.parseColor(Colors.secondaryColor), PorterDuff.Mode.SRC_ATOP);
                    buttonView.setButtonDrawable(checked);
                    addToDatabase();
                } else {
                    Drawable unchecked = getResources().getDrawable(R.drawable.ic_toggle_check_box_outline_blank);
                    unchecked.setColorFilter(Color.parseColor("#939393"), PorterDuff.Mode.SRC_ATOP);
                    buttonView.setButtonDrawable(unchecked);
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
