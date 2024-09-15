package com.vibeviroma.vaincrelemal229;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Zone extends AppCompatActivity {

    public static  String local_string="ma_position_e_t_r_s_r_l.txt";
    private GoogleMap mMap;
    int c=0;
    String type="";
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab2=(FloatingActionButton)findViewById(R.id.fab2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap= googleMap;
                c=1;
                Toast.makeText(Zone.this, "Cartes prêtes", Toast.LENGTH_SHORT).show();
                type= getIntent().getStringExtra("type");
                if(type.equals("@search_from_map@\t"))
                    verification_map();
                else
                    chercherville(type);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog al = new AlertDialog.Builder(Zone.this)
                        .setTitle("NOUVEAU CAS DE COVID19")
                        .setPositiveButton("Non non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sauver(100, POSITION, ST, true);
                            }
                        }).setMessage("Signaler un cas confirmé ?").create();
                al.show();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog al = new AlertDialog.Builder(Zone.this)
                        .setTitle("NOUVEAU CAS DE COVID19")
                        .setPositiveButton("Non non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sauver(100, POSITION, ST, false);
                            }
                        }).setMessage("Retier un cas confirmé ?").create();

                al.show();
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

    public String lire_le_fichier(String fichier){
        String texto="";
        try {
            FileInputStream fil_i=openFileInput(fichier);  // new FileInputStream(fichier);
            byte[] buffer= new byte[fil_i.available()];
            fil_i.read(buffer);
            texto= new String(buffer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return texto;
    }

    private void verification_map() {
        String coordonne=lire_le_fichier(local_string);
        if (!coordonne.equals("")) {
            if(coordonne.contains("#")) {
                double lat= Double.parseDouble(coordonne.substring(0, coordonne.indexOf("#")));
                double longit= Double.parseDouble(coordonne.substring((coordonne.indexOf("#"))+1, coordonne.length()));
                LatLng sydney = new LatLng(lat, longit);
                Geocoder geocoder= new Geocoder(this);
                String pos="";
                try {
                    List<Address> adress=geocoder.getFromLocation(lat, longit, 1);
                    if(adress.size()>0)
                        pos=adress.get(0).getLocality();
                    if(pos!=null && !pos.isEmpty()){
                        sauve(pos, false);
                    }
                    if(adress.size()>0) {
                        String spo = adress.get(0).getSubLocality();
                        if (spo != null)
                            pos = adress.get(0).getLocality() + " ; " + spo;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMap.addMarker(new MarkerOptions().position(sydney).title("Vous: "+pos));
                mMap.addCircle(new CircleOptions().center(sydney).radius(360).visible(true).fillColor(getResources().getColor(R.color.colorTrans)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.8f));
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                /*try {
                    List<Address> adress=geocoder.getFromLocation(lat, longit, 1);
                    pos=adress.get(0).getLocality();
                    if(!pos.isEmpty()){
                        sauve(pos, false);
                    }
                    String spo=adress.get(0).getSubLocality();
                    if(spo!=null)
                        pos=adress.get(0).getLocality()+" ; "+spo;
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

            }
        }
    }

    private void sauve(String pos,final boolean  fromSearch) {
        final DatabaseReference data= FirebaseDatabase.getInstance().getReference().child("Resultats");
        FirebaseAuth mAth= FirebaseAuth.getInstance();
        if(mAth.getCurrentUser()!=null) {
            final String online = mAth.getCurrentUser().getUid();
            if(!fromSearch && pos!=null && !pos.equals(""))
                data.child(online).child("Locality").setValue(pos);

            Query q= data.orderByChild("Locality").equalTo(pos);//String key=dataSnapshot.getKey();
            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dat ) {
                    for(DataSnapshot dataSnapshot:dat.getChildren()){
                        final String key=dataSnapshot.getKey();
                        Log.d("Environnement", key);
                        if(dataSnapshot.hasChild("proba")) {
                            final String proba= dataSnapshot.child("proba").getValue().toString();
                            {
                                if(dataSnapshot.hasChild("cc")) {
                                    if (dataSnapshot.hasChild("Position")&&(!afiche.contains(key))) {
                                        String posi=dataSnapshot.child("Position").getValue().toString();
                                        String cc = dataSnapshot.child("cc").getValue().toString();
                                        integre(posi, proba, cc + " cc");
                                        afiche = afiche + ";" + key;
                                    }
                                }else {
                                    {
                                        if(!afiche.contains(key))
                                        {
                                            //if(fromSearch) {
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.hasChild("Position")) {
                                                            String posi = dataSnapshot.child("Position").getValue().toString();
                                                            integre(posi, proba, proba + "%");
                                                            afiche = afiche + ";" + key;
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            //}else {

                                            //}
                                        }
                                    }
                                }
                            }
                            int p= Integer.parseInt(proba);
                            if(p>=50 && !lire_le_fichier("deja_notif").contains(key)){
                                showNotif("Alerte covid19! Faites attention", "Vous avez un cas"+(p==100?" confirmé": " de "+p+"%")+" dans cette zone", "ALERTE_GRAVE", p+"%", false);
                                ecrire("deja_notif", key);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    String afiche="";

    public void showNotif (String title, String caption, String name, String info, boolean test){
        if(test) {
            Intent chatIntent = new Intent(Zone.this, MapsActivity.class);
            chatIntent.putExtra("name", name);
            PendingIntent pendingIntent = PendingIntent.getActivity(Zone.this, 1, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(Zone.this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(caption)
                    .setContentInfo(info)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(1)
                    .setChannelId(NC_ID);
            NotificationManager nm = (NotificationManager) Zone.this.getSystemService(NOTIFICATION_SERVICE);
            createDefChan(nm, name);
            nm.notify(9, builder.build());
        }else {
            Intent chatIntent = new Intent(Zone.this, Zone.class);
            chatIntent.putExtra("name", name);
            PendingIntent pendingIntent = PendingIntent.getActivity(Zone.this, 1, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(Zone.this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(caption)
                    .setContentInfo(info)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(1)
                    .setChannelId(NC_ID);
            NotificationManager nm = (NotificationManager) Zone.this.getSystemService(NOTIFICATION_SERVICE);
            createDefChan(nm, name);
            nm.notify(10, builder.build());
        }

    }

    public void createDefChan(NotificationManager nm, String name){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel= new NotificationChannel(NC_ID, name, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("TL notification");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
            nm.createNotificationChannel(notificationChannel);
        }


    }


    private String NC_ID="Notification_renseignement_covid19";

    private void integre(String coordonne, String proba, String text) {
        if (!coordonne.equals("")){
            if(coordonne.contains("#")) {
                double lat= Double.parseDouble(coordonne.substring(0, coordonne.indexOf("#")));
                double longit= Double.parseDouble(coordonne.substring((coordonne.indexOf("#"))+1, coordonne.length()));
                LatLng sydney = new LatLng(lat, longit);
                /*Geocoder geocoder= new Geocoder(this);
                String pos="";
                try {
                    List<Address> adress=geocoder.getFromLocation(lat, longit, 1);
                    pos=adress.get(0).getLocality();
                    String spo=adress.get(0).getSubLocality();
                    if(spo!=null)
                        pos=adress.get(0).getLocality()+" ; "+spo;
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                mMap.addMarker(new MarkerOptions()
                        .position(sydney)
                        .icon( BitmapDescriptorFactory.fromBitmap(createBitmap(Integer.parseInt(proba), text)))
                        .title(proba+" %"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.8f));

            }
        }
    }

    public Bitmap createBitmap(int pos, String text){
        View view= LayoutInflater.from(this).inflate(R.layout.marker, null);
        TextView tv=(TextView)view.findViewById(R.id.text);

        if(pos<10)
            tv.setTextColor(Color.BLACK);
        else if(pos<20)
            tv.setTextColor(Color.BLUE);
        else if(pos<30)
            tv.setTextColor(getResources().getColor(R.color.colorAccent));
        else if(pos<40)
            tv.setTextColor(getResources().getColor(R.color.purp));
        else {
            if(text.contains("%"))
                tv.setTextColor(Color.RED);
            else {
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(tv.getTextSize()+1);
            }
        }
        tv.setText(text);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(30, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0,0,displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap= Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas= new Canvas(bitmap);
        view.draw(canvas);
        return  bitmap;
    }


    int cc=0;
    private String POSITION, ST;
    private FloatingActionButton fab, fab2;
    public void chercherville(String name){
        mMap.clear();
        afiche="";
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address>  adresslist= geocoder.getFromLocationName(name, 1);
            if(adresslist.size()>=1) {
                final Address address = adresslist.get(0);
                LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
                final String st = adresslist.get(0).getLocality();

                mMap.addMarker(new MarkerOptions().position(latlng).title(st));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13.8f));
                mMap.addCircle(new CircleOptions().center(latlng).radius(360).visible(true).fillColor(getResources().getColor(R.color.colorTrans2)));
                if(!lire_le_fichier("search__.txt").contains(st)) {
                    ecrire("search__.txt", lire_le_fichier("search__.txt")+"\t"+st);
                }
                fab2.setVisibility(View.GONE);

                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals("JgB3dxP2eiO87L9iHsbmtLcw8Dg1")){
                    fab.setVisibility(View.VISIBLE);
                    fab2.setVisibility(View.VISIBLE);
                    POSITION=address.getLatitude()+"#"+address.getLongitude();
                    ST= st;
                }
                if (!st.isEmpty()) {
                    sauve(st, true);
                }
            }else {
                AlertDialog alertDialog = new AlertDialog.Builder(Zone.this)
                        .setMessage("Impossible de trouver "+name+" sur la carte !")
                        .setPositiveButton("OK, fermer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        }).create();
                alertDialog.show();
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    private void sauver(final int total, final String position, final String ville,final boolean b) {
        {
            FirebaseDatabase.getInstance().getReference().child("Resultats").orderByChild("casC").equalTo(ville)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String[] key = {""};
                    int cc=0;

                    if(dataSnapshot.getChildrenCount()!=0) {
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            if(dataSnapshot1.hasChild("cc")) {
                                cc = Integer.parseInt(dataSnapshot1.child("cc").getValue().toString());
                                key[0] =dataSnapshot1.getKey();
                            }
                        }
                    }

                    if(!b && cc==0)
                        return;

                    final int finalCc = cc;
                    AlertDialog alertDialog = new AlertDialog.Builder(Zone.this)
                            .setMessage("Voulez-vous vraiment"+( (b)? " ajouter": " retier")+" 1 cas confirmé ?")
                            .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    final ProgressDialog progressDialog= new ProgressDialog(Zone.this);
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.setMessage("Ajout de CC en cours...");
                                    progressDialog.show();

                                    Date date =new Date();
                                    SimpleDateFormat sdf= new SimpleDateFormat("dd-MMM-yyyy", Locale.CANADA_FRENCH);
                                    String today= sdf.format(date).replace(".", "");
                                    Map map = new HashMap();
                                    map.put("sum", total+"");
                                    map.put("proba", total+"");
                                    map.put("day",  "0");
                                    map.put("date", today);
                                    map.put("Position", position);
                                    int to_add=finalCc;
                                    if(b)
                                        to_add=finalCc+1;
                                    else
                                        to_add=finalCc-1;

                                    map.put("cc", to_add+"");
                                    map.put ("casC", ville );
                                    map.put ("Locality", ville );
                                    DatabaseReference data= FirebaseDatabase.getInstance().getReference().child("Resultats");
                                    if(key[0].equals(""))
                                        key[0] =data.push().getKey();
                                    String online= key[0];
                                    FirebaseAuth mAth= FirebaseAuth.getInstance();
                                    if(mAth.getCurrentUser()!=null) {
                                        data.child(online).updateChildren(map, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            }).setNegativeButton("Non non", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }



}
