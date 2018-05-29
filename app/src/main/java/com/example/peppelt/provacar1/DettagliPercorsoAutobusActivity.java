package com.example.peppelt.provacar1;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ros on 28/05/2018.
 */

public class DettagliPercorsoAutobusActivity extends Activity implements RemoteCallListener<String> {
    private String codPercorso;
    private String dataPercorso;
    private String oraPartenza;
    private String oraArrivo;
    private String numeroAutobus;

    private JSONArray indFermata;
    private JSONArray oraFermata;
    private JSONArray latFermata;
    private JSONArray lonFermata;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_percorso_autobus);
        Intent i = getIntent();
        Bundle bb = i.getBundleExtra("bundle");
        codPercorso= bb.getString("codPercorso");
        dataPercorso=bb.getString("dataPercorso");
        oraArrivo=bb.getString("oraArrivo");
        oraPartenza=bb.getString("oraPartenza");
        numeroAutobus=bb.getString("numeroAutobus");
        //Toast.makeText(getApplicationContext(),"p: "+codPercorso+" data:"+dataPercorso+" oraAr: "+oraArrivo+" oraP: "+oraPartenza+" num"+numeroAutobus, Toast.LENGTH_LONG).show();

        //servlet per i dellagli sui percorsi
        RequestHttpAsyncTask rh = new RequestHttpAsyncTask(DettagliPercorsoAutobusActivity.this);
        try {
            JSONObject js = new JSONObject();
            js.put("url", getString(R.string.host) + "servletDettagliAutobus");
            js.put("cod", codPercorso.toString());


            rh.execute(js);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onRemoteCallListenerComplete(String dati) {

        try {

            //se non abbiamo trovato niente nel db
            if (dati.equals("niente")) {
                Toast.makeText(getApplicationContext(), "non sono forniti dettagli sul percorso", Toast.LENGTH_LONG).show();
            } else { //prendiamo i dettagli del nostro percorso forniti dalla servlet
                JSONObject js = new JSONObject(dati);
                indFermata = js.getJSONArray("indFermata");
                oraFermata = js.getJSONArray("oraFermata");
                latFermata = js.getJSONArray("latFermata");
                lonFermata = js.getJSONArray("lonFermata");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try { //stampa per vedere se sono arrivati i dati
            for(int i=0;i<indFermata.length();i++) {
                Log.i("DEBUG","oooooooooooooooooooooooooooo indirizzo: "+indFermata.getString(i));
                Log.i("DEBUG","oooooooooooooooooooooooooooo ora: "+oraFermata.getString(i));
                Log.i("DEBUG","oooooooooooooooooooooooooooo lat: "+latFermata.getString(i));
                Log.i("DEBUG","oooooooooooooooooooooooooooo lon: "+lonFermata.getString(i));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
