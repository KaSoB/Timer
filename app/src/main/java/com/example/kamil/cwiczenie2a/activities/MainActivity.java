package com.example.kamil.cwiczenie2a.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kamil.cwiczenie2a.R;
import com.example.kamil.cwiczenie2a.adapter.TimeAdapter;
import com.example.kamil.cwiczenie2a.models.LapTime;
import com.example.kamil.cwiczenie2a.models.Timer;

import java.util.LinkedList;

/*
Zadanie 1 (100 pkt)
Stworzyć aplikacje o nazwie stoper. Aplikacja ta powinna umożliwiać/posiadać:

Przyciski: Start, Lap, Stop/Clear (przy czym powinien być jeden przycisk STOP / CLEAR (jeżeli stoper działa to widoczne jest Stop, a jeżeli nie to Clear)
Widoczność aktualnego czasu od początku oraz listę czasów poszczególnych okrążeń


Zadanie 2 (50 pkt)
Dodaj do wcześniej stworzonego Stopera przycisk "Pomoc" w AppBar. Przycisk powinien otwierać domyślną przeglądarkę na wybranej wcześniej ustalonej stronie WWW (np. http://google.pl).
Uwaga: W docelowej aplikacji zawsze będzie można tutaj wstawić konkretny link do pomocy.


Zadanie 3 (50 pkt)
Stwórz program, który nasłuchuje na przychodzący SMS i automatycznie wyświetla go w danej aktywności.
UWAGA: Zastanów się czy BroadcastReceiver lepiej w takim przypadku rejestrować w pliku AndroidManifest.xml czy w samej aktywności.


Zadanie 4 (50 pkt)
Dodaj do aplikacji "Stoper" mechanizmu zapisującego w SharedPreferences stanu stopera (włączony/wyłączony) tak by działał również podczas wyłączenia aplikacji a nawet telefonu. W jakich metodach powinno nastąpić zapisanie stanu?


Zadanie 5 (50 pkt)
Dodaj do aplikacji "Stoper" tzw. PreferenceActicvity uruchamiane z AppBar. W ustawieniach powinna istnieć możliwość wybrania ile ostatnich okrążeń powinno być widocznych na ekranie oraz czy powinny być widoczne czasu +- porównanie ostatniego okrążenia.


Zadanie 6 (50 pkt)
Dodaj do aplikacji "Stoper" zapisywanie ostatnio mierzonego czasu (tylko cały czas, bez okrążeń) wraz z datą kiedy to zostało wykonane. Czas powinien być zapisywany po naciśnięciu przycisku CLEAR. W {AppBar} dodać przycisk "statystyki" w których widoczne będą czasy odczytane z bazy danych.

 */
public class MainActivity extends AppCompatActivity {

    Timer timer;
    TimeAdapter timeAdapter;

    // LAYOUT
    TextView timerTextView;
    ListView timerListView;

    Button startButton;
    Button lapButton;
    Button stopClearButton;
    // END_LAYOUT


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add toolbar to activity_main
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Pole textView na którym wyświetlony zostanie czas
        timerTextView = (TextView) findViewById(R.id.TimerTextView);

        // Klasa odpowiedzialna za zarządzanie timer'em
        timer = new Timer(timerTextView);

        // Wczytaj dane z preferencji
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // TODO: wczytać int, a nie string
        int sizeLimit;
        boolean showTD;
        try{
             sizeLimit = Integer.parseInt(SP.getString("numOfLaps", "0"));
             showTD = SP.getBoolean("dwadaw",false);
        } catch (NumberFormatException e){
            // TODO: WArtośći z resource
            sizeLimit = 30;
            showTD = true;
        }

        timeAdapter = new TimeAdapter(this, new LinkedList<LapTime>(),sizeLimit,showTD);
        timerListView = (ListView)findViewById(R.id.TimerListView);
        timerListView.setAdapter(timeAdapter);

        // Add headers to listview
        ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.headerslayout,timerListView,false);
        timerListView.addHeaderView(headerView);

        // Przycisk do dodawania międzyczasów
        lapButton = (Button) findViewById(R.id.LapButton);

        // Przycisk do zatrzymywania/resetowania czasu
        stopClearButton = (Button) findViewById(R.id.StopClearButton);

        // Przycisk do uruchamiania czasu
        startButton = (Button) findViewById(R.id.StartButton);



        onRestoreSharedPreferences();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Wczytaj dane z preferencji
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        int sizeLimit;
        boolean showTD;
        try{
            // TODO: wczytać int, a nie string
            sizeLimit = Integer.parseInt(SP.getString("numOfLaps", "0"));
            showTD = SP.getBoolean("dwadaw",false);
        } catch (NumberFormatException e){
            // TODO: WArtośći z resource
            sizeLimit = 30;
            showTD = true;
        }
        timeAdapter.setSizeLimit(sizeLimit);
        timeAdapter.setShowTimeDifference((showTD));
        timeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        onSaveSharedPreferences();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.HelpItemMenu:
                // Wybranie HelpItemMenu przenosi nas na stronę google.pl
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.pl"));
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
                break;

            case R.id.SettingsItemMenu:
                // Wybranie SettingsItemMenu przenosi nas na stronę Preferencji
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                break;

            case R.id.StatsItemMenu:
                // TODO: Cwiczenie 6
                break;

            default:
                // leave empty
        }
        return super.onOptionsItemSelected(item);
    }

    private void onRestoreSharedPreferences() {
        Log.v("a", ":)");
        SharedPreferences prefs = getSharedPreferences("com.example.kamil.cwiczenie2a.activities", Context.MODE_PRIVATE);
        if(prefs.getString("TimerTextView",null) != null){
            timerTextView.setText(prefs.getString("TimerTextView","empty"));
            stopClearButton.setText(prefs.getString("StopClearButton","empty"));
            timer.onRestoreSharedPreferences(prefs);
            timeAdapter.onRestoreSharedPreferences(prefs);
        }
    }
    private void onSaveSharedPreferences() {
        Log.v("a", ":))");
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.kamil.cwiczenie2a.activities", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TimerTextView",timerTextView.getText().toString());
        editor.putString("StopClearButton",stopClearButton.getText().toString());
        timer.onSaveSharedPreferences(editor);
        timeAdapter.onSaveSharedPreferences(editor);
        editor.apply();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v("a", ":))d");
        super.onRestoreInstanceState(savedInstanceState);
        timerTextView.setText(savedInstanceState.getString("TimerTextView"));
        timer = savedInstanceState.getParcelable("Timer");
        timer.SetTextView(timerTextView);
     //   ArrayList<LapTime> p = savedInstanceState.getParcelableArrayList("LapTimes");
      //  timeAdapter.getItems().addAll(p);
        stopClearButton.setText(savedInstanceState.getString("StopClearButton"));
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v("a", ":))33");
        super.onSaveInstanceState(outState);
        outState.putString("TimerTextView",timerTextView.getText().toString());
        outState.putParcelable("Timer",timer);
       outState.putString("StopClearButton",stopClearButton.getText().toString());
    }

    public void startButtonClick(View v){
        if(!timer.IsRunning()){
            timer.Start();
            stopClearButton.setText(R.string.Stop);
        }
    }

    public void stopClearButtonClick(View v){
        if(timer.IsRunning()){
            timer.Stop();
            stopClearButton.setText(R.string.Clear);
        } else {
            // ... when stopClearButton shows "Clear"
            timer.Reset();
            timeAdapter.clear();
            timeAdapter.notifyDataSetChanged();
            timerTextView.setText(R.string.NullTime);
        }
    }

    public void lapButtonClick(View v){
        timeAdapter.add(timer.NewLapTime());
        timeAdapter.notifyDataSetChanged();
    }
}
