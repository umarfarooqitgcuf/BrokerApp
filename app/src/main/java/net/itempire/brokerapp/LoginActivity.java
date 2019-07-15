package net.itempire.brokerapp;


import android.app.ProgressDialog;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    JSONArray JsonData;
    SharedPreferences loginSharedPrefrences,sharedPreferencesFB_user,sharedPreferencesFB_sp,sharedPreferences;
    SharedPreferences.Editor editorlogin,editor_user_data;

    private DatabaseReference mDatabase;

    String login_url = "https://alirazagcufit.000webhostapp.com/26-06-19/broker_App/broker_App/API/login_api.php";

    ProgressDialog loadingDialog;
    String str_pass, str_email;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginSharedPrefrences=getSharedPreferences("loginDetail",MODE_PRIVATE);
        sharedPreferencesFB_user = getSharedPreferences("FBDetailsUSER",MODE_PRIVATE);
        sharedPreferencesFB_sp = getSharedPreferences("FBDetailsSP",MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("userData",MODE_PRIVATE);
        editor_user_data=sharedPreferences.edit();
        editorlogin=loginSharedPrefrences.edit();

        loadingDialog = new ProgressDialog(LoginActivity.this);
        loadingDialog.setTitle("Signing into your Acoount");
        loadingDialog.setMessage("Loading....");

        if (sharedPreferencesFB_user.getString("id","").compareTo("")==0)
        {
            SharedPreferences.Editor editor = sharedPreferencesFB_user.edit();
            editor.putString("id",Long.toString(System.currentTimeMillis()));
            editor.commit();
            editor.apply();
        }
        if (sharedPreferencesFB_sp.getString("id","").compareTo("")==0)
        {
            SharedPreferences.Editor editor = sharedPreferencesFB_sp.edit();
            editor.putString("id",Long.toString(System.currentTimeMillis()));
            editor.commit();
            editor.apply();
        }

        String type=loginSharedPrefrences.getString("type","");
        if (type.equals("user")){
            Intent intent=new Intent(this,UserDrawerActivity.class);
            Intent serviceIntent = new Intent(LoginActivity.this,GetNotificationUser.class);
            startService(serviceIntent);
            startActivity(intent);
            finish();
        }
        else if (type.equals("SP")){
            Log.e("@@@SpDrawer", "onCreate:==");
            Intent intent=new Intent(this,ServiceProviderDrawerActivity.class);
            Intent serviceIntent = new Intent(LoginActivity.this,GetNotificationSP.class);
            startService(serviceIntent);
            startActivity(intent);
            finish();
        }

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
                        str_email = login_email.getText().toString();
                        str_pass = login_password.getText().toString();
                        loadingDialog.show();
                        VollyMethoed(login_url);
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

    public void VollyMethoed(String url) {
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                Log.e("Eror", "success: " + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    Log.e("@@@OBJ", "onResponse:===" + obj.getString("error"));
                    String string = obj.getString("error");

                    if (string.equals("0")) {
                        Log.e("@@@Zero", "onResponse:" + response);
                        Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();
                    } else if (string.equals("1")) {
                        Log.e("@@@ONE", "onResponse:" + response);
                        Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                    } else if (string.equals("2")) {
                        Log.e("@@@ONE", "onResponse:" + response);
                        Toast.makeText(LoginActivity.this, "Account Is Not Active", Toast.LENGTH_LONG).show();
                    } else if (string.equals("3")) {
                        Log.e("@@@ONE", "onResponse:" + response);


                        String type = obj.getString("type");

                        if (type.equals("user")){
                            Intent intent=new Intent(LoginActivity.this,UserDrawerActivity.class);
                            Intent serviceIntent = new Intent(LoginActivity.this,GetNotificationUser.class);
                            startService(serviceIntent);
                            startActivity(intent);
                            finish();

                            String id = obj.getString("id");
                            String name = obj.getString("full_name");
                            String f_name = obj.getString("f_name");
                            String image = obj.getString("img");
                            String occupation = obj.getString("occupation");
                            String email = obj.getString("email");
                            String cnic = obj.getString("cnic");
                            String phone = obj.getString("phone");
                            String address = obj.getString("address");

                            editorlogin.putString("type", "user");
                            editorlogin.apply();
                            editorlogin.commit();

                            editor_user_data.putString("id",id);
                            editor_user_data.putString("name",name);
                            editor_user_data.putString("f_name",f_name);
                            editor_user_data.putString("image",image);
                            editor_user_data.putString("occupation",occupation);
                            editor_user_data.putString("email_user",email);
                            editor_user_data.putString("cnic",cnic);
                            editor_user_data.putString("phone",phone);
                            editor_user_data.putString("address",address);
                            editor_user_data.putString("type",type);
                            editor_user_data.apply();


                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("SpFbId").setValue("");
                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("Status").setValue("null");
                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("Notification").setValue("0");
                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("id").setValue(id);
                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("email").setValue(email);
                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("userType").setValue("user");
                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("bookingDetails").setValue("");
                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("userStatus").setValue("offline");
                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("Location").child("Latitude").setValue("31.418837");
                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("Location").child("Longitude").setValue("73.079098");


                            Toast.makeText(LoginActivity.this, "You are loging as User", Toast.LENGTH_LONG).show();
                            Log.e("@@@AllData", "onResponse:===" + id + "==" + name + "==" + f_name + "==" + image);

                        } else{
                            Intent intent=new Intent(LoginActivity.this,ServiceProviderDrawerActivity.class);
                            Intent serviceIntent = new Intent(LoginActivity.this,GetNotificationSP.class);
                            startService(serviceIntent);
                            startActivity(intent);
                            finish();

                            String id = obj.getString("id");
                            String name = obj.getString("full_name");
                            String f_name = obj.getString("f_name");
                            String image = obj.getString("img");
                            String occupation = obj.getString("occupation");
                            String email = obj.getString("email");
                            String cnic = obj.getString("cnic");
                            String phone = obj.getString("phone");
                            String address = obj.getString("address");

                            editorlogin.putString("type", "SP");
                            editorlogin.apply();
                            editorlogin.commit();

                            editor_user_data.putString("id",id);
                            editor_user_data.putString("name",name);
                            editor_user_data.putString("f_name",f_name);
                            editor_user_data.putString("image",image);
                            editor_user_data.putString("occupation",occupation);
                            editor_user_data.putString("email_user",email);
                            editor_user_data.putString("cnic",cnic);
                            editor_user_data.putString("phone",phone);
                            editor_user_data.putString("address",address);
                            editor_user_data.putString("type",type);
                            editor_user_data.apply();

                            mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("Occupation").setValue(occupation);
                            mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("Status").setValue("null");
                            mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("Notification").setValue("0");
                            mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("id").setValue(id);
                            mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("email").setValue(email);
                            mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("userType").setValue("SP");
                            mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("bookingDetails").setValue("");
                            mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("userStatus").setValue("offline");
                            mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("Location").child("Latitude").setValue("31.418837");
                            mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("Location").child("Longitude").setValue("73.079098");

                            Toast.makeText(LoginActivity.this, "You are loging as Service provider", Toast.LENGTH_LONG).show();
                            Log.e("@@@AllData", "onResponse:===" + id + "==" + name + "==" + f_name + "==" + image);
                        }
                    } else {
                        Log.e("@@@NOTZero", "onResponse:" + response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Log.e("@@@@LoginError", "onErrorResponse: " + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("email", str_email);
                MyData.put("password", str_pass);
                return MyData;
            }
        };
        MyStringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue MyRequestQueue = Volley.newRequestQueue(LoginActivity.this);
        MyRequestQueue.add(MyStringRequest);
    }
}