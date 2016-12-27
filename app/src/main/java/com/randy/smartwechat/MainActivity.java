package com.randy.smartwechat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

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
                performOpenSettings()
                break;
            case R.id.switch_option:
                break;
        }
    }

    private void performOpenSettings() {

    }
}
