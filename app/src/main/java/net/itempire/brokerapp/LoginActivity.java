package net.itempire.brokerapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextView btnCreateAccount, btnForgotPassword;
    EditText login_email, login_password;
    Button loginUserDrawer;
    ImageView temporaryButton;
    TextView btnpasswordshow;
    String pType;
    String url;
    JSONArray JsonData;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnpasswordshow = (TextView) findViewById(R.id.eye);
        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_user_password);
        loginUserDrawer = (Button) findViewById(R.id.btn_login);
        btnCreateAccount = (TextView) findViewById(R.id.btn_create_account);
        btnForgotPassword = (TextView) findViewById(R.id.txt_forgot_password);
        temporaryButton = (ImageView) findViewById(R.id.logo);




        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        loginUserDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (login_email.length() == 0) {
                    login_email.setError("Enter Email");
                    login_email.setFocusable(true);

                } else if (login_password.length() == 0) {
                    login_password.setError("Enter password");
                    login_password.setFocusable(true);
                } else {
                    if (!isEmailValid(login_email.getText().toString())) {
                        login_email.setError("Invalid Email Address");
                        login_email.setFocusable(true);
                    } else {
                        final String email = login_email.getText().toString();
                        final String password = login_password.getText().toString();
                        Toast.makeText(LoginActivity.this, ""+email+"       Your password is "+ password, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, UserDrawerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

        });
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, UserCreateAccountActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        pType = "login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)";
        btnpasswordshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pType == "login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)") {
                    pType = "login_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)";
                    login_password.setTransformationMethod(null);

                    if (login_password.getText().length() > 0)
                        login_password.setSelection(login_password.getText().length());
                    btnpasswordshow.setBackgroundResource(R.drawable.eye);
                } else {
                    pType = "login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)";
                    login_password.setTransformationMethod(new PasswordTransformationMethod());
                    if (login_password.getText().length() > 0)
                        login_password.setSelection(login_password.getText().length());
                    btnpasswordshow.setBackgroundResource(R.drawable.eye1);
                }
            }
        });
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}