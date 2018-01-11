package com.example.peppelt.provacar1;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


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
	}
}
