package com.example.peppelt.provacar1;

/**
 * Created by Ros on 08/05/2018.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Dati> {

    //the dati list that will be displayed
    private List<Dati> datiList;

    //the context object
    private Context mCtx;

    //here we are getting the herolist and context
    //so while creating the object of this adapter class we need to give herolist and context
    public ListViewAdapter(List<Dati> datiList, Context mCtx) {
        super(mCtx, R.layout.list_items, datiList);
        this.datiList = datiList;
        this.mCtx = mCtx;
    }

    //this method will return the list item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //getting the layoutinflater
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        //creating a view with our xml layout
        View listViewItem = inflater.inflate(R.layout.list_items, null, true);

        //getting text views
        TextView textDistance = listViewItem.findViewById(R.id.textDistance);
        TextView textDuration = listViewItem.findViewById(R.id.textDuration);
        TextView textHtml_instructions = listViewItem.findViewById(R.id.textHtml_instructions);


        //Getting the hero for the specified position
        Dati dati = datiList.get(position);

        //setting hero values to textviews
        textDistance.setText(dati.getDistance());
        textDuration.setText(dati.getDuration());
        textHtml_instructions.setText(dati.getHtml_instructions());


        //returning the listitem
        return listViewItem;
    }
}
