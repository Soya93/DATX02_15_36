package se.chalmers.datx02_15_36.studeraeffektivt.view;

/**
 * Created by jesper on 2015-03-27.
 */

        import android.content.Context;
        import android.database.Cursor;
        import android.util.AttributeSet;
        import android.util.Log;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.HashMap;

        import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
        import se.chalmers.datx02_15_36.studeraeffektivt.model.Course;
        import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask;
        import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
        import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;


/**
 * A view container with layout behavior like that of the Swing FlowLayout.
 * Originally from http://nishantvnair.wordpress.com/2010/09/28/flowlayout-in-android/
 *
 * @author Melinda Green
 */
public class FlowLayout extends ViewGroup {
    private final static int PAD_H = 2, PAD_V = 2; // Space between child views.
    private int mHeight;

    private TextView kapitelText;
    private HashMap<Integer, ArrayList<StudyTask>> hashMapOfStudyTasks;
    private HashMap<Integer, ArrayList<StudyTask>> hashMapOfReadingAssignments;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);
        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        int childHeightMeasureSpec;
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        else
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        mHeight = 0;
        for(int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if(child.getVisibility() != GONE) {
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec);
                final int childw = child.getMeasuredWidth();
                mHeight = Math.max(mHeight, child.getMeasuredHeight() + PAD_V);
                if(xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += mHeight;
                }
                xpos += childw + PAD_H;
            }
        }
        if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + mHeight;
        } else if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if(ypos + mHeight < height) {
                height = ypos + mHeight;
            }
        }
        height += 5; // Fudge to avoid clipping bottom of last row.
        setMeasuredDimension(width, height);
    } // end onMeasure()

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();
        for(int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if(child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                if(xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += mHeight;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + PAD_H;
            }
        }
    } // end onLayout()

    public void addMap(HashMap<Integer, ArrayList<StudyTask>> hashMap){
        for (Object value : hashMap.values()) {
            ArrayList<StudyTask> a = (ArrayList) value;
            kapitelText = new TextView(this.getContext());
            kapitelText.setText("KAPITEL " + a.get(0).getChapter());
            this.addView(kapitelText);
            int counter = this.getChildCount();
            for(int i = 0; i < a.size(); i++){

                if(a.get(i).getParent()!=null){
                    ((ViewGroup) a.get(i).getParent()).removeView(a.get(i));
                }
                if(!a.get(i).isChecked()) {
                    this.addView(a.get(i), counter);
                    counter++;
                }
                else{
                    this.addView(a.get(i),counter);
                }
            }
        }
    }

    public void addTasksFromDatabase(DBAdapter dbAdapter, String courseCode) {

        Cursor cursor = dbAdapter.getAssignments();

       /* ArrayList<StudyTask> checkedArray = new ArrayList<>();
        ArrayList<StudyTask> uncheckedArray = new ArrayList<>();*/

        TextView kapitelText = new TextView(this.getContext());

        if (cursor != null) {
            while (cursor.moveToNext()) {

                AssignmentStatus assignmentStatus;
                AssignmentType assignmentType;
                if(cursor.getString(cursor.getColumnIndex("status")).equals(AssignmentStatus.DONE.toString())){
                    assignmentStatus = AssignmentStatus.DONE;
                }
                else{
                    assignmentStatus = null;
                }
                if(cursor.getString(cursor.getColumnIndex("type")).equals(AssignmentType.READ.toString())){
                    assignmentType = AssignmentType.READ;
                }
                else{
                    assignmentType = AssignmentType.OTHER;
                }

                StudyTask studyTask = new StudyTask(
                        this.getContext(),
                        cursor.getInt(cursor.getColumnIndex("_id")),
                        cursor.getString(cursor.getColumnIndex("_ccode")),
                        cursor.getInt(cursor.getColumnIndex("chapter")),
                        cursor.getString(cursor.getColumnIndex("assNr")),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("startPage"))),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("stopPage"))),
                        dbAdapter,
                        assignmentType,
                        assignmentStatus);


                //initCheckbox(studyTask);

               /* if (studyTask.isChecked()) {
                    checkedArray.add(studyTask);
                } else
                    uncheckedArray.add(studyTask);*/
                if(studyTask.getType() == AssignmentType.OTHER) {

                    if (hashMapOfStudyTasks.containsKey(studyTask.getChapter())) {
                        hashMapOfStudyTasks.get(studyTask.getChapter()).add(studyTask);
                    } else {
                        ArrayList<StudyTask> a = new ArrayList();
                        a.add(studyTask);
                        hashMapOfStudyTasks.put(studyTask.getChapter(), a);
                    }

                }
                else {
                    if (hashMapOfReadingAssignments.containsKey(studyTask.getChapter())) {
                        hashMapOfReadingAssignments.get(studyTask.getChapter()).add(studyTask);
                    } else {
                        ArrayList<StudyTask> a = new ArrayList();
                        a.add(studyTask);
                        hashMapOfReadingAssignments.put(studyTask.getChapter(), a);
                    }
                }
            }

            addMap(hashMapOfStudyTasks);
            addMap(hashMapOfReadingAssignments);

        }
    }

}
