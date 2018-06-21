package com.example.peppelt.provacar1;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LocationService extends Service implements RemoteCallListener<String>{
    static final String ACTION_START ="com.example.peppelt.provacar1.LocationService.ACTION_START";

    public LocationService() {
    }

    private LocationManager locationManager;
    private LocationListener listner;

    private static final String TAG = "LocationService";



    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.i(TAG, "location service su oreo registra broadcast");
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.exemple.provacar1.onDestroy");
            try {
                registerReceiver(receiver, filter);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }


        SharedPreferences sharedPref= getApplicationContext().getSharedPreferences("posAutista", Context.MODE_PRIVATE);

        final String autista =  sharedPref.getString("autista",null);
        final String cod = sharedPref.getString("cod",null);
        Log.i(TAG, " service : autista "+autista+" cod "+cod );

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {

            Double lat= location.getLatitude();
            Double lng=location.getLongitude();
            LatLng latlng = new LatLng(lat,lng);

            RequestHttpAsyncTask rh = new RequestHttpAsyncTask(LocationService.this);
            try{
                JSONObject js = new JSONObject();
                js.put("url", getString(R.string.host)+"servletPrendiPosizioneAutista");
                js.put("lat", lat.toString());
                js.put("lng", lng.toString());
                js.put("codAutista", autista);
                js.put("codPercorso", cod);
                rh.execute(js);
                Log.i(TAG, "Service fatta la servlet in location");
            }catch(JSONException e){
                e.printStackTrace();
            }

        } else {
            Log.i("DEBUG", "pos non disp");

        }


        listner=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Double lat= location.getLatitude();
                Double lng=location.getLongitude();


                Log.i(TAG, "Location changed");
                RequestHttpAsyncTask rh = new RequestHttpAsyncTask(LocationService.this);
                try{
                    JSONObject js = new JSONObject();
                    js.put("url", getString(R.string.host)+"servletPrendiPosizioneAutista");
                    js.put("lat", lat.toString());
                    js.put("lng", lng.toString());
                    js.put("codAutista", autista);
                    js.put("codPercorso", cod);
                    rh.execute(js);

                }catch(JSONException e){
                    e.printStackTrace();
                }

                IntentFilter filter = new IntentFilter();
                filter.addAction("com.exemple.provacar1.onDestroy");
                registerReceiver(receiver, filter);

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
       // Toast.makeText(getApplicationContext(), "ci siamo: ", Toast.LENGTH_LONG).show();
        locationManager=(LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listner);


    }

    @Override
    public IBinder onBind(Intent intentt) {
        Log.i(TAG, "Service onBind");


        return null;
    }



    @Override
    public void onDestroy() {
        Log.i(TAG, "Service onDestroyy");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.i(TAG, "location service su oreo");
            unregisterReceiver(receiver);
            //super.onDestroy();
        } else{
            Log.i(TAG, "location service su api"+ android.os.Build.VERSION.SDK_INT);
            Destroy();
        }
    }
    public void Destroy() {
        Log.i(TAG, "Service Destroy");
        if(locationManager!=null)
            locationManager.removeUpdates(listner);
        Log.i(TAG, "Service onDestroy");
        super.onDestroy();
    }

    @Override
    public void onRemoteCallListenerComplete(String dati) {

    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(action.equals("com.exemple.provacar1.onDestroy")){
                //action for sms received
                unregisterReceiver(receiver);
                Toast.makeText(getApplicationContext(), "è arrivato il broadcast message ", Toast.LENGTH_LONG).show();
                Destroy();
            }

        }
    };



}
