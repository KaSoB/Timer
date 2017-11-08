package com.example.kamil.timer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.example.kamil.timer.R;
import com.example.kamil.timer.adapter.TimeAdapter;
import com.example.kamil.timer.data.PreferencesData;
import com.example.kamil.timer.models.LapTime;
import com.example.kamil.timer.models.Timer;

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
 */
public class MainActivity extends AppCompatActivity {
    public static final String SharedPreferencesName = "com.example.kamil.Timer";
    public static final String TimerTextViewName = "TimerTextView";
    public static final String StopClearButtonName = "StopClearButton";
    public static final String TimerName = "Timer";

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
        PreferencesData data = PreferencesData.loadPreferences(this);

        timeAdapter = new TimeAdapter(this, new LinkedList<LapTime>(), data.getSizeLimit(),
                data.getShowIntermediateTimes());

        timerListView = (ListView)findViewById(R.id.TimerListView);
        timerListView.setAdapter(timeAdapter);

        // Add headers to listview
        ViewGroup headerView = (ViewGroup)getLayoutInflater().inflate(R.layout.headerslayout,
                timerListView,false);
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
        PreferencesData data = PreferencesData.loadPreferences(this);
        timeAdapter.setSizeLimit(data.getSizeLimit());
        timeAdapter.setShowIntermediateTimes(data.getShowIntermediateTimes());
        timeAdapter.notifyDataSetChanged();
        onRestoreSharedPreferences();
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

            default:
                // leave empty
        }
        return super.onOptionsItemSelected(item);
    }
    // OnClick events are linked from activity_main.xml
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

    private void onRestoreSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        if(prefs.getString(TimerTextViewName,null) != null) {
            timerTextView.setText(prefs.getString(TimerTextViewName,"empty"));
            stopClearButton.setText(prefs.getString(StopClearButtonName,"empty"));
            timer.onRestoreSharedPreferences(prefs);
            timeAdapter.onRestoreSharedPreferences(prefs);
        }
    }
    private void onSaveSharedPreferences() {
        SharedPreferences prefs = this.getSharedPreferences(
                SharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TimerTextViewName,timerTextView.getText().toString());
        editor.putString(StopClearButtonName,stopClearButton.getText().toString());
        timer.onSaveSharedPreferences(editor);
        timeAdapter.onSaveSharedPreferences(editor);
        editor.apply();
    }


}
