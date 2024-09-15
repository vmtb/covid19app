package com.vibeviroma.vaincrelemal229;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
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
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener/*,OnMapReadyCallback*/ {

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private static final int petit=10;
    private FirebaseUser currentUser;
    private String online;
    private ImageView img_nav;
    private TextView text_nav;
    private ProgressDialog load;
    private int c=0;
    private String infos_user[]= new String[5];
    private String lien="https://www.facebook.com/caporalepapou";
    private final String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int all_permissions = 1;
    private final String [] permissions2 = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int all_permissions2 = 2;
    private CardView zone, test, conseils, search, gouv, symp, oms;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(getIntent()!=null&& getIntent().getStringExtra("name")!=null) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.cancel(10);
        }
        if(getIntent()!=null&& getIntent().getStringExtra("new")!=null) {
            nouveau(getIntent().getStringExtra("new"));
        }
        //mapFragment.getMapAsync(this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        img_nav=(ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageView);
        text_nav=(TextView)navigationView.getHeaderView(0).findViewById(R.id.textView);
        rootRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        load=new ProgressDialog(this);

        zone=(CardView)findViewById(R.id.zone);
        test=(CardView)findViewById(R.id.test);
        conseils=(CardView)findViewById(R.id.conseils);
        search=(CardView)findViewById(R.id.search);
        gouv=(CardView)findViewById(R.id.gouv);
        symp=(CardView)findViewById(R.id.symp);
        oms=(CardView)findViewById(R.id.oms);

        verification();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Toast.makeText(MainActivity.this, "Ouverture du repertoire", Toast.LENGTH_SHORT).show();
               startActivity(new Intent(MainActivity.this, List_Contact.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void nouveau(final String nom)
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("\nL'application est mis à disposition pour servir d'informations de 1er niveau," +
                        " de suivi quotidien des symptômes en vue de limiter le stress et de faire faire attention dans les zones où l'on se trouve.\n\nElle " +
                        "permet de localiser en tant réel tous ses utilisateurs et d'afficher leur probabilité de contamination sous anonymat. Elle donne également des conseils quotidiens pour " +
                        "attirer l'attention sur les gestes barrières de propagation.\n\nEn l'utilisant, " +
                        "\n\t- vous acceptez être localisé; " +
                        "\n\t- vous reconnaissez qu'en cas de pic de proba, nous pouvons vous aider à appeler le SAMU Bénin" +
                        "\n\t- vous reconnaissez que l'appli y compris les tests qui sont intégrés ne constituent en aucun cas un avis, un examen, un diagnostic médical" +
                        "\n\t- vous reconnaissez que les conseils ne remplacent pas les recommandations des médécins" +
                        "\n\t- vous reconnaissez que le test quotidien ne remplace pas un test médical de la covid19." +
                        "\nLes questions de ces tests ont été récupérées sur le site médical https://maladiecoronavirus.fr/se-tester pour un suivi sur TrueLocale.\n" +
                        "Le projet n'étant pas à terme a aussi besoin de soutien pour mettre à disposition des services qui pourront détecter les symptômes sans utiliser le processus des questions.\n\n\t" +
                        "Merci pour la compréhension.")
                .setPositiveButton("Ok, j'ai compris", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent chatIntent = new Intent(MainActivity.this, MapsActivity.class);
                        chatIntent.putExtra("name", nom);
                        chatIntent.putExtra("forced", "yes");
                        Toast.makeText(MainActivity.this, "Vous devez faire un premier Test", Toast.LENGTH_SHORT).show();
                        startActivity(chatIntent);
                    }
                });
        final AlertDialog alertDialog= builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent chatIntent = new Intent(MainActivity.this, MapsActivity.class);
                chatIntent.putExtra("name", nom);
                chatIntent.putExtra("forced", "yes");
                Toast.makeText(MainActivity.this, "Vous devez faire un premier Test", Toast.LENGTH_SHORT).show();
                startActivity(chatIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            String info=lire_le_fichier("tlocale_my_ref.txt");
            if(!info.equals("")) {
                Intent intent= new Intent(this, Profile.class);
                intent.putExtra("nom", infos_user[1]+" "+infos_user[0]);
                intent.putExtra("phone1", infos_user[3]);
                intent.putExtra("phone2", infos_user[4]);
                Toast.makeText(this, infos_user[3]+"-"+infos_user[4], Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }else
                Toast.makeText(this, "Veuillez attendre la fin du chargement..!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {
            String info=lire_le_fichier("tlocale_my_ref.txt");
            /*if(!info.equals("")){
                startActivity(new Intent(MainActivity.this, List_Contact.class));
            }else
                Toast.makeText(this, "Veuillez attendre la fin du chargement..!", Toast.LENGTH_SHORT).show();*/
            String num="51020000";
            if(new Random().nextInt(30)%2==0)
                num="51040000";
            Uri uri= Uri.parse("tel:+229"+num);
            Intent intent= new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        } /*else if (id == R.id.nav_slideshow) {
            String info=lire_le_fichier("tlocale_my_ref.txt");
            if(!info.equals("")){
                startActivity(new Intent(MainActivity.this, Loc_entr.class));
            }else
                Toast.makeText(this, "Veuillez attendre la fin du chargement..!", Toast.LENGTH_SHORT).show();

        } */else if (id == R.id.nav_manage) {
            String info=lire_le_fichier("tlocale_my_ref.txt");
            if(!info.equals("")){
                startActivity(new Intent(MainActivity.this, Loc_sort.class));
            }else
                Toast.makeText(this, "Veuillez attendre la fin du chargement..!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            moreInfo();
        }else if (id == R.id.don) {
            donate();
        }else if (id == R.id.search) {
            rechercher();
        }/*else if (id == R.id.conseils) {
            if(!lire_le_fichier("proba.txt").isEmpty()){
                if(infos_user!=null) {
                    conseils(Integer.parseInt(lire_le_fichier("proba.txt")));
                }
            }
        }else if (id == R.id.ref) {
            mMap.clear();
            afiche="";
            verification_map();
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public static  String local_string="ma_position_e_t_r_s_r_l.txt";

    private void moreInfo(){
        AlertDialog.Builder builder= new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage("Le projet CoronappBj des étudiants de GMM/UNSTIM Bénin avec la startup VIBE-VIROMA est mis en place pour participer à la limitation de la propagation" +
                        " du virus covid19 en Afrique et surtout au Bénin. \nL'application est mis à disposition pour servir d'informations de 1er niveau," +
                        " de suivi quotidien des symptômes en vue de limiter le stress et de faire faire attention dans les zones où l'on se trouve.\n\nElle " +
                        "permet de localiser en tant réel tous ses utilisateurs et d'afficher leur probabilité de contamination sous anonymat. Elle donne également des conseils quotidiens pour " +
                        "attirer l'attention sur les gestes barrières de propagation.\n\nEn l'utilisant, " +
                        "\n\t- vous acceptez être localisé; " +
                        "\n\t- vous reconnaissez qu'en cas de pic de proba, nous pouvons vous aider à appeler le SAMU Bénin" +
                        "\n\t- vous reconnaissez que l'appli y compris les tests qui sont intégrés ne constituent en aucun cas un avis, un examen, un diagnostic médical" +
                        "\n\t- vous reconnaissez que les conseils ne remplacent pas les recommandations des médécins" +
                        "\n\t- vous reconnaissez que le test quotidien ne remplace pas un test médical de la covid19." +
                        "\nLes questions de ces tests ont été récupérées sur le site médical https://maladiecoronavirus.fr/se-tester pour un suivi sur CoronaAppBj.\n" +
                        "Le projet n'étant pas à terme a aussi besoin de soutien pour mettre à disposition des services qui pourront détecter les symptômes sans utiliser le processus des questions.\n\n\t" +
                        "Merci pour la compréhension.")

                .setNegativeButton("Nous suivre sur Facebook", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri= Uri.parse(lien);
                        Intent intent= new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }).setPositiveButton("Ok, compris", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setCancelable(true);
        final AlertDialog alertDialog= builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void donate(){
        Uri uri= Uri.parse("https://wa.me/22965940030");
        Intent intent= new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        /*AlertDialog.Builder builder= new AlertDialog.Builder(this)
                .setTitle("Don "+getString(R.string.app_name))
                .setMessage("Vous pouvez accompagner et soutenir ce projet afin qu'il puisse venir à bout de ses attentes en mettant à disposition de solutions concrètes" +
                        " pouvant être utile à la lutte contre le coronavirus.\n\nVotre contribution serait vraiment la bienvenue. Avec ces moyens, nous agrandirons nos algorithmes et méthodes " +
                        " pour limiter la propagation du coronavirus. \n\nMerci de faire le pas et de contribuer au projet." +
                        "\n\n\t- Moov: +22965940030\n\t- MTN : +22962284255")

                .setPositiveButton("Nous écrire sur WhatsApp", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri= Uri.parse("https://wa.me/22965940030");
                        Intent intent= new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }).setNegativeButton("Revenir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.setCancelable(true);
        final AlertDialog alertDialog= builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();*/
    }

    private void rechercher(){
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            return;
        View view= LayoutInflater.from(this).inflate(R.layout.ville, null, false);
        final EditText ville= (EditText)view.findViewById(R.id.ville);
        Button val= (Button)view.findViewById(R.id.valider);
        ListView list=(ListView)view.findViewById(R.id.list);

        AlertDialog.Builder builder= new AlertDialog.Builder(this)
                .setView(view);
        builder.setCancelable(true);
        final AlertDialog alertDialog= builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        val.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sear= ville.getText().toString().trim();
                if(sear.isEmpty()){
                    Toast.makeText(MainActivity.this, "Veuillez saisir la ville !", Toast.LENGTH_SHORT).show();
                }else {
                    alertDialog.dismiss();
                    Intent intent= new Intent(MainActivity.this, Zone.class);
                    intent.putExtra("type", sear);
                    startActivity(intent);
                }
            }
        });
        alertDialog.show();

        String [] sea2=lire_le_fichier("search__.txt").split("\t");
        final String sea []= new String[sea2.length];
        for (int i = 0; i < sea2.length; i++) {
             sea[i]=sea2[sea2.length-i-1];
        }

        if(sea.length==0)
            list.setVisibility(View.GONE);
        else {
            list.setVisibility(View.VISIBLE);
            ArrayAdapter arra= new ArrayAdapter(this, android.R.layout.simple_list_item_1, sea);
            list.setAdapter(arra);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String villee= sea[position].trim();
                    if(!villee.isEmpty()) {
                        alertDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, Zone.class);
                        intent.putExtra("type", villee);
                        startActivity(intent);
                    }
                }
            });
        }

    }

    public void verification() {
        final DatabaseReference email_ref=FirebaseDatabase.getInstance().getReference().child("Email");
        if(currentUser==null){
            View v=LayoutInflater.from(this).inflate(R.layout.activity_connection, null);
            final AlertDialog al= new AlertDialog.Builder(this)
                    .setView(v)
                    .setCancelable(false)
                    .create();
            al.setCanceledOnTouchOutside(false);
            al.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            Button btn=(Button)v.findViewById(R.id.btn_sign_in);
            final EditText email=(EditText) v.findViewById(R.id.email);
            final EditText mp=(EditText)v.findViewById(R.id.pass);
            TextView t= (TextView)v.findViewById(R.id.condit);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri= Uri.parse(lien);
                    Intent intent= new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            load.setMessage("Chargement...");
            load.setCanceledOnTouchOutside(false);
            load.setCancelable(false);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //          load.show();
                    if((email.getText().toString().isEmpty())||(estEspace(email.getText().toString()))){
                        Toast.makeText(MainActivity.this, "Veuillez remplir le champ d'adresse électronique", Toast.LENGTH_SHORT).show();
                    }else if(mp.getText().toString().isEmpty()){
                        Toast.makeText(MainActivity.this, "Veuillez créer un mot de passe", Toast.LENGTH_SHORT).show();
                    }else {
                        final String e =email.getText().toString();
                        final String m=mp.getText().toString();
                        ecrire("email.txt", e);
                        ecrire("passw.txt", m);
                        load.show();
                        Query em= email_ref.orderByChild("email").equalTo(e);
                        em.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                load.dismiss();
                                if(dataSnapshot.getChildrenCount()!=0){
                                    load.setTitle("Connection");
                                    load.setMessage("Veuillez patienter...");
                                    load.show();
                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(e, m).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            load.dismiss();
                                            if(task.isSuccessful()){
                                                currentUser=mAuth.getCurrentUser();
                                                Toast.makeText(MainActivity.this, "Connection établie", Toast.LENGTH_SHORT).show();
                                                al.dismiss();
                                                verification();
                                            }else {
                                                Toast.makeText(MainActivity.this, "Echec de la connection..! Veuillez vérifier vos coordonnées", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    //   Toast.makeText(MainActivity.this, "Avec compte", Toast.LENGTH_SHORT).show();
                                }else {
                                    load.setTitle("Inscription");
                                    load.setMessage("Veuillez patienter...");
                                    load.show();
                                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(e, m).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                email_ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("mp").setValue(m);
                                                email_ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email")
                                                        .setValue(e).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        load.dismiss();
                                                        if(task.isSuccessful()) {
                                                            Intent i= new Intent(MainActivity.this, Infos_Inscr.class);
                                                            i.putExtra("name", "null");
                                                            i.putExtra("prenom", "null");
                                                            startActivity(i);
                                                            finish();
                                                            Toast.makeText(MainActivity.this, "Inscription reussie", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }else {
                                                Toast.makeText(MainActivity.this, "Echec de l'inscription..! Veuillez vérifier vos coordonnées", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Une erreur s'est produite..!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //       Toast.makeText(MainActivity.this, "Sans compte", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                   /* GoogleSignInOptions googl= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                 //   Toast.makeText(MainActivity.this, googl.getAccount().toString()+"", Toast.LENGTH_SHORT).show();
                    mG= new GoogleApiClient.Builder(MainActivity.this)
                            .addApi(Auth.GOOGLE_SIGN_IN_API, googl)
                            .enableAutoManage(MainActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                    Toast.makeText(MainActivity.this, "Echec de la connection", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .build();
                    Intent signIn= Auth.GoogleSignInApi.getSignInIntent(mG);
                    startActivityForResult(signIn, petit);*/
                    //        load.dismiss();
                }
            });
            al.show();
        }else{
            online=currentUser.getUid();
            rootRef.child(online).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("Prenom")){
                        infos_user[1]=dataSnapshot.child("Prenom").getValue().toString();
                        infos_user[0]=dataSnapshot.child("Nom").getValue().toString();
                        String cont=dataSnapshot.child("Contacts").getValue().toString();
                        infos_user[3]=cont.substring(0, cont.indexOf("@"));
                        infos_user[4]=cont.substring(cont.indexOf("@")+1, cont.length());
                        text_nav.setText(infos_user[1]+" "+infos_user[0]);
                        ecrire("tlocale_my_ref.txt", infos_user[0]+"\n"+infos_user[1]+"\nnull\n"+ infos_user[3] +"\n"+ infos_user[4]);

                        if(dataSnapshot.hasChild("Conf")) {
                            if(dataSnapshot.child("Conf").getValue().toString().equals("auth")) {
                                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(MainActivity.this, permissions, all_permissions);
                                }else
                                    startService(new Intent(MainActivity.this, MapsActivity2.class));
                            }else {
                                stopService(new Intent(MainActivity.this, MapsActivity2.class));
                            }
                        }   else {
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this, permissions, all_permissions);
                            }else
                                startService(new Intent(MainActivity.this, MapsActivity2.class));
                        }
                        if(dataSnapshot.hasChild("Position")) {
                            String coordonne= dataSnapshot.child("Position").getValue().toString();
                            double lat = Double.parseDouble(coordonne.substring(0, coordonne.indexOf("#")));
                            double longit = Double.parseDouble(coordonne.substring((coordonne.indexOf("#")) + 1, coordonne.length()));
                            Geocoder geocoder = new Geocoder(MainActivity.this);
                            String pos = "";
                            try {
                                List<Address> adress = geocoder.getFromLocation(lat, longit, 1);
                                if (adress.size() > 0)
                                    pos = adress.get(0).getLocality();
                                if (pos != null && !pos.isEmpty()) {
                                    FirebaseDatabase.getInstance().getReference().child("Resultats").child(online).child("Locality").setValue(pos);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }else {
                        AlertDialog alertDialog= new AlertDialog.Builder(MainActivity.this)
                                .setTitle("IMPORTANT")
                                .setMessage("Vous devez achever votre inscription")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i= new Intent(MainActivity.this, Infos_Inscr.class);
                                        i.putExtra("name", "null");
                                        i.putExtra("prenom", "null");
                                        startActivity(i);
                                        finish();
                                    }
                                })
                                .create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Intent i= new Intent(MainActivity.this, Infos_Inscr.class);
                                i.putExtra("name", "null");
                                i.putExtra("prenom", "null");
                                startActivity(i);
                                finish();
                            }
                        });
                        alertDialog.show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            String info=lire_le_fichier("tlocale_my_ref.txt");
            if(!info.equals("")) {
                int anc=0, compt=0;
                for (int i = 0; i <info.length() ; i++) {
                    if((info.charAt(i)=='\n')||(i==info.length()-1)){
                       if((info.charAt(i)=='\n')){
                           infos_user[compt]=info.substring(anc, i);
                           anc=i+1;
                           compt++;
                       }else {
                           infos_user[compt]=info.substring(anc, i+1);
                           anc=i+1;
                           compt++;
                       }

                    }
                }
                text_nav.setText(infos_user[1]+" "+infos_user[0]);
            }
            //Toast.makeText(this, lire_le_fichier("prof_loc.txt")+"A", Toast.LENGTH_LONG).show();

            /*if(!lire_le_fichier("prof_loc.txt").equals("")){
                try {

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, permissions2, all_permissions2);
                    }//else
                        //img_nav.setImageURI(Uri.parse(lire_le_fichier("prof_loc.txt")));
                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(this, "La photo d'identification a été supprimée ou peut-être déplacée", Toast.LENGTH_SHORT).show();
                }
            }*/
            final DatabaseReference data= FirebaseDatabase.getInstance().getReference().child("Resultats");
            data.child(online).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("date")){
                        String dat=dataSnapshot.child("date").getValue().toString();  Date date =new Date();
                        SimpleDateFormat sdf= new SimpleDateFormat("dd-MMM-yyyy", Locale.CANADA_FRENCH);
                        String today= sdf.format(date).replace(".", "");
                        //Toast.makeText(MainActivity.this, dat+"", Toast.LENGTH_SHORT).show();
                        if(today.equals(dat)) {
                            startAlarm();
                        }else {
                            AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
                            builder.setCancelable(true);
                            builder .setTitle("Alerte coronavirus");
                            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP)
                                builder .setIcon(getResources().getDrawable(android.R.drawable.stat_sys_warning));
                            builder .setMessage("Vous n'avez pas encore passé votre test sur la covid19 aujourd'hui ! ")
                                    .setPositiveButton("Passer le test en même temps", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent chatIntent = new Intent(MainActivity.this, MapsActivity.class);
                                            chatIntent.putExtra("name", infos_user[1]);
                                            chatIntent.putExtra("forced", "yes");
                                            startActivity(chatIntent);
                                        }
                                    })

                                    .setNegativeButton("Revenir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alertDialog= builder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                        }
                        String total=dataSnapshot.child("proba").getValue().toString();
                        ecrire("proba.txt", total);
                    } else {
                        if(getIntent()!=null&& getIntent().getStringExtra("new")!=null) {
                        }else{
                            Intent chatIntent = new Intent(MainActivity.this, MapsActivity.class);
                            Toast.makeText(MainActivity.this, "Vous devez faire un premier Test", Toast.LENGTH_SHORT).show();
                            chatIntent.putExtra("name", infos_user[1]);
                            chatIntent.putExtra("forced", "yes");
                            startActivity(chatIntent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            zone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(MainActivity.this, Zone.class);
                    intent.putExtra("type", "@search_from_map@\t");
                    startActivity(intent);
                }
            });
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rechercher();
                }
            });
            test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("name", infos_user[1]);
                    startActivity(intent);
                }
            });
            conseils.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!lire_le_fichier("proba.txt").isEmpty()) {
                        if(infos_user!=null) {
                            Intent intent= new Intent(MainActivity.this, Conseils.class);
                            intent.putExtra("nom", infos_user[1]);
                            intent.putExtra("total", Integer.parseInt(lire_le_fichier("proba.txt")));
                            startActivity(intent);
                        }
                    }
                }
            });
            gouv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToGouv();
                }
            });
            symp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToSympt();
                }
            });
            oms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToOms();
                }
            });

        }
    }

    private void startAlarm(){
        AlarmManager al= (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmNotif.class);
        intent.putExtra("name", infos_user[1]);
        PendingIntent pd= PendingIntent.getBroadcast(this, 0, intent, 0);
        Date date_today = new Date();
        Date date = new Date();
        date.setDate(date_today.getDate());
        date.setMonth(date_today.getMonth());
        date.setYear(date_today.getYear());
        date.setHours(7);
        date.setMinutes(0);
        date.setSeconds(0);

        long to= date.getTime()+(24*60*60*1000);

        al.set(AlarmManager.RTC_WAKEUP, to, pd);
    }

    private void startAlarm2() {
        AlarmManager al= (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmBar.class);
        intent.putExtra("name", infos_user[1]);
        PendingIntent pd= PendingIntent.getBroadcast(this, 0, intent, 0);
        Date date_today = new Date();
        Date date = new Date();
        date.setDate(date_today.getDate());
        date.setMonth(date_today.getMonth());
        date.setYear(date_today.getYear());
        date.setHours(12);
        date.setMinutes(0);
        date.setSeconds(0);
        String today=date_today.getDate()+"-"+date_today.getMonth()+"-"+date_today.getYear();
        if(!lire_le_fichier("alarme2.txt").equals(today)){
            ecrire("alarme2.txt", today);
            long to= date.getTime()+(24*60*60*1000);

            al.set(AlarmManager.RTC_WAKEUP, to, pd);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==petit){

            GoogleSignInResult reuslt=  Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(reuslt.isSuccess()) {
                GoogleSignInAccount account= reuslt.getSignInAccount();
                firebaseAuthWithGoogl(account);
            }else {
                AlertDialog al= new AlertDialog.Builder(this)
                        .setMessage("Echec de la connection au compte sélectioné\nVeuillez réessayer...")
                        .create();
                al.setCanceledOnTouchOutside(false);
                al.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {


                    }
                });
                al.show();
            }

        }
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

    public  boolean estEspace(String s){
        boolean vrai= true;
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i)!=' '){
                vrai=false;
            }
        }

        return vrai;
    }

    private void firebaseAuthWithGoogl(final GoogleSignInAccount account) {
        load.setMessage("Veuillez patienter...");
        load.setTitle("Inscription");
        load.setCancelable(false);
        load.setCanceledOnTouchOutside(false);
        load.show();
        AuthCredential auth= GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(auth).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                load.dismiss();
                if(task.isSuccessful()){
                    String name= account.getFamilyName();
                    String prenom=account.getGivenName();
                    Intent i= new Intent(MainActivity.this, Infos_Inscr.class);
                    i.putExtra("name", name);
                    i.putExtra("prenom", prenom);
                    startActivity(i);
                }else {
                    AlertDialog al= new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Echec de l'inscription avec "+account.getEmail()+"\nVeuillez réessayer...")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    al.setCanceledOnTouchOutside(false);
                    al.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {

                        }
                    });
                    al.show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==all_permissions) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions, all_permissions);
            }else
                startService(new Intent(MainActivity.this, MapsActivity2.class));
        }else if(requestCode==all_permissions2){

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions2, all_permissions2);
            }else{

               // img_nav.setImageURI(Uri.parse(lire_le_fichier("prof_loc.txt")));
            }

        }
    }


    public void createDefChan(NotificationManager nm, String name){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel= new NotificationChannel(NC_ID, name, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("TL notification");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
            nm.createNotificationChannel(notificationChannel);
        }


    }


    public void showNotif (String title, String caption, String name, String info, boolean test){
        if(test) {
            Intent chatIntent = new Intent(MainActivity.this, MapsActivity.class);
            chatIntent.putExtra("name", name);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(caption)
                    .setContentInfo(info)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(1)
                    .setChannelId(NC_ID);
            NotificationManager nm = (NotificationManager) MainActivity.this.getSystemService(NOTIFICATION_SERVICE);
            createDefChan(nm, name);
            nm.notify(9, builder.build());
        }else {
            Intent chatIntent = new Intent(MainActivity.this, MainActivity.class);
            chatIntent.putExtra("name", name);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(caption)
                    .setContentInfo(info)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(1)
                    .setChannelId(NC_ID);
            NotificationManager nm = (NotificationManager) MainActivity.this.getSystemService(NOTIFICATION_SERVICE);
            createDefChan(nm, name);
            nm.notify(10, builder.build());
        }

    }

    private String NC_ID="Notification_renseignement_covid19";

    public void goToGouv(){
        String lien= "https://www.gouv.bj/coronavirus/";
        Uri uri= Uri.parse(lien);
        Intent intent= new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public void goToOms(){
        String lien= "https://www.who.int/fr/emergencies/diseases/novel-coronavirus-2019/advice-for-public";
        Uri uri= Uri.parse(lien);
        Intent intent= new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void goToPort(){
        String lien= "https://www.gouv.bj/actualite/592/coronavirus-%E2%80%93-quelques-consignes-respecter-port-masque/";
        Uri uri= Uri.parse(lien);
        Intent intent= new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void goToSympt(){
        Intent intent = new Intent(this, Sympt.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
