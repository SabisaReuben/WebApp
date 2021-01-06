package co.ciao.bluclub.activity;

import android.app.Service;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import co.ciao.bluclub.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.setting_preference);
    }

}
