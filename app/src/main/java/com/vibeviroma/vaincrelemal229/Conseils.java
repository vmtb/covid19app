package com.vibeviroma.vaincrelemal229;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView; //FFCA91FC

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Date;
import java.util.Random;

public class Conseils extends AppCompatActivity {
    TextView res, tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, gouv_t, oms_t;
    ImageView img1, img2, img3, img4, img5, img6, img7, img8, gouv_i, omg_i;
    CardView cd1, cd2, cd3, cd4, cd5, cd6, cd7, cd8, gouv, oms;
    private String NOM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conseils);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);
        res = (TextView) findViewById(R.id.res);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);
        img5 = (ImageView) findViewById(R.id.img5);
        img6 = (ImageView) findViewById(R.id.img6);
        img8 = (ImageView) findViewById(R.id.img8);
        img7 = (ImageView) findViewById(R.id.img7);

        cd1 = (CardView) findViewById(R.id.zone1);
        cd2 = (CardView) findViewById(R.id.zone2);
        cd3 = (CardView) findViewById(R.id.zone3);
        cd4 = (CardView) findViewById(R.id.zone4);
        cd5 = (CardView) findViewById(R.id.zone5);
        cd6 = (CardView) findViewById(R.id.zone6);
        cd7 = (CardView) findViewById(R.id.zone7);
        cd8 = (CardView) findViewById(R.id.zone8);

        int total =getIntent().getIntExtra("total",0);
        NOM= getIntent().getStringExtra("nom").trim();
        if(total<=0){
            res.setText("Aucun résultat de Test");
        }else {
            conseils(total);
        }

        gouv_t=(TextView)findViewById(R.id.gouv_t);
        gouv_i=(ImageView)findViewById(R.id.gouv_i);
        oms_t=(TextView)findViewById(R.id.oms_t);
        omg_i=(ImageView)findViewById(R.id.oms_i);
        gouv=(CardView)findViewById(R.id.gouv);
        oms=(CardView)findViewById(R.id.oms);

        loadFooter();

    }

    int[] sup50 = {R.drawable.restons, R.drawable.restons, R.drawable.restons, R.drawable.cache_nez, R.drawable.cache_nez, R.drawable.distance};
    String cons50[] = {"Vous devez vous mettre en auto-isolement !!", "Vous devez restez chez vous !!", "Vous devez absolument restez chez vous !", "N'oubliez de mettre votre masque médical de protection même à la maison", "N'oubliez pas votre masque de protection", "Maintenez-vous à 1m de distance des autres au min."};
    int[] inf6 = {R.drawable.wash_mains, R.drawable.distance, R.drawable.mains, R.drawable.tousser, R.drawable.cache_nez, R.drawable.restons};
    String cons6[] = {"Lavez-vous fréquemment les mains à l'eau et au savon ou avec une solution à base d'alcool",
            "N'oubliez pas de vous maintenir à 1m de distance au moins des autres. Soyez rigoureux sur cela.",
            "Toussez ou éternuez dans le creux du bras ou se couvrir la bouche et le nez avec un mouchoir en papier puis le jeter et se laver les mains",
            "Mettez toujours votre cache-nez ! N'oubliez pas de mettre votre masque médical de protection pour sortir", "Plutôt, vous pouvez opter pour un léger confinement en restant chez vous..."};
    int[] inf10 = {R.drawable.mains, R.drawable.wash_mains, R.drawable.cache_nez, R.drawable.distance, R.drawable.restons};
    String conso10[] = {"Evitez de saluer vos proches et autres en leur serrant la main", "Lavez-vous les mains à l'eau et au savon ou avec une solution à base d'alcool",
            "N'oubliez pas votre masque de protection pour sortir..! Mettez toujours votre cache-nez", "Maintenez-vous à 1m de distance au moins des autres. Soyez vraiment rigoureux sur cela.",
            "Plutôt que trop de mesures: RESTEZ CHEZ VOUS !"
    };
    int[] inf20 = {R.drawable.wash_mains, R.drawable.cache_nez, R.drawable.cracher, R.drawable.distance, R.drawable.tousser, R.drawable.restons};
    String conso20[] = {"Lavez-vous fréquemment les mains à l'eau et au savon ou avec une solution à base d'alcool", "Couvrez-vous toujours la bouche et le nez avec votre masque médical ",
            "Eviter de cracher et de se moucher au sol", "Maintenez-vous à 1m de distance de sécurité au moins des autres", "Toussez ou éternuez dans le creux du bras", "Ou carrément, restez-vous !"
    };
    int inf30[] = {R.drawable.restons, R.drawable.wash_mains, R.drawable.cracher, R.drawable.tousser, R.drawable.distance, R.drawable.cache_nez};
    String conso30[] = {"Vous devez commencer à rester chez vous !", "Lavez-vous fréquemment les mains à l'eau et au savon ou avec une solution à base d'alcool même à la maison",
            "Eviter de cracher et de se moucher au sol", "Toussez ou éternuez dans le creux du bras ou se couvrir la bouche et le nez avec un mouchoir en papier puis le jeter et se laver les mains",
            "Maintenez-vous à 1m de distance de sécurité au moins des autres",
            "N'oubliez de mettre votre masque médical de protection. Même à la maison, vous devez cacher votre bouche et votre nez !"};

    int infelse[] = {R.drawable.wash_mains, R.drawable.cache_nez, R.drawable.restons, R.drawable.tousser, R.drawable.cracher, R.drawable.distance,};

    String consoelse[] = {"Lavez-vous fréquemment les mains à l'eau et au savon ou avec une solution à base d'alcool ", "N'oubliez de mettre votre masque médical de protection même à la maison",
            "Vous pouvez déjà opter pour un auto-confinement !", "Toussez ou éternuez dans le creux du bras ou se couvrir la bouche et le nez avec un mouchoir en papier puis le jeter et se laver les mains",
            "Eviter de cracher et de se moucher au sol", " Maintenez-vous à 1m de distance au moins des autres. Soyez rigoureux sur cela."
    };
    CardView cardView[];
    ImageView imgs[];
    TextView tvs[];

    private void conseils(final int total) {
        String message = "";
        int images[];
        final String conseils[];
        cardView=new CardView[]{cd1, cd2, cd3, cd4, cd5, cd6, cd7, cd8};
        imgs=new ImageView[]{img1, img2, img3, img4, img5, img6, img7, img8};
        tvs=new TextView[]{tv1, tv2,tv3, tv4,tv5, tv6, tv7,tv8};

        for (CardView cd:cardView) {
            cd.setVisibility(View.GONE);
        }

        if (total > 50) {
            message = NOM + ", à "+total+"% de chances d'être contaminé, votre situation actuellement paraît critique et devient dangereuse! Vous devez vous mettre en auto-isolement en attendant d'appeler le SAMU ou joignant " +
                    "le +229 51 02 00 00 ou +229 51 04 00 00\nDéjà: ";
            images= sup50;
            conseils= cons50;

        } else if (total <= 6) {
            message = "A "+total+"% comme résultat de test, votre situation actuellement n'est pas mal " + NOM + ". Toutefois: ";
            images= inf6;
            conseils= cons6;
        } else if (total <= 10) {
            message = "Par rapport à vos "+total+"% de risques, votre situation actuellement se hisse déjà vers les 10%. Ne soyez pas angoissés; Vous devez commencer par faire attention " + NOM + ". Ceci étant, soyez rigoureux sur les gestes barrières !";
            images= inf10;
            conseils= conso10;
        }else if(total<= 15) {
            message = NOM + ", avec "+total+"% de risques, votre situation actuellement n'est pas grave mais suscite des interrogations. Ne soyez pas stressés. \nCommencez à veiller strictement sur les gestes barrières\nDéjà: ";
            images= inf20;
            conseils= conso20;
        }else if(total<= 20) {
            message = NOM + ", "+total+"% de risques! Je vous en prie, suivez avec la dernière rigueur les consignes d'hygiène. En réalité, votre situation n'est pas grave mais peut révéler grande chose . \nDéjà: ";
            images= inf20;
            conseils= conso20;
        }else if(total <= 30) {
            message = NOM + ", déjà à "+total+"%, votre situation actuellement commence à attirer l'attention. Vous avez besoin d'un suivi quotidien \nSoyez vraiment très rigoureux sur les gestes barrières" +
                    "\n Essayez de limiter au maximum vos déplacements";
            images= inf30;
            conseils= conso30;
        }else {
            message = NOM + ", avec ces "+total+"% de risques, votre situation actuellement commence à atteindre la zone critique\n" +
                    "Soyez vraiment très rigoureux sur les gestes barrières" +
                    "\nEssayez de réduire au strict minimum vos déplacements\n " +
                    "Vous pouvez joindre le SAMU au +229 51 02 00 00 ou +229 51 04 00 00 " +
                    "pour vous faire dépister.";
            images= infelse;
            conseils= consoelse;
        }

        res.setText(message);
        ((TextView)findViewById(R.id.rec))       .setText("NOS "+conseils.length+" RECOMMANDATIONS POUR VOUS !");
        Date date= new Date();
        int sec=date.getSeconds();
        int min= date.getMinutes();
        int mil= conseils.length/2;
        int mini=mil-1;
        int max=mil+1;
        if(sec%2==0){
            String conseil= conseils[mini];
            conseils[mini]= conseils[0];
            conseils[0]=conseil;

            int imge= images[mini];
            images[mini]=images[0];
            images[0]= imge;

        }else if(min%2==0){
            String conseil= conseils[max];
            conseils[max]= conseils[0];
            conseils[0]=conseil;

            int imge= images[max];
            images[max]=images[0];
            images[0]= imge;
        }

        for(int i=0; i<conseils.length; i++){
            cardView[i].setVisibility(View.VISIBLE);
            imgs[i].setImageDrawable(getResources().getDrawable(images[i]));
            tvs[i].setText((i+1)+"/"+conseils.length+": "+conseils[i]);
            final int finalI = i;
            cardView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alerter(conseils[finalI], total);
                }
            });

        }


    }

    public void Alerter(String message, int total){
        AlertDialog al= new AlertDialog.Builder(this)
                .setTitle("Covid19  --- "+total+"% de chances")
                .setMessage(message)
                .setPositiveButton("Ok, j'ai compris! Merci.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .create();
        al.show();
        al.setCanceledOnTouchOutside(false);
    }

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

    public void loadFooter(){

        int ran= new Random().nextInt();
        int seconde= new Random().nextInt(60);
        String type1="", type2="";
        if(ran%2==0){
            gouv_i.setImageDrawable(getResources().getDrawable(R.drawable.gouv));
            gouv_t.setText("Les derniers cas du Bénin et les nouvelles mesures du gouvernement");
            type1="gouv";
            int ran2= new Random().nextInt();
            int ran3= new Random().nextInt();
            if(ran2%2==0){
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.oms));
                oms_t.setText("Les recommandations et actions l'Organisation Mondiale de la Santé");
                type2="oms";
            }else if(ran3%2==0){
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.covid));
                oms_t.setText("Les grandes choses qu'on peut savoir des symptômes");
                type2="sym";
            }else {
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.port));
                oms_t.setText("Comment porter son masque? vous pouvez lire l'article du gouvernement !");
                type2="port";
            }


        }else if(seconde%3==0){
            gouv_i.setImageDrawable(getResources().getDrawable(R.drawable.oms));
            gouv_t.setText("Les recommandations et actions l'Organisation Mondiale de la Santé");
            type1="oms";
            int ran2= new Random().nextInt();
            int ran3= new Random().nextInt();
            if(ran2%2==0){
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.gouv));
                oms_t.setText("Les derniers cas du Bénin et les nouvelles mesures du gouvernement");
                type2="gouv";
            }else if(ran3%2==0){
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.covid));
                oms_t.setText("Les grandes choses qu'on peut savoir des symptômes");
                type2="sym";
            }else {
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.port));
                oms_t.setText("Comment porter son masque? vous pouvez lire l'article du gouvernement !");
                type2="port";
            }

        }else if(ran%3==0){
            gouv_i.setImageDrawable(getResources().getDrawable(R.drawable.covid));
            gouv_t.setText("Les grandes choses qu'on peut savoir des symptômes");
            type1="sym";
            int ran2= new Random().nextInt();
            int ran3= new Random().nextInt();
            if(ran2%2==0){
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.gouv));
                oms_t.setText("Les derniers cas du Bénin et les nouvelles mesures du gouvernement");
                type2="gouv";
            }else if(ran3%2==0){
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.oms));
                oms_t.setText("Les recommandations et actions l'Organisation Mondiale de la Santé");
                type2="oms";
            }else {
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.port));
                oms_t.setText("Comment porter son masque? vous pouvez lire l'article du gouvernement !");
                type2="port";
            }
        }else {
            gouv_i.setImageDrawable(getResources().getDrawable(R.drawable.port));
            gouv_t.setText("Comment porter son masque? vous pouvez lire l'article du gouvernement !");
            type1="port";
            int ran2= new Random().nextInt();
            int ran3= new Random().nextInt();
            if(ran2%2==0){
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.gouv));
                oms_t.setText("Les derniers cas du Bénin et les nouvelles mesures du gouvernement");
                type2="gouv";
            }else if(ran3%2==0){
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.oms));
                oms_t.setText("Les recommandations et actions l'Organisation Mondiale de la Santé");
                type2="oms";
            }else {
                omg_i.setImageDrawable(getResources().getDrawable(R.drawable.covid));
                oms_t.setText("Les grandes choses qu'on peut savoir des symptômes");
                type2="sym";
            }
        }

        final String finalType = type1;
        gouv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalType.equals("gouv")){
                    goToGouv();
                }else if(finalType.equals("oms")){
                    goToOms();
                }else if(finalType.equals("sym")){
                    goToSympt();
                }else if(finalType.equals("port")){
                    goToPort();
                }
            }
        });

        final String finalType1 = type2;
        oms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalType1.equals("gouv")){
                    goToGouv();
                }else if(finalType1.equals("oms")){
                    goToOms();
                }else if(finalType1.equals("sym")){
                    goToSympt();
                }else if(finalType1.equals("port")){
                    goToPort();
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
