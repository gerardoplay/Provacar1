package com.example.peppelt.provacar1;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class InserimentoDettagliVeicoloActivity extends Activity implements RemoteCallListener<String>  {
private Spinner spmarca, spmodello, spversione, spcolore;
protected int marca;
private Button conferma;
private TextView targa;
private ArrayList<String> rifversione,rifmodello,rifmarca;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inserimento_dettagli_veicolo);
		spmarca = (Spinner) findViewById(R.id.spmarca);
		spmodello = (Spinner) findViewById(R.id.spmodello);
		spversione = (Spinner) findViewById(R.id.spversione);
		spcolore = (Spinner) findViewById(R.id.spcolore);
		conferma = (Button) findViewById(R.id.inserimentodettagliconferma);
		targa = (TextView) findViewById(R.id.inserimentodettaglitarga);
		rifversione = new ArrayList<String>();
		rifmodello = new ArrayList<String>();
		rifmarca = new ArrayList<String>();
		riempiSpinnerMarca();
		spmarca.setSelected(false);
		conferma.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(controlloform()){
					String  ver = rifversione.get(spversione.getSelectedItemPosition()-1);
					String  col = spcolore.getSelectedItem().toString();
					//Toast.makeText(getApplicationContext(), "evvau", Toast.LENGTH_LONG).show();
					RequestHttpAsyncTask rh = new RequestHttpAsyncTask(InserimentoDettagliVeicoloActivity.this);
					
					JSONObject js = new JSONObject();
					try {
						js.put("url", getString(R.string.host)+"servletInserimentoAutoUtente");
						js.put("versione", ver);
						js.put("colore", col);
						js.put("targa", targa.getText().toString());
						rh.execute(js);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		});
		spmarca.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(arg2>0){
				//Toast.makeText(getApplicationContext(),(CharSequence) arg0.getItemAtPosition(arg2), Toast.LENGTH_LONG).show();
					marca=arg2;
					riempiSpinnerModello(arg2-1);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		spmodello.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(arg2>0){
				//Toast.makeText(getApplicationContext(),(CharSequence) arg0.getItemAtPosition(arg2), Toast.LENGTH_LONG).show();
					riempiSpinnerVersione(arg2-1);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	protected boolean controlloform() {
		// TODO Auto-generated method stub
		if(spmarca.getSelectedItem().toString().equalsIgnoreCase("seleziona")
				|| spmodello.getSelectedItem().toString().equalsIgnoreCase("seleziona")
				|| spversione.getSelectedItem().toString().equalsIgnoreCase("seleziona")
				|| spcolore.getSelectedItem().toString().equalsIgnoreCase("seleziona")
				|| spmodello.getSelectedItem().toString().equalsIgnoreCase("")
				|| spversione.getSelectedItem().toString().equalsIgnoreCase("")
				|| spcolore.getSelectedItem().toString().equalsIgnoreCase("")
				|| targa.getText().toString().equalsIgnoreCase("")){
			Toast.makeText(getApplicationContext(), "Dati mancanti, assicurati di inserire tutti i dati", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inserimento_dettagli_veicolo, menu);
		return true;
	}

	
	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
		//ArrayAdapter<String> adaptervuoto = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);

		if(!dati.equalsIgnoreCase("ERRORE")){
			try {
				
				JSONObject js = new JSONObject(dati);

				adapter.add("Seleziona");
				int contr = js.getInt("contr");
				if(contr<4){
					JSONArray jsdati = js.getJSONArray("dati");
					JSONArray jscodici = js.getJSONArray("codici");
					for(int i=0; i<jsdati.length();i++){
						adapter.add(jsdati.getString(i));
					}
					switch(contr){
						case 0: 	
							spmarca.setAdapter(adapter);
							for(int i=0; i<jscodici.length();i++){
								rifmarca.add(jscodici.getString(i));
							}
							break;
						case 1:	
							spmodello.setAdapter(adapter);
							for(int i=0; i<jscodici.length();i++){
								rifmodello.add(jscodici.getString(i));
							}
							break;
						case 2:
							spversione.setAdapter(adapter);
							for(int i=0; i<jscodici.length();i++){
								rifversione.add(jscodici.getString(i));
							}
							break;
							
						default:break;
					}
				}else{
					Toast.makeText(getApplicationContext(), "Auto aggiunta", Toast.LENGTH_LONG).show();
					finish();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Errore di rete", Toast.LENGTH_LONG).show();
			}
			
		}
	}

	protected void riempiSpinnerModello(int marca) {
		// TODO Auto-generated method stub
		if(rifmodello!=null)
		rifmodello.clear();
		if(rifmarca.size()>0){
		String  in = rifmarca.get(marca);
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(InserimentoDettagliVeicoloActivity.this);
	
		JSONObject js = new JSONObject();
		try {
			js.put("url", getString(R.string.host)+"servletSpinnerModello");
			js.put("marca", in);
			rh.execute(js);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
	}
	
	protected void riempiSpinnerVersione(int modello) {
		// TODO Auto-generated method stub
		if(rifversione!=null)
		rifversione.clear();
		if(rifmodello.size()>0){
		String in = rifmodello.get(modello);
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(InserimentoDettagliVeicoloActivity.this);	
		
		JSONObject js = new JSONObject();
		try {
			js.put("url", getString(R.string.host)+"servletSpinnerversione");
			js.put("modello", in);
			rh.execute(js);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	
	}

	private void riempiSpinnerMarca() {
		// TODO Auto-generated method stub

		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(InserimentoDettagliVeicoloActivity.this);
		//NameValuePair 	npaurl = 		new BasicNameValuePair("url", getString(R.string.host)+"servletSpinnermarca");
		JSONObject js = new JSONObject();
		try {
			js.put("url", getString(R.string.host)+"servletSpinnermarca");
			rh.execute(js);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
