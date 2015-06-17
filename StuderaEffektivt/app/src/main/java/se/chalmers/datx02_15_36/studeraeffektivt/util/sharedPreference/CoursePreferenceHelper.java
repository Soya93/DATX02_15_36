package se.chalmers.datx02_15_36.studeraeffektivt.util.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Spinner;

/**
 * Created by Patricia on 2015-06-16.
 */
public class CoursePreferenceHelper {
    private static CoursePreferenceHelper instance;

    private SharedPreferences sharedPref;
    private Context ctx;

    //CONSTANTS for preference key names with their attribute names underneath.
    public final static String CCODE_PREF_NAME = "CoursePref";
    public final static String CCODE_POSITION = "course";
    public final static int DEFAULT_CCODE_POSITION = 122;
    //CONSTANTS end.

    public static CoursePreferenceHelper getInstance(Context ctx) {
        if(instance == null) {
            instance = new CoursePreferenceHelper(ctx);
        }
        return instance;
    }

    private CoursePreferenceHelper(Context ctx) {
        this.ctx = ctx;
    }

    public void setSharedCoursePos(int pos){
        sharedPref = ctx.getSharedPreferences(CCODE_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(CCODE_POSITION, pos);
        editor.commit();
    }

    public int getSharedCoursePos(){
        sharedPref = ctx.getSharedPreferences(CCODE_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(CCODE_POSITION, DEFAULT_CCODE_POSITION);
    }

    public void setSpinnerCourseSelection(Spinner spinner){
        if(getSharedCoursePos() != DEFAULT_CCODE_POSITION){
            spinner.setSelection(getSharedCoursePos());
        }else {
            spinner.setSelection(0);
        }
    }
}
