package com.entryview.tenant;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import com.entryview.tenant.R;
import com.entryview.tenant.classes.APIManager;
import com.entryview.tenant.classes.UserManager;
import okhttp3.Response;


public class addUserInfoActivity extends AppCompatActivity {

    private TextView label_phone_number;
    private EditText edit_FirstName;
    private EditText edit_LastName;
    private EditText edit_Street;
    private EditText edit_Apt;
    private EditText edit_City;
    private EditText edit_State;
    private EditText edit_Zip;
    private TextView btn_submit;
    private TextView btn_cancel;
    private ProgressDialog progressDialog;

    private UserManager _user;

    private final static int KEY_SET_USERINFO_FAILED = 401;
    private final static int KEY_SET_USERINFO_SUCCESSED = 402;
    private final static int KEY_UPDATE_USERINFO_FAILED = 403;
    private final static int KEY_UPDATE_USERINFO_SUCCESSED = 404;
    private final static int Key_Network_Failed = 409;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_info);

        _user = (UserManager) getApplication();

        label_phone_number = (TextView) findViewById(R.id.label_realphonenumber);
        edit_FirstName = (EditText) findViewById(R.id.edit_firstname);
        edit_LastName = (EditText) findViewById(R.id.edit_lastname);
        edit_Street = (EditText) findViewById(R.id.edit_street);
        edit_Apt = (EditText) findViewById(R.id.edit_apt);
        edit_City = (EditText) findViewById(R.id.edit_city);
        edit_State = (EditText) findViewById(R.id.edit_state);
        edit_Zip = (EditText) findViewById(R.id.edit_zipcode);
        btn_submit = (TextView) findViewById(R.id.btn_adduserinfo_submit);
        btn_cancel = (TextView) findViewById(R.id.btn_adduserinfo_cancel);

        label_phone_number.setText(_user.getUser_PhoneNumber());

        btn_cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                startActivity(intent);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        addUserInfoActivity.this);
                builder.setTitle("Notice");
                builder.setMessage("Thank you for registering your information. We will notify you once your account is activated");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                progressDialog = new ProgressDialog(addUserInfoActivity.this);
                                progressDialog.setMessage("Storing user info ...");
                                progressDialog.show();
                                setUserInfo(false);
                            }
                        });
                builder.show();
            }
        });
    }

    private void setUserInfo(final boolean flag) {
        APIManager.getInstance().setUserInfo(
                flag,
                _user.getUser_PhoneNumber(),
                FirebaseInstanceId.getInstance().getToken(),
                edit_FirstName.getText().toString(),
                edit_LastName.getText().toString(),
                edit_Street.getText().toString(),
                edit_City.getText().toString(),
                edit_State.getText().toString(),
                edit_Zip.getText().toString(),
                edit_Apt.getText().toString(),
                new APIManager.APISuccessListener() {

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
                                        if (flag)
                                            implementThread(KEY_UPDATE_USERINFO_SUCCESSED);
                                        else
                                            implementThread(KEY_SET_USERINFO_SUCCESSED);
                                    } else {
                                        if (flag)
                                            implementThread(KEY_UPDATE_USERINFO_FAILED);
                                        else
                                            implementThread(KEY_SET_USERINFO_FAILED);
                                    }
                                } else {
                                    if (flag)
                                        implementThread(KEY_UPDATE_USERINFO_FAILED);
                                    else
                                        implementThread(KEY_SET_USERINFO_FAILED);                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (flag)
                                    implementThread(KEY_UPDATE_USERINFO_FAILED);
                                else
                                    implementThread(KEY_SET_USERINFO_FAILED);                            }

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
                                addUserInfoActivity.this);
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
                    case KEY_SET_USERINFO_SUCCESSED:
                    case KEY_UPDATE_USERINFO_SUCCESSED:
                    {
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case KEY_SET_USERINFO_FAILED:
                    {
                        Log.d("RoomActivity.java","Error in registerDeviceTokenWithTenant");
                        setUserInfo(true);
                    }
                    break;
                    case KEY_UPDATE_USERINFO_FAILED: {
                        Log.d("RoomActivity.java", "Error in registerDeviceTokenWithPrequalTenant");
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                        startActivity(intent);
                    }
                    break;
                    default:
                        break;
                }
            }
        });
    }
}
