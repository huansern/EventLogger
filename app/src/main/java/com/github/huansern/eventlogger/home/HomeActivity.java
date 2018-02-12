package com.github.huansern.eventlogger.home;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.huansern.eventlogger.R;
import com.github.huansern.eventlogger.ViewModelFactory;
import com.github.huansern.eventlogger.service.EventLoggingService;
import com.github.huansern.eventlogger.settings.SettingsActivity;

public class HomeActivity extends AppCompatActivity {

    private HomeViewModel mViewModel;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewModel = obtainViewModel(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        updateServiceIcon();
        return true;
    }

    public static HomeViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());

        return ViewModelProviders.of(activity, factory).get(HomeViewModel.class);
    }

    public void refreshLogs(MenuItem item) {
        mViewModel.start();
    }

    public void setServiceStatus(MenuItem item) {
        if(!EventLoggingService.getStatus()) {
            EventLoggingService.start(this);
        } else {
            EventLoggingService.stop(this);
        }
        updateServiceIcon();
    }

    private void updateServiceIcon() {
        if(EventLoggingService.getStatus()) {
            mMenu.getItem(0).setIcon(R.drawable.ic_stop);
        } else {
            mMenu.getItem(0).setIcon(R.drawable.ic_play);
        }
    }

    public void openSettings(MenuItem item) {
        Intent settingIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingIntent);
    }
}
