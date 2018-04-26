package com.example.peppelt.provacar1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Struct;
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
import com.google.gson.Gson;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EventoActivity extends Activity implements RemoteCallListener<String>{
	private int mode;// 1-ric comp   2-per comp 3ric ina 4 per ina 
	private GoogleMap map;
	private ProgressDialog dialog;

	private Button feed;
	private Button annulla;
	private Button annullaper;
	private Button posAutista;
	private Button navigatore;
	private TextView dataora;
	private TextView distanza,indirizzotext;
	private TextView infotit;
	private TextView infocod, infodata;
	private View infodistanza;
	private View infoindirizzo;
	private String type;
	private String cod;
	private String data;
	private String ora;
	private String indirizzo;/////
	private String autista;
	private String percodice;

	private Dialog feeddialog;
	private int ffeed;
	private AlertDialog alert;
	private Builder f;

	private JSONArray jslat;
	private JSONArray jslon;
	private String sDistance;
	private Button partecipanti;
	private Builder p;
	private JSONArray indirizziRichiedenti;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		Intent i = getIntent();
		Bundle bb=i.getBundleExtra("bundle");

		type = bb.getString("type");
		cod = bb.getString("cod");
		indirizzo=bb.getString("indirizzo");
		data=bb.getString("data");
		ora=bb.getString("ora");
		autista=bb.getString("autista");
		percodice=bb.getString("percodice");
		int stato = bb.getInt("stato");
		//dialog = ProgressDialog.show(this, getString(R.string.attendi), getString(R.string.caricamento), false, false);

		if(type.equalsIgnoreCase("ric")){  //richiedente percorso
			if(stato==1)  // percorso completato
				mode=1;
			else           //non completato
				mode=3;
		}else{         //colui che inserisce il percorso
			if(stato==1)
				mode=2;
			else 
				mode=4;
		}
		//



		//

		switch (mode) {
		case 1:


			setTitle("Richiesta Completata");
			setContentView(R.layout.riccomlayout);
			inizializzaComponent();
			infotit.setText("Richiesta: ");
			infocod.setText(cod);
			feed =(Button)findViewById(R.id.feedback);
			f = new Builder(EventoActivity.this);
			f.setTitle("Feedback");
			f.setMessage("Il percorso è stato rispettato?");
			f.setPositiveButton("Si", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					RequestHttpAsyncTask rh = new RequestHttpAsyncTask(EventoActivity.this);
					JSONObject js = new JSONObject();
					try {
						js.put("url", getString(R.string.host)+"servletFeedPercorso");
						js.put("feed", 1);
						js.put("cod", cod);
						rh.execute(js);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}


			});
			f.setNegativeButton("No", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					RequestHttpAsyncTask rh = new RequestHttpAsyncTask(EventoActivity.this);
					JSONObject js = new JSONObject();
					try {
						js.put("url", getString(R.string.host)+"servletFeedPercorso");
						js.put("feed", 0);
						js.put("cod", cod);
						rh.execute(js);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			//alert = f.create();

			feed.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					f.show();	
				}
			});
			break;

		case 2:

			setContentView(R.layout.percomlayout);
			inizializzaComponent();
			infotit.setText("Percorso: ");
			infocod.setText(cod);
			setTitle("Percorso Completato");
			feed= (Button) findViewById(R.id.feedback);
			feed.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i = new Intent(getApplicationContext(), FeedRichiesteActivity.class);
					Bundle b = new Bundle();
					b.putString("cod", cod);
					i.putExtras(b);
					startActivity(i);
				}
			});
			break;
		case 3:

			setTitle("Richiesta Programmata");
			Log.i("DEBUG", "Richiesta Programmata    aaaaa");
			setContentView(R.layout.ricattlayout);
			inizializzaComponent();
			infotit.setText("Richiesta: ");
			infocod.setText(cod);
			annulla = (Button) findViewById(R.id.annullaric);
			posAutista = (Button) findViewById(R.id.posAutista);

			annulla.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					RequestHttpAsyncTask rh= new RequestHttpAsyncTask(EventoActivity.this);
					JSONObject js = new JSONObject();
					try{
						js.put("cod", cod);
						js.put("url", getString(R.string.host)+"servletAnnullaRichiesta");
						rh.execute(js);
					}catch(JSONException E){

					}
				}
			});


			posAutista.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {

					Intent i = new Intent(EventoActivity.this ,PosizioneAutista.class);
					Bundle b = new Bundle();
					b.putString("cod", cod);
					b.putString("type", type);
					b.putString("indirizzo",indirizzo);
					b.putString("data",data);
					b.putString("ora",ora);
					b.putString("autista",autista);
					b.putString("percodice",percodice);
					i.putExtra("bundle", b);

					//Toast.makeText(getApplicationContext(), "cod :"+percodice , Toast.LENGTH_LONG).show();
					startActivity(i);
				}
			});




			break;
		case 4:

			setTitle("Percorso Programmato");

			Log.i("DEBUG", "Percorso Programmata "+ type);
			setContentView(R.layout.perattlayout);
			inizializzaComponent();
			infotit.setText("Percorso: ");
			infocod.setText(cod);
			annullaper = (Button) findViewById(R.id.annullaper);
			partecipanti = (Button) findViewById(R.id.partecipanti);
			p = new Builder(EventoActivity.this);
			p.setTitle("Percorso");

			p.setPositiveButton("ok", new Dialog.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			} );
			partecipanti.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					RequestHttpAsyncTask rh = new RequestHttpAsyncTask(EventoActivity.this);
					try{
						JSONObject js = new JSONObject();
						js.put("cod", cod);
						js.put("url", getString(R.string.host)+"servletIndirizziPercorso");
						rh.execute(js);
					}catch(JSONException e){
						e.printStackTrace();
					}

				}
			});
			annullaper.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					RequestHttpAsyncTask rh= new RequestHttpAsyncTask(EventoActivity.this);
					JSONObject js = new JSONObject();
					try{
						js.put("cod", cod);
						js.put("url", getString(R.string.host)+"servletAnnullaPercorso");
						rh.execute(js);
					}catch(JSONException E){

					}

				}
			});
			navigatore = (Button) findViewById(R.id.nav);
			navigatore.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});
			break;

		default:

			break;
		}

		dataora = (TextView) findViewById(R.id.dataora);
		distanza = (TextView) findViewById(R.id.distanza);
		indirizzotext = (TextView) findViewById(R.id.indirizzo);
		indirizzotext.setText(bb.getString("indirizzo"));
		infocod = (TextView) findViewById(R.id.riepilogotitolo);
		dataora.setText(bb.getString("data")+"  "+bb.getString("ora"));
		map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		LatLng latlngcentro = new LatLng(40.773720, 14.794522);
		map.moveCamera( CameraUpdateFactory.newLatLngZoom(latlngcentro , 8.0f) );
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(this);
		JSONObject js = new JSONObject();
		try{
			js.put("cod", cod);
			js.put("type", type);
			js.put("url", getString(R.string.host)+"servletPercorso");
			rh.execute(js);
		}catch(JSONException e){

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.evento, menu);
		return true;
	}

	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		try{
			JSONObject js = new JSONObject(dati);
			if(js.getInt("contr")==10){
				jslat = js.getJSONArray("lat");
				jslon = js.getJSONArray("lon");
				caricamentoContenuti("");
				//caricamentoContenuti();
			}else{
				switch (mode) {
				case 1:{
					int contr = js.getInt("contr");
					if(contr==5)
						Toast.makeText(getApplicationContext(), "Feedback Inserito con successo", Toast.LENGTH_LONG).show();
					else
						if(contr==6)
							Toast.makeText(getApplicationContext(), "Feedback gi� inserito: � impossibile inserire pi� feedback lo stesso evento", Toast.LENGTH_LONG).show();

					break;
				}
				case 2:{

					break;
				}
				case 3:{

					int contr = js.getInt("contr");
					if(contr==4)
						Toast.makeText(getApplicationContext(), "Richiesta eliminata", Toast.LENGTH_LONG).show();


					break;
				}
				case 4:{

					int contr = js.getInt("contr");
					if(contr==3)
						Toast.makeText(getApplicationContext(), "Percorso eliminato", Toast.LENGTH_LONG).show();
					else
						if(contr==12){
							indirizziRichiedenti = js.getJSONArray("indirizzi");
							if(indirizziRichiedenti.length()>0){
								String msg="";
								for(int i=0;i<indirizziRichiedenti.length();i++){
									try {
										msg += indirizziRichiedenti.getString(i)+"\n";
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								p.setMessage(msg);
							}else
								p.setMessage("Nessuna Richiesta Pervenuta");
							p.show();
						}

					break;
				}
				default:
					break;
				}
				///////////////////
			}
		}catch(JSONException e ){
			e.printStackTrace();
		}
	}
	private void inizializzaComponent(){
		infotit = (TextView) findViewById(R.id.riepilogoinfotitolo);
		infocod = (TextView) findViewById(R.id.riepilogotitolo);
		//infodata = (TextView) findViewById(R.id.dataora);
		//infodistanza =(TextView) findViewById(R.id.distanza);
		//infoindirizzo =(TextView) findViewById(R.id.indirizzo);

		//infoindirizzo.set
	}

	private void caricamentoContenuti(String cod) {
		// TODO Auto-generated method stub
		//dialog = ProgressDialog.show(this, getString(R.string.attendi), getString(R.string.caricamento), false, false);
		//Intent in = getIntent();
		//Bundle bn = in.getParcelableExtra("bundle");
		//LatLng llor = bn.getParcelable("latlng"); 


		Gson gs = new Gson();
		DownloadTask dt = new DownloadTask();
		Geocoder gc = new Geocoder(getApplicationContext());




		LatLng llde = new LatLng(40.773720, 14.794522);

		dt.execute(getUrlGMaps());







		//RequestHttpAsyncTask rh = new RequestHttpAsyncTask(VisualizzaDettagliPercorso.this);
		//rh.execute(new BasicNameValuePair("url",getString(R.string.host)+"servletVisualizzaInformazioniPercorso"),new BasicNameValuePair("cod", cod));



		//getUrlGMaps();
	}



	/*private String getUrlGMaps(LatLng origin,LatLng dest) throws JSONException {
		// TODO Auto-generated method stub
		Geocoder gc = new Geocoder(getApplicationContext());
		String url="";
		try {
			List<Address> lista = gc.getFromLocationName(jsindirizzi.getString(0), 1);
			if(lista.size()==0)
				Toast.makeText(getApplicationContext(), "Indirizzo non trovato", Toast.LENGTH_LONG).show();
			else{
				LatLng llor =  new LatLng(lista.get(0).getLatitude(),lista.get(0).getLongitude());

				//Toast.makeText(getApplicationContext(), dati, Toast.LENGTH_LONG).show();

				LatLng llde = new LatLng(40.773720, 14.794522);
			//	dt.execute(getUrlGMaps(llor, llde));



				String str_origin = "origin="+llor.latitude+","+llor.longitude;
				String str_dest = "destination="+llde.latitude+","+llde.longitude;      
				String sensor = "sensor=false";         
				String parameters = str_origin+"&"+str_dest+"&"+sensor;
				String output = "json";
				url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

			}






		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_LONG).show();
		}
		return url;




	}
	 */
	private String getUrlGMaps() {
		// TODO Auto-generated method stub
		String url ="";
		try{
			String str_origin = "origin="+jslat.getDouble(0)+","+jslon.getDouble(0);
			String str_dest = "destination="+jslat.getDouble(jslat.length()-1)+","+jslon.getDouble(jslat.length()-1);      
			String waipoints="waypoints=optimize:true|";
			for(int i=1;i<jslat.length()-1;i++){
				if(i!=jslat.length()-1)
					waipoints+=jslat.getDouble(i)+","+jslon.getDouble(i)+"|" ;
				else
					waipoints+=jslat.getDouble(i)+","+jslon.getDouble(i) ;
			}
			for(int i =0;i<jslat.length();i++){
				map.addMarker(new MarkerOptions().position(new LatLng(jslat.getDouble(i), jslon.getDouble(i))));
			}
			String sensor = "sensor=false";         
			String parameters = str_origin+"&"+str_dest+"&"+waipoints+"&"+sensor;
			String output = "json";
			url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
			Log.v("mik", url);
		}catch(JSONException e){

		}
		return url;

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
			MarkerOptions markerOptions = new MarkerOptions();

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
				lineOptions.width(5);
				lineOptions.color(Color.BLUE);   

			}

			distanza.setText(sDistance);
			map.addPolyline(lineOptions);  

			// conferma.setEnabled(true);
		}           
	}   

	private List<List<HashMap<String,String>>> parseDirection(JSONObject jObject){

		List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;    

		try {           

			jRoutes = jObject.getJSONArray("routes");

			/** Traversing all routes */
			for(int i=0;i<jRoutes.length();i++){            
				jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
				List path = new ArrayList<HashMap<String, String>>();

				/** Traversing all legs */
				for(int j=0;j<jLegs.length();j++){
					jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

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
			JSONObject distance = jLegs.getJSONObject(0).getJSONObject("distance");
			//Log.d("JSON","distance: "+distance.toString());

			sDistance = distance.getString("text");



		} catch (JSONException e) {         
			e.printStackTrace();
		}catch (Exception e){  
			e.printStackTrace();
		}



		return routes;
	}   


	private void setdistance(String sDistance) {
		// TODO Auto-generated method stub
		distanza.setText(sDistance);
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
}
