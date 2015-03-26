package se.chalmers.datx02_15_36.studeraeffektivt;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import se.chalmers.datx02_15_36.studeraeffektivt.activity.MainActivity;

public class testCalendarFrag extends ActivityInstrumentationTestCase2 <MainActivity>{
    private Solo solo;

    private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "MainActivity";


    public testCalendarFrag() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testDisplayWhiteBox() {

        //Defining our own values to multiply
        float vFirstNumber = 10;
        float vSecondNumber = 20;
        float vResutl = vFirstNumber * vSecondNumber ;

        //Access First value (edit-filed) and putting firstNumber value in it
       /* EditText vFirstEditText = (EditText) solo.getView(R.id.EditText01);
        solo.clearEditText(vFirstEditText);
        solo.enterText(vFirstEditText, String.valueOf(vFirstNumber));

        //Access Second value (edit-filed) and putting SecondNumber value in it
        EditText vSecondEditText = (EditText) solo.getView(R.id.EditText02);
        solo.clearEditText(vSecondEditText);
        solo.enterText(vSecondEditText, String.valueOf(vSecondNumber));

        //Click on Multiply button
        solo.clickOnButton("Multiply");

        assertTrue(solo.searchText(String.valueOf(vResutl)));
        TextView outputField = (TextView) solo.getView(R.id.TextView01);
        //Assert to verify result with visible value
        assertEquals(String.valueOf(vResutl), outputField.getText().toString());*/
    }

    @Override
    protected void tearDown() throws Exception{

        solo.finishOpenedActivities();
    }
}