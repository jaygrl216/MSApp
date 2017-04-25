package com.example.msapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.content.ContextCompat;




import java.util.Formatter;
import java.util.Locale;

public class MainActivity extends Activity implements IBaseGpsListener {

    private Button startWalk;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startWalk = (Button)findViewById(R.id.start_button);

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

    public void startWalk(View view){
        if (ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {


            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION  },
                    1);
        }
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateSpeed(null);
    }
}
