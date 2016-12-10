package mmstart0312.com.webrtc_android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import mmstart0312.com.webrtc_android.classes.APIManager;
import mmstart0312.com.webrtc_android.classes.UserManager;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView submitBtn;
    private EditText phoneEdit;
    private ProgressDialog progressDialog;

    private static final int Key_Log_Successed = 100;
    private static final int Key_Log_Failed = 101;
    private static final int Key_Network_Failed = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        submitBtn = (TextView) findViewById(R.id.btn_submit);
        phoneEdit = (EditText) findViewById(R.id.edit_phone);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Login...");
        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                progressDialog.show();

                APIManager.getInstance().loginPhone(phoneEdit.getText(), new APIManager.APISuccessListener()
                {
                    @Override
                    public void onFailure(String error) {
                        implementThread(Key_Network_Failed);
                    }

                    @Override
                    public void onSuccess(Response response) {
                        UserManager _user = (UserManager) getApplication();
                        _user.setUser_PhoneNumber(phoneEdit.getText().toString());

                        try {
                            String res = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                if (jsonObject.has("result")) {
                                    String resultString = jsonObject.getString("result");
                                    if (resultString.contains("ok")) {
                                        implementThread(Key_Log_Successed);
                                    } else {
                                        implementThread(Key_Log_Failed);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                implementThread(Key_Log_Failed);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            implementThread(Key_Log_Failed);
                        }
                    }
                });
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
                                LoginActivity.this);
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
                    break;
                    case Key_Log_Successed:
                    {
                        Intent intent = new Intent(getApplicationContext(),confirmCodeActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case Key_Log_Failed:
                    {
                        Intent intent = new Intent(getApplicationContext(), addUserInfoActivity.class);
                        startActivity(intent);
                    }
                    default:
                        break;
                }
            }
        });
        progressDialog.dismiss();
    }
}
