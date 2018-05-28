package com.example.peppelt.provacar1;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class PosizioneAutista extends Activity implements RemoteCallListener<String>{
    private Button autobus;
    private String type;
    private String cod;
    private String ar;
    private String data;
    private String ora;
    private String indirizzo;
    private String indlat;
    private String indlon;
    private String autista;
    private String percodice;
    private GoogleMap map;
    private LatLng latlng;
    private String dataold="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        Bundle bb=i.getBundleExtra("bundle");

        type = bb.getString("type");
        cod = bb.getString("cod");
        indirizzo=bb.getString("indirizzo");
        indlat=bb.getString("indlat");
        indlon=bb.getString("indlon");
        data=bb.getString("data");
        ora=bb.getString("ora");
        autista=bb.getString("autista");
        percodice=bb.getString("percodice");
        ar = bb.getString("ar");

        setContentView(R.layout.activity_posizione_autista);

        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng latlngcentro = new LatLng(40.773720, 14.794522);
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(latlngcentro , 8.0f) );
        autobus = (Button) findViewById(R.id.autobus);

        //map.addCircle()
        richiediPos();











        //Toast.makeText(getApplicationContext(), "autista: "+autista+" codice"+ cod , Toast.LENGTH_LONG).show();

        autobus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(PosizioneAutista.this ,ListaBusActivity.class);
                Bundle b = new Bundle();
                b.putString("cod", cod);
                b.putString("indirizzo",indirizzo);
                b.putString("data",data);
                b.putString("ora",ora);
                b.putString("ar",ar);
                b.putString("indlat",indlat);
                b.putString("indlon",indlon);

                i.putExtra("bundle", b);

               // Toast.makeText(getApplicationContext(), "codice :"+cod +"tipo"+type , Toast.LENGTH_LONG).show();
                startActivity(i);
            }
        });





        new Thread(new Runnable() {
            public void run() {
                while (true) {
                   richiediPos();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }
    @Override
    public void onRemoteCallListenerComplete(String dati) {
        /*
        if(dati.equalsIgnoreCase("ERROR"))
            ab.setMessage(getString(R.string.erroredirete));

        else
        {*/



        try{
            double lat = Double.parseDouble(new JSONObject(dati).getString("lat").toString());
            double lng = Double.parseDouble(new JSONObject(dati).getString("lng").toString());
            String dataora = new JSONObject(dati).getString("data");

            if(lat!=0) {
                latlng = new LatLng(lat, lng);
                if (!dataora.equals(dataold))
                    aggiornaMappa();
                dataold = dataora;
            }
            else{ //far uscire un messaggio ma non con il toast perchè con il tread che pompa continuamente non va bene
                //Toast.makeText(getApplicationContext(),"posizione autista non disponibile", Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(getApplicationContext(),"lat "+lat+"lng "+lng +"dataora"+dataora, Toast.LENGTH_LONG).show();
        }
        catch(JSONException e){
            e.printStackTrace();

        }

        // }
    }
    protected void aggiornaMappa() {
        // TODO Auto-generated method stub
        map.clear();
        MarkerOptions mrk = new MarkerOptions().position(latlng);



        map.addMarker(mrk.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_mark)));
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(latlng , 13.0f) );
        //Toast.makeText(getApplicationContext(), "aggiorno la mappa", Toast.LENGTH_LONG).show();
    }

    protected  void richiediPos(){
        RequestHttpAsyncTask rh = new RequestHttpAsyncTask(PosizioneAutista.this);
        try{
            JSONObject js = new JSONObject();
            js.put("url", getString(R.string.host)+"servletInvioPosizioneAutista");
            js.put("codAutista", autista.toString());
            js.put("percodice",percodice);


            rh.execute(js);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }




}
