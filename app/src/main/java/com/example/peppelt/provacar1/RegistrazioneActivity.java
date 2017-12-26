package com.example.peppelt.provacar1;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.provacar.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class RegistrazioneActivity extends Activity implements RemoteCallListener<String> {
	private TextView email,password,passwordconferma;//nome,cognome,
	private Button conferma;
	final Context context = this;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrazione);
		//nome = (TextView) findViewById(R.id.reg_nome);
		//cognome = (TextView) findViewById(R.id.reg_cognome);
		email = (TextView) findViewById(R.id.reg_email);
		password = (TextView) findViewById(R.id.reg_password);
		passwordconferma = (TextView) findViewById(R.id.reg_confermapassword);
		conferma = (Button) findViewById(R.id.reg_conferma);
		
		conferma.setOnClickListener(new View.OnClickListener() {
		
			

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(controlloform()){
					dialog = ProgressDialog.show(RegistrazioneActivity.this, "Attendi", "Operazione in corso", false, false);

					RequestHttpAsyncTask rha = new RequestHttpAsyncTask(RegistrazioneActivity.this);
					//
					try{
						JSONObject js = new  JSONObject();
						js.put( "url", getString(R.string.host)+"servletRegistrazione");
						js.put( "email",email.getText().toString());
						js.put("password",password.getText().toString() );
						//npanome = new BasicNameValuePair("nome",nome.getText().toString()),
						//npacognome = new BasicNameValuePair("cognome",cognome.getText().toString()),
						
						rha.execute(js);
					}catch(JSONException e){
						
					}

				}
				
			}
		});
	}

	
	protected boolean controlloform() {
		// TODO Auto-generated method stub
		boolean corr = true, corrpass=true;
	/*
		if(nome.length()==0){
			nome.setText("");
			nome.setHintTextColor(Color.RED);
			nome.setHint(R.string.reginscampo);
			corr = false;
		}
		if(cognome.length()==0){
			cognome.setText("");
			cognome.setHintTextColor(Color.RED);
			cognome.setHint(R.string.reginscampo);
			corr = false;
		}*/
		if(email.length()<5){
			email.setText("");
			email.setHintTextColor(Color.RED);
			email.setHint(R.string.reginscampoemail);
			corr = false;
		}else{
			if(!controlloemail(email.getText().toString())){
				corr=false;
				email.setText("");
				email.setHintTextColor(Color.RED);
				email.setHint(R.string.reginscampoemail);
			}
		}
		if(password.length()<5){
			password.setText("");
			password.setHintTextColor(Color.RED);
			password.setHint(R.string.reginscampo);
			corrpass = false;
		}
		if(passwordconferma.length()<5){
			passwordconferma.setText("");
			passwordconferma.setHintTextColor(Color.RED);
			passwordconferma.setHint(R.string.reginscampo);
			corrpass = false;
		}
		if(corrpass)
			if(! password.getText().toString().equalsIgnoreCase(passwordconferma.getText().toString())){
				corrpass = false;
				passwordconferma.setText("");
				passwordconferma.setHintTextColor(Color.RED);
				passwordconferma.setHint(getString(R.string.regerrpassword));
				password.setText("");
				password.setHintTextColor(Color.RED);
				password.setHint(getString(R.string.regerrpassword));
				
			}
		
		return (corr && corrpass);
	}
	private boolean controlloemail(String email)
	   {
	       
	        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	        Matcher m = p.matcher(email);
	 
	        boolean matchFound = m.matches();
	 
	        StringTokenizer st = new StringTokenizer(email, ".");
	        String lastToken = null;
	        while (st.hasMoreTokens()) {
	            lastToken = st.nextToken();
	        }
	 
	        if (matchFound && lastToken.length() >= 2
	                && email.length() - 1 != lastToken.length() && email.endsWith("unisa.it")) {
	 
	            return true;
	        } else {
	            return false;
	        }
	 
	    }
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registrazione, menu);
		return true;
	}


	@Override
	public void onRemoteCallListenerComplete(String dati) {
		// TODO Auto-generated method stub
		dialog.dismiss();
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		ab.setTitle(getString(R.string.avviso));
		if(dati.equalsIgnoreCase("ERROR"))
			ab.setMessage(getString(R.string.erroredirete));

		else
		{
			try{
			int cod = new JSONObject(dati).getInt("contr");
			switch(cod){
			case(-2):   ab.setMessage(getString(R.string.regerrutenteiscritto));		break;
			case(-1): 	ab.setMessage(getString(R.string.regerrrichiestainoltrata)); 	break;
			case(0)	:	ab.setMessage(getString(R.string.regerrgenerico)); 				break;
			case(1)	:	ab.setMessage(getString(R.string.regrichiestaok)); 				break;
			default: ab.setMessage("Boh roba strana"); break;

			}
			}catch(JSONException e){
				ab.setMessage(getString(R.string.erroredirete));

			}
		}		
		ab.setCancelable(false);
		ab.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		AlertDialog alert = ab.create();
		alert.show();
				
	}

}
