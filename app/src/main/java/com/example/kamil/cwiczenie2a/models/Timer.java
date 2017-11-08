package com.example.kamil.cwiczenie2a.models;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.example.kamil.cwiczenie2a.R;
import com.example.kamil.cwiczenie2a.interfaces.ISharedPreferences;

import java.util.Locale;

/**
 * Created by Kamil on 15.10.2017.
 */

public class Timer implements Parcelable, ISharedPreferences {
    private boolean IsRunning = false;

    // Czas w danym cyklu timera, wyrażony w milisekundach.
    private long totalTimeMilliseconds = 0L;

    // Czas jaki zmierzono w momencie uruchomienia timera.
    private long startTime = 0L;

    // Czas jaki został zmierzony z chwilą zatrzymania timera.
    // Służy do uwzględniania zmierzonego czasu w następnych cyklach po ponownym uruchomieniu timera.
    private long timeStopPoint = 0L;



    private static final int TimerUpdateInMilliseconds = 60;


    // TextView na którym zostanie wyświetlony czas
    private TextView timerTextView;

    public Timer(TextView timerTextView ){
        this.timerTextView = timerTextView;
    }
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            totalTimeMilliseconds = SystemClock.uptimeMillis() - startTime + timeStopPoint;
            SetTimer(Transform(totalTimeMilliseconds));
            timerHandler.postDelayed(this, TimerUpdateInMilliseconds);
        }
    };




    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Timer> CREATOR = new Creator<Timer>() {
        @Override
        public Timer createFromParcel(Parcel in) {
            return new Timer(in);
        }

        @Override
        public Timer[] newArray(int size) {
            return new Timer[size];
        }
    };

    public void SetTextView(TextView timerTextView){
        this.timerTextView = timerTextView;
    }

    public void Start(){
        if(!IsRunning){
            IsRunning = true;
            startTime = SystemClock.uptimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }
    public void Stop(){
        if(IsRunning){
            IsRunning = false;
            timerHandler.removeCallbacks(timerRunnable);
            timeStopPoint = totalTimeMilliseconds;
        }
    }
    public void Reset(){
        timerHandler.removeCallbacks(timerRunnable);
        IsRunning = false;
        timeStopPoint = startTime = totalTimeMilliseconds =
                lapTimePoint= previousLapTimePoint = previousTotalTimeMilliseconds = 0;
        SetTimer(R.string.NullTime);
    }
    public boolean IsRunning(){
        return IsRunning;
    }
    private String Transform(long timeInMilliseconds){
        long milliseconds = timeInMilliseconds;
        int seconds = (int)(milliseconds / 1000);
        int minutes = seconds / 60;

        milliseconds = milliseconds % 1000;
        seconds = seconds % 60;
        minutes = minutes % 60;

        return String.format(Locale.UK, "%02d:%02d.%03d", minutes, seconds, milliseconds);
    }

    private long previousTotalTimeMilliseconds = 0L;
    private long previousLapTimePoint = 0L;
    private long lapTimePoint = 0L;

    public LapTime NewLapTime(){
        // TODO: Zapytać o thread lock
        previousLapTimePoint = lapTimePoint;
        lapTimePoint =  totalTimeMilliseconds - previousTotalTimeMilliseconds;
        previousTotalTimeMilliseconds = totalTimeMilliseconds;

        long lapTimeDifference = Math.abs(lapTimePoint - previousLapTimePoint);
        return new LapTime(Transform(totalTimeMilliseconds),Transform(lapTimePoint),Transform(lapTimeDifference));
    }

    private void SetTimer(String time){
        timerTextView.setText(time);
    }
    private void SetTimer(int textID){
        timerTextView.setText(textID);
    }
    protected Timer(Parcel in) {
        IsRunning = in.readByte() != 0;
        totalTimeMilliseconds = in.readLong();
        startTime = in.readLong();
        timeStopPoint = in.readLong();
        previousTotalTimeMilliseconds = in.readLong();
        previousLapTimePoint = in.readLong();
        lapTimePoint = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (IsRunning ? 1 : 0));
        dest.writeLong(totalTimeMilliseconds);
        dest.writeLong(startTime);
        dest.writeLong(timeStopPoint);
        dest.writeLong(previousTotalTimeMilliseconds);
        dest.writeLong(previousLapTimePoint);
        dest.writeLong(lapTimePoint);
    }
    public void onSaveSharedPreferences(SharedPreferences.Editor editor){
        editor.putLong("StartTime", startTime);
        editor.putLong("TimeStopPoint", timeStopPoint);
        editor.putLong("PreviousTotalTimeMilliseconds", previousTotalTimeMilliseconds);
        editor.putBoolean("IsRunning",IsRunning);
        editor.putLong("TotalTimeMilliseconds",totalTimeMilliseconds);
        editor.putLong("PreviousLapTimePoint",previousLapTimePoint);
        editor.putLong("LapTimePoint",lapTimePoint);

    }
    public void onRestoreSharedPreferences(SharedPreferences prefs){
        previousTotalTimeMilliseconds = prefs.getLong("PreviousTotalTimeMilliseconds",0);
        startTime = prefs.getLong("StartTime",0);
        timeStopPoint = prefs.getLong("TimeStopPoint",0);
        totalTimeMilliseconds = prefs.getLong("TotalTimeMilliseconds",0);
        IsRunning = prefs.getBoolean("IsRunning",false);
        previousLapTimePoint = prefs.getLong("PreviousLapTimePoint",0);
        lapTimePoint = prefs.getLong("LapTimePoint",0);
        if(IsRunning){
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }
}
