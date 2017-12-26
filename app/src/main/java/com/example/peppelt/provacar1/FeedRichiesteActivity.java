package com.example.peppelt.provacar1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FeedRichiesteActivity extends Activity implements RemoteCallListener<String> {
	private ListView utenti;
	private JSONArray jsarrcod;
	private JSONArray jsarrus;
	private JSONArray jsarrtext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_richieste);
		utenti =(ListView) findViewById(R.id.listViewFeedRichieste);
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(this);
		try{
			String cod = getIntent().getExtras().getString("cod");
			JSONObject js = new JSONObject();
			js.put("url", getString(R.string.host)+"servletRichiedentiFeed");
			js.put("cod", cod);
			rh.execute(js);
		}catch(JSONException e){
			e.printStackTrace();
		}
		utenti.setOnItemClickListener(new OnItemClickListener() {

			private Builder f;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				f = new Builder(FeedRichiesteActivity.this);
				f.setTitle("Feedback");
				try {
					f.setMessage(jsarrus.getString(arg2)+" ha rispettato le indicazioni fornite?");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				f.setPositiveButton("Si", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						RequestHttpAsyncTask rh = new RequestHttpAsyncTask(FeedRichiesteActivity.this);
						JSONObject js = new JSONObject();
						try {
							js.put("url", getString(R.string.host)+"servletFeedRichiesta");
							js.put("feed", 1);
							js.put("cod", jsarrcod.get(arg2));
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
						RequestHttpAsyncTask rh = new RequestHttpAsyncTask(FeedRichiesteActivity.this);
						JSONObject js = new JSONObject();
						try {
							js.put("url", getString(R.string.host)+"servletFeedRichiesta");
							js.put("feed", 0);
							js.put("cod", jsarrcod.get(arg2));
							rh.execute(js);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				f.show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feed_richieste, menu);
		return true;
	}

	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		try{
			JSONObject js = new JSONObject(dati);
			int contr = js.getInt("contr");
			if(contr==1){
				jsarrcod = js.getJSONArray("cod");
				jsarrtext = js.getJSONArray("text");
				jsarrus = js.getJSONArray("utenti");
				ArrayAdapter<String> aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
				for(int i=0;i<jsarrcod.length();i++){
					aa.add(jsarrtext.getString(i));
					System.out.println("ad");

				}
				if(jsarrcod.length()==0){
					aa.add("Nessun partecipante");
					utenti.setClickable(false);
				}else
					utenti.setAdapter(aa);

			}
			
			if(contr==5)
				Toast.makeText(getApplicationContext(), "Feedback Inserito con successo", Toast.LENGTH_LONG).show();
			else
				if(contr==6)
					Toast.makeText(getApplicationContext(), "Feedback gi� inserito: � impossibile inserire pi� feedback lo stesso evento", Toast.LENGTH_LONG).show();

			
			
		}catch(JSONException e){
			e.printStackTrace();
		}
	}

}
