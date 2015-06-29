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
        import se.chalmers.datx02_15_36.studeraeffektivt.database.AssignmentsDBAdapter;
        import se.chalmers.datx02_15_36.studeraeffektivt.database.AssignmentsFactory;
        import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
        import se.chalmers.datx02_15_36.studeraeffektivt.database.HandInAssignmentsDBAdapter;
        import se.chalmers.datx02_15_36.studeraeffektivt.database.LabAssignmentsDBAdapter;
        import se.chalmers.datx02_15_36.studeraeffektivt.database.OtherAssignmentsDBAdapter;
        import se.chalmers.datx02_15_36.studeraeffektivt.database.ProblemAssignmentsDBAdapter;
        import se.chalmers.datx02_15_36.studeraeffektivt.database.ReadAssignmentsDBAdapter;
        import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
        import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
        import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
        import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;


/**
 * Created by SoyaPanda on 15-06-27.
 */
public class StudyTask extends CheckBox {

    AssignmentsDBAdapter assignmentsDBAdapter;
    AssignmentType type;
    AssignmentStatus status;
    private int id;
    private CompoundButton buttonView;
    public String taskString;



    public int getWeek() {
        //fråga dbr
        //DBAdapter db = assignmentsFactory.getDBAdapter(type);
        return 0;
    }


    public AssignmentStatus getStatus() {
        return status;
    }

    public StudyTask(Context context, int id, AssignmentsDBAdapter assignmentsFactory, AssignmentType type, AssignmentStatus status){
        super(context);
        this.id = id;
        this.status = status;
        this.type = type;
        AssignmentsFactory dbf = new AssignmentsFactory(context);

        switch (type){
            case HANDIN:
                assignmentsDBAdapter =  new HandInAssignmentsDBAdapter(context);

            case LAB:
                assignmentsDBAdapter = new LabAssignmentsDBAdapter(context);

            case PROBLEM:
                assignmentsDBAdapter = new ProblemAssignmentsDBAdapter(context);

            case READ:
                assignmentsDBAdapter = new ReadAssignmentsDBAdapter(context);

            case OTHER:
                assignmentsDBAdapter = new OtherAssignmentsDBAdapter(context);

            default:
                //never invoked
        }

        if(status==AssignmentStatus.DONE) {
            this.setChecked(true);
        }

        if(type == AssignmentType.READ){
            //getStart and endpage from db
            //setText(startPage + "-" + endPage);
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
                PopupMenu popup = new PopupMenu(StudyTask.this.getContext(), getStudyTask());
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_remove_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        assDBAdapter.deleteAssignment(getStudyTask().getIdNr());
                        Toast.makeText(OldStudyTask.this.getContext(), "Uppgift borttagen", Toast.LENGTH_SHORT).show();
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

    public String getTaskString() {
        return taskString;
    }

    public StudyTask getStudyTask(){ return this; }

}