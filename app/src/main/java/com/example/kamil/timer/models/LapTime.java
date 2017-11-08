package com.example.kamil.timer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kamil on 02.11.2017.
 */

public class LapTime {
    public static final String TotalTimeName = "TotalTime";
    public static final String LapTimeName = "LapTime";
    public static final String IntermediateTimeName = "IntermediateTime";

    private String totalTime;
    private String lapTime;
    private String intermediateTime;
    public LapTime(String totalTime, String lapTime, String intermediateTime){
        this.totalTime = totalTime;
        this.lapTime = lapTime;
        this.intermediateTime = intermediateTime;
    }
    protected LapTime(Parcel in) {
        totalTime = in.readString();
        lapTime = in.readString();
        intermediateTime = in.readString();
    }
    public String getTotalTime(){
        return totalTime;
    }
    public String getLapTime(){
        return lapTime;
    }
    public String getIntermediateTime(){
        return intermediateTime;
    }


}
