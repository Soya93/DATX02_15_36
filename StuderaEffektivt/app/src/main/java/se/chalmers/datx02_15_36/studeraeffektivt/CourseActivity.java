package se.chalmers.datx02_15_36.studeraeffektivt;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CourseActivity extends ActionBarActivity {

    private ListView courseList;
    private Button addButton;
    List<Map<String, String>> planetsList = new ArrayList<Map<String,String>>();
    SimpleAdapter simpleAdpt;
    ArrayAdapter simpleAdpt2;
    LinearLayout popUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        addButton = (Button) findViewById(R.id.addButton);
        initList();
        courseList = (ListView) findViewById(R.id.listView);
        popUp = (LinearLayout) findViewById(R.id.linLayout);
        //courseList.
        //courseList.add
        setAddButton();
        //simpleAdpt2 = new ArrayAdapter(this, planetsList, android.R.layout.simple_list_item_1, new String[] {"planet"}, new int[] {android.R.id.text1})
        simpleAdpt = new SimpleAdapter(this, planetsList, android.R.layout.simple_list_item_1, new String[] {"planet"}, new int[] {android.R.id.text1});
        courseList.setAdapter(simpleAdpt);

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
        planetsList.add(createPlanet("planet", "Mercury"));
        planetsList.add(createPlanet("planet", "Venus"));
        planetsList.add(createPlanet("planet", "Mars"));
        planetsList.add(createPlanet("planet", "Jupiter"));
        planetsList.add(createPlanet("planet", "Saturn"));
        planetsList.add(createPlanet("planet", "Uranus"));
        planetsList.add(createPlanet("planet", "Neptune"));
    }


    private HashMap<String, String> createPlanet(String key, String name) {
        HashMap<String, String> planet = new HashMap<String, String>();
        planet.put(key, name);

        return planet;
    }

    public void updateList(String planet, String whichPlanet){
        HashMap<String, String> a = createPlanet(planet, whichPlanet);
        planetsList.add(a);
    }

    public void setAddButton(){
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(popUp.getVisibility()==View.INVISIBLE)
                    popUp.setVisibility(View.VISIBLE);
                else
                    popUp.setVisibility(View.INVISIBLE);

                updateList("planet", "Earth");
                adapter();
            }

        });

    }

    public void adapter(){
        simpleAdpt = new SimpleAdapter(this, planetsList, android.R.layout.simple_list_item_1, new String[] {"planet"}, new int[] {android.R.id.text1});
        courseList.setAdapter(simpleAdpt);
    }

}
