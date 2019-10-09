package com.cradletrial.cradlevhtapp.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toolbar;

import com.cradletrial.cradlevhtapp.BuildConfig;
import com.cradletrial.cradlevhtapp.R;
import com.cradletrial.cradlevhtapp.view.ui.settings.SettingsActivity;

import java.util.Objects;

abstract public class TabActivityBase extends AppCompatActivity {
    protected static final int TAB_ACTIVITY_BASE_SETTINGS_DONE = 948;
    private int myTabButtonId;

    public TabActivityBase(int tabButtonId) {
        this.myTabButtonId = tabButtonId;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //changing the color for all the activity status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    /**
     * Bottom Bar Navigation
     */
    public void setupBottomBarNavigation() {
//        BottomNavigationView navView = findViewById(R.id.bottom_bar_nav);
//        navView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
//
//        String title = String.format("%s (%s)", getTitle(), BuildConfig.VERSION_NAME);
//        setTitle(title);
    }
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Do nothing if we are already there
        if (item.getItemId() == myTabButtonId) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.nav_readings:
                startActivity(ReadingsListActivity.makeIntent(this));
                finish();
                return true;
            case R.id.nav_patients:
                startActivity(PatientsActivity.makeIntent(this));
                finish();
                return true;
            case R.id.nav_upload:
                startActivity(UploadActivity.makeIntent(this));
                finish();
                return true;
            case R.id.nav_help:
                startActivity(HelpActivity.makeIntent(this));
                finish();
                return true;
        }
        return false;
    }
}
