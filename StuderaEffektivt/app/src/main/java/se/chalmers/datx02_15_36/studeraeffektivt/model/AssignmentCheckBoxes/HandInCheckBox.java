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
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.view.AssignmentCheckBoxLayout;

/**
 * Created by SoyaPanda on 15-06-30.
 */
public class HandInCheckBox extends AssignmentCheckBox {

    private HandInAssignmentsDBAdapter handInDB;


    public HandInCheckBox(Context context, int id, AssignmentStatus status){
        super(context,id,status);

        handInDB = new HandInAssignmentsDBAdapter(context);
    }

    @Override
    public void initCheckbox(){
        this.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    Drawable checked = getResources().getDrawable(R.drawable.ic_toggle_check_box);
                    checked.setColorFilter(Color.parseColor(Colors.secondaryColor), PorterDuff.Mode.SRC_ATOP);
                    buttonView.setButtonDrawable(checked);
                    handInDB.setDone(getStudyTask().getIdNr());
                }
                else{
                    Drawable unchecked = getResources().getDrawable(R.drawable.ic_toggle_check_box_outline_blank);
                    unchecked.setColorFilter(Color.parseColor("#939393"), PorterDuff.Mode.SRC_ATOP);
                    buttonView.setButtonDrawable(unchecked);
                    handInDB.setUndone(getStudyTask().getIdNr());
                }

            }
        });

        getStudyTask().setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(HandInCheckBox.this.getContext(), getStudyTask());
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_remove_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        handInDB.deleteAssignment(getStudyTask().getIdNr());
                        Toast.makeText(HandInCheckBox.this.getContext(), "Uppgift borttagen", Toast.LENGTH_SHORT).show();
                        AssignmentCheckBoxLayout assignmentCheckBoxLayout = (AssignmentCheckBoxLayout)getParent();
                        assignmentCheckBoxLayout.removeAllViews();
                        assignmentCheckBoxLayout.addHandInsFromDatabase(getCourseCode(), handInDB);

                        if(assignmentCheckBoxLayout.isEmpty()){
                            TextView textView = new TextView(getContext());
                            textView.setText("Du har för närvaranade inga" + AssignmentType.HANDIN.toString() + "för den här kursen, lägg till en uppgift genom att fylla i informationen ovan och trycka på spara-knappen i övre högra hörnet");
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

        getStudyTask().setLongClickable(true);
        getStudyTask().setClickable(true);
    }

    public String getCourseCode(){ return handInDB.getCourse(super.getIdNr());}

    public int getWeek() {
        return handInDB.getWeek(super.getIdNr());
    }

    public String getSortingString(){
        return handInDB.getNr(super.getIdNr());
    }

    public String getTaskString() {
        return handInDB.getAssNr(super.getIdNr());
    }

}
