package com.example.kamil.cwiczenie2a.interfaces;

import android.content.SharedPreferences;

public interface ISharedPreferences{
    void onSaveSharedPreferences(SharedPreferences.Editor editor);
    void onRestoreSharedPreferences(SharedPreferences prefs);
}
