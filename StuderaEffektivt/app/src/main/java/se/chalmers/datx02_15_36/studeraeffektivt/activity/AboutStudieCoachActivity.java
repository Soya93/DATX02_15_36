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

package se.chalmers.datx02_15_36.studeraeffektivt.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.datx02_15_36.studeraeffektivt.R;
import se.chalmers.datx02_15_36.studeraeffektivt.util.Colors;
import se.chalmers.datx02_15_36.studeraeffektivt.util.service.ServiceHandler;

public class AboutStudieCoachActivity extends ActionBarActivity {

    android.support.v7.app.ActionBar actionBar;


    //Webconnection
    private String URL_CONNECTION = "http://studiecoachchalmers.se/php/mobile/StudieCoachUsers.php";
    private String email;
    private boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutstudiecoach);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Om StudieCoach");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Colors.primaryColor)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about_studiecoach, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_evaluator:
                addEvaluator();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addEvaluator() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Ange din chalmersmail (CID@student.chalmers.se)");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                setEmail(input.getText().toString());
                new addEmailToWeb().execute();
            }
        });

        builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
        Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setTextColor(Color.parseColor(Colors.primaryDarkColor));
    }

    private void setEmail(String email){
        this.email = email;
    }

    private String getEmail(){
       return this.email;
    }

    private void showToast(){
        if(success){
            Toast.makeText(getBaseContext(), "Din emailadress har registrerats. Tack för din hjälp!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Gick inte att registrera emailadressen. Kontrollera att du har internetuppkoppling", Toast.LENGTH_LONG).show();
        }
    }

    private class addEmailToWeb extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));

            ServiceHandler sh = new ServiceHandler();

            String jsonStr = sh.makeServiceCall(URL_CONNECTION, ServiceHandler.POST, params);
            Log.i("OMGASHU", "email from phone" + getEmail());


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray addedRow = jsonObj.getJSONArray("addedRow");

                    Log.i("OMGASHU", "email from phone" + getEmail());

                    for (int i = 0; i < addedRow.length(); i++) {
                        JSONObject c = addedRow.getJSONObject(i);
                        String email = c.getString("email");
                        Log.i("OMGASHU", "email from web" + email);
                        Log.i("OMGASHU", "email from phone" + getEmail());
                        success = email.equals(getEmail());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        protected void onPostExecute(String file_url) {
            showToast();
        }
    }


}

