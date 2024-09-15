package com.vibeviroma.vaincrelemal229;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.vibeviroma.vaincrelemal229.ui.Termo;

import static android.view.View.VISIBLE;

public class MapsActivity extends AppCompatActivity{

    private TextView tv, res, cliq;
    private Button start;
    private String today, NOM;
    private int total=0; private int total_sum1=0;
    private int contributor=-1;
    private Termo affich;
    private int current= 0;
    private String maux_de_tete="non",gorge="non", rhumes="non", diare="non", fatigue="non", sueur="non" ;

    private String[] questions= {
            "Sentez-vous des maux de tête actuellement? (Céphalées)",
            "Souffrez-vous d'une maladie de la foie?",
            "Souffrez-vous d'une insuffisance rénale?",
            "Sentez-vous une fatigue inhabituelle?",
            "Souffrez-vous habituellement d'une maladie respiratoire?",
            "Avez-vous une toux ou constatez-vous une augmentation de votre toux habituelle?",
            "Avez-vous remarqué une perte de votre goût? Ou une diminution de votre odorat?",
            "Avez-vous été en contact avec un potentiel porteur de la covid19 dans la journée d'hier ?",
            "Sentez-vous la diarrhée ? Ou avez-vous au moins trois selles molles? ",
            "Constatez-vous un manque de souffle inhabituel? Ou de la difficulté à respirer?",
            "Etes-vous dans l'un des cas suivants?\n\t- Hypertension mal équilibrée\n\t- Maladie cardiaque\n\t- Diabète\n\t- Quelconque cancer",
            "Avez-vous un mal de gorge ce matin? ou Sentez-vous la gorge qui vous démange?",
            "En buvant de l'eau, sentez-vous un mal de gorge?",
            "Avez-vous voyagé hier?",
            "Remarquez-vous une sueur inhabituelle?",
            "Etes-vous enceinte?",
            "Quelle est votre tranche d'âge?",
            "Quelle est votre tranche de température corporelle actuellement?"
    };
    private String points[]= {
            "3; 0",
            "3; 0",
            "3; 0",
            "6; 0",
            "6; 0",
            "6; 0",
            "3; 0",
            "14; 0; 0",
            "6; 0",
            "10; 0",
            "3; 0",
            "6; 0",
            "4; 0",
            "5; 0",
            "3; 0",
            "3; 0; 0",
            "2; 3; 5",
            "2; 4 ; 6"
    };
    private String rep[]={
            "Oui; Non",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non; Je n' sais pas",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non",
            "Oui; Non; Non applicable",
            "0-30 ans; 31-50 ans; 51ans et plus",
            "36 - 38 °C; 38 - 39.5°C ; 39.5°C et plus"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NotificationManager nm  = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(9);
        tv=(TextView)findViewById(R.id.pos);
        res=(TextView)findViewById(R.id.res);
        cliq=(TextView)findViewById(R.id.cliq);
        start=(Button )findViewById(R.id.valider);
        affich=(Termo)findViewById(R.id.thermometer);
        NOM= getIntent().getExtras().getString("name", "");

        Date date =new Date();
        SimpleDateFormat sdf= new SimpleDateFormat("dd-MMM-yyyy", Locale.CANADA_FRENCH);
        today= sdf.format(date).replace(".", "");
        getQuests();
        tv.setText("Test en date du "+today);
        start.setText("DEMARRER POUR AUJOURD'HUI");
        int ran1= (new Random()).nextInt(30);
        int ran2= (new Random()).nextInt(50);
        int ran3= (new Random()).nextInt(60);
        if(ran1%2==0){
            questions[4]="Avez-vous remarqué des rougeurs ou lésions au niveau de l'arrière de vos mains ou pieds ?";
        }

        if(ran2%2==0) {
            questions[0]="Sentez-vous des maux de tête actuellement? (ou céphalées parfois intenses)";
        }

        if(ran3%2==0){
            questions[12]="Sentez-vous le rhume ou le nez qui coule (même mineur) ? ";
            rhume =true;
        }


    }

    boolean rhume=false, isNewDiare=false; boolean forced=false;

    private void alertQuests(int size) {
        forced=true;
        AlertDialog al= new AlertDialog.Builder(this)
                .setTitle("Bonjour "+NOM+"!")
                .setMessage("Jour de test "+day+"\nAujourd'hui, vous n'aurez que "+size+" questions. Demain, vous en aurez peut-être moins, en fonction de vos résulats." +
                        "\n\nMerci de contribuer vivement à la maitrise de la propagation du coronavirus ! \nSoyez bénis.\n\nAppuyez sur 'DEMARRER' pour commencer le test de ce jour "+today+"" +
                        "\n\n\tPour rappel, ce test ne remplace pas un test médical de la covid19, ni les recommandations des médécins.")
                .setPositiveButton("Ok, compris", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .create();
        al.show();
        al.setCanceledOnTouchOutside(false);
    }

    private void start_quest(final int[] quests) {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
        final View view= LayoutInflater.from(this).inflate(R.layout.quest, null, false);
        final TextView etape= (TextView)view.findViewById(R.id.pos);
        final TextView titre= (TextView)view.findViewById(R.id.quest);
        final RadioButton r1=(RadioButton)view.findViewById(R.id.r1);
        final RadioButton r2=(RadioButton)view.findViewById(R.id.r2);
        final RadioButton r3=(RadioButton)view.findViewById(R.id.r3);
        final Button valid=(Button)view.findViewById(R.id.valider);
        final int size= quests.length;
        final int[] pos = {0};
        final RadioGroup rg= (RadioGroup)view.findViewById(R.id.rg);
        rg.clearCheck();

        alertDialog.setTitle("Pour ce "+today);
        alertDialog.setView(view);
        if(forced)
            alertDialog.setCancelable(false);
        else
            alertDialog.setCancelable(true);

        //valid.setEnabled(false);

        next(titre, etape, r1,r2,r3,false, pos[0], quests, valid);


        final AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();//Toast.makeText(MapsActivity.this, total+"", Toast.LENGTH_SHORT).show();

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!r1.isChecked() && !r2.isChecked()&& !r3.isChecked()){
                    Toast.makeText(MapsActivity.this, "Vous devez cocher quelque chose !", Toast.LENGTH_SHORT).show();
                }else {
                    total=total+contributor;
                    pos[0]++;
                    if(size>=18) {
                        if(pos[0]==2||pos[0]==3||pos[0]==17) {
                            total_sum1=total_sum1+contributor;
                        }
                    }
                    if(pos[0] ==size-1)
                        valid.setText("Terminer");

                    rg.clearCheck();
                    //Toast.makeText(MapsActivity.this, total+"", Toast.LENGTH_SHORT).show();
                    if(pos[0] !=size)
                        next(titre, etape, r1, r2, r3, false, pos[0], quests, valid);
                    else {
                        handler.removeCallbacks(run);
                        affich.setmCurrentTemp(100-total);
                        res.setText(total+" % !");
                        cliq.setVisibility(VISIBLE);
                        if(current<total)
                            cliq.setText("Augmentation de "+(total-current)+"%\nVoir les conseils");
                        else if(current>total)
                            cliq.setText("Diminution de "+Math.abs(total-current)+"%\nVoir les conseils");
                        else
                            cliq.setText("Constance!\nRevoir les conseils");
                        ecrire("proba.txt", total+"");
                        cliq.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                conseils(total);
                            }
                        });

                        start.setText("FERMER");
                        start.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        });
                        dialog.dismiss();
                        sauver(total, total_sum1);
                    }
                    //r1.setChecked(false);r2.setChecked(false);r3.setChecked(false);
                }
            }
        });

    }
    int day=1;
    String last_dat="";

    private void sauver(int total, int total_sum1) {
        FirebaseAuth mAth= FirebaseAuth.getInstance();
        if(mAth.getCurrentUser()!=null) {
            final String online = mAth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("Users").child(online).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("Position")) {
                        String coordonne= dataSnapshot.child("Position").getValue().toString();
                        double lat = Double.parseDouble(coordonne.substring(0, coordonne.indexOf("#")));
                        double longit = Double.parseDouble(coordonne.substring((coordonne.indexOf("#")) + 1, coordonne.length()));
                        Geocoder geocoder = new Geocoder(MapsActivity.this);
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } if(!last_dat.equals(today) || last_dat.equals("")) {
            Map map = new HashMap();
            map.put("sum", total_sum1+"");
            map.put("proba", total+"");
            map.put("day", (day+1)+"");
            map.put("date", today);
            map.put("sueur", sueur);
            map.put("gorge", gorge);
            map.put("rhume", rhumes);
            map.put("diare", diare);
            map.put("maux_de_tete", maux_de_tete);
            map.put("fatigue", fatigue);
            DatabaseReference data= FirebaseDatabase.getInstance().getReference().child("Resultats");
            //FirebaseAuth mAth= FirebaseAuth.getInstance();
            if(mAth.getCurrentUser()!=null) {
                String online = mAth.getCurrentUser().getUid();
                data.child(online).updateChildren(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    }
                });
            }
        }else {
            Toast.makeText(this, "Les autres ne pourront pas répérer ce nouveau résultat de test car vous avez déjà fait un test aujourd'hui !", Toast.LENGTH_LONG).show();
        }
    }

    private void conseils(int total) {
        Intent intent= new Intent(this, Conseils.class);
        intent.putExtra("nom", NOM);
        intent.putExtra("total", total);
        startActivity(intent);
    }

    private Handler handler=new Handler(); private Runnable run;

    private void next(final TextView titre, TextView etape, final RadioButton r1, final RadioButton r2, final RadioButton r3, boolean b, final int pos, final int[] quests, final Button valid) {
        etape.setText("Etape "+((pos)+1)+"/"+quests.length);

        r1.setVisibility(View.GONE);
        r2.setVisibility(View.GONE);
        final String[] p1 = {"0"};
        final String[] p2 = { "0" };
        final String[] p3 = { "0" };

        run= ((new Runnable(){
            @Override
            public void run() {
                r1.setVisibility(VISIBLE);
                r2.setVisibility(VISIBLE);
                titre.setText(questions[quests[pos]]);
                String reponse= rep[quests[pos]];
                String pt= points[quests[pos]];
                int type=1;

                for (int i = 0; i < reponse.length(); i++) {
                    if(reponse.charAt(i)==';')
                        type++;
                }
                if(type==2) {
                    r3.setVisibility(View.GONE);
                    r1.setText("Oui");
                    r2.setText("Non");
                    p1[0] =pt.substring(0, pt.indexOf(";")).trim();
                    p2[0] =pt.substring(pt.lastIndexOf(";")+1).trim();
                } else if(type==3) {
                    r3.setVisibility(VISIBLE);
                    reponse=reponse.replaceAll(";", "@");
                    String positif= reponse.substring(0, reponse.indexOf("@"));
                    String negatif= reponse.substring(reponse.indexOf("@")+1, reponse.lastIndexOf("@"));
                    String neutre= reponse.substring(reponse.lastIndexOf("@")+1);
                    r1.setText(positif); r2.setText(negatif); r3.setText(neutre);
                    p1[0] =pt.substring(0, pt.indexOf(";")).trim();
                    p2[0] =pt.substring(pt.indexOf(";")+1, pt.lastIndexOf(";")).trim();
                    p3[0] =pt.substring(pt.lastIndexOf(";")+1).trim();
                }



                r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            contributor= Integer.parseInt( p1[0]);
                        }
                        if(quests[pos]==0)
                            maux_de_tete="oui";
                        if(quests[pos]==3)
                            fatigue= "oui";
                        if(quests[pos]==12 && rhume)
                            rhumes= "oui";
                        if(quests[pos]==11)
                            gorge= "oui";
                        if(quests[pos]==14)
                            sueur= "oui";
                        if(isNewDiare){
                            if(quests[pos]==8)
                                diare= "non";
                        }else{
                            if(quests[pos]==8)
                                diare= "oui";
                        }
                        /*else{
                            if(! r2.isChecked() && !r3.isChecked()){
                                contributor=-1;
                                valid.setEnabled(false);
                            }else
                                valid.setEnabled(true);
                        }*/
                             }
                });
                r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            contributor= Integer.parseInt(p2[0]);
                        }

                        if(quests[pos]==0)
                            maux_de_tete="non";
                        if(quests[pos]==3)
                            fatigue= "non";
                        if(quests[pos]==12 && rhume)
                            rhumes= "non";
                        if(quests[pos]==11)
                            gorge= "non";
                        if(quests[pos]==14)
                            sueur= "non";
                        if(isNewDiare){
                            if(quests[pos]==8)
                                diare= "oui";
                        }else{
                            if(quests[pos]==8)
                                diare= "non";
                        }
                    }
                });
                r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            contributor= Integer.parseInt(p3[0]);
                        }/*else{
                            if(! r1.isChecked() && !r2.isChecked()){
                                contributor=-1;
                                valid.setEnabled(false);
                            }else
                                valid.setEnabled(true);

                        }*/
                    }
                });
            }
        }));

        if(pos==0)
            handler.post(run);
        else
            handler.postDelayed(run, 200);

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
    public void getQuests(){
        DatabaseReference data= FirebaseDatabase.getInstance().getReference().child("Resultats");
        FirebaseAuth mAth= FirebaseAuth.getInstance();
        if(mAth.getCurrentUser()!=null) {
            String online= mAth.getCurrentUser().getUid();
            data.child(online).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int [] ques={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};

                    if(dataSnapshot.hasChild("sum")){
                        String sum= dataSnapshot.child("sum").getValue().toString();
                        total = Integer.parseInt(sum);
                        total_sum1=total;
                        int sec= (new Random()).nextInt(60);
                        int min= (new Random()).nextInt(60);
                        int ran= (new Random()).nextInt(30);
                        if(sec>ran){
                            ques = new int[]{4, 7, 0, 5, 6, 3, 8, 9, 10, 11, 12, 13, 14, 15, 17};
                        }else if(sec+ran< 30){
                            ques=new int[]{11,13,9,5,10,3,8,0,6,4,12,7,14,15,17};
                        }else if(sec%2==0){
                            ques=new int[]{0,8,3,5,10,9,13,12,14,4,11,7,6,15,17};
                        }else if( ran %2==0){
                            ques=new int[]{3,7,0,13,10,4,5,12,9,6,11,8,14,15,17};
                        }else if(min%2==0){
                            ques=new int[]{4,9,3,8,5,0,13,12,11,6,7,10,14,15,17};
                        }else {
                            ques=new int[]{0,3,4,5,6,7,8,9,10,11,12,13,14,15,17};
                        }
                    }
                    if(dataSnapshot.hasChild("proba")) {
                        current= Integer.parseInt(dataSnapshot.child("proba").getValue().toString());
                    }
                    if(dataSnapshot.hasChild("day")) {
                        day= Integer.parseInt(dataSnapshot.child("day").getValue().toString());
                    }
                    if(dataSnapshot.hasChild("date")) {
                        last_dat=  (dataSnapshot.child("date").getValue().toString());
                    }

                    final int[] finalQues = ques;

                    start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            start_quest(finalQues);
                        }
                    });

                    affich.setmCurrentTemp(100-current);
                    res.setText(current+"%");
                    regler_mise_a_jour(dataSnapshot);
                    if(!last_dat.equals(today)) {
                        alertQuests(finalQues.length);
                    }else {
                        AlertDialog al=new AlertDialog.Builder(MapsActivity.this)
                                .setMessage("Vous avez déjà effectué un test aujourd'hui; Cliquez sur Demarrer pour essayer un test partiel.\n\nLes changements de questions s'effectueront demain.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create();
                        al.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getExtras().getString("forced")==null)
            super.onBackPressed();
        else {
            if(!lire_le_fichier("proba.txt").isEmpty()){
                super.onBackPressed();
            }else {
                Toast.makeText(this, "Faut devez faire le test pour une 1ère fois !", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void regler_mise_a_jour(DataSnapshot dataSnapshot){
        if(dataSnapshot.hasChild("maux_de_tete")){
            String maux_de_tete = dataSnapshot.child("maux_de_tete").getValue().toString();
            if(maux_de_tete.equals("oui"))
                questions[0]="Vos maux de tête de la fois dernière ont-ils persistés?";
        }

        if(dataSnapshot.hasChild("fatigue")){
            String fatigue = dataSnapshot.child("fatigue").getValue().toString();
            if(fatigue.equals("oui"))
                questions[3]="Remarquez-vous toujours la fatigue inhabituelle ?";
        }


        if(dataSnapshot.hasChild("diare")){
            String diare = dataSnapshot.child("diare").getValue().toString();
            if(diare.equals("oui")) {
                isNewDiare=true;
                questions[8] = "La diarrhee signalée la fois dernière a t-elle regressée ?";
                String p8=points[8];
                StringBuilder n8= new StringBuilder();
                for (int i = 0; i < p8.length(); i++) {
                    n8.append(p8.charAt(p8.length() - i - 1));
                }
                points[8]= n8.toString();
            }
        }

        if(rhume) {
            if(dataSnapshot.hasChild("rhume")){
                String rhume = dataSnapshot.child("rhume").getValue().toString();
                if(rhume.equals("oui"))
                    questions[12] = "Votre nez coule t-il toujours? Ou sentez-vous toujours le rhume?";
            }
        }

        if(dataSnapshot.hasChild("gorge")){
            String gorge = dataSnapshot.child("gorge").getValue().toString();
            if(gorge.equals("oui"))
                questions[11] = "Votre mal de gorge persiste t-il toujours?";
        }

        if(dataSnapshot.hasChild("sueur")){
            String sueur = dataSnapshot.child("sueur").getValue().toString();
            if(sueur.equals("oui"))
                questions[14] = "Avez-vous toujours une sueur inhabituelle ?";
        }
    }
}
