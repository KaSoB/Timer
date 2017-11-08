package com.example.kamil.timer.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.kamil.timer.R;

/**
 * Created by Kamil on 08.11.2017.
 */

public class PreferencesData {
    private int sizeLimit;
    private boolean showIntermediateTimes;
    public PreferencesData(){ }
    public void setShowIntermediateTimes(boolean v){
        showIntermediateTimes = v;
    }
    public void setSizeLimit(int v){
        sizeLimit = v;
    }
    public boolean getShowIntermediateTimes(){
        return showIntermediateTimes;
    }
    public int getSizeLimit(){
        return sizeLimit;
    }

    public static PreferencesData loadPreferences(Activity activity){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        PreferencesData data = new PreferencesData();
        try {
            data.setSizeLimit(Integer.parseInt(sharedPreferences.getString("NumOfLaps", "0")));
        } catch (NumberFormatException e){
            Log.e("NumberFormatException", e.getMessage());
            // Load default value
            data.setSizeLimit(Integer.parseInt(activity.getResources().getString(R.string.numOfLapsDefaultValue)));
        }
         data.setShowIntermediateTimes(sharedPreferences.getBoolean("ShowIntermediateTimes",true));

        return data;
    }
}
