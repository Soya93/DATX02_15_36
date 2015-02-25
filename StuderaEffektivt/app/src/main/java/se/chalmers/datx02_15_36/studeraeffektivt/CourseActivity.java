package se.chalmers.datx02_15_36.studeraeffektivt;
/* Saker att göra
toString i Course - för att skicka till annan aktivitet och för att skriva ut bättre i listan
Fortsätta på onclick till listview

 */
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CourseActivity extends ActionBarActivity {

    private ListView listOfCourses;
    private Button addButton;
    private Button addButtonInner;
    private EditText nameEditText;
    private EditText codeEditText;
    List<Map<String, Course>> courseList = new ArrayList<Map<String,Course>>();
    SimpleAdapter simpleAdpt;
    LinearLayout popUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initComponents();
        initList();
        simpleAdpt = new SimpleAdapter(this, courseList, android.R.layout.simple_list_item_1, new String[] {"Courses"}, new int[] {android.R.id.text1});
        listOfCourses.setAdapter(simpleAdpt);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initList(){
        courseList.add(createCourse("Courses", new Course("Objektorienterad", "TDA043")));
        courseList.add(createCourse("Courses", new Course("Design", "DAT216")));
    }


    private HashMap<String, Course> createCourse(String key, Course course) {
        HashMap<String, Course> newCourse = new HashMap<String, Course>();
        newCourse.put(key, course);

        return newCourse;
    }

    public void updateList(String group, Course course){
        HashMap<String, Course> a = createCourse(group, course);
        courseList.add(a);
    }

    public void setAddButton(){
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (popUp.getVisibility() == View.INVISIBLE) {
                    popUp.setVisibility(View.VISIBLE);
                    addButton.setText("Show");
                } else {
                    popUp.setVisibility(View.INVISIBLE);
                    addButton.setText("Lägg till kurs");
                }
            }
        });

    }

    public void setAddButtonInner(){
        addButtonInner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.setVisibility(View.INVISIBLE);
                addButton.setText("Lägg till kurs");

                updateList("Courses", new Course(nameEditText.getText().toString(), codeEditText.getText().toString()));
                adapter();
            }

        });
    }

    public void setListOfCourses() {
        listOfCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id){

                HashMap course = (HashMap) parent.getItemAtPosition(position);
                Course course1 = (Course) course.get("Courses");
                Intent intent = new Intent(CourseActivity.this, CourseDetails.class);
                String courseString = course.toString();
                intent.putExtra("kurs", courseString);
                Log.d("HHHHHHHHHHHHHH",courseString);
                startActivity(intent);
                // Start your Activity according to the item just clicked.
            }
        });
    }

    public void adapter(){
        simpleAdpt = new SimpleAdapter(this, courseList, android.R.layout.simple_list_item_1, new String[] {"Courses"}, new int[] {android.R.id.text1});
        listOfCourses.setAdapter(simpleAdpt);
    }

    public void initComponents(){
        addButton = (Button) findViewById(R.id.addButton);
        listOfCourses = (ListView) findViewById(R.id.listView);
        popUp = (LinearLayout) findViewById(R.id.linLayout);
        addButtonInner = (Button) findViewById(R.id.addButtonInner);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        codeEditText = (EditText) findViewById(R.id.codeEditText);

        setAddButton();
        setAddButtonInner();
        setListOfCourses();
    }

}
