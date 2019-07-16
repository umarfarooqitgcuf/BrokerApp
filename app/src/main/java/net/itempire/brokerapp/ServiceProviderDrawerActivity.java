package net.itempire.brokerapp;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceProviderDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    ImageView navigationButton;

    public double mlatti, mlongi;
    public LocationManager locationManager;
    SupportMapFragment mapFragment;
    public Double longitude;
    public Double lattitude;
    ImageView myLocationButton;
    private int progressStatus;
    private static final long START_TIME_IN_MILLIS = 20000;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private Handler handler = new Handler();
    private boolean mTimerRunning;
    Vibrator vibrator;
    public static MediaPlayer mp;

    Switch switchONOFF;
    Button btnAccept, btnReject,btnStartWork,btnStopWork;

    SharedPreferences sharedPreferencesFB_sp, sharedPreferences,sharedRideData;
    SharedPreferences.Editor editor_user_data;
    private DatabaseReference mDatabase;
    ProgressDialog loadingDialog;
    ProgressBar progressBar;
    String url_accept = "https://alirazagcufit.000webhostapp.com/26-06-19/broker_App/broker_App/API/accept_booking.php";
    String url_startWork = "https://alirazagcufit.000webhostapp.com/26-06-19/broker_App/broker_App/API/start_trns.php";
    String url_stopWork = "https://alirazagcufit.000webhostapp.com/26-06-19/broker_App/broker_App/API/end_trns.php";
    ImageView imageView;
    TextView user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        imageView = (CircleImageView) headerView.findViewById(R.id.imageView);
        user_name = (TextView) headerView.findViewById(R.id.txt_name);



        switchONOFF = (Switch) findViewById(R.id.simpleSwitch);
        btnAccept = (Button) findViewById(R.id.btnaccept);
        btnReject = (Button) findViewById(R.id.btnreject);
        btnStartWork = (Button) findViewById(R.id.btn_start_work);
        btnStopWork = (Button) findViewById(R.id.btn_end_work);
        myLocationButton = findViewById(R.id.myLocationCustomButton);
        navigationButton = (ImageView) findViewById(R.id.image);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        vibrator = (Vibrator) ServiceProviderDrawerActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        mp = MediaPlayer.create(ServiceProviderDrawerActivity.this, R.raw.clocksound);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressBar.setVisibility(View.GONE);
        sharedRideData=getSharedPreferences("rideData",MODE_PRIVATE);
        sharedPreferencesFB_sp = getSharedPreferences("FBDetailsSP", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        editor_user_data = sharedPreferences.edit();

        loadingDialog = new ProgressDialog(ServiceProviderDrawerActivity.this);
        loadingDialog.setTitle("Fetching your data");
        loadingDialog.setMessage("Loading....");

        if (sharedPreferencesFB_sp.getString("id", "").compareTo("") == 0) {
            SharedPreferences.Editor editor = sharedPreferencesFB_sp.edit();
            editor.putString("id", Long.toString(System.currentTimeMillis()));
            editor.commit();
            editor.apply();
        }
        btn_online_ofline_check();
        request_checking();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String imageurl = sharedPreferences.getString("image", "").replace("\\", "");
        Picasso.get()
                .load(imageurl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.mipmap.ic_launcher_round)
                .into(imageView);
        user_name.setText(sharedPreferences.getString("name",""));

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        onMyLocationUpdate();

        switchONOFF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // do something when check is selected
                    mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("userStatus").setValue("online");
                } else {
                    //do something when unchecked
                    mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("userStatus").setValue("offline");
                }
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                datagetting();
                accept_request(sharedRideData.getString("BookingID",""),sharedPreferences.getString("id",""),"1");

            }
        });
        btnStartWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                startWork(sharedRideData.getString("BookingID",""),sharedPreferences.getString("id",""));


            }
        });
        btnStopWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                stopWork(sharedRideData.getString("BookingID",""),sharedPreferences.getString("id",""));

            }
        });
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                datagetting();
                accept_request(sharedRideData.getString("BookingID",""),sharedPreferences.getString("id",""),"0");
            }
        });
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ServiceProviderDrawerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ServiceProviderDrawerActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


                    ServiceProviderDrawerActivity.this.mMap.animateCamera(cameraUpdate, 250, null);

                } else {

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        /*locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
                        locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
                                LocationProvider.AVAILABLE,
                                null, System.currentTimeMillis());*/
                        AlertDialog dialog;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ServiceProviderDrawerActivity.this);
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
        getMenuInflater().inflate(R.menu.service_provider_drawer, menu);
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
            Intent intent = new Intent(ServiceProviderDrawerActivity.this,UserProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_wallet) {
            Intent intent=new Intent(ServiceProviderDrawerActivity.this,UserWallet.class);
            startActivity(intent);

        } else if (id == R.id.nav_history) {

        }else if (id == R.id.nav_active_services) {

        } else if (id == R.id.nav_pending_services) {

        } else if (id == R.id.nav_completed_services) {

        }  else if (id == R.id.nav_rate_app) {


        }  else if (id == R.id.nav_logout) {
            SharedPreferences loginSharedPrefrences=getSharedPreferences("loginDetail",MODE_PRIVATE);
            SharedPreferences.Editor editor=loginSharedPrefrences.edit();
            editor.clear();
            editor.apply();

            sharedPreferences = getSharedPreferences("userData",MODE_PRIVATE);
            SharedPreferences.Editor editor_user_data=sharedPreferences.edit();
            editor_user_data.clear();
            editor.apply();

            Intent intent=new Intent(ServiceProviderDrawerActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void startTimer() {
        progressStatus = 100;
        mCountDownTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
               /* btnAcceptRequest.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressStatus > 0) {
                            progressStatus -= 10;
                            // start_tune();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    startAnimation();
                                    progressBar.setProgress(progressStatus);
                                }
                            });
                        }
                    }
                }).start();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Log.e("@@@Finish", "onFinish:==");
                vibrator.cancel();
                pause_tune();
            }
        }.start();
        mTimerRunning = true;
    }
    public void pause_tune() {
        if (mp.isPlaying()) {
            mp.stop();
        }
    }public void start_tune() {
        mp.start();
    }

    private void startAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", 100, 0);
        objectAnimator.setDuration(20000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    public void request_checking() {
        mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String dataFB = (String) dataSnapshot.getValue().toString();
                    Log.e("@@@Data", "onDataChange:===" + dataFB);
                    if (dataFB.equals("null")) {
                        btnAccept.setVisibility(View.GONE);
                        btnReject.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

                        NotificationManager notificationManager = (NotificationManager)
                                getSystemService(Context.
                                        NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                        pause_tune();



                    } else if (dataFB.equals("RR")) {
                        btnAccept.setVisibility(View.VISIBLE);
                        btnReject.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        start_tune();
                        startTimer();
                        vibrator.vibrate(15000);

                        String Notification_text = "You have received request for service";

                        Intent intent = new Intent(getApplicationContext(), ServiceProviderDrawerActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        intent.setAction("Notification.getFromBarUser");
                        intent.putExtra("openingNotiFromBarUser", "1");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            CharSequence name = "chanelname";
                            String description = "channelDiscription";
                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                            NotificationChannel channel = new NotificationChannel("1", name, importance);
                            channel.setDescription(description);
                            // Register the channel with the system; you can't change the importance
                            // or other notification behaviors after this
                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(channel);
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                                .setSmallIcon(R.drawable.green)
                                .setContentTitle("Broker App")
                                .setContentText(Notification_text)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);
                        ;

                        int PROGRESS_MAX = 0;
                        int PROGRESS_CURRENT = 0;
                        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, true);

                        NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager1.notify(1, builder.build());


                    } else if (dataFB.equals("AR")) {
                        String PickUpLat=sharedRideData.getString("PickUpLat","");
                        String PickUpLng=sharedRideData.getString("PickUpLng","");
                        pause_tune();
                        vibrator.cancel();

                        Double DpicupLat=Double.valueOf(PickUpLat);
                        Double DpicupLng=Double.valueOf(PickUpLng);

                        NotificationManager notificationManager = (NotificationManager)
                                getSystemService(Context.
                                        NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();

                        LatLng pickup=new LatLng(DpicupLat,DpicupLng);
                        mMap.addMarker(new MarkerOptions().position(pickup).title("Drop location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickup, 17));

                        Intent intent=new Intent(ServiceProviderDrawerActivity.this,USER_Detail_Activity.class);
                        startActivity(intent);

                        //move intent here for sp details fragment
                    }else if (dataFB.equals("STOP")) {
                        Intent intent=new Intent(ServiceProviderDrawerActivity.this,FareActivity.class);
                        startActivity(intent);


                    }else if (dataFB.equals("Collect")) {
                        Intent intent=new Intent(ServiceProviderDrawerActivity.this,FareActivity.class);
                        startActivity(intent);


                    } else if (dataFB.equals("SPC")) {
                        btnAccept.setVisibility(View.GONE);
                        btnReject.setVisibility(View.GONE);
                        btnStartWork.setVisibility(View.VISIBLE);

                        DateTime now = new DateTime();
                        SharedPreferences sharedPreferencesUSERData = getSharedPreferences("USERData",MODE_PRIVATE);
                        String strriderpickuplat = sharedPreferencesUSERData.getString("sp_lat","");
                        String strriderpickuplng = sharedPreferencesUSERData.getString("sp_lng","");

                        try {
                            if (String.valueOf(mlatti) != null && String.valueOf(mlongi) != null && strriderpickuplat != null && strriderpickuplng != null) {

                                DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                                        .mode(TravelMode.DRIVING)
                                        .origin(mlatti + "," + mlongi)
                                        .destination(strriderpickuplat + "," + strriderpickuplng)
                                        .departureTime(now)
                                        .await();

                                Double la = Double.valueOf(strriderpickuplat);
                                Double ln = Double.valueOf(strriderpickuplng);

                                LatLng pickup = new LatLng(la, ln);
                                Log.e("@@@Crashhh", "onDataChange:==" + pickup + "==" + la + "==" + ln + "==" + result + "==" + mMap);
                                if (pickup != null) {
                                    try {
                                        mMap.addMarker(new MarkerOptions().position(pickup).title("PickUp location"));
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickup, 15));

                                        addPolyline(result, mMap);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(String.valueOf(mlatti)), Double.parseDouble(String.valueOf(mlongi))), 15));

                                    } catch (Exception e) {
                                        Toast.makeText(ServiceProviderDrawerActivity.this, "Route Not Found", Toast.LENGTH_SHORT).show();
                                        Log.e("@@@Exeption", "onDataChange:==" + e);
                                    }

                                }
                                if (mMap != null) {
                                    addPolyline(result, mMap);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(sharedPreferences.getString("mlati", "")), Double.parseDouble(sharedPreferences.getString("mlongi", ""))), 15));
                                }
                            } else {
                                Log.e("Location is empty", "onDataChange: " + mlongi + "=" + strriderpickuplat + "=" + strriderpickuplng);
                            }

                        } catch (ApiException e) {
                            e.printStackTrace();
                            Log.e("ApiException", "onDataChange: " + e);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e("InterruptedException", "onDataChange: " + e);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("IOException", "onDataChange: " + e);
                        }

                    } else if (dataFB.equals("RC")) {
                        btnAccept.setVisibility(View.GONE);
                        btnReject.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        pause_tune();
                        vibrator.cancel();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void btn_online_ofline_check() {
        mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("userStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String ustatus = (String) dataSnapshot.getValue();
                    if (ustatus.equals("online")) {
                        switchONOFF.setChecked(true);
                       /* btnOnLine.setVisibility(View.GONE);
                        btnOffLine.setVisibility(View.VISIBLE);*/
                        Log.e("$$$online", "onDataChange: ");
                    } else if (ustatus.equals("offline")) {
                        switchONOFF.setChecked(false);
                       /* btnOnLine.setVisibility(View.VISIBLE);
                        btnOffLine.setVisibility(View.GONE);*/
                        Log.e("$$$offline", "onDataChange:OnLoineOfLine Check");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation(googleMap);
        getLocation();
        onMyLocationUpdate();

        LatLng mLocation = new LatLng(mlatti, mlongi);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 15));

      /*  View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("4"));
        locationButton.setVisibility(View.GONE);
        // and next place it, for example, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();*/

       mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // position on right bottom
        /*rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, -500, 70, 150);*/
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

                    mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("Location").child("Longitude").setValue(longitude = location.getLongitude());
                    mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("Location").child("Latitude").setValue(lattitude = location.getLatitude());
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

                /*latLng_service = new LatLng(mlatti, mlongi);
                selectDropLocation.setText(getCompleteAddressString(mlatti, mlongi));*/


                mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("Location").child("Longitude").setValue(longitude = location.getLongitude());
                mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("Location").child("Latitude").setValue(lattitude = location.getLatitude());


                Log.e("@@@LocationLAT", "getLocation:==" + mlatti + "==" + mlongi);
                // Log.e("@@@CompleteAddress", "getLocation:=="+getCompleteAddressString(mlatti,mlongi) );
            }
        }
    }

    public void accept_request(final String bID, final String spID, final String accept) {
        final StringRequest StringRequest = new StringRequest(Request.Method.POST, url_accept, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                Log.e("@#Service_list", "onResponse: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String acceptORreject=jsonObject.getString("reject");


                    if (accept.equals("1")){
                        mDatabase.child("User").child(sharedRideData.getString("riderFbid", "")).child("USER Data").child("Status").setValue("AR");
                        mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("Status").setValue("AR");

                        /*Intent intent=new Intent(ServiceProviderDrawerActivity.this,SP_Detail_Activity.class);
                        startActivity(intent);*/

                    }else if (accept.equals("0")){
                        mDatabase.child("User").child(sharedRideData.getString("riderFbid", "")).child("USER Data").child("Status").setValue("RC");
                        mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("Status").setValue("RC");

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(ServiceProviderDrawerActivity.this, "Ops please Try Again No Service Found", Toast.LENGTH_SHORT).show();
                Log.e("@#loginError", "onResponse:" + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("booking_id",bID);
                MyData.put("sp_id",spID );
                MyData.put("accept", accept);
                return MyData;
            }
        };
        RequestQueue RequestQueue = Volley.newRequestQueue(ServiceProviderDrawerActivity.this);
        RequestQueue.add(StringRequest);
    }

    public void datagetting() {
        mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("Sp Data").child("bookingDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String Fbridedetails = (String) dataSnapshot.getValue().toString();
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
                            Log.e("PickUpLocation = ", objjsaon.getString("PickUpLocation"));
                            Log.e("pickUpLat = ", objjsaon.getString("PickUpLat"));
                            Log.e("pickUpLng = ", objjsaon.getString("PickUpLng"));
                            Log.e("riderFbid = ", objjsaon.getString("myFb"));

                            String PickUpLocation = (String) objjsaon.getString("PickUpLocation");
                            String PickUpLat = (String) objjsaon.getString("PickUpLat");
                            String PickUpLng = (String) objjsaon.getString("DropLng");
                            String riderFbid = (String) objjsaon.getString("myFb");
                            String userEmail = (String) objjsaon.getString("userEmail");
                            String userId = (String) objjsaon.getString("userId");
                            String BookingID = (String) objjsaon.getString("rideApiId");
                            String username = (String) objjsaon.getString("username");
                            String phone_number = (String) objjsaon.getString("phone_number");
                            String image = (String) objjsaon.getString("image");



                            SharedPreferences.Editor editor=sharedRideData.edit();
                            editor.putString("PickUpLocation",PickUpLocation);
                            editor.putString("PickUpLat",PickUpLat);
                            editor.putString("PickUpLng",PickUpLng);
                            editor.putString("riderFbid",riderFbid);
                            editor.putString("userEmail",userEmail);
                            editor.putString("userId",userId);
                            editor.putString("BookingID",BookingID);
                            editor.putString("username",username);
                            editor.putString("phone_number",phone_number);
                            editor.putString("image",image);
                            editor.apply();

                            /*double lat = objjsaon.getDouble("DropLat");
                            double lng = objjsaon.getDouble("DropLng");
                            LatLng drop = new LatLng(lat, lng);
                            Log.e("@@@ManualNotDrop", "onDataChange:==" + drop);
                            double lat = objjsaon.getDouble("DropLat");
                            double lng = objjsaon.getDouble("DropLng");
                            drop = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(drop).title("Service Location location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(drop, 15));
                            pickupLocationServer.setText(obj.getString("PickUpLocation"));
                            dropLocatioServer.setText(obj.getString("DropLocation"));*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //pickupLocationServer.setText(Fbridedetails);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void stopWork(final String bID, final String spID) {
        final StringRequest StringRequest = new StringRequest(Request.Method.POST, url_stopWork, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                Log.e("@#WorkStart", "onResponse: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String total_fare = jsonObject.getString("total_fare");
                    String Collectible_Amount = jsonObject.getString("Collectible Amount");

                    SharedPreferences sharedfare = getSharedPreferences("fare", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedfare.edit();
                    editor.putString("fare", Collectible_Amount);
                    editor.putString("total_fare", total_fare);
                    editor.putString("r_name", sharedPreferences.getString("name",""));

                    mDatabase.child("User").child(sharedRideData.getString("riderFbid", "")).child("USER Data").child("Status").setValue("STOP");
                    mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("Status").setValue("STOP");

                    if (mMap != null) {
                        mMap.clear();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(ServiceProviderDrawerActivity.this, "Ops please Try Again No Service Found", Toast.LENGTH_SHORT).show();
                Log.e("@#loginError", "onResponse:" + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("booking_id",bID);
                MyData.put("sp_id",spID );
                return MyData;
            }
        };
        RequestQueue RequestQueue = Volley.newRequestQueue(ServiceProviderDrawerActivity.this);
        RequestQueue.add(StringRequest);
    }

    public void startWork(final String bID, final String spID) {
        final StringRequest StringRequest = new StringRequest(Request.Method.POST, url_startWork, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                Log.e("@#WorkStart", "onResponse: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String acceptORreject=jsonObject.getString("reject");

                        mDatabase.child("User").child(sharedRideData.getString("riderFbid", "")).child("USER Data").child("Status").setValue("WS");
                        mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("Status").setValue("WS");

                        Toast.makeText(ServiceProviderDrawerActivity.this,"Start your service for the user",Toast.LENGTH_LONG).show();
                        if (mMap != null) {
                            mMap.clear();
                        }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(ServiceProviderDrawerActivity.this, "Ops please Try Again No Service Found", Toast.LENGTH_SHORT).show();
                Log.e("@#loginError", "onResponse:" + error);
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("booking_id",bID);
                MyData.put("sp_id",spID );
                return MyData;
            }
        };
        RequestQueue RequestQueue = Volley.newRequestQueue(ServiceProviderDrawerActivity.this);
        RequestQueue.add(StringRequest);
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(4)
                .setApiKey("AIzaSyBUz5pGJnJ1mSNnZm-FikqvTGFDeIlCYW8")
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        loadingDialog.dismiss();
    }
}
