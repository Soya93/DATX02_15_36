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

    public String readFromFile(String fileName) {
        AssetManager am = context.getAssets();
        String receiveString = "";

        try {
            InputStream inputStream = am.open(fileName +".txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String currentString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (currentString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(currentString);
                }

                inputStream.close();
                receiveString = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("tip handler", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("tip handler", "Can not read file: " + e.toString());
        }

        return receiveString;

    }
}
