package com.entryview.tenant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences AppInfo;
    private String deviceToken_string;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {
            Boolean fromPush = false;
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                if (key.equalsIgnoreCase("rtcconnectionkey")) {
                    fromPush = true;
                    Intent callIntent = new Intent(getApplicationContext(), ConnectCallActivity.class);
                    callIntent.putExtra("roomID", value.toString());
                    startActivity(callIntent);
                }
            }

            if (!fromPush) {
                Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                startActivity(intent);
            }
        } else {

            AppInfo = getApplication().getSharedPreferences("AppInfo", MODE_PRIVATE);
            deviceToken_string = AppInfo.getString("deviceToken", "");

            if (deviceToken_string != "") {
                Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        }
    }
}
