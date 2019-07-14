package net.itempire.brokerapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    public double mlatti, mlongi;
    public LocationManager locationManager;
    SupportMapFragment mapFragment;
    ImageView myLocationButton,navigationButton;
    private GoogleApiClient googleApiClient;
    Button btn_send_request;

    Spinner spinnerServices;
    private static List<String> service;
    ArrayAdapter<String> spinnerArrayAdapter;
    String selectedService;
    int COUNTER = 0;
    ProgressDialog loadingDialog;
    TextView pickup,selectDropLocation;
    CircleImageView imageView;
    TextView user_name;
    public Double longitude;
    public Double lattitude;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    LatLng latLng_service;
    String DropText;
    String Boking_id;
    int driver_i;
    boolean returnedValue;
    TimerTask timerTask;
    Timer bookingTime;
    Timer changetStatusTimer;
    TimerTask changeStatus;

    private DatabaseReference mDatabase;
    SharedPreferences sharedPreferencesFB_user, sharedPreferences;

    HashMap<String, String> HashMapService = new HashMap<String, String>();
    HashMap<String, String> HshMapServiceCharges = new HashMap<String, String>();
    HashMap<String, String> HshMapServiceChargesmax = new HashMap<String, String>();

    String url = "https://alirazagcufit.000webhostapp.com/23-06/broker_App/broker_App/API/select_services_api.php";
    private static JSONArray myservices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_drawer);
       // Toolbar toolbar = findViewById(R.id.toolbar);
      /*  setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);*/

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        myLocationButton = findViewById(R.id.myLocationCustomButton);
        btn_send_request = (Button)findViewById(R.id.btnConfirmPickUp);
        //pickup = (TextView) findViewById(R.id.pickup);
        imageView = (CircleImageView) headerView.findViewById(R.id.imageView);
        user_name = (TextView) headerView.findViewById(R.id.txt_name);
        spinnerServices = (Spinner) findViewById(R.id.spinner_services);
        selectDropLocation = (TextView) findViewById(R.id.pickup);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferencesFB_user = getSharedPreferences("FBDetailsUSER", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);

        if (sharedPreferencesFB_user.getString("id", "").compareTo("") == 0) {
            SharedPreferences.Editor editor = sharedPreferencesFB_user.edit();
            editor.putString("id", Long.toString(System.currentTimeMillis()));
            editor.commit();
            editor.apply();
        }

        loadingDialog = new ProgressDialog(UserDrawerActivity.this);
        loadingDialog.setTitle("Fetching Your Data");
        loadingDialog.setMessage("Loading....");
        loadingDialog.show();

        String imageurl = sharedPreferences.getString("image", "").replace("\\", "");
        Picasso.get()
                .load(imageurl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.mipmap.ic_launcher_round)
                .into(imageView);
        user_name.setText(sharedPreferences.getString("name",""));

        service = new ArrayList<String>();
        service.add("Select Your Service");

        spinnerArrayAdapter = new ArrayAdapter<String>(
                UserDrawerActivity.this, R.layout.spinner_style_layout, service) {
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setBackgroundColor(Color.parseColor("#01411c"));
                if (position % 2 == 1) {
                    // Set the item background color
                    tv.setBackgroundColor(Color.parseColor("#01411c"));
                } else {
                    // Set the alternate item background color
                    tv.setBackgroundColor(Color.parseColor("#045c29"));
                }
                return view;
            }
        };
        spinnerServices.setPrompt("Select Your Service");
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_style_layout);
        spinnerServices.setAdapter(spinnerArrayAdapter);
        getServices();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        onMyLocationUpdate();


        btn_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedService = spinnerServices.getSelectedItem().toString();
                loadingDialog.show();
                if (selectedService.equals("Select Your Service")) {
                    loadingDialog.dismiss();
                    Toast.makeText(UserDrawerActivity.this, "Select A Service First", Toast.LENGTH_SHORT).show();
                } else if (latLng_service == null) {
                    loadingDialog.dismiss();
                    Toast.makeText(UserDrawerActivity.this, "Select your location First", Toast.LENGTH_SHORT).show();
                } else if (getCompleteAddressString(latLng_service.latitude, latLng_service.longitude) == null) {
                    loadingDialog.dismiss();
                    Toast.makeText(UserDrawerActivity.this, "Select your location First", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("@@@BTNsubmit", "onClick:===" + latLng_service + "==" + selectedService + "===" + HashMapService.get(selectedService));
                    Log.e("@@@LOcation", "onClick:===" + getCompleteAddressString(latLng_service.latitude, latLng_service.longitude));
                    final String SelectedLocation = getCompleteAddressString(latLng_service.latitude, latLng_service.longitude).trim();
                    final String lat = String.valueOf(latLng_service.latitude);
                    final String lng = String.valueOf(latLng_service.longitude);

                    /*final User user = new User(mlongi, mlatti, 1, "SR", rideDetails.toString(), "", "xyz", sharedPreferences.getString("uEmail", ""), "");
                    mDatabase.child("User").child(sharedPreferences.getString("id", "")).setValue(user);*/


                    String url_booking = "https://alirazagcufit.000webhostapp.com/26-06-19/broker_App/broker_App/API/booking_api.php";
                    // String url_booking = "https://alirazagcufit.000webhostapp.com/26-06-19/broker_App/broker_App/include/booking_api.php";

                    final StringRequest StringRequest = new StringRequest(Request.Method.POST, url_booking, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loadingDialog.dismiss();
                            Log.e("@#RESPONSE", "onResponse: " + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Boking_id = jsonObject.getString("return_id");
                                Log.e("@@@Bokingid", "onResponse:===" + Boking_id);
                                if (Boking_id != null) {

                                    final ArrayList<Float> listOfDriversDistances = new ArrayList<>();
                                    final ArrayList<String> listOfDrivers = new ArrayList<>();
                                    final ArrayList<String> driversToBook = new ArrayList<>();
                                    final float[] results = new float[1];
                                    mDatabase.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.e("@@@MyDriversLoc", "onDataChange: " + dataSnapshot.getValue());
                                            Iterable<DataSnapshot> drivers = dataSnapshot.getChildren();
                                            for (DataSnapshot objMyDrivers : drivers) {
                                                Log.e("$MyDriver", "onDataChange: " + objMyDrivers);
                                                Log.e("$MyDriverData", "onDataChange: " + objMyDrivers.child("SP Data").getValue());
                                                Log.e("$MyDriverStatus", "onDataChange: " + objMyDrivers.child("SP Data").child("userStatus").getValue());
                                                if (objMyDrivers.child("SP Data").getValue() != null) {
                                                    Log.e("@@@@NOTNULL", "onDataChange:==" + objMyDrivers.child("SP Data").getValue());
                                                    if (objMyDrivers.child("SP Data").child("Status").getValue() != null) {
                                                        Log.e("logOnUserStatus != null", "onDataChange: ");
                                                        if (objMyDrivers.child("SP Data").child("userStatus").getValue().toString().equals("online")) {
                                                            Log.e("logOnUserStatusIsOnline", "onDataChange: ==" + objMyDrivers.getKey());
                                                            if (objMyDrivers.child("SP Data").child("Status").getValue() != null) {
                                                                if (objMyDrivers.child("SP Data").child("Status").getValue().toString().equals("null") || objMyDrivers.child("SP Data").child("Status").getValue().toString().equals("RC")) {
                                                                    Log.e("logOnDriverStatusIsnull", "onDataChange: ");
                                                                    if (objMyDrivers.child("SP Data").child("Occupation").getValue() != null) {
                                                                        if (objMyDrivers.child("SP Data").child("Occupation").getValue().toString().equals(selectedService)) {
                                                                            Log.e("logonDriverOccuption", "onDataChange:==" + objMyDrivers.getKey());
                                                                            LatLng myDriverLatLng = new LatLng(Double.parseDouble(objMyDrivers.child("Location").child("Latitude").getValue().toString()), Double.parseDouble(objMyDrivers.child("Location").child("Longitude").getValue().toString()));
                                                                            Log.e("$MyDriverLatLng", "onDataChange: " + myDriverLatLng);
                                                                            Location.distanceBetween(latLng_service.latitude, latLng_service.longitude,
                                                                                    myDriverLatLng.latitude, myDriverLatLng.longitude, results);
                                                                            Log.e("@DistanceResult", "onClick: " + results[0]);
                                                                            listOfDriversDistances.add(results[0]);
                                                                            listOfDrivers.add(objMyDrivers.getKey());
                                                                            Log.e("@@@@DriverLIst", "onDataChange:==" + listOfDrivers);

                                                                            Log.e("DriverArraySize", "onDataChange: " + listOfDriversDistances.size());
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            bookingTime = new Timer();
                                            driver_i = 0;
                                            returnedValue = true;
                                            int loopCondition = listOfDriversDistances.size();
                                            if (loopCondition > 0 && driver_i <= 4) {

                                                if (returnedValue && Boking_id != null) {
                                                    Log.e("@LoopRunning..?", "onDataChange: True");
                                                    int min = listOfDriversDistances.indexOf(Collections.min(listOfDriversDistances));
                                                    listOfDriversDistances.remove(min);
                                                    driversToBook.add(listOfDrivers.get(min));
                                                    Log.e("$ClosestDriver", "run: " + listOfDrivers.get(min));
                                                    listOfDrivers.remove(min);
                                                    Log.e("$BookDrivers", "onDataChange: " + driversToBook.get(driver_i));

                                                    final JSONObject myRideDetails = new JSONObject();
                                                    try {
                                                        myRideDetails.put("PickUpLocation", getCompleteAddressString(latLng_service.latitude, latLng_service.longitude));
                                                        myRideDetails.put("PickUpLat", lat);
                                                        myRideDetails.put("PickUpLng", lng);
                                                        /*myRideDetails.put("DropLocation", getCompleteAddressString(dropLatLng.latitude, dropLatLng.longitude));
                                                        myRideDetails.put("DropLat", dropLatLng.latitude);
                                                        myRideDetails.put("DropLng", dropLatLng.longitude);*/
                                                        myRideDetails.put("userId", sharedPreferences.getString("email", ""));
                                                        myRideDetails.put("myFb", sharedPreferences.getString("id", ""));
                                                        myRideDetails.put("rideApiId", Boking_id);
                                                        myRideDetails.put("username", sharedPreferences.getString("name", ""));
                                                        myRideDetails.put("phone_number", sharedPreferences.getString("phone", ""));
                                                    } catch (Exception e) {
                                                        Log.e("Exception", "onDataChange: " + e);
                                                    }

                                                    mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("Status").setValue("SR");
                                                    mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("Notification").setValue("1");
                                                    mDatabase.child("User").child(driversToBook.get(driver_i)).child("SP Data").child("Status").setValue("RR");
                                                    mDatabase.child("User").child(driversToBook.get(driver_i)).child("SP Data").child("Notification").setValue("1");
                                                    mDatabase.child("User").child(driversToBook.get(driver_i)).child("SP Data").child("bookingDetails").setValue(myRideDetails.toString());

                                                    COUNTER = 0;
                                                    changetStatusTimer = new Timer();
                                                    changeStatus = new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            new Thread() {
                                                                public void run() {
                                                                    UserDrawerActivity.this.runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            COUNTER++;
                                                                            mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("Status").addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    Log.e("@@Index", "onDataChange: " + driver_i + " COUNTER == " + COUNTER);
                                                                                    if (dataSnapshot.getValue() != null) {
                                                                                        if (COUNTER == 10) {
                                                                                            if (dataSnapshot.getValue().toString().equals("SR")) {
                                                                                                returnedValue = true;
                                                                                                int old_i = 0;
                                                                                                if (driver_i == 0) {
                                                                                                    Log.e("@@@Counter", "onDataChange:===");
                                                                                                            /*mDatabase.child("User").child(driversToBook.get(driver_i)).child("Driver Data").child("driverStatus").setValue("null");
                                                                                                            mDatabase.child("User").child(driversToBook.get(driver_i)).child("Driver Data").child("rideDetails").setValue("null");*/
                                                                                                    changetStatusTimer.cancel();
                                                                                                    changetStatusTimer.purge();
                                                                                                }
                                                                                                if (driver_i > 0 || driversToBook.size() - 1 == driver_i) {
                                                                                                    Log.e("@@@Counter1", "onDataChange:===");
                                                                                                    old_i = driver_i - 1;
                                                                                                    mDatabase.child("User").child(driversToBook.get(old_i)).child("SP Data").child("Status").setValue("null");
                                                                                                    mDatabase.child("User").child(driversToBook.get(old_i)).child("SP Data").child("bookingDetails").setValue("null");
                                                                                                    changetStatusTimer.cancel();
                                                                                                    changetStatusTimer.purge();
                                                                                                }
                                                                                                //Below Code set null values to last driver if he too did't accept request.
                                                                                                        /*if (dataSnapshot.getValue().toString().equals("null")) {
                                                                                                            mDatabase.child("User").child(driversToBook.get(driver_i)).child("Driver Data").child("driverStatus").setValue("null");
                                                                                                            mDatabase.child("User").child(driversToBook.get(driver_i)).child("Driver Data").child("rideDetails").setValue("null");
                                                                                                        }*/
                                                                                                COUNTER = 0;
                                                                                            } else {
                                                                                                if (dataSnapshot.getValue().toString().equals("null")) {
                                                                                                    Log.e("@@@Counter3", "onDataChange:===");
                                                                                                    mDatabase.child("User").child(driversToBook.get(driver_i - 1)).child("SP Data").child("Status").setValue("null");
                                                                                                    mDatabase.child("User").child(driversToBook.get(driver_i - 1)).child("SP Data").child("bookingDetails").setValue("null");
                                                                                                }
                                                                                                COUNTER = 0;
                                                                                                returnedValue = false;
                                                                                                changetStatusTimer.cancel();
                                                                                                changetStatusTimer.purge();
                                                                                            }
                                                                                        } else {
                                                                                            if (dataSnapshot.getValue().toString().equals("null")) {
                                                                                                returnedValue = false;
                                                                                                COUNTER = 0;
                                                                                                mDatabase.child("User").child(driversToBook.get(driver_i - 1)).child("SP Data").child("Status").setValue("null");
                                                                                                mDatabase.child("User").child(driversToBook.get(driver_i - 1)).child("SP Data").child("bookingDetails").setValue("null");
                                                                                                changetStatusTimer.cancel();
                                                                                                changetStatusTimer.purge();
                                                                                            }
                                                                                        }

                                                                                    }

                                                                                }

                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                }
                                                                            });

                                                                        }
                                                                    });
                                                                }

                                                            }.start();
                                                        }
                                                    };
                                                    changetStatusTimer.scheduleAtFixedRate(changeStatus, 0, 1000);
                                                    driver_i++;
                                                } else {
                                                    //break;
                                                    listOfDrivers.clear();
                                                    listOfDriversDistances.clear();
                                                    driversToBook.clear();
                                                    new Thread() {
                                                        public void run() {
                                                            UserDrawerActivity.this.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    //mMap.clear();
                                                                }
                                                            });
                                                        }
                                                    };
                                                    bookingTime.cancel();
                                                    bookingTime.purge();
                                                }
                                            } else {
                                                //if no driver accepts your request
                                                new Thread() {
                                                    public void run() {
                                                        UserDrawerActivity.this.runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                //Do your UI operations like dialog opening or Toast here
                                                                Toast.makeText(UserDrawerActivity.this, "No Service Provider found for you yet.", Toast.LENGTH_SHORT).show();
                                                                /*dropLatLng = null;
                                                                selectVehicle.setVisibility(View.VISIBLE);
                                                                spinnerLayout.setVisibility(View.VISIBLE);*/
                                                                mMap.clear();
                                                            }
                                                        });
                                                    }
                                                }.start();
                                                //Check if no driver has accepted my requested and changes my statuses to null
                                                /*editor.putString("myFbStatus", null);
                                                editor.putString("ride_id", null);
                                                editor.apply();*/
                                                Boking_id = null;
                                                mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("SpFbId").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getValue() != null) {
                                                            if (dataSnapshot.getValue().toString().equals("")) {
                                                                mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("userStatus").setValue("null");
                                                                mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("USER Data").child("rideDetails").setValue("null");
                                                                /*finish();
                                                                startActivity(myIntent);*/

                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                                bookingTime.cancel();
                                                bookingTime.purge();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loadingDialog.dismiss();
                            Toast.makeText(UserDrawerActivity.this, "Ops please Try Again", Toast.LENGTH_SHORT).show();
                            //This code is executed if there is an error.
                            Log.e("@#Volly Error search", "onResponse:" + error);
                        }
                    }) {
                        protected Map<String, String> getParams() {
                            Map<String, String> MyData = new HashMap<String, String>();
                            MyData.put("user_id", sharedPreferences.getString("id", ""));
                            MyData.put("service_id", HashMapService.get(selectedService).toString());
                            MyData.put("location", SelectedLocation.toString());
                            MyData.put("lattitude", lat.toString());
                            MyData.put("longitude", lng.toString());

                            Log.e("@@@PARAMS", "getParams:===" + MyData);
                            return MyData;
                        }
                    };
                    RequestQueue RequestQueue = Volley.newRequestQueue(UserDrawerActivity.this);
                    RequestQueue.add(StringRequest);
                }
            }
        });
        navigationButton = (ImageView) findViewById(R.id.image);

        selectDropLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //  btnClicked = "1";
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                            .setCountry("PAK")
                            .build();
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .setFilter(typeFilter)
                                    .build(UserDrawerActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);


                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(UserDrawerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserDrawerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16);
                    selectDropLocation.setText(getCompleteAddressString(location.getLatitude(), location.getLongitude()));

                    UserDrawerActivity.this.mMap.animateCamera(cameraUpdate, 250, null);

                } else {

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        /*locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
                        locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
                                LocationProvider.AVAILABLE,
                                null, System.currentTimeMillis());*/
                        AlertDialog dialog;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(UserDrawerActivity.this);
                        builder.setMessage("Your location service is disable, enable your location to continue!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                builder.setCancelable(true);
                            }
                        });
                        dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        onMyLocationUpdate();
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
           Intent intent = new Intent(UserDrawerActivity.this,UserProfileActivity.class);
           startActivity(intent);
        } else if (id == R.id.nav_wallet) {

        }  else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_active_services) {
            Intent intent = new Intent(UserDrawerActivity.this,ActiveServicesActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_pending_services) {

        }
        else if (id == R.id.nav_completed_services) {

        }
        else if (id == R.id.nav_rate_app) {

        }
        else if (id == R.id.nav_logout) {
            SharedPreferences loginSharedPrefrences=getSharedPreferences("loginDetail",MODE_PRIVATE);
            SharedPreferences.Editor editor=loginSharedPrefrences.edit();
            editor.clear();
            editor.apply();

            sharedPreferences = getSharedPreferences("userData",MODE_PRIVATE);
            SharedPreferences.Editor editor_user_data=sharedPreferences.edit();
            editor_user_data.clear();
            editor.apply();

            Intent intent=new Intent(UserDrawerActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation(googleMap);
        getLocation();
        onMyLocationUpdate();

        LatLng mLocation = new LatLng(mlatti, mlongi);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 15));

    }
    public void getServices(){
        StringRequest StringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                Log.e("@#Service_list", "onResponse: " + response);

                try {
                    myservices = new JSONArray(response);
                    Log.e("service_length", "success: " + myservices.length());
                    if (myservices.length() > 0) {
                        Log.e("1234", "onResponse: " + myservices);
                        for (int i = 0; i < myservices.length(); i++) {
                            Log.e("4321", "onResponse: " + myservices.length());
                            try {
                                JSONObject objServive = (JSONObject) myservices.get(i);
                                Log.e("@#Service_list" + i, "onResponse:" + objServive);
                                Log.e("@#Service_list" + i, "onResponse:" + objServive.getString("s_name"));

                                service.add(objServive.getString("s_name"));

                                HashMapService.put(objServive.getString("s_name"), objServive.getString("id"));
                                HshMapServiceCharges.put(objServive.getString("s_name"), objServive.getString("s_min"));
                                HshMapServiceChargesmax.put(objServive.getString("s_name"), objServive.getString("s_max"));
                                Log.e("#$hashmap", "==" + HashMapService);
                                Log.e("#$hashmapCharges", "==" + HshMapServiceCharges);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("EEEE", "onResponse: " + e);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(UserDrawerActivity.this, "Ops please Try Again No Service Found", Toast.LENGTH_SHORT).show();
                //This code is executed if there is an error.
                Log.e("@#loginError", "onResponse:" + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                return MyData;
            }
        };
        RequestQueue RequestQueue = Volley.newRequestQueue(UserDrawerActivity.this);
        RequestQueue.add(StringRequest);
    }
    public void onMyLocationUpdate() {
        Log.e("@@@permission", "null123");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("@@@permission", "null");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else {
            Log.e("@@@permission", "null12345");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mlatti = location.getLatitude();
                    mlongi = location.getLongitude();
                    Log.e("@@@latLong", "onLocationChanged:==" + mlatti + "==" + mlongi);

                    mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("Location").child("Longitude").setValue(longitude = location.getLongitude());
                    mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("Location").child("Latitude").setValue(lattitude = location.getLatitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });
        }
    }

    public void enableMyLocation(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            Log.e("@@@permission", "===enableMyLocation: Enabled...");
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    }, 1);
            Log.e("@@@permissionreq", "enableMyLocation: Requesting...");
        }
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else {
            mMap.setMyLocationEnabled(true);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.e("@@@@Location", "getLocation: " + location);
            if (location != null) {
                mlatti = location.getLatitude();
                mlongi = location.getLongitude();

                latLng_service = new LatLng(mlatti, mlongi);
                selectDropLocation.setText(getCompleteAddressString(mlatti, mlongi));


                mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("Location").child("Longitude").setValue(longitude = location.getLongitude());
                mDatabase.child("User").child(sharedPreferencesFB_user.getString("id", "")).child("Location").child("Latitude").setValue(lattitude = location.getLatitude());


                Log.e("@@@LocationLAT", "getLocation:==" + mlatti + "==" + mlongi);
                // Log.e("@@@CompleteAddress", "getLocation:=="+getCompleteAddressString(mlatti,mlongi) );
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, intentData);
                Log.e("@@@Place", "Place: " + place.getName());
                selectDropLocation.setText(place.getName());
                DropText = place.getName().toString();
                final LatLng latLngloc = place.getLatLng();

                lattitude = latLngloc.latitude;
                longitude = latLngloc.longitude;
                Log.e("@@@LatLang", "onActivityResult:==" + latLngloc);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngloc, 16));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngloc, 16));
                mMap.addMarker(new MarkerOptions().draggable(true).position(latLngloc).title("Your Service Location")).showInfoWindow();

                latLng_service = new LatLng(latLngloc.latitude, latLngloc.longitude);
                //Log.e("@@@Lat", "onActivityResult:=="+lat);
                //Log.e("@@@Lng", "onActivityResult:=="+lng);

               /* if (btnClicked != null) {
                    if (btnClicked.equals("2")) {
                        textDrop.setText(place.getName());
                        final LatLng latLngloc = place.getLatLng();
                       // dropLatLng = new LatLng(latLngloc.latitude, latLngloc.longitude);
                       // Log.e("#1", "onPlaceSelected: " + dropLatLng);
                        if (marker != null) {
                            marker.remove();
                            Log.e("@@@ marker ? ", "onPlaceSelected: ");
                        }
                        mMap.addMarker(new MarkerOptions().position(dropLatLng).title("Your Drop Location")).showInfoWindow();
                    }
                    if (btnClicked.equals("1")) {
                        pickUpLoc.setText(place.getName());
                        final LatLng latLngloc = place.getLatLng();
                        pickupLatLng = new LatLng(latLngloc.latitude, latLngloc.longitude);
                        Log.e("#1", "onPlaceSelected: " + pickupLatLng);
                        if (marker != null) {
                            marker.remove();
                            Log.e("@@@ marker ? ", "onPlaceSelected: ");
                        }
                        if (mMap != null) {
                            mMap.clear();
                        }
                        pinImage.setVisibility(View.GONE);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, 15));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickupLatLng, 15));
                        mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Your PickUp Location")).showInfoWindow();
                    }
                    btnClicked = null;
                }*/

                ///mMap.addMarker(new MarkerOptions().position(latLngloc).title(place.getName().toString()));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngloc,15), 2000, null);
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngloc, 15));
                // Log.e("@@@ addMarker", "onPlaceSelected: ");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, intentData);
                // TODO: Handle the error.
                Log.e("@#!", status.getStatusMessage());
                Log.e("@#!", status.toString());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.e("@LocationAddress", "My Current loction address" + strReturnedAddress.toString());
            } else {
                Log.e("@AddressNotFound", "My Current loction address No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("@ErrinInAAddress", "My Current loction address Canont get Address!");
        }
        return strAdd;
    }
}
