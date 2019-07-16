package net.itempire.brokerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FareActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar mToolbar;
    Button collectAmount;
    TextView txt_fare,txt_rider_name,price_collectable;
    String fare,rname,ride_id,total_fare;
    private DatabaseReference mDatabase;
    public SharedPreferences sharedPreferences,sharedfare;
    String sharedfare1,sharedtotal_fare,sharedr_name,sharedride_id;
    String FireBaseDriverStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare);
        txt_fare=(TextView)findViewById(R.id.price_text);
        price_collectable=(TextView)findViewById(R.id.price_collectable);
        txt_rider_name=(TextView )findViewById(R.id.name_rider);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences("driverApp", MODE_PRIVATE);
        sharedfare=getSharedPreferences("fare",MODE_PRIVATE);

        sharedfare1 = sharedfare.getString("fare","");
        sharedtotal_fare=sharedfare.getString("total_fare","");
        sharedr_name=sharedfare.getString("r_name","");
        sharedride_id=sharedfare.getString("ride_id","");

        Log.e("sharedfare1", "onCreate: "+sharedfare1);
        Log.e("sharedtotal_fare", "onCreate: "+sharedfare1);
        Log.e("sharedr_name", "onCreate: "+sharedfare1);
        Log.e("sharedride_id", "onCreate: "+sharedfare1);

        if (sharedPreferences.getString("id", "").compareTo("") == 0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("id", Long.toString(System.currentTimeMillis()));
            editor.commit();
            editor.apply();
        }
        if (sharedr_name.contains("%20")) {
            sharedr_name = sharedr_name.replace("%20", " ");
        }
        txt_fare.setText(sharedtotal_fare);
        price_collectable.setText(sharedfare1);

        txt_rider_name.setText(sharedr_name);
        mToolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.fare_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("FARE CALCULATION");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collectAmount = (Button)findViewById(R.id.btn_Collect_Amount);
        collectAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mDatabase.child("User").child(sharedPreferences.getString("id", "")).child("Driver Data").child("driverStatus").setValue("CA");
                Intent intent = new Intent(FareActivity.this,ConfirmFareActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}