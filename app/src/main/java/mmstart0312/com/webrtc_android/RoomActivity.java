package mmstart0312.com.webrtc_android;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

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
                final View innerView = getLayoutInflater().inflate(R.layout.userinfo_layout, null);

                Dialog mDialog = new Dialog(context);
                mDialog.setTitle("Title");
                mDialog.setContentView(innerView);
                mDialog.setCancelable(true);

                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.x = -20;
                params.height = 100;
                params.width = 550;
                params.y = -10;

                mDialog.getWindow().setAttributes(params);
//                WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
//                params.width = WindowManager.LayoutParams.MATCH_PARENT;
//                params.height = WindowManager.LayoutParams.MATCH_PARENT;
//                mDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

                mDialog.show();
            }
        });
    }
}
