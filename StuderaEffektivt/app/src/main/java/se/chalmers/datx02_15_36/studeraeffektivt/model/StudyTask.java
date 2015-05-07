package se.chalmers.datx02_15_36.studeraeffektivt.model;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

/**
 * Created by jesper on 2015-03-23.
 */
public class StudyTask extends CheckBox{

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
    DBAdapter dbAdapter;

    AssignmentType type;
    AssignmentStatus status;


    public AssignmentStatus getStatus() {
        return status;
    }

    public StudyTask(Context context,int id, String courseCode,int chapter, int week, String taskString, int startPage, int endPage, DBAdapter dbAdapter, AssignmentType type, AssignmentStatus status) {
        super(context);
        this.id = id;
        this.courseCode = courseCode;
        this.chapter = chapter;
        this.week = week;
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
                    Toast.makeText(StudyTask.this.getContext(),"Uppgiften avbockad!",Toast.LENGTH_SHORT).show();

                }
                else if (!buttonView.isChecked()) {
                    dbAdapter.setUndone(getStudyTask().getIdNr());
                    Toast.makeText(StudyTask.this.getContext(),"Uppgiften inte avbockad!",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(StudyTask.this.getContext(),"Uppgiften är varken avbockad eller urbockad av någon anledning!",Toast.LENGTH_SHORT).show();
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
                        /*if(getStudyTask().getType() == AssignmentType.READ)
                            ((FlowLayout)getParent()).removeView(getStudyTask());
                        else
                            ((FlowLayout)getParent()).removeView(getStudyTask());*/

                        dbAdapter.deleteAssignment(getStudyTask().getIdNr());
                        Toast.makeText(StudyTask.this.getContext(),"Uppgift borttagen",Toast.LENGTH_SHORT).show();
                        FlowLayout flowLayout = (FlowLayout)getParent();
                        flowLayout.removeAllViews();
                        flowLayout.addTasksFromDatabase(dbAdapter, courseCode, type);

                        if(flowLayout.isEmpty()){
                            TextView textView = new TextView(getContext());
                            if(type==AssignmentType.OTHER)
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

    public StudyTask getStudyTask(){ return this; }

}