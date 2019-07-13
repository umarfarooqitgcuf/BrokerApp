package net.itempire.brokerapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class UserDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private GoogleMap mMap;
    public double mlatti, mlongi;
    public LocationManager locationManager;
    SupportMapFragment mapFragment;
    ImageView myLocationButton,navigationButton;
    private GoogleApiClient googleApiClient;
     Button btn_send_request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_drawer);
       // Toolbar toolbar = findViewById(R.id.toolbar);
      /*  setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);*/
        myLocationButton = findViewById(R.id.myLocationCustomButton);
        btn_send_request = (Button)findViewById(R.id.btnConfirmPickUp);
        btn_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDrawerActivity.this,SP_Detail_Activity.class);
                startActivity(intent);
                finish();
            }
        });
        navigationButton = (ImageView) findViewById(R.id.image);

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
                Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                if (location != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16);
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
        NavigationView navigationView = findViewById(R.id.nav_view);
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
           finish();
        } else if (id == R.id.nav_wallet) {

        }  else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_active_services) {
            Intent intent = new Intent(UserDrawerActivity.this,ActiveServicesActivity.class);
            startActivity(intent);
            finish();

        }
        else if (id == R.id.nav_pending_services) {

        }
        else if (id == R.id.nav_completed_services) {

        }
        else if (id == R.id.nav_rate_app) {

        }
        else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation(mMap);
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
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            Log.e("@@@permission", "===enableMyLocation: Enabled...");
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
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
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.e("@@@@Location", "getLocation: " + location);
            if (location != null) {
                mlatti = location.getLatitude();
                mlongi = location.getLongitude();

                Log.e("@@@LocationLAT", "getLocation:=="+mlatti+"=="+mlongi );
                // Log.e("@@@CompleteAddress", "getLocation:=="+getCompleteAddressString(mlatti,mlongi) );
            }
        }
    }
}
