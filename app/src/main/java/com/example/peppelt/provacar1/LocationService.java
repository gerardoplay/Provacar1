package com.example.peppelt.provacar1;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationService extends Service implements RemoteCallListener<String>{
    public LocationService() {
    }

    private LocationManager locationManager;
    private LocationListener listner;

    private static final String TAG = "LocationService";

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        /*
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Double lat1= location.getLatitude();
        Double lng1=location.getLongitude();

        SharedPreferences pref = getSharedPreferences("CARPOOLING", MODE_PRIVATE);
        String user = pref.getString("username", "-1");


        RequestHttpAsyncTask rh = new RequestHttpAsyncTask(LocationService.this);
        try{
            JSONObject js = new JSONObject();
            js.put("url", getString(R.string.host)+"servletPrendiPosizioneAutista");
            js.put("lat", lat1.toString());
            js.put("lng", lng1.toString());
            js.put("codAutista", user);
            js.put("codPercorso", "222");
            rh.execute(js);
            Log.i(TAG, "Service fatta la servlet in location");
        }catch(JSONException e){
            e.printStackTrace();
        }


*/










        listner=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Double lat= location.getLatitude();
                Double lng=location.getLongitude();
//                                                        MOMENTANEO DA VEDERE COME PRENDERE IL NOME UTENTE DA MAINACTIVITY
                SharedPreferences pref = getSharedPreferences("CARPOOLING", MODE_PRIVATE);
                String user = pref.getString("username", "-1");

                Log.i(TAG, "Location changed aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                RequestHttpAsyncTask rh = new RequestHttpAsyncTask(LocationService.this);
                try{
                    JSONObject js = new JSONObject();
                    js.put("url", getString(R.string.host)+"servletPrendiPosizioneAutista");
                    js.put("lat", lat.toString());
                    js.put("lng", lng.toString());
                    js.put("codAutista", "b");
                    js.put("codPercorso", "20");
                    rh.execute(js);

                }catch(JSONException e){
                    e.printStackTrace();
                }



            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i =new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
        };

        locationManager=(LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listner);


    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(locationManager!=null)
            locationManager.removeUpdates(listner);
        Log.i(TAG, "Service onDestroy");
    }

    @Override
    public void onRemoteCallListenerComplete(String dati) {

    }
}
