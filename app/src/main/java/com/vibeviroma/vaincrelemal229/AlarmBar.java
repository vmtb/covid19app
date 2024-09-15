package com.vibeviroma.vaincrelemal229;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmBar extends BroadcastReceiver {
    Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {
        ctx=context;
        String name = intent.getExtras().getString("name", "");
        int ran = (new Random()).nextInt(10);
        if(ran%2==0)
        showNotif("Alerte coronavirus",
                "Nous espérons que vous lavez fréquemment les mains !",
                name,
                "La covid19 est déjà là parmi nous, nous devons déjà nous méfier de notre entourage immédiat ! #Stopcorona", false);
        else if(ran%3==0)
            showNotif("Alerte coronavirus",
                    "Nous espérons que vous limitez vos déplacements !!",
                    name,
                    "La covid19 est déjà là parmi nous, nous devons déjà nous méfier de notre entourage immédiat ! #Stopcorona", false);
        else
            showNotif("Alerte coronavirus",
                    "Nous espérons que vous respectez les gestes barrières !!",
                    name,
                    "La covid19 est déjà là parmi nous, nous devons déjà nous méfier de notre entourage immédiat ! #Stopcorona", false);


    }

    private String NC_ID="Notification_renseignement_covid19";

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

    public void showNotif (String title, String caption, String name, String info, boolean test){
        if(test) {
            Intent chatIntent = new Intent(ctx, MapsActivity.class);
            chatIntent.putExtra("name", name);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 1, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(caption)
                    .setContentInfo(info)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(1)
                    .setChannelId(NC_ID);
            NotificationManager nm = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            createDefChan(nm, name);
            nm.notify(9, builder.build());
        }else {
            Intent chatIntent = new Intent(ctx, MainActivity.class);
            chatIntent.putExtra("name", name);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 1, chatIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(title)
                    .setContentText(caption)
                    .setContentInfo(info)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(1)
                    .setChannelId(NC_ID);
            NotificationManager nm = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            createDefChan(nm, name);
            nm.notify(10, builder.build());
        }

    }

}
