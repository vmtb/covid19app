package com.vibeviroma.vaincrelemal229;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Infos_Inscr extends AppCompatActivity {

    private String name, prenom, EMAIL, PASSWORD;
    private FirebaseAuth mAuth;
    private EditText n, p, phon1, phon2;
    private Button save;
    private ImageView imageView;
    private int code=2, img=1;
    private Uri uri_img;
    private Spinner spinner;
    private String indicatif[]= {"+229, Bénin", "+228, Togo", "Autre, à préciser dans le contact"};
    private String ind="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos__inscr);
        n=(EditText)findViewById(R.id.n);
        p=(EditText)findViewById(R.id.p);
        phon1=(EditText)findViewById(R.id.p1);
        phon2=(EditText)findViewById(R.id.p2);
        spinner=(Spinner)findViewById(R.id.ind);
        save=(Button)findViewById(R.id.valider);
        name=getIntent().getExtras().getString("name");
        prenom=getIntent().getExtras().getString("prenom");
        imageView=(ImageView)findViewById(R.id.image);
        ArrayAdapter a= new ArrayAdapter(this, android.R.layout.simple_spinner_item, indicatif);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(a);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("fr");
        EMAIL= lire_le_fichier("email.txt");
        PASSWORD=lire_le_fichier("passw.txt");
        if(!name.equals("null")){
           n.setText(name);
            p.setText(prenom);
        }
        ind=indicatif[0].substring(0, indicatif[0].indexOf(','));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ind=indicatif[position].substring(0, indicatif[position].indexOf(','));
                /*if(position==0){
                    phon1.setI(8);
                }else if(position==1){
                    phon1.setFadingEdgeLength(8);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom=n.getText().toString(), prenom=p.getText().toString(), pho1=phon1.getText().toString(), pho2=phon2.getText().toString();
                if(nom.isEmpty()||estEspace(nom))
                    Toast.makeText(Infos_Inscr.this, "Nom invalide", Toast.LENGTH_SHORT).show();
                else if(prenom.isEmpty()||estEspace(prenom))
                    Toast.makeText(Infos_Inscr.this, "Prénom invalide", Toast.LENGTH_SHORT).show();
                  /* else if(contient_fausse(nom))
                    Toast.makeText(Infos_Inscr.this, "Le nom contient des caractères invalides \'*, /, +, -...\'", Toast.LENGTH_SHORT).show();
                else if(contient_fausse(prenom))
                    Toast.makeText(Infos_Inscr.this, "Le prénom contient des caractères invalides \'*, /, +, -...\'", Toast.LENGTH_SHORT).show();
             else*/
                  if(pho1.isEmpty()||estEspace(pho1))
                    Toast.makeText(Infos_Inscr.this, "Vous devez ajouter au moins 1 numéro de téléphone", Toast.LENGTH_SHORT).show();
             else {
                 if(pho1.length()==8 && ind.startsWith("+"))
                     pho1=ind+""+pho1;
                    /*if(!pho1.isEmpty()) {
                        if ((pho1.length() <= 7) || contient_etoile(pho1)) {
                            Toast.makeText(Infos_Inscr.this, "Le numéro " + pho1 + "est invalide", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    }
                    if (!pho2.isEmpty()){
                        if ((pho2.length() <= 7) || contient_etoile(pho2)){
                            Toast.makeText(Infos_Inscr.this, "Le numéro " + pho2 + "est invalide", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }*/
                    save_all(nom, prenom, pho1, pho2);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, code);*/
                Toast.makeText(Infos_Inscr.this, "Non possible actuellement !", Toast.LENGTH_SHORT).show();
            }
        });
    }
    String NOM="", PRENOM="";

    private void save_all(final String nom, final String prenom, final String pho1, final String pho2) {
        final ProgressDialog p= new ProgressDialog(this);
        p.setTitle("Sauvegarde");
        p.setMessage("Veuillez patienter");
        p.setCancelable(false);
        p.setCanceledOnTouchOutside(false);

        final String online=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        DatabaseReference dat_ref= FirebaseDatabase.getInstance().getReference().child("Users").child(online);
        AlertDialog a= new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Voulez-vous vraiment vous inscrire avec ces renseignements ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StorageReference sto= FirebaseStorage.getInstance().getReference().child("Profiles");
                        if(img==1){
                            NOM=nom;
                            PRENOM=prenom;

                            changer_num(pho1);
                           // save(nom, prenom, pho1, pho2, "null");
                        }else {
                            sto.child(online+".jpg").putFile(uri_img).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        save(nom, prenom, pho1, pho2, task.getResult().toString());
                                    }else {
                                        p.dismiss();
                                        Toast.makeText(Infos_Inscr.this, "Erreur lors du téléchargement de la photo d'identification", Toast.LENGTH_SHORT).show();
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
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        a.show();
    }


    private void changer_num(String phone) {

        View v= LayoutInflater.from(this).inflate(R.layout.phone, null);
        compteur = (TextView) v.findViewById(R.id.compt);
        phone__ = (EditText) v.findViewById(R.id.mp_connecter);
        conf = (EditText) v.findViewById(R.id.code);
        verif =(Button)v.findViewById(R.id.btn_time);
        conf.setVisibility(View.GONE);
        compteur.setVisibility(View.GONE);
        phone__.setText(phone);
        verif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Réessayer plus tard", Snackbar.LENGTH_SHORT).show();
            }
        });
        if(state==0) {
            conf.setVisibility(View.GONE);
            compteur.setVisibility(View.GONE);
            verif.setText("VERIFIER");
            verif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contact= phone__.getText().toString().trim();
                    if(contact.isEmpty())
                        Toast.makeText(Infos_Inscr.this, "Contenu vide", Toast.LENGTH_SHORT).show();
                    else {
                        ProgressDialog pdial= new ProgressDialog(Infos_Inscr.this);
                        pdial.setCanceledOnTouchOutside(false);;
                        pdial.setMessage("Verification");
                        pdial.show();
                        sendVerifi(contact, pdial);
                        phone__.setText(contact);
                        phon1.setText(contact);
                    }
                }
            });
        }

        dialog =new AlertDialog.Builder(this)
                .setTitle("Numéro + indicatif")
                .setView(v)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


    }


    private void save(final String nom, final String prenom, String pho1, String pho2, final String lien){
        final ProgressDialog pp= new ProgressDialog(this);
        pp.setTitle("Sauvegarde");
        pp.setMessage("Veuillez patienter");
        pp.setCancelable(false);
        pp.setCanceledOnTouchOutside(false);
        pp.show();
        String online=FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Toast.makeText(this, online+"", Toast.LENGTH_SHORT).show();
        DatabaseReference dat_ref= FirebaseDatabase.getInstance().getReference().child("Users").child(online);


        final String finalPho = pho1;
        final String finalPho1 = pho2;
        Map map= new HashMap();
        map.put("Prenom", prenom);
        map.put("Nom", nom);
        map.put("Phone", pho1);
        map.put("Mail", EMAIL);
        map.put("Contacts", pho1+"@"+pho2);
        map.put("Photo" , lien);
        map.put("Indicatif", ind);

        dat_ref.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                pp.dismiss();
               {
                    ecrire("tlocale_my_ref.txt", nom+"\n"+prenom+"\nnull\n"+ finalPho +"\n"+ finalPho1);
                    Toast.makeText(Infos_Inscr.this, "Sauvegarde reussie !", Toast.LENGTH_SHORT).show();
                    startService(new Intent(Infos_Inscr.this, MapsActivity2.class));
                    Intent intent = new Intent(Infos_Inscr.this, MainActivity.class);
                    intent.putExtra("new", prenom);
                    startActivity(intent);
                    finish();
                }
            };
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

    public boolean contient_etoile(String text){
        boolean est= false;
        for (int i = 0; i < text.length(); i++) {
            if((text.charAt(i)!='1')&&(text.charAt(i)!='2')&&(text.charAt(i)!='3')&&(text.charAt(i)!='4')&&(text.charAt(i)!='5')
                    &&(text.charAt(i)!='6')&&(text.charAt(i)!='7')&&(text.charAt(i)!='8')&&(text.charAt(i)!='9')&&(text.charAt(i)!='0')){
                est=true;
                break;
            }
        }
        return  est;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==code){
            if(resultCode==RESULT_OK){
                try {
                    FileOutputStream fileOutputStream = openFileOutput("prof_loc.txt", MODE_PRIVATE);
                    fileOutputStream.write(data.getDataString().getBytes());
                    Toast.makeText(this, data.getDataString()+"", Toast.LENGTH_LONG).show();
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                uri_img=data.getData();

                imageView.setImageURI(uri_img);

                img=3;
            }
        }
    }


    public boolean contient_fausse(String text){
        boolean est= false;
        for (int i = 0; i < text.length(); i++) {
            if(((text.charAt(i)<65)&&(text.charAt(i)!=32))||(text.charAt(i)>122)||((text.charAt(i)>=91)&&(text.charAt(i)<=96))){
                est=true;
                break;
            }
        }
        return  est;
    }

    EditText phone__, conf; TextView compteur;
    Button verif; int state=0;
    String vrai_contact="";
    private String veerif_id;


    private void sendVerifi(final String email, final ProgressDialog pdial) {
        handler= new Handler();
        compteur.setVisibility(View.VISIBLE);
        dialog.setCanceledOnTouchOutside(false);
        PhoneAuthProvider.OnVerificationStateChangedCallbacks changedCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                vrai_contact=email;
                pdial.dismiss();
                state=1;
                Toast.makeText(Infos_Inscr.this, "Message détecté automatiquement !", Toast.LENGTH_LONG).show();
                Inscription(pdial);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                pdial.dismiss();
                AlertDialog alertDialog= new AlertDialog.Builder(Infos_Inscr.this)
                        .setMessage("Echec de la vérification du numéro de téléphone...! Vérifiez le numéro (et l'indicatif) et votre connexion internet")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                alertDialog.show();

            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                veerif_id=s;
                Log.d("wechat_code", s);
                pdial.dismiss();
                conf.setVisibility(View.VISIBLE);
                phon1.setEnabled(false);
                verif.setText("IDENTIFIER");
                dialog.show();
                verif.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String code= conf.getText().toString().trim();
                        if( code.isEmpty()) {
                            Toast.makeText(Infos_Inscr.this, "Saisissez le code", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        pdial.setMessage("Vérification...");
                        pdial.show();
                        handler.removeCallbacks(rn);
                        PhoneAuthCredential p= PhoneAuthProvider.getCredential(s, code);
                        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
                        mAuth.signInWithCredential(p)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        pdial.dismiss();

                                        if(task.isSuccessful()){
                                            dialog.dismiss();
                                            mAuth.signOut();
                                            state=1;
                                            vrai_contact=email;
                                            Inscription(pdial);
                                        } else {
                                            Toast.makeText(Infos_Inscr.this, "Une erreur s'est produite... Les codes ne correspondent pas ou le code est expiré !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                Toast.makeText(Infos_Inscr.this, "Un code de vérification a été envoyé sur ce numéro ! Veuillez bien saisir ce code !", Toast.LENGTH_LONG).show();
            }
        };


        PhoneAuthProvider.getInstance().verifyPhoneNumber (
                email,
                70,
                TimeUnit.SECONDS,
                this,
                changedCallbacks
        );
        total=70;
        pdial.setMessage("Envoi de code de vérification en cours...");
        pdial.show();
        compter(compteur);
        handler.post(rn);

    }

    private void Inscription(final ProgressDialog progressDialog) {
        progressDialog.setMessage("Mise à jour ...");
        mAuth.signInWithEmailAndPassword(EMAIL, PASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    save(NOM, PRENOM, vrai_contact, "", "null");
                }
            }
        });
    }

    AlertDialog dialog;

    private Handler handler;
    private Runnable rn; int total=70;


    void compter(final TextView view){
        total=70;
        rn= new Runnable() {
            @Override
            public void run() {
                if(dialog.isShowing()) {
                    view.setText("Expire dans "+(total--)+" s");
                    handler.postDelayed(rn, 1000);
                    if(total==0) {
                        handler.removeCallbacks(rn);
                        Toast.makeText(Infos_Inscr.this, "Désolé, le code est expiré !", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                    }
                } else {
                    handler.removeCallbacks(rn);
                }
            }
        };
    }


}
