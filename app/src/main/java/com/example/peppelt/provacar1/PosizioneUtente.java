package com.example.peppelt.provacar1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ros on 17/04/2018.
 */

public class PosizioneUtente extends Activity implements LocationListener {

    private GoogleMap map;
    private TextView latitudine;
    private TextView longitudine;
    private LocationManager locationManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualizza_posizione_utente);
        Intent intent = getIntent();


        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng latlngcentro = new LatLng(40.773720, 14.794522);
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(latlngcentro , 8.0f) );

        latitudine = 	(TextView) findViewById(R.id.lat);
        longitudine = 	(TextView) findViewById(R.id.longitudine);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            float lat = (int) (location.getLatitude());
            float lng = (int) (location.getLongitude());
            latitudine.setText(String.valueOf(lat));
            longitudine.setText(String.valueOf(lng));
        } else {
            latitudine.setText("Provider non disponibile");
            longitudine.setText("Provider non disponibile");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latitudine.setText(String.valueOf(lat));
        longitudine.setText(String.valueOf(lng));
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
}
