package net.itempire.brokerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SP_Detail_Activity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    SharedPreferences sharedPreferencesFB_user,sharedPreferencesSPData;
    CircleImageView img_sp;
    TextView txtname,txtemail,txtphone,txtaddress;
    SharedPreferences.Editor editor;
    ImageView map_button;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp__detail_);

        img_sp=(CircleImageView)findViewById(R.id.user_image);
        txtname=(TextView)findViewById(R.id.user_name);
        txtemail=(TextView)findViewById(R.id.user_email);
        txtphone=(TextView)findViewById(R.id.user_mob_number);
        txtaddress=(TextView)findViewById(R.id.user_address);
        map_button=(ImageView)findViewById(R.id.move_map);
        sharedPreferencesFB_user = getSharedPreferences("FBDetailsUSER",MODE_PRIVATE);
        sharedPreferencesSPData = getSharedPreferences("SpData",MODE_PRIVATE);
        editor=sharedPreferencesSPData.edit();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (sharedPreferencesFB_user.getString("id","").compareTo("")==0)
        {
            SharedPreferences.Editor editor = sharedPreferencesFB_user.edit();
            editor.putString("id",Long.toString(System.currentTimeMillis()));
            editor.apply();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            toolbar.setTitle("SP Detail");
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        sp_data();

        txtname.setText(sharedPreferencesSPData.getString("name",""));
        txtemail.setText(sharedPreferencesSPData.getString("email",""));
        txtphone.setText(sharedPreferencesSPData.getString("phone",""));
        txtaddress.setText(sharedPreferencesSPData.getString("address",""));

        String imageurl = sharedPreferencesSPData.getString("image", "").replace("\\", "");
        Picasso.get()
                .load(imageurl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.mipmap.ic_launcher_round)
                .into(img_sp);


        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("Status").setValue("SPC");

                Intent intent=new Intent(SP_Detail_Activity.this,UserDrawerActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
    public void sp_data(){
        mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("bookingDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String  Fbridedetails = (String) dataSnapshot.getValue().toString();
                    Fbridedetails.replace("\"", "");
                    Fbridedetails.replace("/", "");
                    Log.e("tag321", Fbridedetails);
                    if (dataSnapshot.getValue().toString().compareTo("") == 0) {
                        // FireBaseDriverStatus=dataSnapshot.getValue().toString();
                        Log.e("@@tag", "null value in fire base");
                    } else {
                        try {
                            JSONObject objjsaon = new JSONObject(Fbridedetails);
                            Log.e("tag@@@@@", objjsaon.toString());
                            Log.e("DropLocation = ", objjsaon.getString("image"));
                            Log.e("PickUpLocation = ", objjsaon.getString("sp_location"));
                            Log.e("pickUpLat = ", objjsaon.getString("sp_lat"));
                            Log.e("pickUpLng = ", objjsaon.getString("sp_lng"));;
                            Log.e("riderFbid = ", objjsaon.getString("myFb"));
                            Log.e("phone = ", objjsaon.getString("phone"));
                            Log.e("email = ", objjsaon.getString("email"));
                            Log.e("name = ", objjsaon.getString("name"));

                            double lat = objjsaon.getDouble("DropLat");
                            double lng = objjsaon.getDouble("DropLng");
                            LatLng drop = new LatLng(lat, lng);
                            Log.e("@@@ManualNotDrop", "onDataChange:==" + drop);


                            editor.putString("name",objjsaon.getString("name"));
                            editor.putString("image",objjsaon.getString("image"));
                            editor.putString("email",objjsaon.getString("email"));
                            editor.putString("phone",objjsaon.getString("phone"));
                            editor.putString("address",objjsaon.getString("address"));
                            editor.putString("sp_lat",objjsaon.getString("sp_lat"));
                            editor.putString("sp_lng",objjsaon.getString("splng"));
                            editor.apply();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
