package mmstart0312.com.webrtc_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class confirmCodeActivity extends AppCompatActivity {

    private EditText confirmCodeEdit;
    private TextView confirmBtn;
    private ProgressDialog progressDialog;

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

                Intent intent = new Intent(getApplicationContext(),RoomActivity.class);
                startActivity(intent);
            }
        });
    }
}
