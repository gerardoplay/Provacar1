package com.example.peppelt.provacar1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RiepilogoPercorsoActivity extends Activity implements RemoteCallListener<String> {

	private GoogleMap map;
	private TextView titolo, distanza, tempo, costo, co2;
	private ProgressDialog dialog;
	private Button conferma;
	private boolean ar;
	private TextView dataora;
	private TextView Range;
	private TextView posti;
	private String dataoras, ranges, postis;
	private int ss;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_riepilogo_percorso);
		
		map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		caricamentoContenuti();
		LatLng latlngcentro = new LatLng(40.773720, 14.794522);
		map.moveCamera( CameraUpdateFactory.newLatLngZoom(latlngcentro , 8.0f) );
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("bundle");
		
		titolo = 	(TextView) findViewById(R.id.riepilogotitolo);
		distanza = 	(TextView) findViewById(R.id.riepilogodistanza);
		dataora = 	(TextView) findViewById(R.id.riepilogoinfotempo);
		Range = 	(TextView) findViewById(R.id.riepilogoinfocosto);
		posti = 		(TextView) findViewById(R.id.riepilogoinfoco2);
		posti.setText("Posti: "+bundle.getString("posti"));
		dataora.setText("Data e ora: "+bundle.getString("data")+"  "+bundle.getString("orario"));
		Range.setText("Range: "+bundle.getString("range"));
		
		conferma = 	(Button) findViewById(R.id.riepilogoconferma);
		conferma.setEnabled(false);
		conferma.setOnClickListener(new OnClickListener() {



			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog = ProgressDialog.show(RiepilogoPercorsoActivity.this, getString(R.string.attendi), getString(R.string.caricamento), false, false);

				Intent intent = getIntent();
				Bundle bundle = intent.getBundleExtra("bundle");
				RequestHttpAsyncTask rh = new RequestHttpAsyncTask(RiepilogoPercorsoActivity.this);
				String 	data=bundle.getString("data"),
						orario = bundle.getString("orario"),
						indirizzo = bundle.getString("indirizzo"),
						targa = bundle.getString("targa"),
						posti = bundle.getString("posti"),
						range = bundle.getString("range");

				double	lat = ((LatLng)bundle.getParcelable("latlng")).latitude,
						lon = ((LatLng)bundle.getParcelable("latlng")).longitude;

				ar = bundle.getBoolean("ar");


				try{
					JSONObject js = new JSONObject();
					js.put("url", getString(R.string.host)+"servletInserimentoPercorso");
					js.put("data",data);
					js.put("orario", orario);
					js.put("indirizzo", indirizzo);
					js.put("targa", targa);
					js.put("posti", posti);
					js.put("range", range);
					Log.e("mik", lat+"  "+lon);
					js.put("lat", lat);
					js.put("lon", lon);
					js.put("ar", ar);
					js.put("distanza", ss);
					rh.execute(js);
				}catch(JSONException e){

				}

			}
		});

	}

	private void caricamentoContenuti() {
		// TODO Auto-generated method stub
		dialog = ProgressDialog.show(this, getString(R.string.attendi), getString(R.string.caricamento), false, false);
		Intent in = getIntent();
		Bundle bn = in.getParcelableExtra("bundle");
		LatLng llor = bn.getParcelable("latlng"); 
		DownloadTask dt = new DownloadTask();
		LatLng llde = new LatLng(40.773720, 14.794522);
		dt.execute(getUrlGMaps(llor, llde));

		//getUrlGMaps();
	}

	private String getUrlGMaps(LatLng origin,LatLng dest) {
		// TODO Auto-generated method stub
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		String str_dest = "destination="+dest.latitude+","+dest.longitude;      
		String sensor = "sensor=false";         
		String parameters = str_origin+"&"+str_dest+"&"+sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

		map.addMarker(new MarkerOptions().position(origin));

		map.addMarker(new MarkerOptions().position(dest));
		return url;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.riepilogo_percorso, menu);
		return true;
	}

	private String downloadUrl(String strUrl) throws IOException{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try{
			URL url = new URL(strUrl);

			urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.connect();

			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb  = new StringBuffer();

			String line = "";
			while( ( line = br.readLine())  != null){
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		}catch(Exception e){
			//Log.d("Exception while downloading url", e.toString());
		}finally{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	private class DownloadTask extends AsyncTask<String, Void, String>{         
		@Override
		protected String doInBackground(String... url) {

			String data = "";
			try{
				data = downloadUrl(url[0]);
			}catch(Exception e){
				//Log.d("Background Task",e.toString());
			}
			return data;        
		}

		@Override
		protected void onPostExecute(String result) {           
			super.onPostExecute(result);            
			// Gson gs = new Gson();
			// gs.toJson(result);
			ParserTask parserTask = new ParserTask();

			parserTask.execute(result);

		}       
	}
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{


		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

			JSONObject jObject; 
			List<List<HashMap<String, String>>> routes = null;                     

			try{
				jObject = new JSONObject(jsonData[0]);
				//DirectionsJSONParser parser = new DirectionsJSONParser();
				routes = parseDirection(jObject);    
			}catch(Exception e){
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			// MarkerOptions markerOptions = new MarkerOptions();

			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				List<HashMap<String, String>> path = result.get(i);


				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);                 

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng); 

					points.add(position);                       
				}

				//Toast.makeText(getApplicationContext(), points.size()+"", Toast.LENGTH_LONG).show();

				lineOptions.addAll(points);
				lineOptions.width(4);
				lineOptions.color(Color.BLUE);   

			}


			map.addPolyline(lineOptions);  
			if(dialog.isShowing())
				dialog.dismiss();
			conferma.setEnabled(true);
		}           
	}   

	private List<List<HashMap<String,String>>> parseDirection(JSONObject jObject){

		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;    

		try {           

			jRoutes = jObject.getJSONArray("routes");
			ss=0;
			/** Traversing all routes */
			for(int i=0;i<jRoutes.length();i++){            
				jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
				List path = new ArrayList<HashMap<String, String>>();

				/** Traversing all legs */
				for(int j=0;j<jLegs.length();j++){
					jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
					ss += jLegs.getJSONObject(0).getJSONObject("distance").getLong("value")/1000;
					/** Traversing all steps */
					for(int k=0;k<jSteps.length();k++){
						String polyline = "";
						polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
						List<LatLng> list = decodePoly(polyline);

						/** Traversing all points */
						for(int l=0;l<list.size();l++){
							HashMap<String, String> hm = new HashMap<String, String>();
							hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
							hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
							path.add(hm);                       
						}                               
					}
					routes.add(path);
				}
			}

			distanza.setText(" "+ss);
			
		} catch (JSONException e) {         
			e.printStackTrace();
		}catch (Exception e){           
		}


		return routes;
	}   


	/**
	 * Method to decode polyline points 
	 * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java 
	 * */
	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		if(!dati.equalsIgnoreCase("ERRORE")){
			try{
				JSONObject js = new JSONObject(dati);

				if(js.getInt("contr")==1){
					if(dialog.isShowing()){
						dialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), "Percorso inserito", Toast.LENGTH_LONG).show();
					Intent i = new Intent(getApplicationContext(),MenuActivity.class);
					startActivity(i);
					finish();
				}
			}catch(JSONException e){

			}
		}
	}

}
