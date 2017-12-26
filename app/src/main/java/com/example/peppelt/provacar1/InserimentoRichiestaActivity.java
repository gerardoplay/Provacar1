package com.example.peppelt.provacar1;

import java.text.NumberFormat;
import java.util.Calendar;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class InserimentoRichiestaActivity extends Activity implements RemoteCallListener<String>,OnOkPressedDialogPicker {
	private Intent in;
	private TextView indirizzo,informazioni, infodata, infoora, infoindirizzo,datatext,oratext;
	private Button conferma;
	private LatLng latlng=null;
	private ProgressDialog dialog;
	private Calendar cal;
	private int anno,mese,giorno,ore,minuti;
	private int orario[];
	private ToggleButton ar;
	private boolean arboole=true;
	private TextView iinfo, iora, iindirizzo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inserimento_richiesta);
		orario = new int[2];

		indirizzo = 	(TextView) findViewById(R.id.inserimentorichiestaindirizzo);
		conferma = 		(Button)   findViewById(R.id.richiestaconferma);
		ar = (ToggleButton) findViewById(R.id.toggleButton1);
		oratext = 		(TextView) findViewById(R.id.inserimentorichiestaoratext);
		datatext = 		(TextView) findViewById(R.id.inserimentorichiestadatatext);
		iinfo = 		(TextView) findViewById(R.id.richiestainformazioni);
		iora = 			(TextView) findViewById(R.id.richiestainfoorario);
		iindirizzo =	(TextView) findViewById(R.id.richiestainfoindirizzo);
		cal = Calendar.getInstance();
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
				if(indirizzo.getText().toString().length()>1 && latlng !=null && oratext.getText().length()>1 && datatext.getText().length()>1){
					showdialog();
					RequestHttpAsyncTask rha = new RequestHttpAsyncTask(InserimentoRichiestaActivity.this);
					try{
						JSONObject js = new JSONObject();
						js.put("url", getString(R.string.host)+"servletInserimentoRichiesta"  );
						js.put( "tipo","andata" );
						js.put( "orario",oratext.getText().toString() );
						js.put( "indirizzo",indirizzo.getText().toString() );
						js.put("lat", latlng.latitude+""  );
						js.put( "lng", latlng.longitude+"" );
						js.put("data",datatext.getText().toString()  );
						js.put("ar", arboole);


						//rha.execute(js);
					}catch(JSONException e){

					}
					Intent in = new Intent(InserimentoRichiestaActivity.this, VisualizzaPercorsiActivity.class);
					Bundle b = new Bundle();
					b.putString("indirizzo", indirizzo.getText().toString());
					b.putBoolean("ar", arboole);
					b.putParcelable("latlng", latlng);
					b.putString("data",  datatext.getText().toString());
					b.putBoolean("ar", arboole);
					b.putString("ora", oratext.getText().toString());
					in.putExtra("bundle", b);
					startActivity(in);
				}
			}	
		});

	}

	protected void showdialog() {
		// TODO Auto-generated method stub
		//dialog = ProgressDialog.show(this, getString(R.string.attendi), "Operazione in corso", true, false);

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
	}



	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		try{
			JSONObject js =new JSONObject(dati);
			int contr = js.getInt("contr");
			//			if(dialog.isShowing())
			//			dialog.dismiss();
			if(contr==1){
				//ok
				Toast.makeText(getApplicationContext(), "Richiesta Inserita", Toast.LENGTH_LONG).show();
			}else{
				//ko
				Toast.makeText(getApplicationContext(), "Errore nel inserimento", Toast.LENGTH_LONG).show();
			}
		}catch(JSONException e){
			Toast.makeText(getApplicationContext(), "Richiesta Inserita", Toast.LENGTH_LONG).show();
		}
		/*AlertDialog.Builder bui = new AlertDialog.Builder(this);
		bui.setMessage("Richiesta inserita");
		bui.setCancelable(false);
		bui.setPositiveButton("Chiudi",new android.content.DialogInterface.OnClickListener(){
	        public void onClick(DialogInterface dialog, int id){
	                dialog.dismiss();
	        		finish();

	                }
	        });
		AlertDialog ald= bui.create();
		ald.show();*/
		//dialog = ProgressDialog.show(getApplicationContext(), "Attendi", "Operazione in corso", false, false);


	}

	@Override
	public void onDialogDateOkPressed(int anno,int mese,int giorno){
		this.anno=anno;
		this.mese=mese;
		this.giorno=giorno;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(2);
		datatext.setText(nf.format(giorno)+"/"+nf.format(mese+1)+"/"+anno);
	}


	@Override
	public void onDialogTimeOkPressed(int ore, int minuti) {
		orario[0] = ore;
		orario[1] = minuti;
		this.ore=ore;
		this.minuti=minuti;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(2);
		oratext.setText(nf.format(ore)+":"+nf.format(minuti));
	}


}
