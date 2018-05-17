package com.example.peppelt.provacar1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MenuActivity extends Activity implements RemoteCallListener<String> {
	private Button auto, richiesta;
	private Button evprogramma;
	private Button evcompleti;
	private TextView anidride, costo;

	private JSONArray indlat;
	private JSONArray indlon;
	private JSONArray ar;
	private JSONArray codice;
	private JSONArray data;
	private JSONArray partenza;
	private int i=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		auto = (Button) findViewById(R.id.menuinspercorso);
		richiesta = (Button) findViewById(R.id.menuinsrichiesta);
		evcompleti = (Button) findViewById(R.id.eventicompletati);
		evprogramma = (Button) findViewById(R.id.eventiprogrammati);
		anidride = (TextView) findViewById(R.id.anidridenn);
		costo = (TextView) findViewById(R.id.costo);
		OnClickListener lis = new listener();
		auto.setOnClickListener(lis);
		richiesta.setOnClickListener(lis);
		evcompleti.setOnClickListener(lis);
		evprogramma.setOnClickListener(lis);

		Intent i = getIntent();
		String username=i.getStringExtra("user");
		Toast.makeText(getApplicationContext(), "benvenuto "+username, Toast.LENGTH_LONG).show();

//ora bisogna contrallare le richieste del utente e se i percorsi associati contengono un percorso annullato bisogna cmunicarlo all'utente
        try {
            RequestHttpAsyncTask rh = new RequestHttpAsyncTask(this);
            JSONObject js = new JSONObject();

            js.put("url", getString(R.string.host)+"/serveltCheckPercorsiAnnullati");
            js.put("user",username);
            rh.execute(js);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



		try {
			RequestHttpAsyncTask rh = new RequestHttpAsyncTask(this);
			JSONObject js = new JSONObject();

			js.put("url", getString(R.string.host)+"servletInfoHome");
			rh.execute(js);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	class listener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();		
			if(id== auto.getId()){
				Intent i = new Intent(getApplicationContext(),InserimentoAutoActivity.class);
				startActivity(i);
			}else
				if(id== richiesta.getId()){
					Intent i = new Intent(getApplicationContext(),InserimentoRichiestaActivity.class);
					startActivityForResult(i,110);
				}else
					if(id== evcompleti.getId()){
						Intent i = new Intent(getApplicationContext(),EventiList.class);
						Bundle b = new Bundle();
						b.putInt("stato", 1);
						i.putExtra("bundle", b);
						startActivity(i);
					}else
						if(id== evprogramma.getId()){
							Intent i = new Intent(getApplicationContext(),EventiList.class);
							Bundle b = new Bundle();
							b.putInt("stato", 0);
							i.putExtra("bundle", b);
							startActivity(i);
						}
		}

	}
	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		try {
			JSONObject js = new JSONObject(dati);
			costo.setText(js.getDouble("costo")+" €");
			anidride.setText(js.getDouble("anidride")+" Kg");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		//prova non so come si fanno 2 call back nella stessa clasee

		try {
			JSONObject js = new JSONObject(dati);

				codice = js.getJSONArray("cod");
				data = js.getJSONArray("data");
				partenza = js.getJSONArray("partenza");
				ar = js.getJSONArray("ar");
				indlat = js.getJSONArray("indlat");
				indlon = js.getJSONArray("indlon");

			AlertDialog.Builder builder;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
			} else {
				builder = new AlertDialog.Builder(this);
			}
			//se ci sono più percorsi annullati
			if(codice.length()>1){

					builder.setTitle("Percorsi annullati")
							.setMessage("attenzione "+codice.length()+" percorsi sono stati annullati, controlla i tuoi percorsi programmati")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setNegativeButton(android.R.string.no, null)
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									Intent i = new Intent(getApplicationContext(),EventiList.class);
									Bundle b = new Bundle();
									b.putInt("stato", 0);
									i.putExtra("bundle", b);
									startActivity(i);
								}
							});

				builder.create().show();
			}
		//se ci sta solo una richiesta annullata
		 else {

					builder.setTitle("Percorso annullato")
							.setMessage("il percorso " + codice.getString(0) + " del " + data.getString(0) + " da " + partenza.getString(0) + " non è più disponibile vuoi visualizzare i trasporti alternativi?")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setNegativeButton(android.R.string.no, null)
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									Intent in = new Intent(MenuActivity.this, TrasportoAlternativoActivity.class);
									Bundle b = new Bundle();
									try {
										b.putString("ar", ar.getString(0));
										b.putString("indlat", indlat.getString(0));
										b.putString("indlon", indlon.getString(0));
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									in.putExtra("bundle", b);

									// Toast.makeText(getApplicationContext(), "codice :"+cod +"tipo"+type , Toast.LENGTH_LONG).show();
									startActivity(in);

									// continue with delete
								}
							});



				builder.create().show();
				//Toast.makeText(getApplicationContext(),"il viaggio del "+ js.getString("data")+" è stato annullato", Toast.LENGTH_LONG).show();

				//}

			}} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
