package net.itempire.brokerapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConfirmFareActivity extends AppCompatActivity {
    String fare,rider_name,ride_id,total_fare;
    Button ok,cancel;
    TextView txt_fare,Customer_Name;
    EditText amount;
    ProgressDialog loadingDialog;

    private DatabaseReference mDatabase;
    public SharedPreferences sharedfare;
    String sharedfare1,sharedtotal_fare,sharedr_name,sharedride_id;
    String FireBaseDriverStatus;
    String urlCollect="";
    SharedPreferences sharedRideData,sharedPreferencesFB_sp,sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_fare);
        txt_fare=(TextView) findViewById(R.id.txt_fare);
        Customer_Name=(TextView) findViewById(R.id.Customer_Name);
        ok=(Button)findViewById(R.id.btn_ok);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedRideData=getSharedPreferences("rideData",MODE_PRIVATE);
        sharedPreferencesFB_sp = getSharedPreferences("FBDetailsSP", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);

        sharedfare=getSharedPreferences("fare",MODE_PRIVATE);
        sharedfare1 = sharedfare.getString("fare","");
        sharedtotal_fare=sharedfare.getString("total_fare","");
        sharedr_name=sharedfare.getString("r_name","");
        sharedride_id=sharedfare.getString("ride_id","");

        loadingDialog=new ProgressDialog(ConfirmFareActivity.this);
        loadingDialog.setTitle("Collecting Fare");
        loadingDialog.setMessage("Loading....");

        if (sharedPreferencesFB_sp.getString("id", "").compareTo("") == 0) {
            SharedPreferences.Editor editor = sharedPreferencesFB_sp.edit();
            editor.putString("id", Long.toString(System.currentTimeMillis()));
            editor.commit();
            editor.apply();
        }
        amount=(EditText)findViewById(R.id.amount);

        if (sharedr_name.contains("%20")) {
            sharedr_name = sharedr_name.replace("%20", " ");
        }
        txt_fare.setText(sharedfare1);
        Customer_Name.setText(sharedr_name);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();

                if (amount.length()==0){
                    amount.setError("please enter some value");
                    loadingDialog.dismiss();
                } else
                    {
                        try {
                            Float num = Float.parseFloat(sharedfare1);
                            Log.e("",num+" is a number");
                            if (Integer.parseInt(amount.getText().toString()) >= Integer.parseInt(sharedfare1)) {

                                CollectAmount(sharedRideData.getString("BookingID",""),sharedPreferences.getString("id",""),amount.getText().toString());

                            } else {
                                loadingDialog.dismiss();
                                amount.setError("Collect atleast given fare");
                            }
                        } catch (NumberFormatException e) {
                            loadingDialog.dismiss();
                            Log.e("@@@",sharedfare1+" is not a number");
                            Toast.makeText(ConfirmFareActivity.this,"Fare Is not Calculated",Toast.LENGTH_LONG);
                        }
                }
            }
        });
    }
    public void CollectAmount(final String bID, final String spID, final String amount) {
        final StringRequest StringRequest = new StringRequest(Request.Method.POST, urlCollect, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                Log.e("@#WorkStart", "onResponse: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String acceptORreject=jsonObject.getString("reject");

                    mDatabase.child("User").child(sharedRideData.getString("riderFbid", "")).child("USER Data").child("Status").setValue("WS");
                    mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("Status").setValue("null");


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(ConfirmFareActivity.this, "Ops please Try Again No Service Found", Toast.LENGTH_SHORT).show();
                Log.e("@#loginError", "onResponse:" + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("booking_id",bID);
                MyData.put("sp_id",spID );
                MyData.put("collected_amount",amount );

                return MyData;
            }
        };
        RequestQueue RequestQueue = Volley.newRequestQueue(ConfirmFareActivity.this);
        RequestQueue.add(StringRequest);
    }
}