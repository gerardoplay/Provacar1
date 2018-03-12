package com.example.peppelt.provacar1;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class TrasportoAlternativoActivity extends Activity implements RemoteCallListener<String>{
    String codice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trasporto_alternativo);
        Intent i = getIntent();
        Bundle bb=i.getBundleExtra("bundle");
        codice = bb.getString("cod");
        Toast.makeText(getApplicationContext(), codice, Toast.LENGTH_LONG).show();

        RequestHttpAsyncTask rh = new RequestHttpAsyncTask(TrasportoAlternativoActivity.this);
        try{
            JSONObject js = new JSONObject();
            js.put("cod", codice);
            js.put("url", getString(R.string.host)+"servletTrasportoAlternativo");
            rh.execute(js);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoteCallListenerComplete(String dati) {
        Toast.makeText(getApplicationContext(), "fattoo "+ codice, Toast.LENGTH_LONG).show();
    }
}
