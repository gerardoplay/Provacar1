package com.example.peppelt.provacar1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity implements RemoteCallListener<String>{
	private Button accedi,registrati;
	private TextView username, password;
	private ProgressDialog dialog;
	private CheckBox ricordami;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		accedi 	= 	(Button) findViewById(R.id.accedi);
		registrati = (Button) findViewById(R.id.registrati);
		password = 	(TextView) findViewById(R.id.password);
		username = 	(TextView) findViewById(R.id.username);
		ricordami = (CheckBox) findViewById(R.id.ricordami);
		//esito = 	(TextView) findViewById(R.id.esito);
		riempimentoForm();
		accedi.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				login(username.getText().toString(),password.getText().toString());
				
				
			}
			
		});
		ricordami.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				   	SharedPreferences pref = getSharedPreferences("CARPOOLING", MODE_PRIVATE);
				   	Editor ed = pref.edit();
				   	if(!isChecked){
					   	ed.putString("username","-1");
						ed.putString("password", "-1");
						ed.putInt("checked", 0);
						ed.commit();
				   }else{
					   	ed.putInt("checked", 1);
					   	ed.commit();
				   }
			   }
			});
		registrati.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent(getApplicationContext(),RegistrazioneActivity.class);
				Intent intent = new Intent(getApplicationContext(),PosizioneUtente.class);
				startActivity(intent);
			}
		});
	}

	private void riempimentoForm() {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences("CARPOOLING", MODE_PRIVATE);
		String user = pref.getString("username", "-1");
		String pass = pref.getString("password", "-1");
		int check = pref.getInt("checked", -1);
		if(check==0)
			ricordami.setChecked(false);
		if(check==1)
			ricordami.setChecked(true);
		if(!user.equalsIgnoreCase("-1") || !pass.equalsIgnoreCase("-1")){
			username.setText(user);
			password.setText(pass);
		}
	}

	@SuppressLint("NewApi")
	protected void login(String username, String password) {
		// TODO Auto-generated method stub
		//StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//StrictMode.setThreadPolicy(policy); 
		
		
		if(controlloForm()){
			dialog = ProgressDialog.show(this, getString(R.string.attendi), getString(R.string.loginincorso), false, false);
			RequestHttpAsyncTask rl = new RequestHttpAsyncTask(this);
			String url = getString(R.string.host)+"servletLogin";
			JSONObject js = new JSONObject();
			
			try {
				js.put("username", username);
				js.put("url", url);
				js.put("password", password);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			rl.execute(js); 
		}        
	}

	private boolean controlloForm() {
		// TODO Auto-generated method stub
		if(username.getText().toString().length()<1 || password.getText().toString().length()<1){
			Toast.makeText(getApplicationContext(),"Inserisci Dati", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onRemoteCallListenerComplete(String tipolog) {
		// TODO Auto-generated method stub

			
			aggiornamentoSalvataggio();
			if(dialog.isShowing())
				dialog.dismiss();
			if(tipolog.equalsIgnoreCase("ERROR")||tipolog==null)
				Toast.makeText(getApplicationContext(),getString(R.string.erroredirete),Toast.LENGTH_LONG).show();
			
			else
				try {
					JSONObject js = new JSONObject(tipolog);
					int contr = js.getInt("contr");
				switch(contr){
					case(-1): 	Toast.makeText(getApplicationContext(), R.string.loginerrattivazione, Toast.LENGTH_LONG).show(); break;
					case(0):	Toast.makeText(getApplicationContext(), "credenziali errate", Toast.LENGTH_LONG).show(); break;
					case(1): 	Intent i = new Intent(getApplicationContext(),MenuActivity.class);
					        startActivity(i);break;	
					default: 	Toast.makeText(getApplicationContext(), "Errore imprevisto", Toast.LENGTH_LONG).show(); break;
				}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	private void aggiornamentoSalvataggio() {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences("CARPOOLING", MODE_PRIVATE);

		if(pref.getInt("checked", -1)==1){
			String user = pref.getString("username", "-1");
			String pass = pref.getString("password", "-1");
			if(!user.equalsIgnoreCase(username.getText().toString()) || !pass.equalsIgnoreCase(password.getText().toString())){
				Editor ed = pref.edit();
				ed.putString("username",username.getText().toString() );
				ed.putString("password", password.getText().toString());
				ed.commit();
			}
		}
	}

}
