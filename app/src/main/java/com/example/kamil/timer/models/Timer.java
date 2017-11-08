package com.example.kamil.timer.models;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.example.kamil.timer.R;
import com.example.kamil.timer.interfaces.ISharedPreferences;

import java.util.Locale;

/**
 * Created by Kamil on 15.10.2017.
 */

public class Timer implements ISharedPreferences {
    private boolean IsRunning = false;

    // Czas w danym cyklu timera, wyrażony w milisekundach.
    private long totalTimeMilliseconds = 0L;

    // Czas jaki zmierzono w momencie uruchomienia timera.
    private long startTime = 0L;

    // Czas jaki został zmierzony z chwilą zatrzymania timera.
    // Służy do uwzględniania zmierzonego czasu w następnych cyklach po ponownym uruchomieniu timera.
    private long timeStopPoint = 0L;

    private long previousTotalTimeMilliseconds = 0L;
    private long previousLapTimePoint = 0L;
    private long lapTimePoint = 0L;

    // TextView na którym zostanie wyświetlony czas
    private TextView timerTextView;

    private static final int TimerUpdateInMilliseconds = 100;

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

    public LapTime NewLapTime(){
        // TODO: Zapytać o thread lock
        previousLapTimePoint = lapTimePoint;
        lapTimePoint =  totalTimeMilliseconds - previousTotalTimeMilliseconds;
        previousTotalTimeMilliseconds = totalTimeMilliseconds;

        long intermediateTime = Math.abs(lapTimePoint - previousLapTimePoint);
        return new LapTime(Transform(totalTimeMilliseconds),Transform(lapTimePoint),Transform(intermediateTime));
    }
    private void SetTimer(String time){
        timerTextView.setText(time);
    }
    private void SetTimer(int textID){
        timerTextView.setText(textID);
    }

    public void onSaveSharedPreferences(SharedPreferences.Editor editor){
        timerHandler.removeCallbacks(timerRunnable);
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
