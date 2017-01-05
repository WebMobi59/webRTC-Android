package mmstart0312.com.webrtc_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences AppInfo;
    private String deviceToken_string;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                if (key.equalsIgnoreCase("rtcconnectionkey")) {
                    Intent callIntent = new Intent(getApplicationContext(), ConnectCallActivity.class);
                    callIntent.putExtra("roomID", value.toString());
                    startActivity(callIntent);
                }
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
