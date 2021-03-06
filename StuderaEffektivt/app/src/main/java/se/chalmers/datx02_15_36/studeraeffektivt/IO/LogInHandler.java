package se.chalmers.datx02_15_36.studeraeffektivt.IO;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by SoyaPanda on 15-02-27.
 */
public class LogInHandler {
    String email;
    String password;
    String fileName = "logInFile";
    public static FileOutputStream outputStream;
    Context context;

    public LogInHandler(Context context){
        this.context = context;
    }

    public void writeToFile(String email, String password){
        String text = "\nGoogleAccount\n" + "\nEmail:\n" + email + "\nPassword:\n" + password;
        try {
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readFromFile() {
        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                    if (receiveString.equals("Email:")){
                        if( (receiveString = bufferedReader.readLine()) != null){
                            email = receiveString;
                        }
                       // Log.i("email in readFromFile", email);
                    }else  if (receiveString.equals("Password:")){
                        if( (receiveString = bufferedReader.readLine()) != null){
                                password = receiveString;
                         }
                      //  Log.i("password in readFromFile", password);
                    }
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    public String getEmail(){
        this.readFromFile();
        return email;
    }

    public String getPassword(){
        this.readFromFile();
        return password;
    }
}
