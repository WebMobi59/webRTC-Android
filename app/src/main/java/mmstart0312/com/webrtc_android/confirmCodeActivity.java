package mmstart0312.com.webrtc_android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class confirmCodeActivity extends AppCompatActivity {

    private EditText confirmCodeEdit;
    private TextView confirmBtn;
    private ProgressDialog progressDialog;

    private static final int Key_Confirm_Successed = 201;
    private static final int Key_Confirm_Rejected = 202;
    private static final int Key_Confirm_Expired = 203;
    private static final int Key_Network_Failed = 204;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_code);

        confirmCodeEdit = (EditText) findViewById(R.id.edit_confirm);
        confirmBtn = (TextView) findViewById(R.id.btn_confirm);
        progressDialog = new ProgressDialog(confirmCodeActivity.this);
        progressDialog.setMessage("Validating...");

        confirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                progressDialog.show();

                UserManager _user = (UserManager) getApplication();
                String phone_number = _user.getUser_PhoneNumber();

                APIManager.getInstance().confirmCode(phone_number, confirmCodeEdit.getText().toString(), new APIManager.APISuccessListener() {
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
                                if (jsonObject.has("result")) {
                                    String resultString = jsonObject.getString("result");
                                    if (resultString.contains("rejected")) {
                                        implementThread(Key_Confirm_Rejected);
                                    } else if (resultString.contains("expired")) {
                                        implementThread(Key_Confirm_Expired);
                                    } else {
                                        implementThread(Key_Confirm_Successed);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                implementThread(Key_Network_Failed);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            implementThread(Key_Network_Failed);
                        }
                    }
                });
                Intent intent = new Intent(getApplicationContext(),RoomActivity.class);
                startActivity(intent);
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
                                confirmCodeActivity.this);
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
                    case Key_Confirm_Successed:
                    {
                        Intent intent = new Intent(getApplicationContext(),RoomActivity.class);
                        startActivity(intent);
                    }
                    break;
                    case Key_Confirm_Expired:
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                confirmCodeActivity.this);
                        builder.setTitle("Notice");
                        builder.setMessage("Please check the number you entered and try again.");
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(loginIntent);
                                    }
                                });
                        builder.show();
                    }
                    case  Key_Confirm_Rejected:
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                confirmCodeActivity.this);
                        builder.setTitle("Notice");
                        builder.setMessage("The Activation Code you entered is incorrect. Please check the code and try again");
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                    }
                                });
                        builder.show();
                    }
                    default:
                        break;
                }
            }
        });
        progressDialog.dismiss();
    }
}
