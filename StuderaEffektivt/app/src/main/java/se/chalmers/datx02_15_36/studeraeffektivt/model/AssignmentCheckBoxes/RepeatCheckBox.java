package se.chalmers.datx02_15_36.studeraeffektivt.model.AssignmentCheckBoxes;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.HandInAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.ReadAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.database.RepetitionAssignmentsDBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.view.AssignmentCheckBoxLayout;

/**
 * Created by SoyaPanda on 15-06-30.
 */
public class RepeatCheckBox extends AssignmentCheckBox {

    private RepetitionAssignmentsDBAdapter repeatDB;

    public RepeatCheckBox(Context context, int id, AssignmentStatus status){
        super(context,id,status);

        repeatDB = new RepetitionAssignmentsDBAdapter(context);
        initCheckbox();
        setText(getTaskString());
    }

    public void initCheckbox(){
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    Drawable checked = getResources().getDrawable(R.drawable.ic_toggle_check_box);
                    checked.setColorFilter(Color.parseColor(Colors.secondaryColor), PorterDuff.Mode.SRC_ATOP);
                    buttonView.setButtonDrawable(checked);
                    repeatDB.setDone(getAssignmentCheckBox().getIdNr());
                }
                else{
                    Drawable unchecked = getResources().getDrawable(R.drawable.ic_toggle_check_box_outline_blank);
                    unchecked.setColorFilter(Color.parseColor("#939393"), PorterDuff.Mode.SRC_ATOP);
                    buttonView.setButtonDrawable(unchecked);
                    repeatDB.setUndone(getAssignmentCheckBox().getIdNr());
                }

            }
        });

        getAssignmentCheckBox().setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(RepeatCheckBox.this.getContext(), getAssignmentCheckBox());
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_remove_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String courseCode = getCourseCode();
                        repeatDB.deleteAssignment(getAssignmentCheckBox().getIdNr());
                        Toast.makeText(RepeatCheckBox.this.getContext(), "Uppgift borttagen", Toast.LENGTH_SHORT).show();
                        AssignmentCheckBoxLayout assignmentCheckBoxLayout = (AssignmentCheckBoxLayout)getParent();
                        assignmentCheckBoxLayout.removeAllViews();
                        assignmentCheckBoxLayout.addRepeatsFromDatabase(courseCode, repeatDB);

                        if(assignmentCheckBoxLayout.isEmpty()){
                            TextView textView = new TextView(getContext());
                            textView.setText("Du har för närvaranade inga " + AssignmentType.READ.toString().toLowerCase() + " för den här kursen, lägg till en uppgift genom att fylla i informationen ovan och trycka på spara-knappen i övre högra hörnet");
                            textView.setPadding(15,5,15,5);
                            assignmentCheckBoxLayout.addView(textView);
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
                return true;
            }
        });

        getAssignmentCheckBox().setLongClickable(true);
        getAssignmentCheckBox().setClickable(true);
    }

    public String getCourseCode(){
        return repeatDB.getCourse(super.getIdNr());
    }

    public int getWeek() {
        return repeatDB.getWeek(super.getIdNr());
    }

    public String getSortingString(){
        return repeatDB.getSortedString(super.getIdNr());
    }

    public String getTaskString() {
        return repeatDB.getTaskString(super.getIdNr());
    }
}
