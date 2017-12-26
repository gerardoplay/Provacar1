package com.example.peppelt.provacar1;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class VisualizzaPercorsiActivity extends Activity implements RemoteCallListener<String> {

	private ListView listview;
	private String indirizzo;
	private ArrayList<String> codrif;
	private boolean ar;
	private LatLng latlng;
	private String data;
	private String ora;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visualizza_percorsi);
		listview =(ListView) findViewById(R.id.visualizzapercorsilist);
		Intent in = getIntent();
		Bundle b = in.getBundleExtra("bundle");
		indirizzo = b.getString("indirizzo");
		ar = b.getBoolean("ar");
		data = b.getString("data");
		ora = b.getString("ora");
		latlng = b.getParcelable("latlng");
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(this);
		try{
			JSONObject js = new JSONObject();
			js.put("url",getString(R.string.host)+"servletPercorsiDisponibili");
			js.put("lat", latlng.latitude);
			js.put("lon", latlng.longitude);
			js.put("data", data);
			js.put("ora", ora);
			js.put("ar", ar);
			rh.execute(js);
		}catch(JSONException e){

		}




		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				// TODO Auto-generated method stub
				//String it = arg0.getItemAtPosition(arg2).toString();
				String codper = codrif.get(arg2);
				Toast.makeText(getApplicationContext(), codper, Toast.LENGTH_LONG).show();
				Intent i = new Intent(VisualizzaPercorsiActivity.this, VisualizzaDettagliPercorso.class);
				Bundle b = new Bundle();
				b.putString("codper", codper);
				
				b.putString("indirizzo", indirizzo);
				b.putBoolean("ar", ar);
				b.putParcelable("latlng", latlng);
				b.putString("ora", ora);
				b.putString("data", data);
				i.putExtra("bundle",b);
				startActivity(i);
				finish();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.visualizza_percorsi, menu);
		return true;
	}


	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		try{
			JSONObject js = new JSONObject(dati);


			codrif = new ArrayList<String>();
			ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
			if(js.getInt("contr")==0){
				JSONArray jsdati = js.getJSONArray("dati");
				JSONArray jscod = js.getJSONArray("codici");
				for(int i=0;i<jsdati.length();i++){
					aa.add(jsdati.getString(i));
					codrif.add(jscod.getString(i));
				}

			}else{

				aa.add("Non risultato percorsi disponibili");
				listview.setClickable(false);
			}
			//aa= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,a);
			listview.setAdapter(aa);


			//	String it = listview.getItemAtPosition(0).toString();
			//String cod = it.substring(it.indexOf("[")+1, it.indexOf("]"));
			//Toast.makeText(getApplicationContext(), cod, Toast.LENGTH_LONG).show();

		}catch(JSONException e){

		}
	}

}
