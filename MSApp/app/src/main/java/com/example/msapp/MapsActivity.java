package com.example.msapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Formatter;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, IBaseGpsListener {

    private GoogleMap map;
    private Button startWalk;
    private LocationManager locationManager;
    private boolean testStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startWalk = (Button)findViewById(R.id.button);
        testStart = false;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }



    private void updateSpeed(CLocation location){
        float nCurrentSpeed = 0;
        if(location != null){
            location.setUseMetricunits(false);
            nCurrentSpeed = location.getSpeed() * 5280/60;
        }
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');
        String strUnits = "feet per minute";
        TextView txtCurrentSpeed = (TextView)this.findViewById(R.id.txtCurrentSpeed);
        txtCurrentSpeed.setText("Current speed: "+ strCurrentSpeed + " " + strUnits);
    }

    public void startWalk(View view){
        //permission denied
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    1);
        } else {
            testStart = true;


            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            this.updateSpeed(null);


            //setting location
            map.setMyLocationEnabled(true);
            //code modified from http://stackoverflow.com/questions/34608517/map-setmylocationenabledtrue-not-working
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    //noinspection MissingPermission
                    Location temp = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng currentLatLng = new LatLng(temp.getLatitude(), temp.getLongitude());
                    map.addMarker(new MarkerOptions().position(currentLatLng).title("Start Location"));
                    map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
                    return false;
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == 1) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Access Location permission granted", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
                    testStart = true;
                    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    this.updateSpeed(null);
                }
            } else {
                Toast.makeText(this, "Access Location permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onLocationChanged(Location location){
        if(location != null){
            CLocation myLocation = new CLocation(location, false);
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}
