package com.example.peppelt.provacar1;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PosizioneAutista extends Activity {
    private Button autobus;
    private String type;
    private String cod;
    private String data;
    private String ora;
    private String indirizzo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        Bundle bb=i.getBundleExtra("bundle");

        type = bb.getString("type");
        cod = bb.getString("cod");
        indirizzo=bb.getString("indirizzo");
        data=bb.getString("data");
        ora=bb.getString("ora");



        setContentView(R.layout.activity_posizione_autista);
        autobus = (Button) findViewById(R.id.autobus);




        autobus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(PosizioneAutista.this ,TrasportoAlternativoActivity.class);
                Bundle b = new Bundle();
                b.putString("cod", cod);
                b.putString("type", type);
                b.putString("indirizzo",indirizzo);
                b.putString("data",data);
                b.putString("ora",ora);

                i.putExtra("bundle", b);

                Toast.makeText(getApplicationContext(), "codice :"+cod +"tipo"+type , Toast.LENGTH_LONG).show();
                startActivity(i);
            }
        });


    }

}
