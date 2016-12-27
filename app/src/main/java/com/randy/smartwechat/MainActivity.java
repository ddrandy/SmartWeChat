package com.randy.smartwechat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private Intent mSettingsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.open_settings, R.id.switch_option})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_settings:
                performOpenSettings();
                break;
            case R.id.switch_option:
                performSwitchOptions();
                break;
        }
    }

    private void performOpenSettings() {
        if (mSettingsIntent == null) {
            mSettingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        }
        startActivity(mSettingsIntent);
    }

    private void performSwitchOptions() {

    }
}
