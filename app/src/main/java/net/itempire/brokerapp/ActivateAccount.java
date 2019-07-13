package net.itempire.brokerapp;



import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ActivateAccount extends AppCompatActivity {

    Button activate;
    EditText edit_code;
    TextView txt_email;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);

        activate=(Button) findViewById(R.id.btn_activate);
        edit_code=(EditText) findViewById(R.id.edit_code);
        txt_email=(TextView) findViewById(R.id.txt_email);
        sharedPreferences=getSharedPreferences("activate_email",MODE_PRIVATE);
        txt_email.setText(sharedPreferences.getString("user_email",""));
        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ActivateAccount.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
