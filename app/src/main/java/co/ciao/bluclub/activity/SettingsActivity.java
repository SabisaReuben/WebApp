package co.ciao.bluclub.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;

import co.ciao.bluclub.R;

public class SettingsActivity extends AppCompatActivity {
    private Fragment settingFragment = new SettingsFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settngs);
        init();
        addFragment();
    }

    private void init() {
        MaterialToolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar;
        if ((actionBar = getSupportActionBar()) != null) {
            actionBar.setHomeAsUpIndicator(null);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, settingFragment).commit();
    }
}