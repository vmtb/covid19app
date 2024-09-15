package com.vibeviroma.vaincrelemal229;

 import android.Manifest;
 import android.app.Activity;
 import android.app.AlertDialog;
 import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
 import android.content.pm.PackageManager;
 import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.core.app.ActivityCompat;

 import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

 public class Profile extends AppCompatActivity {

     private TextView nom, loc, pos, pp1, pp2, cont;
     private ImageView img;
     private CheckBox checkBox;
     private String n,l, p,ph1, ph2;
     private String local_string="ma_position_e_t_r_s_r_l.txt";
     private DatabaseReference databaseReference;
     private static int code=5;
     private final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
     private final int all_permissions = 1;

     public static Activity ACTIVITY_PROFILE;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nom=(TextView)findViewById(R.id.name);
        loc=(TextView)findViewById(R.id.coor);
        pos=(TextView)findViewById(R.id.pos);
        pp1=(TextView)findViewById(R.id.p1);
        pp2=(TextView)findViewById(R.id.p2);
        cont=(TextView)findViewById(R.id.contact);
        checkBox=(CheckBox)findViewById(R.id.cb);
        img=(ImageView)findViewById(R.id.img);
        n=getIntent().getExtras().getString("nom","null");
        ph1=getIntent().getExtras().getString("phone1","null");
        ph2=getIntent().getExtras().getString("phone2","null");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        verification_map();
        ACTIVITY_PROFILE=this;

        if(ph1.equals("null"))
            pp1.setText("Phone 1: Aucun");
        else
            pp1.setText("Phone 1: "+ph1);

        if(ph2.equals("null"))
            pp2.setText("Phone 2: Aucun");
        else
         pp2.setText("Phone 2: "+ph2);

         nom.setText(n);
         cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, List_Contact.class));
             }
        });


         databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 if(dataSnapshot.hasChild("Conf")) {
                     if(dataSnapshot.child("Conf").getValue().toString().equals("auth")){
                         ecrire("conf_loc.txt", "auth");
                         //checkBox.setChecked(false);
                     }else {
                         ecrire("conf_loc.txt", "non");
                        // checkBox.setChecked(true);
                     }
                 }   else {
                     ecrire("conf_loc.txt", "auth");
                    // checkBox.setChecked(false);
                 }
                 verifie();

             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
         //verifie();
         img.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View v) {
                 CharSequence c[]= new CharSequence[]{"Changer la photo d'identification"};
                 AlertDialog a= new AlertDialog.Builder(Profile.this)
                         .setItems(c, new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 if(which==0){
                                    /* Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                     i.setType("image/*");
                                     startActivityForResult(i, code);*/
                                     Toast.makeText(Profile.this, "Cette fonctionnalité n'est disponible qu'en version premium", Toast.LENGTH_SHORT).show();
                                 }
                             }
                         }).create();
                 a.show();
                 return false;
             }
         });

         if(!lire_le_fichier("prof_loc.txt").equals("")) {
             try {
              //  img.setImageURI(Uri.parse(lire_le_fichier("prof_loc.txt")));
             } catch (Exception e) {
                 e.printStackTrace();
                 Toast.makeText(this, "La photo d'identification a été supprimée ou peut-être déplacée", Toast.LENGTH_SHORT).show();
             }
         }   else
                 Toast.makeText(this, "Aucune photo d'identification n'est trouvée dans dans votre galerie", Toast.LENGTH_SHORT).show();

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
     private void verification_map(){
         String coordonne=lire_le_fichier(local_string);
         if (!coordonne.equals("")){
             Toast.makeText(this, ph1+"-"+ph2, Toast.LENGTH_SHORT).show();
             double lat= Double.parseDouble(coordonne.substring(0, coordonne.indexOf("#")));
             double longit= Double.parseDouble(coordonne.substring((coordonne.indexOf("#"))+1, coordonne.length()));
             Geocoder geocoder= new Geocoder(this);
             String poss="";
             loc.setText("Coordonnées: ("+lat+" , "+longit+")");
             try {
                 List<Address> adress=geocoder.getFromLocation(lat, longit, 1);
                 poss=adress.get(0).getLocality();

                 pos.setText("Position actuelle: "+poss);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }else {
             loc.setText("Introuvable");
             pos.setText("Introuvable");
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
     public void verifie() {

         if((lire_le_fichier("conf_loc.txt").equals(""))||(lire_le_fichier("conf_loc.txt").equals("auth")))
             checkBox.setChecked(false);
         else
             checkBox.setChecked(true);

         checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(isChecked) {
                     databaseReference.child("Conf").setValue("non").addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()){
                                 ecrire("etat.txt", "false");
                                 stopService(new Intent(Profile.this, MapsActivity2.class));
                                 ecrire("conf_loc.txt", "non");
                                 Toast.makeText(Profile.this, "Paramètre changé avec succès", Toast.LENGTH_SHORT).show();
                             }else {
                                 ecrire("conf_loc.txt", "auth");
                                 Toast.makeText(Profile.this, "Echec du changement du PARAMETRE DE CONFIDENTIALITE", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                 }   else   {
                     databaseReference.child("Conf").setValue("auth").addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful()) {
                                 ecrire("etat.txt", "true");
                                 if (ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                     ActivityCompat.requestPermissions(Profile.this, permissions, all_permissions);
                                 }else
                                     startService(new Intent(Profile.this, MapsActivity2.class));

                                 ecrire("conf_loc.txt", "auth");
                                 Toast.makeText(Profile.this, "Paramètre changé avec succès", Toast.LENGTH_SHORT).show();
                             }else {
                                 ecrire("conf_loc.txt", "non");
                                 Toast.makeText(Profile.this, "Echec du changement du PARAMETRE DE CONFIDENTIALITE", Toast.LENGTH_SHORT).show();
                             }
                         }
                     });
                 }

             }
         });


     }
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if(requestCode==code){
             if(resultCode==RESULT_OK){
                 final ProgressDialog p = new ProgressDialog(this);
                 p.setCanceledOnTouchOutside(false);
                 try {
                     FileOutputStream fileOutputStream = openFileOutput("prof_loc.txt", MODE_PRIVATE);
                     fileOutputStream.write(data.getDataString().getBytes());
                     Toast.makeText(this, data.getDataString()+"", Toast.LENGTH_LONG).show();
                 }catch (FileNotFoundException e) {
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

                 Uri uri_img=data.getData();

                 try {


                     /*if (ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                         ActivityCompat.requestPermissions(Profile.this, permissions2, all_permissions2);
                     }else
                         img.setImageURI(Uri.parse(lire_le_fichier("prof_loc.txt")));*/
                 } catch (Exception e) {
                     Toast.makeText(this, "Aucune photo d'identification n'est trouvée dans dans votre galerie", Toast.LENGTH_SHORT).show();
                     e.printStackTrace();
                 }
                 String online= FirebaseAuth.getInstance().getCurrentUser().getUid();
                 StorageReference sto= FirebaseStorage.getInstance().getReference().child("Profiles");

                     sto.child(online+".jpg").putFile(uri_img).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                             if(task.isSuccessful()){
                                 Toast.makeText(Profile.this, "Photo changée avec succès..!", Toast.LENGTH_SHORT).show();
                             }else {
                                 p.dismiss();
                                 Toast.makeText(Profile.this, "Erreur lors du téléchargement de la photo d'identification", Toast.LENGTH_SHORT).show();
                             }
                         }
                     }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                         @Override
                         public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                             p.setTitle("Téléchargement");
                             p.setMessage(taskSnapshot.getBytesTransferred()/1000+" Ko / "+taskSnapshot.getTotalByteCount()/1000);
                         }
                     });

             }
         }
     }

     @Override
     protected void onResume() {
         super.onResume();
         verification_map();
     }


     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if(requestCode==all_permissions) {
             if (ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(Profile.this, permissions, all_permissions);
             }else
                 startService(new Intent(Profile.this, MapsActivity2.class));
         }else if(requestCode==all_permissions2){

             /*if (ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(Profile.this, permissions2, all_permissions2);
             }else
                 img.setImageURI(Uri.parse(lire_le_fichier("prof_loc.txt")));*/
         }
     }


     private final String [] permissions2 = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
     private final int all_permissions2 = 2;



 }
