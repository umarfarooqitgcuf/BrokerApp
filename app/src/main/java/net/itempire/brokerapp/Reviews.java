package net.itempire.brokerapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Reviews extends AppCompatActivity {
    EditText reviews;
    Button btn_submit_review;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String ride_id;
    private DatabaseReference mDatabase;
    String URL_REVIEWS = "http://adminpanel.cargoroid.com/m_ride_request?ride_reviews--=1&";
    String URL_FARE = "http://adminpanel.cargoroid.com/m_ride_request?fare_ride--=1&ride_id=";
    TextView text_ride_fare, text_payable_fare;
    String rating = "";
    String url_rating = "https://alirazagcufit.000webhostapp.com/26-06-19/broker_App/broker_App/API/rating_trns.php";
    CircleImageView image;
    SharedPreferences sharedPreferencesSPData,sharedPreferencesFB_user,sharedRideData;
    ProgressDialog loadingDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final RatingBar minimumRating = (RatingBar) findViewById(R.id.myRatingBar);
        text_ride_fare = (TextView) findViewById(R.id.text_ride_fare);
        text_payable_fare = (TextView) findViewById(R.id.text_payable_fare);
        image = (CircleImageView) findViewById(R.id.user_image);
        sharedPreferencesSPData = getSharedPreferences("SpData", MODE_PRIVATE);

        sharedPreferencesFB_user = getSharedPreferences("FBDetailsUSER", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        sharedRideData=getSharedPreferences("rideData",MODE_PRIVATE);

        if (sharedPreferencesFB_user.getString("id", "").compareTo("") == 0) {
            SharedPreferences.Editor editor = sharedPreferencesFB_user.edit();
            editor.putString("id", Long.toString(System.currentTimeMillis()));
            editor.commit();
            editor.apply();
        }

        loadingDialog = new ProgressDialog(Reviews.this);
        loadingDialog.setTitle("Submiting Your reviews");
        loadingDialog.setMessage("Loading....");
        loadingDialog.show();

        String imageurl = sharedPreferencesSPData.getString("image", "").replace("\\", "");
        Picasso.get()
                .load(imageurl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.mipmap.ic_launcher_round)
                .into(image);

        minimumRating.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                float touchPositionX = event.getX();
                float width = minimumRating.getWidth();
                float starsf = (touchPositionX / width) * 5.0f;
                int stars = (int) starsf + 1;
                minimumRating.setRating(stars);
                return true;
            }
        });


        //reviews = (EditText) findViewById(R.id.editText_reviews);
        sharedPreferences = getSharedPreferences("riderApp", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        btn_submit_review = (Button) findViewById(R.id.btn_submit_review);

        Bundle extras = getIntent().getExtras();

        //Fare Api
        if (sharedPreferences.getString("ride_id", null) != null) {
           /* Fuel.get(URL_FARE + sharedPreferences.getString("ride_id",null)).responseString(new Handler<String>() {
                @Override
                public void success(Request request, Response response, String data) {
                    Log.e("FareResponse", "success: " + response);
                    try {
                        if (data!=null){
                            JSONObject jsonObject = new JSONObject(data);
                            text_ride_fare.setText(String.valueOf(jsonObject.getInt("total_fare")));
                            text_payable_fare.setText(String.valueOf(jsonObject.getInt("Collectible Amount")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                   *//* if (data != null) {
                        text_ride_fare.setText(data);
                    }*//*
                }

                @Override
                public void failure(Request request, Response response, FuelError fuelError) {
                    Log.e("FareResponse", "failure: " + response);
                    Toast.makeText(getApplicationContext(), "Slow Internet connection!", Toast.LENGTH_SHORT).show();
                }
            });*/
        }


        //Reviews Api
        btn_submit_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("User").child(sharedPreferences.getString("id", "")).child("userStatus").setValue("null");
                Bundle extras = getIntent().getExtras();
                Log.e("rideId_1", "onCreate: " + extras.getString("rideId"));
                if (extras.getString("rideId") != null) {
                    if (extras.getString("rideId").length() > 0) {
                        Log.e("rideId_2", "onCreate: " + extras.getString("rideId"));
                        ride_id = extras.getString("rideId");
                        Log.e("Rating..", "onClick: ==" + minimumRating.getRating());

                        if (minimumRating.getRating() == 1.0) {
                            rating = "poor";
                        } else if (minimumRating.getRating() == 2.0) {
                            rating = "average";
                        } else if (minimumRating.getRating() == 3.0) {
                            rating = "good";
                        } else if (minimumRating.getRating() == 4.0) {
                            rating = "excellent";
                        } else if (minimumRating.getRating() == 5.0) {
                            rating = "extraOrdinary";
                        } else {
                            /*if (minimumRating.getRating() == 0.0) {
                                rating = "Bad";
                            }else {
                                rating = "no rating";
                            }*/
                            rating = "noRating";

                        }
                       /* Fuel.get(URL_REVIEWS + "fk_ride_id=" + ride_id + "&review=" +rating).responseString(new Handler<String>() {
                            @Override
                            public void success(Request request, Response response, String s) {
                                Log.e("Reviews", "success: " + response);
                                Log.e("Succeeded", "success: ");
                                finish();
                                startActivity(new Intent(Reviews.this, DrawerActivity.class));
                            }

                            @Override
                            public void failure(Request request, Response response, FuelError fuelError) {
                                Log.e("Failed", "failure: ");
                            }
                        });*/
                    }
                }
                //editor.putString("ride_id",null);
                //finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDatabase.child("User").child(sharedPreferences.getString("id", "")).child("userStatus").setValue("null");
        finish();
        startActivity(new Intent(Reviews.this, UserDrawerActivity.class));
        editor.putString("activityCreated", "1");
        editor.apply();
    }

    public void put_review() {
        StringRequest StringRequest = new StringRequest(Request.Method.POST, url_rating, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                Log.e("@#Service_list", "onResponse: " + response);

                mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("Status").setValue("null");

                Intent intent=new Intent(Reviews.this,UserDrawerActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(Reviews.this, "Ops please Try Again No Service Found", Toast.LENGTH_SHORT).show();
                //This code is executed if there is an error.
                Log.e("@#loginError", "onResponse:" + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("rating",rating);
                MyData.put("booking_id",sharedRideData.getString("Booking_id",""));
                MyData.put("sp_id",rating);
                return MyData;
            }
        };
        RequestQueue RequestQueue = Volley.newRequestQueue(Reviews.this);
        RequestQueue.add(StringRequest);
    }
}
