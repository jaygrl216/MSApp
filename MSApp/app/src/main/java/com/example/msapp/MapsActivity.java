package com.example.msapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Formatter;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, IBaseGpsListener {

    private GoogleMap map;
    private Button startWalk;
    private LocationManager locationManager;
    private boolean testStart;

    protected long startTime = 0;
    protected long stopTime = 0;
    protected double time = 0.00;
    MarkerOptions startMarker = null, endMarker = null;
    Double startAltitude = 0.0, endAltitude;
    Double distance, speed;
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
    }

    //distance function was created by stackoverflow user David George
    //link to the said post: http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
    public static double getDistance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }


    public void startWalk(View view){
        //checking to see if test started


        if(!testStart) {
            //permission denied
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            } else {
                testStart = true;


                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                this.updateSpeed(null);


                //setting location
                map.setMyLocationEnabled(true);
                Location temp = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng currentLatLng = new LatLng(temp.getLatitude(), temp.getLongitude());
                startMarker = new MarkerOptions().position(currentLatLng).title("Start Location");
                map.addMarker(startMarker);
                startAltitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getAltitude();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0f));
                startTime = System.currentTimeMillis();

                Button tempCast = (Button) view;
                tempCast.setText("End Test");
            }
        }
        else{
            //setting end location

            Location temp = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng currentLatLng = new LatLng(temp.getLatitude(), temp.getLongitude());
            endMarker = new MarkerOptions().position(currentLatLng).title("Final Location");
            map.addMarker(endMarker);
            endAltitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getAltitude();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0f));
            Button tempCast = (Button) view;
            tempCast.setEnabled(false);
            stopTime = System.currentTimeMillis();
            time = (stopTime - startTime);
            time = time / 1000;
            //distance in meters
            System.out.println(endMarker.getPosition().latitude);
            distance = getDistance(startMarker.getPosition().latitude, endMarker.getPosition().latitude,
                    startMarker.getPosition().longitude, endMarker.getPosition().longitude,
                   startAltitude, endAltitude);
            map.addPolyline(new PolylineOptions()
                    .add(startMarker.getPosition(), endMarker.getPosition())
                    .width(5)
                    .color(Color.RED));
            TextView txtCurrentSpeed = (TextView)this.findViewById(R.id.txtCurrentSpeed);
            txtCurrentSpeed.setText("Current speed: " + (distance/time) + " M/S");
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
