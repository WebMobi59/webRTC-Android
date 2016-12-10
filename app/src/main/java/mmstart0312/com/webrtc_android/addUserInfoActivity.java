package mmstart0312.com.webrtc_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import mmstart0312.com.webrtc_android.classes.UserManager;


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

    private UserManager _user;

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
    }
}
