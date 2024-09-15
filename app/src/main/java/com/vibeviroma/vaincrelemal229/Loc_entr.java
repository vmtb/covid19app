package com.vibeviroma.vaincrelemal229;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Loc_entr extends AppCompatActivity {

    //private DatabaseReference hist_ref;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String user_id;
    private TextView textView;
    private ProgressDialog progressDialog;
    private List<Hist> list= new ArrayList<>();
    private Hist_Adap hist_adap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_entr);
        linearLayoutManager=new LinearLayoutManager(this);
        textView=(TextView)findViewById(R.id.vide);
        recyclerView=(RecyclerView)findViewById(R.id.rv);
        user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        hist_adap=new Hist_Adap(list);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Chargement...");
        progressDialog.show();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(hist_adap);
        recyclerView.setHasFixedSize(true);


        load();
    }

    public void load(){
        DatabaseReference hist_ref= FirebaseDatabase.getInstance().getReference().child("Historique");
        hist_ref.keepSynced(true);

        hist_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key=dataSnapshot.getKey();
             //   Toast.makeText(Loc_entr.this, key+"", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                if(key!=null){
                    textView.setVisibility(View.GONE);
                    if(dataSnapshot.child(key).hasChild("to"))
                    {
                        String val=dataSnapshot.child(key).child("to").getValue().toString();
                   //     Toast.makeText(Loc_entr.this, val, Toast.LENGTH_SHORT).show();
                        if(val.equals(user_id)){
                            Hist h= new Hist();
                            h.setCode("entrant");
                            h.setDate(dataSnapshot.child(key).child("date").getValue().toString());
                            h.setId(key);
                            list.add(h);
                            hist_adap.notifyDataSetChanged();
                            recyclerView.scrollToPosition(0);
                        }
                    }else
                        textView.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
