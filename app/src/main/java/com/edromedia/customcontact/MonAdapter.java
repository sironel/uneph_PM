package com.edromedia.customcontact;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MonAdapter extends ArrayAdapter<contact> {
    List<contact> cont;
    Context c;
    int r;
    public MonAdapter(Context context, int resource, List<contact> objects) {
        super(context, resource, objects);
        this.cont = objects;
        this.c = context;
        this.r = resource;
    }


    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {

        // Get the data item for this position
        contact cont = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout, parent, false);
        }
        // Lookup view for data population
        TextView tvNom = (TextView) convertView.findViewById(R.id.tv1);
        TextView tvPrenom = (TextView) convertView.findViewById(R.id.tv2);
        TextView tvTel = (TextView) convertView.findViewById(R.id.tv3);
        // Populate the data into the template view using the data object
        tvNom.setText(cont.getNom());
        tvPrenom.setText(cont.getPrenom());
        tvTel.setText(cont.getTel());
        // Return the completed view to render on screen
        return convertView;
    }


//        LayoutInflater inflater = ((Activity) c).getLayoutInflater();
//        View v = inflater.inflate(r,parent,false);
//        return v;

}
