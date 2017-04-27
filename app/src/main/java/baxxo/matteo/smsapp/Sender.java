package baxxo.matteo.smsapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

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
    PendingIntent pendingIntent;
    DatabaseManager database;

    public Sender() {
        super("Sender");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        numero = intent.getStringExtra("Numero");
        testo = intent.getStringExtra("Testo");
        nomeNumero = intent.getStringExtra("Nome");

        database = new DatabaseManager(this);

        try {
            sms = SmsManager.getDefault();
            sms.sendTextMessage(numero, null, testo, null, null);
            text = "Inviato! \n" + testo;
            sub = "Inviato!";

            Intent intent1 = new Intent(this, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        } catch (Exception e) {
            text = "Non inviato! \n" + testo;
            sub = "Non inviato!";
        }

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
        NotificationManagerCompat.from(this).notify(0, notification);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1500);
        Receiver.completeWakefulIntent(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
