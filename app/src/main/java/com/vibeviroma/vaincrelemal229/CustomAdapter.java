package com.vibeviroma.vaincrelemal229;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Marcos T VITOULEY on 10/11/2018 for TrueLocation.
 */

public class CustomAdapter extends BaseAdapter {
    LayoutInflater inflat;
    String [] list;
    int taille;
    Context context;

    public CustomAdapter(LayoutInflater inflat, String[] list, int taille, Context context) {
        this.inflat = inflat;
        this.list = list;
        this.taille = taille;
        this.context = context;
    }

    @Override
    public int getCount() {
        return taille;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        @SuppressLint("ViewHolder") View v= inflat.inflate(R.layout.contact, parent, false);
        TextView t= (TextView)v.findViewById(R.id.textView);
        t.setText(list[position]);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nom=list[position].substring(0, list[position].indexOf('\n'));
                final String num=list[position].substring(list[position].indexOf('\n')+1);

                Uri uri= Uri.parse("tel:"+num);
                Intent intent= new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
                /*  final String finalCont1 = num.substring(num.length() - 8, num.length());
              CharSequence [] c = new CharSequence[]{"Localiser "+nom +"?"};
               // Toast.makeText(List_Contact.this, finalCont1+"", Toast.LENGTH_SHORT).show();
             //   final String s = "-229"+finalCont1;
               // Toast.makeText(List_Contact.this, s, Toast.LENGTH_SHORT).show();

                AlertDialog a= new AlertDialog.Builder(parent.getContext())
                        .setItems(c, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0) {
                                    Intent intent= new Intent(parent.getContext(), MapsActivity3.class);
                                    intent.putExtra("nom", nom);
                                    intent.putExtra("num", num);
                                    parent.getContext().startActivity(intent);
                                }
                            }
                        })
                        .create();
                a.show();*/
            }
        });
        return v;
    }
}
