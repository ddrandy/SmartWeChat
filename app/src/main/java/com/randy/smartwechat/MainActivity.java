package com.randy.smartwechat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.randy.smartwechat.utils.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.randy.smartwechat.utils.Constants.TAG;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_notify_mark)
    EditText mEtNotifyMark;
    @BindView(R.id.et_receive_open)
    EditText mEtReceiveOpen;
    @BindView(R.id.et_receive_quit)
    EditText mEtReceiveQuit;
    @BindView(R.id.et_detail_quit)
    EditText mEtDetailQuit;
    private Intent mSettingsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        get_config();
        Log.d(TAG, "onCreate: ");
    }

    private void get_config() {
        mEtNotifyMark.setText(StringUtil.getNotifyMark());
        mEtDetailQuit.setText(StringUtil.getDetailQuit());
        mEtReceiveOpen.setText(StringUtil.getReceiveOpen());
        mEtReceiveQuit.setText(StringUtil.getReceiveQuit());
    }


    private void openSettings() {
        if (mSettingsIntent == null) {
            mSettingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        }
        startActivity(mSettingsIntent);
    }

    @OnClick({R.id.save_config, R.id.reset_config, R.id.open_settings})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_config:
                saveConfig();
                break;
            case R.id.reset_config:
                resetConfig();
                break;
            case R.id.open_settings:
                openSettings();
                break;
        }
    }

    private void resetConfig() {
        StringUtil.clear();
        get_config();
        saveConfig();
    }

    private void saveConfig() {
        StringUtil.putDetailQuit(mEtDetailQuit.getText().toString());
        StringUtil.putNotifyMark(mEtNotifyMark.getText().toString());
        StringUtil.putReceiveOpen(mEtReceiveOpen.getText().toString());
        StringUtil.putReceiveQuit(mEtReceiveQuit.getText().toString());
    }
}
