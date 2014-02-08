package com.madwin.coordinaterecorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;


/*
 * Created by Andrew on 7/25/13.
 * Preferences Activity
 *
 */
 public class PreferencesUI extends PreferenceActivity {

    //String TAG = "MWCalc";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }




    public boolean onOptionsItemSelected(MenuItem item){
        Intent back_to_main = new Intent(this, MainActivity.class);
        this.startActivity(back_to_main);
        finish();
        return true;

    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent back_to_main = new Intent(this, MainActivity.class);
            this.startActivity(back_to_main);
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }
}

