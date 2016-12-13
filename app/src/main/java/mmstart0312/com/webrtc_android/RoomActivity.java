package mmstart0312.com.webrtc_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import mmstart0312.com.webrtc_android.classes.APIManager;
import mmstart0312.com.webrtc_android.classes.UserManager;
import okhttp3.Response;

public class RoomActivity extends AppCompatActivity {

    private ImageButton roomInfoBtn;
    private TextView roomBtn;
    private ProgressDialog progressDialog;

    private String phone_number;
    private String deviceToken;
    private UserManager _user;

    final Context context = this;

    private final static int Key_RegisterToken_WithTenant_Failed = 401;
    private final static int Key_RegisterToken_WithTenant_Successed = 402;
    private final static int Key_RegisterToken_WithPrequalTenant_Failed = 403;
    private final static int Key_RegisterToken_WithPrequalTenant_Successed = 404;
    private final static int Key_GetUserInfo_WithTenant_Failed = 405;
    private final static int Key_GetUserInfo_WithTenant_Successed = 406;
    private final static int Key_GetUserInfo_WithPrequalTenant_Failed = 407;
    private final static int Key_GetUserInfo_WithPrequalTenant_Successed = 408;
    private final static int Key_Network_Failed = 409;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        roomInfoBtn = (ImageButton) findViewById (R.id.btn_information);

        UserManager _user = (UserManager) getApplication();
        phone_number = _user.getUser_PhoneNumber();
        deviceToken = FirebaseInstanceId.getInstance().getToken();

        registerDeviceToken();

        roomInfoBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(),UserInfoActivity.class);
                startActivity(intent);
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
                implementThread(Key_Network_Failed);
            }

            @Override
            public void onSuccess(Response response) {
                try {
                    String res = response.body().string();
                    try {
                        JSONObject jsonObject =new JSONObject(res);
                        if (jsonObject.has("result")) {
                            String resultString = jsonObject.getString("result");
                            if (resultString.contains("ok")) {
                                implementThread(Key_RegisterToken_WithTenant_Successed);
                            } else {
                                implementThread(Key_RegisterToken_WithTenant_Failed);
                            }
                        } else {
                            implementThread(Key_RegisterToken_WithTenant_Failed);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        implementThread(Key_RegisterToken_WithTenant_Failed);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    implementThread(Key_Network_Failed);
                }
            }
        });
    }

    private void registerDeviceTokenWithPrequalTenant() {

        APIManager.getInstance().registerDeviceToken(phone_number, deviceToken, false, new APIManager.APISuccessListener() {
            @Override
            public void onFailure(String error) {
                implementThread(Key_Network_Failed);
            }

            @Override
            public void onSuccess(Response response) {
                try {
                    String res = response.body().string();
                    try {
                        JSONObject jsonObject =new JSONObject(res);
                        if (jsonObject.has("result")) {
                            String resultString = jsonObject.getString("result");
                            if (resultString.contains("ok")) {
                                implementThread(Key_RegisterToken_WithPrequalTenant_Successed);
                            } else {
                                implementThread(Key_RegisterToken_WithPrequalTenant_Failed);
                            }
                        } else {
                            implementThread(Key_RegisterToken_WithPrequalTenant_Failed);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        implementThread(Key_RegisterToken_WithPrequalTenant_Failed);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    implementThread(Key_Network_Failed);
                }
            }
        });
    }

    public void implementThread(final int kind){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (kind){
                    case Key_Network_Failed:
                    {
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
                        progressDialog.dismiss();
                        builder.show();
                    }
                    break;
                    case Key_RegisterToken_WithPrequalTenant_Successed:
                    case Key_RegisterToken_WithTenant_Successed:
                    {
                        progressDialog.dismiss();
                        getUserInfo(true);
                    }
                    break;
                    case Key_RegisterToken_WithTenant_Failed:
                    {
                        Log.d("RoomActivity.java","Error in registerDeviceTokenWithTenant");
                        registerDeviceTokenWithPrequalTenant();
                    }
                    break;
                    case Key_RegisterToken_WithPrequalTenant_Failed: {
                        Log.d("RoomActivity.java", "Error in registerDeviceTokenWithPrequalTenant");
                        progressDialog.dismiss();
                    }
                    break;
                    case Key_GetUserInfo_WithPrequalTenant_Successed:
                    case Key_GetUserInfo_WithTenant_Successed:
                    {
                        roomBtn.setText(_user.getUser_Apt() + _user.getUser_Street());
                        progressDialog.dismiss();
                    }
                    break;
                    case Key_GetUserInfo_WithTenant_Failed: {
                        Log.d("RoomActivity.java", "Error in getUserInfoWithTenant");
                        getUserInfo(false);
                    }
                    break;
                    case Key_GetUserInfo_WithPrequalTenant_Failed:
                    {
                        Log.d("RoomActivity.java", "Error in getUserInfoWithPrequalTenant");
                        progressDialog.dismiss();
                    }
                    break;
                    default:
                        break;
                }
            }
        });
    }

    private void getUserInfo(final boolean isTenant) {
//        progressDialog = new ProgressDialog(RoomActivity.this);
        progressDialog.setMessage("Loading User Information...");
        progressDialog.show();
        APIManager.getInstance().getUserInfo(phone_number, isTenant, new APIManager.APISuccessListener() {
            @Override
            public void onFailure(String error) {
                implementThread(Key_Network_Failed);
            }

            @Override
            public void onSuccess(Response response) {
                try {
                    String res = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.has("apartments")) {
                            String resultString = jsonObject.getString("apartments");
                            if (parseUserInfoJsonData(resultString)) {
                                if (isTenant)
                                    implementThread(Key_GetUserInfo_WithTenant_Successed);
                                else
                                    implementThread(Key_GetUserInfo_WithPrequalTenant_Successed);
                            } else {
                                if (isTenant)
                                    implementThread(Key_GetUserInfo_WithTenant_Failed);
                                else
                                    implementThread(Key_GetUserInfo_WithPrequalTenant_Failed);
                            }
                        } else {
                            if (isTenant)
                                implementThread(Key_GetUserInfo_WithTenant_Failed);
                            else
                                implementThread(Key_GetUserInfo_WithPrequalTenant_Failed);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (isTenant)
                            implementThread(Key_GetUserInfo_WithTenant_Failed);
                        else
                            implementThread(Key_GetUserInfo_WithPrequalTenant_Failed);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    implementThread(Key_Network_Failed);
                }
            }
        });
    }

    private boolean parseUserInfoJsonData(String data) {
        _user = (UserManager) getApplication();
        initUserInfo();
        try {
            JSONArray parseData = new JSONArray(data);
            if (parseData.length() > 0) {
                String userDataString = parseData.getString(0);
                JSONObject userData = new JSONObject(userDataString);
                if (userData.has("first")) {
                    _user.setUser_Firstname(userData.getString("first"));
                }
                if (userData.has("last")) {
                    _user.setUser_Lastname(userData.getString("last"));
                }
                if (userData.has("street")) {
                    _user.setUser_Street(userData.getString("street"));
                }
                if (userData.has("state")) {
                    _user.setUser_State(userData.getString("state"));
                }
                if (userData.has("apt")) {
                    _user.setUser_Apt(userData.getString("apt"));
                }
                if (userData.has("city")) {
                    _user.setUser_City(userData.getString("city"));
                }
                if (userData.has("zip")) {
                    _user.setUser_Zip(userData.getString("zip"));
                }
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initUserInfo() {
        _user.setUser_Firstname("");
        _user.setUser_Lastname("");
        _user.setUser_Apt("");
        _user.setUser_City("");
        _user.setUser_State("");
        _user.setUser_Street("");
        _user.setUser_Apt("");
    }
}
