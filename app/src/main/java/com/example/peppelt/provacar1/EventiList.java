package com.example.peppelt.provacar1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class EventiList extends Activity implements RemoteCallListener<String> {

	private ListView listview;
	//private String indirizzo;
	private ArrayList<String> aper, aric;
	private int stato;
	private JSONArray jsdperindirizzo;
	private JSONArray jsricindirizzo;
	private JSONArray jspercodici;
	private JSONArray jsperdata;
	private JSONArray jsperorario;
	private JSONArray jsriccodici;
	private JSONArray jsricdata;
	private JSONArray jsricorario;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eventi_lis);
		listview =(ListView) findViewById(R.id.listViewRichiesteIn);
		Bundle b = getIntent().getBundleExtra("bundle");
		stato = b.getInt("stato");
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(this);
		if(stato==1){
			try{
				setTitle("Eventi Completati");
				JSONObject js = new JSONObject();
				js.put("url",getString(R.string.host)+"servletEveComList");
				rh.execute(js);
			}catch(JSONException e){
				e.printStackTrace();
			}
		}else{
			try{
				setTitle("Eventi Programmati");
				JSONObject js = new JSONObject();
				js.put("url",getString(R.string.host)+"servletEveProList");
				rh.execute(js);
			}catch(JSONException e){
				e.printStackTrace();
			}	
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.richieste_in, menu);
		return true;
	}

	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub

		ArrayAdapter<String> aa= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		aric = new ArrayList<String>();
		aper = new ArrayList<String>();
		try{
			

			JSONObject js = new JSONObject(dati);

			if(js.getInt("contr")!=-1){
				
				jsdperindirizzo = js.getJSONArray("perindirizzo");
				jspercodici = js.getJSONArray("percodici");
				jsperdata = js.getJSONArray("perdata");
				jsperorario = js.getJSONArray("perorario");
				
				jsricindirizzo = js.getJSONArray("ricindirizzo");
				jsriccodici = js.getJSONArray("riccodici");
				jsricdata = js.getJSONArray("ricdata");
				jsricorario = js.getJSONArray("ricorario");
				for(int i =0; i<jspercodici.length();i++){
					aa.add("Percorso: "+jspercodici.getString(i)+"\n Del: "+jsperdata.getString(i)+" alle:  "+jsperorario.getString(i));
					aper.add(jspercodici.getString(i));
				}
				for(int i =0; i<jsriccodici.length();i++){
					aa.add("Richiesta: "+jsriccodici.getString(i)+"\n Del "+jsricdata.getString(i)+"   alle  "+jsricorario.getString(i));
					aric.add(jsriccodici.getString(i));
				}
				if(jsriccodici.length()==0 && jspercodici.length()==0){
					aa.add("Nessun evento da visualizzare");
					listview.setClickable(false);
				}
			}else{
				aa.add("Nessun evento da visualizzare");
				
				listview.setClickable(false);
				
			}
			listview.setAdapter(aa);
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					// TODO Auto-generated method stub
					//	Toast.makeText(getApplicationContext(), a1.get(arg2)+"ciaoooo", Toast.LENGTH_LONG).show();
					Intent i = new Intent(EventiList.this,EventoActivity.class);
					Bundle b = new Bundle();
					String ind ,type;
					if(arg2<aper.size()){
						try {
							b.putString("indirizzo", jsdperindirizzo.getString(arg2));
							b.putString("data", jsperdata.getString(arg2));
							b.putString("ora", jsperorario.getString(arg2));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ind = aper.get(arg2);
						type = "per";
					}else{
						try {
							
							b.putString("indirizzo", jsricindirizzo.getString(arg2-aper.size()));
							b.putString("data", jsricdata.getString(arg2-aper.size()));
							b.putString("ora", jsricorario.getString(arg2-aper.size()));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						ind = aric.get(arg2-aper.size());
						type = "ric";
					}
					b.putString("cod", ind);
					b.putString("type", type);
					
					b.putInt("stato", stato);
					
						//b.put(data);
					
					i.putExtra("bundle", b);
					startActivityForResult(i, 12);
				}


			});

			//String it = listview.getItemAtPosition(0).toString();
			//String cod = it.substring(it.indexOf("[")+1, it.indexOf("]"));
			//Toast.makeText(getApplicationContext(), cod, Toast.LENGTH_LONG).show();
		}catch(JSONException e){	
			e.printStackTrace();
		}
	}
}
