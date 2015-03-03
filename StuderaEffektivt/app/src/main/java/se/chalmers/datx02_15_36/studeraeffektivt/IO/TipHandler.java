package se.chalmers.datx02_15_36.studeraeffektivt.IO;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by SoyaPanda on 15-03-03.
 */
public class TipHandler {

    Context context;

    public TipHandler(Context context){
        this.context = context;
    }

    private void readFromFile(String fileName) {
        AssetManager am = context.getAssets();
        try {
            InputStream inputStream = context.openFileInput(fileName +".txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                    //TODO do something
                    //  Log.i("password in readFromFile", password);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("tip handler", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("tip handler", "Can not read file: " + e.toString());
        }
    }
}
