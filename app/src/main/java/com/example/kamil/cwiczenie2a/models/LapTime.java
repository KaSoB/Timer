package com.example.kamil.cwiczenie2a.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kamil on 02.11.2017.
 */

public class LapTime implements Parcelable{
    private String totalTime;
    private String lapTime;
    private String lapTimeDifference;
    public LapTime(String totalTime, String lapTime, String lapTimeDifference){
        this.totalTime = totalTime;
        this.lapTime = lapTime;
        this.lapTimeDifference = lapTimeDifference;
    }

    protected LapTime(Parcel in) {
        totalTime = in.readString();
        lapTime = in.readString();
        lapTimeDifference = in.readString();
    }

    public static final Creator<LapTime> CREATOR = new Creator<LapTime>() {
        @Override
        public LapTime createFromParcel(Parcel in) {
            return new LapTime(in);
        }

        @Override
        public LapTime[] newArray(int size) {
            return new LapTime[size];
        }
    };

    public String getTotalTime(){
        return totalTime;
    }
    public String getLapTime(){
        return lapTime;
    }
    public String getLapTimeDifference(){
        return lapTimeDifference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(totalTime);
        dest.writeString(lapTime);
        dest.writeString(lapTimeDifference);
    }
}
