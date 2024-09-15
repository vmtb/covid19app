package com.vibeviroma.vaincrelemal229;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity3 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String num, nom;
    private ProgressDialog progressDialog;
    private DatabaseReference d_ref;
    private DatabaseReference hist_ref;
    private int co=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps3);
        progressDialog= new ProgressDialog(this);
        num=getIntent().getExtras().getString("num");
        nom=getIntent().getExtras().getString("nom");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map) ;
        mapFragment.getMapAsync(this);

        d_ref= FirebaseDatabase.getInstance().getReference();

        progressDialog.setMessage("Chargement...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(MapsActivity3.this, "Annulé !", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if(!num.contains("+")){
            String online= FirebaseAuth.getInstance().getCurrentUser().getUid();
            d_ref.child("Users").child(online).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    if(dataSnapshot.hasChild("Indicatif")){
                        String indicatif= dataSnapshot.child("Indicatif").getValue().toString();
                        chercher(indicatif+""+num);
                    }else {
                        Toast.makeText(MapsActivity3.this, "Indisponible", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else {
            chercher(num);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    public void chercher(final String num){
        progressDialog.show();
        d_ref.child("Contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(num.replace("+", "-"))){
                    final String online= dataSnapshot.child(num.replace("+", "-")).getValue().toString();
                    d_ref.child("Users").child(online).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressDialog.dismiss();
                            if(dataSnapshot.hasChild("Position")){
                                String coordonne= dataSnapshot.child("Position").getValue().toString();
                                double lat= Double.parseDouble(coordonne.substring(0, coordonne.indexOf("#")));
                                double longit= Double.parseDouble(coordonne.substring((coordonne.indexOf("#"))+1, coordonne.length()));
                                LatLng sydney = new LatLng(lat, longit);
                                Geocoder geocoder= new Geocoder(MapsActivity3.this);
                                String pos=";";
                                try {
                                    List<Address> adress=geocoder.getFromLocation(lat, longit, 1);
                                    String spo=adress.get(0).getSubLocality();
                                    pos=adress.get(0).getLocality();
                                    if(spo!=null)
                                        pos=adress.get(0).getLocality()+" ; "+spo;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                mMap.addMarker(new MarkerOptions().position(sydney).title("Position de "+nom+": "+pos));
                                mMap.addPolygon(new PolygonOptions().add(sydney).fillColor(getResources().getColor(R.color.colorPrimar)));
                                mMap.addCircle(new CircleOptions().center(sydney).radius(360).visible(true).fillColor(getResources().getColor(R.color.colorTrans)).strokeColor(getResources().getColor(R.color.colorPrimar)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.7f));
                                if(co==5)
                                    sauver(online);
                            }else {
                                Toast.makeText(MapsActivity3.this, "Position de "+nom+" non disponible", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void ecrire(String fichier, String texte){
        try {
            FileOutputStream fil= openFileOutput(fichier, MODE_PRIVATE);
            fil.write(texte.getBytes());
            fil.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sauver(String online){
        Date d= new Date();
        Map m= new HashMap();
        m.put("from", FirebaseAuth.getInstance().getCurrentUser().getUid());
        m.put("to", online);
        m.put("date", d.getDate()+" / "+(d.getMonth()+1)+" / "+(d.getYear()+1900)+ " à "+d.getHours()+":"+d.getMinutes()+":"+d.getSeconds());
        String key= d_ref.child("Historique").push().getKey();
        d_ref.child("Historique").child(key).updateChildren(m, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                co=10;
            }
        });
    }




}
