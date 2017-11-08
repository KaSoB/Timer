package com.example.kamil.timer.interfaces;

import android.content.SharedPreferences;

public interface ISharedPreferences{
    void onSaveSharedPreferences(SharedPreferences.Editor editor);
    void onRestoreSharedPreferences(SharedPreferences prefs);
}
