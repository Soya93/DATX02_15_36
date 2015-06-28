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
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.OldAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

/**
 * Created by jesper on 2015-03-23.
 */
public class OldStudyTask extends CheckBox{

    public int chapter;
    private int week;

    public int getWeek() {
        return week;
    }

    public String taskString;
    public String courseCode;
    private int startPage;
    private int endPage;
    private int id;
    OldAssignmentsDBAdapter assDBAdapter;

    AssignmentType type;
    AssignmentStatus status;
    private CompoundButton buttonView;


    public AssignmentStatus getStatus() {
        return status;
    }

    public OldStudyTask(Context context, int id, String courseCode, int chapter, int week, String taskString, int startPage, int endPage, OldAssignmentsDBAdapter assDBAdapter, AssignmentType type, AssignmentStatus status) {
        super(context);
        this.id = id;
        this.courseCode = courseCode;
        this.chapter = chapter;
        this.week = week;
        this.taskString = taskString;
        this.startPage = startPage;
        this.endPage = endPage;
        this.assDBAdapter = assDBAdapter;
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

    public void initCheckbox(){
        this.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub


                if (buttonView.isChecked()) {
                    Drawable checked = getResources().getDrawable(R.drawable.ic_toggle_check_box);
                    checked.setColorFilter(Color.parseColor(Colors.secondaryColor), PorterDuff.Mode.SRC_ATOP);
                    buttonView.setButtonDrawable(checked);
                    assDBAdapter.setDone(getStudyTask().getIdNr());
                }
                else{
                    Drawable unchecked = getResources().getDrawable(R.drawable.ic_toggle_check_box_outline_blank);
                    unchecked.setColorFilter(Color.parseColor("#939393"), PorterDuff.Mode.SRC_ATOP);
                    buttonView.setButtonDrawable(unchecked);
                    assDBAdapter.setUndone(getStudyTask().getIdNr());
                }

            }
        });

        getStudyTask().setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(OldStudyTask.this.getContext(), getStudyTask());
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_remove_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        /*if(getStudyTask().getType() == AssignmentType.READ)
                            ((FlowLayout)getParent()).removeView(getStudyTask());
                        else
                            ((FlowLayout)getParent()).removeView(getStudyTask());*/

                        assDBAdapter.deleteAssignment(getStudyTask().getIdNr());
                        Toast.makeText(OldStudyTask.this.getContext(),"Uppgift borttagen",Toast.LENGTH_SHORT).show();
                        FlowLayout flowLayout = (FlowLayout)getParent();
                        flowLayout.removeAllViews();
                        flowLayout.addTasksFromDatabase(assDBAdapter, courseCode, type);

                        if(flowLayout.isEmpty()){
                            TextView textView = new TextView(getContext());
                            if(type==AssignmentType.PROBLEM)
                                textView.setText("Du har för närvaranade räkneuppgifter för den här kursen, lägg till en uppgift genom att fylla i informationen ovan och trycka på spara-knappen i övre högra hörnet");
                            else
                                textView.setText("Du har för närvaranade inga läsanvisningar för den här kursen, lägg till en uppgift genom att fylla i informationen ovan och trycka på spara-knappen i övre högra hörnet");
                            textView.setPadding(15,5,15,5);
                            flowLayout.addView(textView);
                        }
                        //flowLayout.updateDatabase(type, courseCode);
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

    public AssignmentType getType(){
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

    public OldStudyTask getStudyTask(){ return this; }

}