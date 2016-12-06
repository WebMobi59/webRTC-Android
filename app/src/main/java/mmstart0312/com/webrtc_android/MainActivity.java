package mmstart0312.com.webrtc_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView button = (TextView) findViewById(R.id.btn_submit);
        final EditText phone_number = (EditText) findViewById(R.id.edit_phone);
        final OkHttpClientManager clientManager = new OkHttpClientManager();
        final Map<String,String> params =  new HashMap<String,String>();
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(getApplicationContext(),confirmCodeActivity.class);
                startActivity(intent);
            }
        });
    }
}
