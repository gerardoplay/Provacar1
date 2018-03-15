package com.example.peppelt.provacar1;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class TrasportoAlternativoActivity extends Activity implements RemoteCallListener<String>{
    private String codice;
    private String type;
    private String data;
    private String ora;
    private String indirizzo;
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
            js.put("url", getString(R.string.host)+"servletTrasportoAlternativo");
            js.put("cod", codice.toString());
            js.put("data", data.toString());
            js.put("ora", ora.toString());
            js.put("indirizzo", indirizzo.toString());
            js.put("type", type.toString());

            rh.execute(js);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoteCallListenerComplete(String dati) {
        /*
        if(dati.equalsIgnoreCase("ERROR"))
            ab.setMessage(getString(R.string.erroredirete));

        else
        {*/
            try{
                int cod = new JSONObject(dati).getInt("cod");
                Toast.makeText(getApplicationContext(),"il codice è"+ cod, Toast.LENGTH_LONG).show();
            }
            catch(JSONException e){
                e.printStackTrace();

            }
       // }
    }
}
