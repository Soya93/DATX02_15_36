package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.database.DBAdapter;
import se.chalmers.datx02_15_36.studeraeffektivt.model.StudyTask2;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentStatus;
import se.chalmers.datx02_15_36.studeraeffektivt.util.AssignmentType;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Constants;
import se.chalmers.datx02_15_36.studeraeffektivt.util.ServiceHandler;
import se.chalmers.datx02_15_36.studeraeffektivt.view.FlowLayout;

/**
 * Created by alexandraback on 06/05/15.
 */
public class GetAssignmetsFromWeb extends ActionBarActivity {

    private String courseName;
    private String courseCode;
    private String URL_CONNECTION = "http://studiecoachchalmers.se/getassignmets2.php";
    private DBAdapter dbAdapter;
    private FlowLayout taskListfromWeb;
    private HashMap<Integer, StudyTask2> assignmetsHashMap = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getassignmetsfromweb);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        courseName = getIntent().getStringExtra("CourseName");
        courseCode = getIntent().getStringExtra("CourseCode");
        taskListfromWeb = (FlowLayout) findViewById(R.id.taskListfromWeb);
        actionBar.setTitle("Hämta uppgifter " + courseName);
        if (this != null) {
            dbAdapter = new DBAdapter(this);
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Constants.primaryColor)));

        new GetAllAssignments().execute(courseCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_event) {
            this.finish();
            return true;
        }else if (id == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class GetAllAssignments extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            String courseCode = args[0];

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("courseCode", courseCode));


            ServiceHandler sh = new ServiceHandler();


            String jsonStr = sh.makeServiceCall(URL_CONNECTION, ServiceHandler.POST, params);
            if (jsonStr != null) {
                try {
                    //Log.e("BAJS",jsonStr);
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray assignmetsList = jsonObj.getJSONArray("assignments");

                    // looping through All Contacts
                    for (int i = 0; i < assignmetsList.length(); i++) {
                        JSONObject c = assignmetsList.getJSONObject(i);

                        String returnedCod = c.getString("course");

                        String chapter = c.getString("chapter");
                        String week = c.getString("week");
                        String assNr = c.getString("assNr");
                        String startPage = c.getString("startPage");
                        String endPage = c.getString("endPage");
                        String type = c.getString("type");
                        String status = c.getString("status");
                        AssignmentType status1;
                        if (status.equals("READ")) {
                            status1 = AssignmentType.READ;
                        } else {
                            status1 = AssignmentType.OTHER;

                        }
                        StudyTask2 studyTask2 = new StudyTask2(getBaseContext(), returnedCod, Integer.parseInt(chapter),
                                Integer.parseInt(week), assNr, Integer.parseInt(startPage), Integer.parseInt(endPage), dbAdapter, status1
                                , AssignmentStatus.UNDONE);
                        assignmetsHashMap.put(studyTask2.getIdNr(), studyTask2);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            taskListfromWeb.removeAllViews();
            Iterator it = assignmetsHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                StudyTask2 test = (StudyTask2) pair.getValue();

                taskListfromWeb.addTasksFromWeb(test.getIdNr(), test.getCourseCode(),
                        test.getChapter(), test.getWeek(), test.getTaskString(), test.getStartPage(),
                        test.getEndPage(), "UNDONE", "READ", dbAdapter);
                it.remove();

            }

        }


    }
}
