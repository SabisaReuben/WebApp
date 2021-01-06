package co.ciao.bluclub.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import co.ciao.bluclub.R;
import co.ciao.bluclub.widget.BluClubAlertDialog;

public class LauncherActivity extends AppCompatActivity  {
    private static CountrySelectionFragment countrySelectionFragment = new CountrySelectionFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        swipeFragments(countrySelectionFragment);
    }

    private void swipeFragments(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_launcher, fragment);
        fragmentTransaction.commit();
    }

}
