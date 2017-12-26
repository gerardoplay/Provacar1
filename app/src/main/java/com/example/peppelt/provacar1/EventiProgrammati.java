package com.example.peppelt.provacar1;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EventiProgrammati extends Activity implements RemoteCallListener<String> {
private ListView listview;
private ArrayList<String> a1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eventi_programmati);
		listview = (ListView) findViewById(R.id.listViewEventiProgrammati);
		try{
			JSONObject js = new JSONObject();
			js.put("url", getString(R.string.host)+"servletEventiprogrammati");
			RequestHttpAsyncTask rh = new  RequestHttpAsyncTask(this);
			rh.execute(js);
		}catch(JSONException e){
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.eventi_programmati, menu);
		return true;
	}

	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		ArrayAdapter<String> aa= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
		a1 = new ArrayList<String>();
		try{
		JSONObject js = new JSONObject(dati);
		JSONArray jsdati = js.getJSONArray("dati");
		JSONArray jscodici = js.getJSONArray("codici");
		if(js.getInt("contr")!=-1){
			for(int i =0; i<jsdati.length();i++){
				aa.add(jsdati.getString(i));
				a1.add(jscodici.getString(i));
			}
		}else{
			a1 = new ArrayList<String>();
			a1.add("Nessuna Richiesta Pervenuta");
			listview.setClickable(false);
		}
		listview.setAdapter(aa);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), a1.get(arg2)+"ciaoooo", Toast.LENGTH_LONG).show();
				Intent i = new Intent(EventiProgrammati.this,VisualizzaDettagliPercorso.class);
				Bundle b = new Bundle();
				b.putInt("codrichiesta", arg2);
				i.putExtra("bundle", b);
				startActivityForResult(i, 12);
			}
			
	
		});

		}catch(JSONException e){	
			
		}
	}
	

}
