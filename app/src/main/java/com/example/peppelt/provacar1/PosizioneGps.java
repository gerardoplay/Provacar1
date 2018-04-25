package com.example.peppelt.provacar1;

import java.io.IOException;
import java.util.List;
import com.example.peppelt.provacar1.R;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class PosizioneGps extends Activity implements android.location.LocationListener {

private GoogleMap map;
private TextView indirizzo;
private Button ok, gps, submit;
private Geocoder gc;
private String strindirizzo;
private LatLng latlng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posizione_gps);
		gc = new Geocoder(getApplicationContext());
		map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		indirizzo = (TextView) findViewById(R.id.posizionegpsindirizzo);
		gps = (Button) findViewById(R.id.posizionegpsgps);
		submit = (Button) findViewById(R.id.posizionegpssubmit);
		LatLng latlngcentro = new LatLng(40.773720, 14.794522);
		map.moveCamera( CameraUpdateFactory.newLatLngZoom(latlngcentro , 8.0f) );
		ok = (Button) findViewById(R.id.posizionegpsok);
		
		map.setOnMapLongClickListener(new OnMapLongClickListener() {
			
			

			@Override
			public void onMapLongClick(LatLng point) {
				// TODO Auto-generated method stub
				
				try {
					List<Address> l = gc.getFromLocation(point.latitude, point.longitude, 1);
					if(l.size()>0){
						latlng=point;
						Address indricavato =   l.get(0);
						indirizzo.setText(indricavato.getAddressLine(0).replace("'", " ")+", "+indricavato.getAddressLine(1).replace("'", " "));
						
						aggiornaMappa();
						

					}
					else
						Toast.makeText(getApplicationContext(), "Posizione non recuperata", Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		ok.setOnClickListener(new OnClickListener() {
			
			

			

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				strindirizzo = indirizzo.getText().toString();
				List<Address> lista;
				try {
					lista = gc.getFromLocationName(strindirizzo, 1);
					if(lista.size()==0)
						Toast.makeText(getApplicationContext(), "Indirizzo non trovato", Toast.LENGTH_LONG).show();
					else{
						latlng = new LatLng(lista.get(0).getLatitude(),lista.get(0).getLongitude());
						aggiornaMappa();

					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_LONG).show();
				}
			}
		});
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				Intent in = new Intent();
				in.putExtra("indirizzo", indirizzo.getText().toString());
				Bundle bun = new Bundle();
				bun.putParcelable("latlng",latlng );
				in.putExtra("bundle", bun);
				setResult(RESULT_OK, in);
				finish();
			}
		});
		
		
		
		gps.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
						&& checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					return;
				}
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,(long)1000,(float)3,PosizioneGps.this);
			}
		});
		
	}
	protected void aggiornaMappa() {
		// TODO Auto-generated method stub
		map.clear();
		MarkerOptions mrk = new MarkerOptions().position(latlng);
		map.addMarker(mrk);
		map.moveCamera( CameraUpdateFactory.newLatLngZoom(latlng , 15.0f) );
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		//Toast.makeText(getApplicationContext(), "qualco", Toast.LENGTH_LONG).show();
		latlng = new LatLng(location.getLatitude(), location.getLongitude());
		List<Address> l;
		try {
			l = gc.getFromLocation(latlng.latitude, latlng.longitude, 1);

		if(l.size()>0){
			Address indricavato =   l.get(0);
			indirizzo.setText(indricavato.getAddressLine(0)+", "+indricavato.getAddressLine(1));
			aggiornaMappa();
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "Errore gps", Toast.LENGTH_LONG).show();
		}
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	

}
