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

package se.chalmers.datx02_15_36.studeraeffektivt.model.AssignmentCheckBoxes;

        import android.content.Context;
        import android.graphics.Color;
        import android.graphics.PorterDuff;
        import android.graphics.drawable.Drawable;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;

        import se.chalmers.datx02_15_36.studeraeffektivt.R;
        import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
        import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
        import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;


/**
 * Created by SoyaPanda on 15-06-27.
 */
public abstract class AssignmentCheckBox extends CheckBox {

    protected AssignmentType type;
    protected AssignmentStatus status;
    protected int id;
    protected CompoundButton buttonView;

    public AssignmentStatus getStatus() {
        return status;
    }

    public AssignmentCheckBox(Context context, int id, AssignmentStatus status){
        super(context);
        this.id = id;
        this.status = status;
        this.setChecked(status==AssignmentStatus.DONE);

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
    }


    public abstract void initCheckbox();

    public abstract String getCourseCode();

    public abstract int getWeek();

    public abstract String getSortingString();

    public abstract String getTaskString();

    public int getIdNr() {
        return id;
    }

    public AssignmentType getType(){
        return type;
    }

    public AssignmentCheckBox getAssignmentCheckBox(){ return this; }

}