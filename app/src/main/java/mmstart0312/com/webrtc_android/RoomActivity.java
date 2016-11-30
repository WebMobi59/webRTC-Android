package mmstart0312.com.webrtc_android;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class RoomActivity extends AppCompatActivity {

    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        ImageButton roomInfobtn = (ImageButton) findViewById (R.id.btn_information);
        roomInfobtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                final View innerView = getLayoutInflater().inflate(R.layout.userinfo_layout, null);

                Dialog mDialog = new Dialog(context);
                mDialog.setTitle("Title");
                mDialog.setContentView(R.layout.userinfo_layout);
                mDialog.setCancelable(true);
                mDialog.show();
                Window window = mDialog.getWindow();
//                window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                window.setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            }
        });

        TextView roombtn = (TextView) findViewById(R.id.room_info_label);
        roombtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),ConnectCallActivity.class);
                startActivity(intent);
            }
        });
    }
}
