package baxxo.matteo.smsapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

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
    String profilo;
    PendingIntent pendingIntent;
    DatabaseManager database;
    int numMess;
    ArrayList<Messaggio> mess;
    Uri sound;

    public Sender() {
        super("Sender");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        numero = intent.getStringExtra("Numero");
        testo = intent.getStringExtra("Testo");
        nomeNumero = intent.getStringExtra("Nome");
        id = intent.getStringExtra("Id");

        testo = testo + "\n\n(Messaggio inviato con SmsApp!)";

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

        //verifico modalità aereo
        if (Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1) {

            text = "Non inviato! Modalità aereo attiva\n" + testo;
            sub = "Modalità aereo attiva!";

        } else {

            try {

                sms = SmsManager.getDefault();
                ArrayList<String> parts = sms.divideMessage(testo);
                sms.sendMultipartTextMessage(numero, null, parts, null, null);

                text = "Inviato! \n" + testo;
                sub = "Inviato!";

                try {

                    mess.get(i).setInviato(true);
                    database.updateMessaggio(mess.get(i));

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
        //Log.i("SoundPref", s);

        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.unnamed);

        builder = new NotificationCompat.Builder(this);
        builder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(text))
                .setSmallIcon(R.mipmap.unnamed)
                .setLargeIcon(defaultPhoto)
                .setTicker("SmsApp")
                .setContentTitle("Sms a: " + nomeNumero)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLights(Color.CYAN, 1, 10)
                .setSubText(sub)
                .setContentText(text);

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                profilo = "Silent";
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                profilo = "Vibrate";
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                profilo = "Normal";
                break;
        }

        if (!s.equals("") && profilo.equals("Normal")) {
            sound = Uri.parse(s);
            builder.setSound(sound);

        }

        builder.build();

        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(Integer.parseInt(id), notification);
        //-----------------------------------------------------------------------------------------------------------------


        if (!profilo.equals("Silent")) {
            if (!vibrate == false) {
                //Log.i("Vibrate1Pref", "true");
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = new long[8];
                pattern[1] = 700;
                pattern[2] = 230;
                pattern[3] = 700;
                vibrator.vibrate(pattern, -1);
            } else {
                //Log.i("Vibrate1Pref", "false");
            }
        }

        Receiver.completeWakefulIntent(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
