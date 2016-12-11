package mmstart0312.com.webrtc_android;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.test.suitebuilder.TestMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import mmstart0312.com.webrtc_android.classes.APIManager;
import mmstart0312.com.webrtc_android.classes.UserManager;
import okhttp3.Response;

public class RoomActivity extends AppCompatActivity {

    private ImageButton roomInfoBtn;
    private TextView roomBtn;
    private ProgressDialog progressDialog;

    private String phone_number;
    private String deviceToken;

    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        roomInfoBtn = (ImageButton) findViewById (R.id.btn_information);

        UserManager _user = (UserManager) getApplication();
        phone_number = _user.getUser_PhoneNumber();
        deviceToken = FirebaseInstanceId.getInstance().getToken();

        roomInfoBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Dialog mDialog = new Dialog(context);
                mDialog.setTitle("Title");
                mDialog.setContentView(R.layout.userinfo_layout);
                mDialog.setCancelable(true);
                mDialog.show();
                Window window = mDialog.getWindow();
                window.setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            }
        });

        roomBtn = (TextView) findViewById(R.id.room_info_label);
        roomBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),ConnectCallActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerDeviceToken() {
        progressDialog = new ProgressDialog(RoomActivity.this);
        progressDialog.setMessage("Registing the DeviceToken...");
        progressDialog.show();
        APIManager.getInstance().registerDeviceToken(phone_number, deviceToken, true, new APIManager.APISuccessListener() {
            @Override
            public void onFailure(String error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        RoomActivity.this);
                builder.setTitle("Notice");
                builder.setMessage("Network connection was failed. Please check your connection and try again later");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        });
                builder.show();
            }

            @Override
            public void onSuccess(Response response) {

            }
        });
    }

    private void getUserInfo() {

    }
}
