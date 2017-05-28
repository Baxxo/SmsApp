package baxxo.matteo.smsapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import static baxxo.matteo.smsapp.MainActivity.context;

/**
 * Created by Matteo on 20/03/2017.
 */

public class Sender extends IntentService {

    NotificationCompat.Builder builder;

    SmsManager sms;
    String text = "";
    String numero = "";
    String nomeNumero = "";
    String testo = "";
    String sub = "";
    String id = "";
    PendingIntent pendingIntent;
    DatabaseManager database;
    int numMess;
    ArrayList<Messaggio> mess;

    public Sender() {
        super("Sender");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        numero = intent.getStringExtra("Numero");
        testo = intent.getStringExtra("Testo");
        nomeNumero = intent.getStringExtra("Nome");
        id = intent.getStringExtra("Id");

        database = new DatabaseManager(this);
        mess = database.getNotSentMessages();
        int i = 0;
        //prendo il messaggoi con il giusto id
        for (Messaggio messaggio : mess) {
            if (messaggio.getId().equals(id)) {
                numMess = i;
            }
            i++;
        }
        i--;

        //fa aprire l'app quando si clicca sulla notifica
        Intent intent1 = new Intent(this, MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        if (Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1) {

            text = "Non inviato! Modalità aereo attiva\n" + testo;
            sub = "Modalità aereo attiva !";

        } else {

            try {

                sms = SmsManager.getDefault();
                ArrayList<String> parts = sms.divideMessage(testo);
                sms.sendMultipartTextMessage(numero, null, parts, null, null);

                //sms.sendTextMessage(numero, null, testo, null, null);
                text = "Inviato! \n" + testo;
                sub = "Inviato!";
                int db = 112233;

                try {

                    mess.get(i).setInviato(true);
                    db = database.updateMessaggio(mess.get(i));

                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), "Errore nel database", Toast.LENGTH_LONG).show();
                    // Log.i("Sender", "Errore messagio non diventa true " + i);
                }

            } catch (Exception e) {
                text = "Non inviato! \n" + testo;
                sub = "Non inviato!";
            }

        }

        //-----------------------------------------------------------------------------------------------------------------

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean vibrate = preferences.getBoolean("Vibrate", false);
        Log.i("VibratePref", String.valueOf(vibrate));
        String s = preferences.getString("Sound", "");
        Log.i("SoundPref", s);

        Uri sound;
        if (!s.equals("")) {
            Log.i("Sound1Pref", "true");

            sound = Uri.parse(s);
            builder = new NotificationCompat.Builder(this);
            builder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(text));
            builder.setSmallIcon(R.mipmap.unnamed)
                    .setTicker("Sms Simulator")
                    .setContentTitle("SMS a: " + nomeNumero)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setLights(Color.CYAN, 1, 10)
                    .setSubText(sub)
                    .setSound(sound)
                    .setContentText(text)
                    .build();
            Notification notification = builder.build();
            NotificationManagerCompat.from(this).notify(Integer.parseInt(id), notification);

        } else {
            Log.i("Sound1Pref", "false");

            builder = new NotificationCompat.Builder(this);
            builder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(text));
            builder.setSmallIcon(R.mipmap.unnamed)
                    .setTicker("Sms Simulator")
                    .setContentTitle("SMS a: " + nomeNumero)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setLights(Color.CYAN, 1, 10)
                    .setSubText(sub)
                    .setContentText(text)
                    .build();
            Notification notification = builder.build();
            NotificationManagerCompat.from(this).notify(Integer.parseInt(id), notification);

        }

        //-----------------------------------------------------------------------------------------------------------------


        if (!vibrate == false) {
            Log.i("Vibrate1Pref", "true");
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        } else {
            Log.i("Vibrate1Pref", "false");
        }

        Receiver.completeWakefulIntent(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
