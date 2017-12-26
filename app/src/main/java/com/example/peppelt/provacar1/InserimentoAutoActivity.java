package com.example.peppelt.provacar1;

import java.text.NumberFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.model.LatLng;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class InserimentoAutoActivity extends Activity implements RemoteCallListener<String>,OnOkPressedDialogPicker {
	private Intent in;
	private TextView indirizzo,datatext,oratext,range, posti,iinfo,idata,iora,iindirizzo;
	private Button conferma,addveicolo;
	private LatLng latlng=null;
	private Spinner sceltaveicolo;
	private ProgressDialog dialog;
	private ToggleButton ar;
	private boolean arboole=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inserimentoauto);
		indirizzo = 	(TextView) findViewById(R.id.inserimentorichiestaindirizzo);
		conferma = 		(Button) findViewById(R.id.richiestaconferma);
		oratext = 		(TextView) findViewById(R.id.inserimentorichiestaoratext);
		datatext = 		(TextView) findViewById(R.id.inserimentorichiestadatatext);
		addveicolo =	(Button)   findViewById(R.id.inserimentoveicoliinsveicolo);
		sceltaveicolo = (Spinner) findViewById(R.id.spinnersceltaveicolo);
		ar = (ToggleButton) findViewById(R.id.inserimentoautoar);

		iinfo = (TextView) findViewById(R.id.richiestainformazioni);
		idata = (TextView) findViewById(R.id.richiestainfodata);
		iora = (TextView) findViewById(R.id.richiestainfoorario);
		iindirizzo = (TextView) findViewById(R.id.richiestainfoindirizzo);





		ArrayAdapter<String> a =new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, android.R.id.text1);
		a.add("");
		sceltaveicolo.setAdapter(a);
		inizializzaauto();
		range = (TextView) findViewById(R.id.rangekm);
		posti = (TextView) findViewById(R.id.posti);
		//cal = Calendar.getInstance();
		datatext.setKeyListener(null);
		oratext.setKeyListener(null);
		indirizzo.setKeyListener(null);

		ar.setOnCheckedChangeListener(new OnCheckedChangeListener() {



			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				arboole=arg1;
				if(arg1){
					iinfo.setText("indica la data, l'orario e il luogo di partenza");
					//idata.setText("data partenza");
					iora.setText("orario partenza");
					iindirizzo.setText("indirizzo di partenza");

				}else{
					iinfo.setText("indica la data, l'orario e il luogo di destinazione");
					//idata.setText("data destinazione");
					iora.setText("orario destinazione");
					iindirizzo.setText("indirizzo di destinazione");
				}
			}
		});

		addveicolo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*if(latlng!=null){
					Intent i = new Intent(getApplicationContext(),InserimentoDettagliVeicoloActivity.class);
					Bundle bd = new Bundle();
					bd.putParcelable("latlng", latlng);
					bd.putInt("posti", Integer.parseInt((String) posti.getText()));
					bd.putInt("range", Integer.parseInt((String) range.getText()));
					//dtfghbnjmk,l
					i.putExtra("bundle", bd);
					startActivityForResult(i, 101);
				}*/
				Intent i = new Intent(getApplicationContext(),InserimentoDettagliVeicoloActivity.class);
				startActivityForResult(i, 101);
			}
		});

		datatext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerFragment newFragment = new DatePickerFragment();
				newFragment.show(getFragmentManager(), "datePicker");
			}
		});

		oratext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TimePickerFragment newFragment = new TimePickerFragment();
				newFragment.show(getFragmentManager(), "timePicker");
			}
		});

		indirizzo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				in = new Intent(getApplicationContext(),PosizioneGps.class);
				startActivityForResult(in, 1);
			}			
		} );

		conferma.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(latlng!=null && datatext.getText().length()!=0
						&& oratext.getText().length()!=0
						&& posti.getText().length()!=0
						&& range.getText().length()!=0
						&& !sceltaveicolo.getSelectedItem().toString().equalsIgnoreCase("seleziona")
						){

					Intent i = new Intent(getApplicationContext(),RiepilogoPercorsoActivity.class);
					Bundle bd = new Bundle();
					bd.putParcelable("latlng", latlng);
					bd.putString("data", datatext.getText().toString());
					bd.putString("orario", oratext.getText().toString());
					bd.putString("targa", sceltaveicolo.getSelectedItem().toString());
					bd.putString("posti", posti.getText().toString());
					bd.putString("range", range.getText().toString());
					bd.putString("indirizzo", indirizzo.getText().toString());
					//bd.putParcelable("latlng", latlng);
					bd.putBoolean("ar", arboole);
					i.putExtra("bundle", bd);
					startActivity(i);
				}else
					Toast.makeText(getApplicationContext(), "Compila tutti i campi prima di procedere", Toast.LENGTH_LONG).show();

			}	
		});


	}

	private void inizializzaauto() {
		// TODO Auto-generated method stub
		RequestHttpAsyncTask rh = new RequestHttpAsyncTask(InserimentoAutoActivity.this);
		JSONObject js = new JSONObject();
		try {
			js.put("url", getString(R.string.host)+"servletSpinnerAuto");
			rh.execute(js);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void showdialog() {
		// TODO Auto-generated method stub
		dialog = ProgressDialog.show(this, getString(R.string.attendi), "Operazione in corso", true, false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inserimento_richiesta, menu);
		return true;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==1 && resultCode== RESULT_OK && data!=null){
			String strindirizzo = data.getStringExtra("indirizzo");
			Bundle bun = data.getParcelableExtra("bundle");
			latlng = bun.getParcelable("latlng");
			indirizzo.setText(strindirizzo);
		}
		if(requestCode==101){
			inizializzaauto();
		}
	}



	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		if(!dati.equalsIgnoreCase("ERRORE")&&!dati.equalsIgnoreCase("OK")){
			try{			
				JSONObject js = new JSONObject(dati);
				JSONArray jsarr = js.getJSONArray("auto");
				jsarr.length();
				//arst.add(0,"Seleziona");
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
				//ArrayAdapter<String> adaptervuoto = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
				//arst.add(0,"Seleziona");
				adapter.add("Seleziona");
				for(int i=1;i<jsarr.length();i++){
					adapter.add(jsarr.getString(i));
				}

				sceltaveicolo.setAdapter(adapter);

			}catch(JSONException e1){
				Toast.makeText(getApplicationContext(), "Errore di rete", Toast.LENGTH_LONG).show();

			}


		}else{

			Toast.makeText(getApplicationContext(), "Errore di rete", Toast.LENGTH_LONG).show();

		}


		//dialog = ProgressDialog.show(getApplicationContext(), "Attendi", "Operazione in corso", false, false);


	}

	@Override
	public void onDialogDateOkPressed(int anno,int mese,int giorno){
		//this.anno=anno;
		//this.mese=mese;
		//this.giorno=giorno;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(2);
		datatext.setText(nf.format(giorno)+"/"+nf.format(mese+1)+"/"+anno);
	}


	@Override
	public void onDialogTimeOkPressed(int ore, int minuti) {
		//this.ore=ore;
		//this.minuti=minuti;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(2);
		oratext.setText(nf.format(ore)+":"+nf.format(minuti));
	}


}
