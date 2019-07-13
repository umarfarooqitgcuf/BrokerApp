package net.itempire.brokerapp;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotActivity extends AppCompatActivity {

    EditText edt_email;
    Button btn_getCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        edt_email=(EditText) findViewById(R.id.forget_email);
        btn_getCode=(Button) findViewById(R.id.btn_forget);
        btn_getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isEmailValid(edt_email.getText().toString())){
                    edt_email.setError("Invalid Email Address");
                    // valid_email=null;
                }
                else {
                    if (edt_email.length() == 0) {
                        edt_email.setError("Enter Email");
                        edt_email.setFocusable(true);
                    }
                    else {
                    }

                }

            }
        });
    }
    boolean isEmailValid(CharSequence email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
