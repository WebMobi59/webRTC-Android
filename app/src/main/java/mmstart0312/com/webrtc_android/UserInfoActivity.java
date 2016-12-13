package mmstart0312.com.webrtc_android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import mmstart0312.com.webrtc_android.classes.APIManager;
import mmstart0312.com.webrtc_android.classes.UserManager;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity {

    private EditText firstNameEdit;
    private EditText lastNameEdit;
    private TextView streetLabel;
    private TextView aptLabel;
    private TextView cityLabel;
    private TextView stateLabel;
    private TextView zipLabel;
    private TextView phoneLabel;
    private TextView submitBtn;
    private TextView cancelBtn;

    private ProgressDialog progressDialog;

    private final static int KEY_GETUSERNAME_WITHTENANT_FAILED = 401;
    private final static int KEY_GETUSERNAME_WITHTENANT_SUCCESSED = 402;
    private final static int KEY_GETUSERNAME_WITHPREQUALTENANT_FAILED = 403;
    private final static int KEY_GETUSERNAME_WITHPREQUALTENANT_SUCCESSED = 404;
    private final static int KEY_SETUSERINFO_FAILED = 405;
    private final static int KEY_SETUSERINFO_SUCCESSED = 406;
    private final static int Key_Network_Failed = 409;

    UserManager _user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_layout);

        this.setFinishOnTouchOutside(true);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        firstNameEdit = (EditText) findViewById(R.id.info_firstname_textedit);
        lastNameEdit = (EditText) findViewById(R.id.info_lastname_textedit);
        streetLabel = (TextView) findViewById(R.id.info_street_label);
        aptLabel = (TextView) findViewById(R.id.info_apt_label);
        cityLabel = (TextView) findViewById(R.id.info_city_label);
        stateLabel = (TextView) findViewById(R.id.info_state_label);
        zipLabel = (TextView) findViewById(R.id.info_zip_label);
        phoneLabel = (TextView) findViewById(R.id.info_phone_label);
        submitBtn = (TextView) findViewById(R.id.btn_userinfo_submit);
        cancelBtn = (TextView) findViewById(R.id.btn_userinfo_cancel);

        progressDialog = new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        _user = (UserManager) getApplication();
        initComponent();
        getUserName(true);

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                progressDialog.setMessage("Uploading ...");
                progressDialog.show();
                setUserName();
                finish();
            }
        });
    }

    private void initComponent() {
        firstNameEdit.setText(_user.getUser_Firstname());
        lastNameEdit.setText(_user.getUser_Lastname());
        streetLabel.setText(_user.getUser_Street());
        aptLabel.setText(_user.getUser_Apt());
        cityLabel.setText(_user.getUser_City());
        stateLabel.setText(_user.getUser_State());
        zipLabel.setText(_user.getUser_Zip());
        phoneLabel.setText(_user.getUser_PhoneNumber());
    }

    private void getUserName(final boolean isTenant) {
        APIManager.getInstance().getUserName(_user.getUser_PhoneNumber(), isTenant, new APIManager.APISuccessListener() {
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
                        if (jsonObject.has("error")) {
                            if (isTenant) {
                                implementThread(KEY_GETUSERNAME_WITHTENANT_FAILED);
                            } else {
                                implementThread(KEY_GETUSERNAME_WITHPREQUALTENANT_FAILED);
                            }
                        } else {
                            if (jsonObject.has("first"))
                                _user.setUser_Firstname(jsonObject.getString("first"));
                            if (jsonObject.has("last"))
                                _user.setUser_Lastname(jsonObject.getString("last"));
                            if (isTenant)
                                implementThread(KEY_GETUSERNAME_WITHTENANT_SUCCESSED);
                            else
                                implementThread(KEY_GETUSERNAME_WITHPREQUALTENANT_SUCCESSED);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (isTenant)
                            implementThread(KEY_GETUSERNAME_WITHTENANT_FAILED);
                        else
                            implementThread(KEY_GETUSERNAME_WITHPREQUALTENANT_FAILED);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    implementThread(Key_Network_Failed);
                }
            }
        });
    }

    private void setUserName() {
        APIManager.getInstance().setUserName(_user.getUser_PhoneNumber(), firstNameEdit.getText().toString(), lastNameEdit.getText().toString(), new APIManager.APISuccessListener() {
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
                        if (jsonObject.has("error")) {
                            implementThread(KEY_SETUSERINFO_FAILED);
                        } else {
                            implementThread(KEY_SETUSERINFO_SUCCESSED);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        implementThread(KEY_SETUSERINFO_FAILED);
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
                                UserInfoActivity.this);
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
                    case KEY_GETUSERNAME_WITHPREQUALTENANT_SUCCESSED:
                    case KEY_GETUSERNAME_WITHTENANT_SUCCESSED:
                    {
                        progressDialog.dismiss();
                        firstNameEdit.setText(_user.getUser_Firstname());
                        lastNameEdit.setText(_user.getUser_Lastname());
                    }
                    break;
                    case KEY_GETUSERNAME_WITHTENANT_FAILED:
                    {
                        Log.d("UserInfoActivity.java","Error in getUserNameWithTenant");
                        getUserName(false);
                }
                    break;
                    case KEY_GETUSERNAME_WITHPREQUALTENANT_FAILED: {
                        Log.d("UserInfoActivity.java", "Error in getUserNameWithPrequalTenant");
                        progressDialog.dismiss();
                    }
                    break;
                    case KEY_SETUSERINFO_SUCCESSED:
                    {
                        progressDialog.dismiss();
                    }
                    break;
                    case KEY_SETUSERINFO_FAILED:
                    {
                        Log.d("UserInfo.java", "Error in setUserInfoWithTenant");
                        progressDialog.dismiss();
                    }
                    break;
                    default:
                        break;
                }
            }
        });
    }
}
