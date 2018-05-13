package com.example.peppelt.provacar1;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MenuActivity extends Activity implements RemoteCallListener<String> {
	private Button auto, richiesta;
	private Button evprogramma;
	private Button evcompleti;
	private TextView anidride, costo;
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






		if(js.getString("data")!=null) {
			AlertDialog.Builder builder;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
			} else {
				builder = new AlertDialog.Builder(this);
			}
			builder.setTitle("Percorso annullato")
					.setMessage("il percorso "+js.getString("cod")+"del "+js.getString("data")+"da "+js.getString("partenza")+" non è più disponibile vuoi visualizzare i trasporti alternativi?")
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// continue with delete
						}
					})
					.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// do nothing
						}
					})
					.setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		}



			Toast.makeText(getApplicationContext(),"il viaggio del "+ js.getString("data")+" è stato annullato", Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
