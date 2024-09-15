package com.vibeviroma.vaincrelemal229;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class List_Contact extends AppCompatActivity {

    private String list_phone[], new_list[], other_list[];
    private ListView recycler;
    private DatabaseReference dat_ref;
    private int niv=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__contact);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle("Contacts "+getString(R.string.app_name));

        recycler = (ListView) findViewById(R.id.list_item);

        //Toast.makeText(this, "Contacts non visibles actuellement !", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            load_contact();
        }
        dat_ref= FirebaseDatabase.getInstance().getReference().child("Contacts");
        dat_ref.keepSynced(true);


        //new_list[0]="rien\nmongar";


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void load_contact() {
        String per[] = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(per, 10);
        } else {
            ProgressDialog p = new ProgressDialog(this);
            p.setMessage("Chargement contacts...");
            p.setCanceledOnTouchOutside(false);
            p.show();
            ContentResolver contentResolver = getContentResolver();
            if(contentResolver==null)
                return;
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if(cursor==null)
                return;
            int existe1 = cursor.getCount();
            if (existe1 > 0) {
                final int[] level = {0};
                list_phone = new String[existe1*2];

                while (cursor.moveToNext()) {
                    int has_phone_number = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    String name = (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                    String id=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    if(name==null)
                        continue;
                    list_phone[level[0]] = name;
                    if (has_phone_number > 0) {
                        Cursor cursor1 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
                                    new String[]{id},
                                    null);
                        if(cursor1==null)
                            continue;
                        while ( cursor1.moveToNext() ) {
                            final String cont = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            {
                                if(!ne_contient_pas(list_phone, cont)) {
                                    list_phone[level[0]] += "\n" + cont;
                                    level[0]++;
                                    Log.d("The_contact", name + "\t" + cont);
                                }
                            }
                        }
                        cursor1.close();
                    }
                }
                    p.dismiss();

                cursor.close();
                verifie(list_phone);

            } else {
                p.dismiss();
                Toast.makeText(this, "Aucun Contact", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private boolean ne_contient_pas(String[] list_phone, String cont) {
        boolean result=false;
        for (int i = 0; i < list_phone.length; i++) {
            if(list_phone[i]==null){
                break;
            }
            if(list_phone[i].contains("\n"+cont)){
                result=true;
                break;
            }
        }
        return result;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==10){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED) {
               load_contact();
            }else {
                finish();
                Toast.makeText(this, "La permission doit être accordée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void verifie(final String[] list_phone){
        final ProgressDialog p = new ProgressDialog(this);
        p.setMessage("Chargement...");
        p.setCanceledOnTouchOutside(false);
        p.show();
        int pos=0;
        for (int j = 0; j < list_phone.length; j++) {
            if (list_phone[j]!=null && list_phone[j].contains("\n")) {
                pos++;
            } else if(list_phone[j]==null)
                break;
        }
        other_list= new String[pos];
        final String[] inter =new String[pos];
        pos=0;
        for (int j = 0; j < list_phone.length; j++) {
            if (list_phone[j]!=null && list_phone[j].contains("\n")) {
                final String nom=list_phone[j].substring(0, list_phone[j].indexOf('\n'));;
                final String cont= list_phone[j].substring(list_phone[j].lastIndexOf('\n')+1);
                inter[pos]=cont;
                other_list[pos]= nom;
                pos++;
            } else if(list_phone[j]==null)
                break;
        }
        if(inter.length==0)
            p.dismiss();

        DatabaseReference data=FirebaseDatabase.getInstance().getReference().child("Users");
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                p.dismiss();
                if(data.getChildrenCount()==0){
                    Toast.makeText(List_Contact.this, "Vous n'avez aucun contact sur "+R.string.app_name, Toast.LENGTH_SHORT).show();
                }
                String[] integral=new String[list_phone.length];
                int the_pos=0;
                for (DataSnapshot dataSnapshot:
                        data.getChildren()) {
                    //String key=dataSnapshot.getKey();
                    if(dataSnapshot.hasChild("Phone")){
                        String contact= dataSnapshot.child("Phone").getValue().toString();
                        Log.d("The_contact@@@", contact)  ;
                        for (int i = 0; i < inter.length; i++) {
                            if(inter[i].equals(contact)||contact.contains(inter[i])){
                                if(other_list[i].equals("null")){
                                    if(dataSnapshot.hasChild("Prenom"))
                                        other_list[i]=dataSnapshot.child("Prenom").getValue().toString() +" "+dataSnapshot.child("Nom").getValue().toString().charAt(0)+".";
                                }
                                integral[the_pos]=other_list[i]+"\n"+inter[i];
                                the_pos++;
                            }
                        }
                    }
                }
                int all_total=0;
                for (int i = 0; i < integral.length; i++) {
                    if(integral[i]!=null)
                        all_total++;
                }
                String[] copie=new String[all_total];
                for (int i = 0; i < all_total; i++) {
                    if(integral[i]!=null)
                        copie[i]=integral[i];
                }

                CustomAdapter c= new CustomAdapter(LayoutInflater.from(List_Contact.this), copie, all_total, List_Contact.this);
                recycler.setAdapter(c);
                p.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    public String  refaire(int po, String[] inter, String elem){

        String res="";
        String indicatif_elem=elem.substring(0, 3).replace('-', '+');
        elem=elem.substring(4, elem.length());

        for (int i = 0; i < po; i++) {
            String nom=inter[i].substring(0, inter[i].indexOf('\n'));
            String num=inter[i].substring(inter[i].indexOf('\n')+1, inter[i].length());

            String sans_plus=num.substring(num.length()-8, num.length());
            if(elem.equals(sans_plus)) {
                if(num.substring(0,3).equals("+229")||num.substring(0,5).equals("00229")){
                    if(indicatif_elem.equals("+229")){
                        res=nom+"\n+229"+elem;
                    }
                }else if(num.substring(0,3).equals("+228")||num.substring(0,5).equals("00228")) {
                    if(indicatif_elem.equals("+228")){
                        res=nom+"\n+228"+elem;
                    }
                }else {
                    res=nom+"\n"+elem;
                }
                break;
            }
        }

        return res;
    }




}