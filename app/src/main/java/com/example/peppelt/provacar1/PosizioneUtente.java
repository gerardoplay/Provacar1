package com.example.peppelt.provacar1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.ContentValues.TAG;

/**
 * Created by Ros on 17/04/2018.
 */

public class PosizioneUtente extends Activity implements LocationListener {
    private Geocoder gc;
    private GoogleMap map;
    private TextView latitudine;
    private TextView longitudine;
    private LocationManager locationManager;
    private LatLng latlng;
    private String cod;
    private String autista;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Bundle bb=i.getBundleExtra("bundle");
        cod = bb.getString("cod");
        autista=bb.getString("autista");

        SharedPreferences sharedPref = this.getSharedPreferences("posAutista", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("autista",autista);
        editor.putString("cod",cod);
        editor.commit();


        setContentView(R.layout.visualizza_posizione_utente);
        Intent intent = getIntent();

        Toast.makeText(getApplicationContext(), "cod:"+cod+ "autista:"+autista, Toast.LENGTH_LONG).show();
        gc = new Geocoder(getApplicationContext());
        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng latlngcentro = new LatLng(40.773720, 14.794522);
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(latlngcentro , 8.0f) );

        latitudine = 	(TextView) findViewById(R.id.lat);
        longitudine = 	(TextView) findViewById(R.id.longitudine);






   // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(PosizioneUtente.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(PosizioneUtente.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {


               //Toast.makeText(getApplicationContext(), "permessi già ok prima", Toast.LENGTH_LONG).show();


                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);



                if (location != null) {

                    float lat = (float) (location.getLatitude());
                    float lng = (float) (location.getLongitude());
                    latitudine.setText(String.valueOf(lat));
                    longitudine.setText(String.valueOf(lng));
                    latlng = new LatLng(lat,lng);
                    aggiornaMappa();

                    Log.i("DEBUG", "chiamo il service");
                     start_service();


                } else {
                    latitudine.setText("Provider non disponibile");
                    longitudine.setText("Provider non disponibile");
                   // Toast.makeText(getApplicationContext(), "gps non disp", Toast.LENGTH_LONG).show();
                   start_service();
                }



                // Permission has already been granted
            }
    }

    protected void aggiornaMappa() {
        // TODO Auto-generated method stub
        map.clear();
        MarkerOptions mrk = new MarkerOptions().position(latlng);
        map.addMarker(mrk);
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(latlng , 15.0f) );
        Toast.makeText(getApplicationContext(), "aggiorno la mappa", Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "posizione inviata al passeggero", Toast.LENGTH_LONG).show();
    }
    protected void start_service() {
        Intent intent = new Intent(this,LocationService.class);
        startService(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "permessi accettati", Toast.LENGTH_LONG).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "permessi non disp", Toast.LENGTH_LONG).show();
                        return;
                    }
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);



                    if (location != null) {

                        float lat = (float) (location.getLatitude());
                        float lng = (float) (location.getLongitude());
                        latitudine.setText(String.valueOf(lat));
                        longitudine.setText(String.valueOf(lng));
                        latlng = new LatLng(lat,lng);
                        aggiornaMappa();

                        Log.i("DEBUG", "chiamo il service");
                       start_service();
                    } else {
                        latitudine.setText("Provider non disponibile");
                        longitudine.setText("Provider non disponibile");
                        Toast.makeText(getApplicationContext(), "gps non disp", Toast.LENGTH_LONG).show();
                        Log.i("DEBUG", "chiamo il service");
                        start_service();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "permessi negati", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
        }
        catch(Exception e){
            e.printStackTrace();}
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, " posutente onPause");
        try {
            locationManager.removeUpdates(this);
        }
        catch(Exception e){
            e.printStackTrace();}
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i(TAG, " posutente onStop");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG, " posutente onDestroy");
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
