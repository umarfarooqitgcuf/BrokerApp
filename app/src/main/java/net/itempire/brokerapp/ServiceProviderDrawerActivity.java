package net.itempire.brokerapp;

import android.Manifest;
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
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServiceProviderDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;

    public double mlatti, mlongi;
    public LocationManager locationManager;
    SupportMapFragment mapFragment;
    public Double longitude;
    public Double lattitude;
    ImageView myLocationButton;

    Switch switchONOFF;
     Button btnAccept,btnReject;

    SharedPreferences sharedPreferencesFB_sp,sharedPreferences;
    SharedPreferences.Editor editor_user_data;
    private DatabaseReference mDatabase;
    ProgressDialog loadingDialog;

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

        switchONOFF=(Switch)findViewById(R.id.simpleSwitch);
        btnAccept=(Button)findViewById(R.id.btnaccept);
        btnReject=(Button)findViewById(R.id.btnreject);
        myLocationButton = findViewById(R.id.myLocationCustomButton);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferencesFB_sp = getSharedPreferences("FBDetailsSP",MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("userData",MODE_PRIVATE);
        editor_user_data=sharedPreferences.edit();

        loadingDialog = new ProgressDialog(ServiceProviderDrawerActivity.this);
        loadingDialog.setTitle("Fetching your data");
        loadingDialog.setMessage("Loading....");

        if (sharedPreferencesFB_sp.getString("id","").compareTo("")==0)
        {
            SharedPreferences.Editor editor = sharedPreferencesFB_sp.edit();
            editor.putString("id",Long.toString(System.currentTimeMillis()));
            editor.commit();
            editor.apply();
        }
        btn_online_ofline_check();
        request_checking();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

            }
        });
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
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
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    public void request_checking() {
        mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("SP Data").child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    String dataFB = (String) dataSnapshot.getValue().toString();
                    Log.e("@@@Data", "onDataChange:==="+dataFB);
                    if (dataFB.equals("null")) {
                        btnAccept.setVisibility(View.GONE);
                        btnReject.setVisibility(View.GONE);

                        NotificationManager notificationManager = (NotificationManager)
                                getSystemService(Context.
                                        NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();


                    } else if (dataFB.equals("RR")) {
                        btnAccept.setVisibility(View.VISIBLE);
                        btnReject.setVisibility(View.VISIBLE);

                        String Notification_text="You have recieved request for service";

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

                        /*NotificationManager notificationManager = (NotificationManager)
                                getSystemService(Context.
                                        NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                        mDatabase.child("User").child(sharedPreferencesFB_sp.getString("id", "")).child("USER Data").child("Notification").setValue("0");

                        Intent intent=new Intent(ServiceProviderDrawerActivity.this,SP_Detail_Activity.class);
                        startActivity(intent);*/

                        //move intent here for sp details fragment
                    } else if (dataFB.equals("SR")) {

                    } else if (dataFB.equals("SPR")) {

                    }else if (dataFB.equals("SPC")) {
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
}
