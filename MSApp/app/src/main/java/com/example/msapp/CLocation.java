package com.example.msapp;

import android.location.Location;

/**
 * Created by Ben McHugh on 4/22/2017.
 */

public class CLocation extends Location {

    private boolean bUseMetricUnits = false;
    public CLocation(Location location){
        this(location, true);
    }

    public CLocation(Location location, boolean bUseMetricUnits){
        super(location);
        this.bUseMetricUnits = bUseMetricUnits;
    }

    public boolean getUseMetricUnits(){
        return this.bUseMetricUnits;
    }

    public void setUseMetricunits(boolean bUseMetricUnits){
        this.bUseMetricUnits = bUseMetricUnits;
    }

    public float distanceTo(Location dest){
        float nDistance = super.distanceTo(dest);
        //Converts from meters to feet
        nDistance = nDistance * 3.28083989501312f;
        return nDistance;
    }

    public float getAccuracy(){
        float nAccuracy = super.getAccuracy();
        nAccuracy = nAccuracy * 3.28083989501312f;
        return nAccuracy;
    }

    public double getAltitude(){
        double nAltitude = super.getAltitude();
        nAltitude = nAltitude * 3.28083989501312f;
        return nAltitude;
    }

    public float getSpeed(){
        float nSpeed = super.getSpeed() * 2.2369362920544f;
        return nSpeed;
    }

}
