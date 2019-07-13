package net.itempire.brokerapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class UserCreateAccountActivity extends AppCompatActivity {

    Button btnSignUp;
    EditText user_name, father_name, cnic, email, occuption, phone_number, current_address, permenanat_address, cnfrm_password, password;
    private final static int Gallery_Pick = 1;
    String url;
    private static int RESULT_LOAD_IMAGE = 1;
    String picturePath;
    Bitmap bitmap;
    ImageView userProfileImage;
    EditText txtPassword, txtConfirmPassword;
    TextView btnCreatepasswordshow, btnConfirmShowPassword;
    String pType;
    SharedPreferences sharedPreferences;
    Intent myActivityIntent;

    private String[] galleryPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create_account);


        txtPassword = (EditText) findViewById(R.id.create_account_user_password);
        txtConfirmPassword = (EditText) findViewById(R.id.create_account_user_confirm_password);
        btnCreatepasswordshow = (TextView) findViewById(R.id.eye_password);
        btnConfirmShowPassword = (TextView) findViewById(R.id.eye_confirm_password);
        userProfileImage = (ImageView) findViewById(R.id.create_account_image);
        //Show Hide Password
        pType = "txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)";
        btnCreatepasswordshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pType == "txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)") {
                    pType = "txtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)";
                    txtPassword.setTransformationMethod(null);

                    if (txtPassword.getText().length() > 0)
                        txtPassword.setSelection(txtPassword.getText().length());
                    btnCreatepasswordshow.setBackgroundResource(R.drawable.eye);
                } else {
                    pType = "txtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)";
                    txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    if (txtPassword.getText().length() > 0)
                        txtPassword.setSelection(txtPassword.getText().length());
                    btnCreatepasswordshow.setBackgroundResource(R.drawable.eye1);
                }
            }
        });

        pType = "txtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)";
        btnConfirmShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pType == "txtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)") {
                    pType = "txtConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)";
                    txtConfirmPassword.setTransformationMethod(null);

                    if (txtConfirmPassword.getText().length() > 0)
                        txtConfirmPassword.setSelection(txtConfirmPassword.getText().length());
                    btnConfirmShowPassword.setBackgroundResource(R.drawable.eye);
                } else {
                    pType = "txtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)";
                    txtConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    if (txtConfirmPassword.getText().length() > 0)
                        txtConfirmPassword.setSelection(txtConfirmPassword.getText().length());
                    btnConfirmShowPassword.setBackgroundResource(R.drawable.eye1);
                }
            }
        });


        if (EasyPermissions.hasPermissions(this, galleryPermissions)) {
            /* pickImageFromGallery();*/
            Log.e("@#permission", "onCreate: ");
        } else {
            EasyPermissions.requestPermissions(this, "Access for storage",
                    101, galleryPermissions);
            Log.e("@#permission", "onCreate: ");
        }

        user_name = (EditText) findViewById(R.id.create_account_Name);
        father_name = (EditText) findViewById(R.id.create_account_father_Name);
        cnic = (EditText) findViewById(R.id.create_account_cnic);
        email = (EditText) findViewById(R.id.create_account_email);
        occuption = (EditText) findViewById(R.id.create_account_occupation);
        password = (EditText) findViewById(R.id.create_account_user_password);
        cnfrm_password = (EditText) findViewById(R.id.create_account_user_confirm_password);
        phone_number = (EditText) findViewById(R.id.create_account_number);
        current_address = (EditText) findViewById(R.id.create_account_address);
        permenanat_address = (EditText) findViewById(R.id.create_account_permanent_address);

        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_name.length() == 0) {
                    user_name.setError("Enter Name");
                    user_name.setFocusable(true);
                } else if (father_name.length() == 0) {
                    father_name.setError("Enter Email");
                    father_name.setFocusable(true);
                } else if (cnic.length() == 0) {
                    cnic.setError("Enter Email");
                    cnic.setFocusable(true);
                } else if (email.length() == 0) {
                    email.setError("Enter Email");
                    email.setFocusable(true);
                } else if (occuption.length() == 0) {
                    occuption.setError("Enter Email");
                    occuption.setFocusable(true);
                } else if (password.length() == 0) {
                    password.setError("Enter Password");
                    password.setFocusable(true);
                } else if (!password.getText().toString().equals(cnfrm_password.getText().toString())) {
                    cnfrm_password.setError("Please conform Password");
                } else if (phone_number.length() == 0) {
                    phone_number.setError("Enter Phone no");
                    phone_number.setFocusable(true);
                } else if (current_address.length() == 0) {
                    current_address.setError("Enter Phone no");
                    current_address.setFocusable(true);
                } else if (permenanat_address.length() == 0) {
                    permenanat_address.setError("Enter Phone no");
                    permenanat_address.setFocusable(true);
                } else if (email.length() != 0) {
                    if (!isEmailValid(email.getText().toString())) {
                        email.setError("Invalid Email Address");
                        // valid_email=null;
                    } else {
                        Intent intent = new Intent(UserCreateAccountActivity.this, ActivateAccount.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
    });
    }
    public void CircleImage(View v) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            Log.e("@#image", "onActivityResult: " + selectedImage);
           /* Picasso.get().load(selectedImage).noPlaceholder().centerCrop().fit()
                    .into((ImageView) findViewById(R.id.create_account_image));*/
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                userProfileImage.setImageBitmap(bitmap);
                Log.e("bitmap", "onActivityResult: " + bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("eror", "onActivityResult: " + e);
            }
        }
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void setPhoto(Bitmap bitmapm) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] byteArrayImage = baos.toByteArray();
            String imagebase64string = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}