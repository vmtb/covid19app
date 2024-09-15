package com.vibeviroma.vaincrelemal229;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MapsActivity2 extends Service {

    private final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int all_permissions = 1;
    private LocationManager locationManager;
    private String local_string="ma_position_e_t_r_s_r_l.txt";
    private String etat="etat.txt";
    private DatabaseReference data;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Localisation stoppée !", Toast.LENGTH_SHORT).show();
        {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                data = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Position");
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (((Build.VERSION.SDK_INT >= 16) && (!isPermissionGranted())) || (((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) != PackageManager.PERMISSION_GRANTED)) {
                    //ActivityCompat.requestPermissions(Profile.ACTIVITY_PROFILE,  permissions, all_permissions);
                } else if (!isLocationEnabled()) {
                    Toast.makeText(this, "Veuillez activer la localisation !", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent2);
                } else {
                    requestLocation();
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Localisation stoppée !", Toast.LENGTH_SHORT).show();
        ecrire(etat, "false");
    }

    public void requestLocation() {
        /*Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        //  criteria.setAltitudeRequired(true);
        criteria.setSpeedRequired(true);
        String provider = locationManager.getBestProvider(criteria, false);
        // if(locationManager.isProviderEnabled(provider)) {
  */      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double Latitudeuser = location.getLatitude();
                double Longitudeuser = location.getLongitude();
                data.setValue(Latitudeuser+"#"+Longitudeuser);
                if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                    FirebaseDatabase.getInstance().getReference().child("Resultats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Position").setValue(Latitudeuser+"#"+Longitudeuser);
                ecrire(local_string, Latitudeuser+"#"+Longitudeuser);
                onDestroy();
                //Toast.makeText(MapsActivity2.this, Latitudeuser+"#"+Longitudeuser, Toast.LENGTH_SHORT).show();
               /* LatLng mapositio = new LatLng(Latitudeuser, Longitudeuser);
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                List<Address> adress = null;
                try {
                    adress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
                    String str = adress.get(0).getLocality();
                    String st = adress.get(0).getCountryName();
                   *//* mMap.addMarker(new MarkerOptions().position(mapositio).title(str + ", " + st));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapositio, 13.2f));
                 *//*
                } catch (IOException e) {
                    e.printStackTrace();*/
                //   Toast.makeText(MapsActivity.this, "ERREUR, REESSAYER", Toast.LENGTH_SHORT).show();
                //     finish();

                //   mMap.addMarker(new MarkerOptions().position(mapositio).title("MA POSITION ACTUELLE"));
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapositio, 15.2f));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double Latitudeuser = location.getLatitude();
                double Longitudeuser = location.getLongitude();
                data.setValue(Latitudeuser+"#"+Longitudeuser);
                ecrire(local_string, Latitudeuser+"#"+Longitudeuser);
                /*if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    LatLng sydney = new LatLng(Latitudeuser, Longitudeuser);
                    Geocoder geocoder= new Geocoder(MapsActivity2.this);
                    String pos="";
                    try {
                        List<Address> adress=geocoder.getFromLocation(Latitudeuser, Longitudeuser, 1);
                        if(adress.size()>0)
                            pos=adress.get(0).getLocality();
                        if(pos!=null && !pos.isEmpty()){
                            FirebaseDatabase.getInstance().getReference().child("Resultats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Locality").setValue(pos);
                        }
                        if(adress.size()>0) {
                            String spo = adress.get(0).getSubLocality();
                            if (spo != null)
                                pos = adress.get(0).getLocality() + " ; " + spo;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    FirebaseDatabase.getInstance().getReference().child("Resultats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Position").setValue(Latitudeuser+"#"+Longitudeuser);
                }*/
                ecrire(local_string, Latitudeuser+"#"+Longitudeuser);
                onDestroy();

                //Toast.makeText(MapsActivity2.this, Latitudeuser+"#"+Longitudeuser, Toast.LENGTH_SHORT).show();
               /* LatLng mapositio = new LatLng(Latitudeuser, Longitudeuser);
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                List<Address> adress = null;
                try {
                    adress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
                    String str = adress.get(0).getLocality();
                    String st = adress.get(0).getCountryName();
                   *//* mMap.addMarker(new MarkerOptions().position(mapositio).title(str + ", " + st));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapositio, 13.2f));
                 *//*
                } catch (IOException e) {
                    e.printStackTrace();*/
                //   Toast.makeText(MapsActivity.this, "ERREUR, REESSAYER", Toast.LENGTH_SHORT).show();
                //     finish();

                //   mMap.addMarker(new MarkerOptions().position(mapositio).title("MA POSITION ACTUELLE"));
                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapositio, 15.2f));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
        // }else
        {
            //     Toast.makeText(this, "ERREUR DE FOURNISSEUR!!!!", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isLocationEnabled()
    {
        return ((locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                ||(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))));

    }

    private void showAlert(final int i)
    {
        String message, title, btn;
        if(i==1)
        {
            message="Votre GPS est désactivé! \n Activer votre GPS avant d'utiliser ce service";
            title="MON BILLET DE BUS";
            btn="Paramètres";
        }
        else
        {
            message="Vous n'avez pas accès à internet! Connecctez-vous";
            title="MON BILLET DE BUS";
            btn="Activer la connection";
        }
        AlertDialog alert=new AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(i==1)
                        {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }else
                        {
                            startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));

                        }
                    }
                })
                .setNegativeButton("Non Merci", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                })
                .setNeutralButton("Ignorer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                })
                .create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }

    private boolean isPermissionGranted()
    {
        if ((ActivityCompat.checkSelfPermission(MapsActivity2.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED))

        {
            return true;
        }else
            return false;
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
        String texto="true";
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





}
