package com.example.peppelt.provacar1;

import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private GoogleMap map;
    ListView listView;
    //the hero list where we will store all the hero objects after parsing json
    ArrayAdapter<String> aa;

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
        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng latlngcentro = new LatLng(40.773720, 14.794522);
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(latlngcentro , 8.0f) );
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


        listView =(ListView) findViewById(R.id.List);
        aa= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        try { //inserisce l'indirizzo della fermata e l'ora nell'adapter
            for(int i=0;i<indFermata.length();i++) {
               aa.add(indFermata.getString(i)+" "+oraFermata.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView.setAdapter(aa);

        //per disegnare il tragitto e i punti sulla mappa
        DownloadTask dt = new DownloadTask();
        dt.execute(getUrlGMaps());

    }



        // da qui in avanti sono tutti codici per costruire i tragitti che fa il pullman da un punto all'altro
        // per farlo si costruisce l'url json con origine detinazione e tutti i punti di mezzo, invia l'url a google e
        // google restituisce il file json con il tragitto che farà il nostro Autobus, successivamente disegna il tragitto

    private String getUrlGMaps() {
        // TODO Auto-generated method stub
        String url ="";
        try{
            String str_origin = "origin="+latFermata.getDouble(0)+","+lonFermata.getDouble(0);
            String str_dest = "destination="+latFermata.getDouble(latFermata.length()-1)+","+lonFermata.getDouble(lonFermata.length()-1);
            String waipoints="waypoints=optimize:true|";
            for(int i=1;i<latFermata.length()-1;i++){
                if(i!=latFermata.length()-1)
                    waipoints+=latFermata.getDouble(i)+","+lonFermata.getDouble(i)+"|" ;
                else
                    waipoints+=latFermata.getDouble(i)+","+lonFermata.getDouble(i) ;
            }
            for(int i =0;i<latFermata.length();i++){
                map.addMarker(new MarkerOptions().position(new LatLng(latFermata.getDouble(i), lonFermata.getDouble(i))));
            }
            String sensor = "sensor=false";
            String parameters = str_origin+"&"+str_dest+"&"+waipoints+"&"+sensor;
            String output = "json";
            url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
            Log.v("mik", url);
        }catch(JSONException e){

        }
        return url;

    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {

            String data = "";
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                //Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Gson gs = new Gson();
            // gs.toJson(result);
            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);

        }

    }
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{


        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                //DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parseDirection(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);


                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                //Toast.makeText(getApplicationContext(), points.size()+"", Toast.LENGTH_LONG).show();

                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.BLUE);

            }



            if(points!=null ){
                map.addPolyline(lineOptions);
            }

            // conferma.setEnabled(true);
        }
    }

    private List<List<HashMap<String,String>>> parseDirection(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }
            JSONObject distance = jLegs.getJSONObject(0).getJSONObject("distance");
            //Log.d("JSON","distance: "+distance.toString());





        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }



        return routes;
    }




    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}

