package com.vibeviroma.vaincrelemal229;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Marcos T VITOULEY on 03/11/2018 for TrueLocation.
 */

public class Hist_Adap extends RecyclerView.Adapter<Hist_Adap.r_view_h> {
    Context ctx;
    private List<Hist> list;

    public Hist_Adap(List<Hist> list) {
        this.list = list;
    }

    @Override
    public r_view_h onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx=parent.getContext();
        View v=LayoutInflater.from(ctx).inflate(R.layout.layout_hist, parent, false);
        return new r_view_h(v);
    }

    @Override
    public void onBindViewHolder(final r_view_h holder, int position) {
        Hist h= list.get(position);
        String id=h.getId();
        final String code=h.getCode();
        final String date=h.getDate();
        FirebaseDatabase.getInstance().getReference().child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nom= dataSnapshot.child("Nom").getValue().toString();
                String prenom=dataSnapshot.child("Prenom").getValue().toString();
                if(code.equals("entrant")) {
                    holder.textView.setText(prenom+" "+nom+" a obtenu votre position\nLe "+date);
                }else {
                    holder.textView.setText("Vous avez obtenu la position de "+prenom+" "+nom+"\nLe "+date);
                }/*
                DecimalFormat df= new DecimalFormat("##,##");
                double res=41/3;
                df.format(res);*/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class r_view_h extends RecyclerView.ViewHolder{
        TextView textView;
        public r_view_h(View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.text);
        }
    }
}
