package net.itempire.brokerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    CircleImageView img_user;
    EditText edt_name,edt_phone,edt_address,edt_email;
    TextView change_password,logout;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        img_user=(CircleImageView)findViewById(R.id.user_image);
        edt_name=(EditText) findViewById(R.id.user_name);
        edt_email=(EditText) findViewById(R.id.user_email);
        edt_phone=(EditText) findViewById(R.id.user_mob_number);
        edt_address=(EditText) findViewById(R.id.user_address);
        change_password=(TextView) findViewById(R.id.btnChangePassword);
        logout=(TextView) findViewById(R.id.btnLogout);

        sharedPreferences = getSharedPreferences("userData",MODE_PRIVATE);


        String imageurl = sharedPreferences.getString("image", "").replace("\\", "");
        Picasso.get()
                .load(imageurl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.mipmap.ic_launcher_round)
                .into(img_user);

        edt_name.setText(sharedPreferences.getString("name",""));
        edt_email.setText(sharedPreferences.getString("email_user",""));
        edt_phone.setText(sharedPreferences.getString("phone",""));
        edt_address.setText(sharedPreferences.getString("address",""));


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences loginSharedPrefrences=getSharedPreferences("loginDetail",MODE_PRIVATE);
                SharedPreferences.Editor editor=loginSharedPrefrences.edit();
                editor.clear();
                editor.apply();

                sharedPreferences = getSharedPreferences("userData",MODE_PRIVATE);
                SharedPreferences.Editor editor_user_data=sharedPreferences.edit();
                editor_user_data.clear();
                editor.apply();

                Intent intent=new Intent(UserProfileActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
