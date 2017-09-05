package baxxo.matteo.smsapp;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


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
    boolean air = false;
    int i = 0;
    boolean esito = true;
    PendingIntent sentPI;


    public Sender() {
        super("Sender");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        numero = intent.getStringExtra("Numero");
        testo = intent.getStringExtra("Testo");
        nomeNumero = intent.getStringExtra("Nome");
        id = intent.getStringExtra("Id");

        if (String.valueOf(nomeNumero.charAt(nomeNumero.length() - 1)).equals("|")) {
            nomeNumero = String.valueOf(nomeNumero.substring(0, nomeNumero.length() - 1));
        }

        testo = testo + "\n\n(" + getString(R.string.def_text) + ")\n" + getString(R.string.def_text_2);

        database = new DatabaseManager(this);
        mess = database.getNotSentMessages();

        for (Messaggio messaggio : mess) {
            if (messaggio.getId().equals(id)) {
                numMess = Integer.parseInt(id);
            } else {
                i++;
            }
        }

        if (mess.size() <= 0) {

            Receiver.completeWakefulIntent(intent);

        } else {

            if (isSimExists()) {

                air = false;

                try {

                    String SENT = "SMS_SENT";

                    sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

                    text = getString(R.string.inviato) + "\n" + testo;
                    sub = getString(R.string.inviato);

                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context arg0, Intent arg1) {
                            int resultCode = getResultCode();
                            switch (resultCode) {
                                case Activity.RESULT_OK:

                                    break;

                                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                                    text = getString(R.string.non_inviato) + "\n" + testo;
                                    sub = getString(R.string.non_inviato);

                                    esito = false;

                                    break;

                                case SmsManager.RESULT_ERROR_NO_SERVICE:

                                    text = getString(R.string.non_inviato) + "\n" + testo;
                                    sub = getString(R.string.non_inviato);

                                    esito = false;

                                    break;

                                case SmsManager.RESULT_ERROR_NULL_PDU:

                                    text = getString(R.string.non_inviato) + "\n" + testo;
                                    sub = getString(R.string.non_inviato);

                                    esito = false;

                                    break;

                                case SmsManager.RESULT_ERROR_RADIO_OFF:


                                    text = getString(R.string.plane) + "\n" + testo;
                                    sub = getString(R.string.plane_sub);

                                    esito = false;

                                    break;
                            }
                        }
                    }, new IntentFilter(SENT));

                } catch (Exception e) {
                    text = getString(R.string.non_inviato) + "\n" + testo;
                    sub = getString(R.string.non_inviato);
                    esito = false;
                }

            } else {

                text = getString(R.string.sim_err) + "\n" + testo;
                sub = getString(R.string.sim_err);
                esito = false;

            }

            sms = SmsManager.getDefault();

            try {

                mess.get(i).setInviato(true);
                database.updateMessaggio(mess.get(i));

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Errore nel database", Toast.LENGTH_LONG).show();

            }

            ArrayList<String> parts = sms.divideMessage(testo);

            ArrayList<PendingIntent> pend = new ArrayList<>();

            for (int l = 0; l < parts.size(); l++) {
                pend.add(sentPI);
            }

            sms.sendMultipartTextMessage(numero, null, parts, pend, null);


            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Boolean vibrate = preferences.getBoolean("Vibrate", false);

            String s = preferences.getString("Sound", "");

            Calendar c = Calendar.getInstance();
            String m = String.valueOf(c.get(Calendar.MINUTE));
            if (m.length() == 1) {
                m = "0" + m;
            }
            String h = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
            if (h.length() == 1) {
                h = "0" + h;
            }

            String time = " (" + h + ":" + m + ")";

            Intent intent1 = new Intent(this, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);


            builder = new NotificationCompat.Builder(this);
            builder.setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(text))
                    .setTicker("SmsApp")
                    .setContentTitle(getString(R.string.sms_a) + " " + nomeNumero + " " + time)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setLights(Color.CYAN, 1, 10)
                    .setSubText(sub)
                    .setContentText(text);


            if (esito) {
                Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.green3);
                builder.setSmallIcon(R.mipmap.green3)
                        .setLargeIcon(defaultPhoto);
            } else {
                Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.red3);
                builder.setSmallIcon(R.mipmap.red3)
                        .setLargeIcon(defaultPhoto);
            }

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

            if (!profilo.equals("Silent")) {
                if (vibrate) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = new long[8];
                    pattern[1] = 700;
                    pattern[2] = 230;
                    pattern[3] = 700;
                    vibrator.vibrate(pattern, -1);
                }
            }

            try {
                if (database.getNotSentMessages().size() <= 0) {

                    MainActivity.remove();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Receiver.completeWakefulIntent(intent);

        }

    }

    public boolean isSimExists() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int SIM_STATE = telephonyManager.getSimState();

        if (SIM_STATE == TelephonyManager.SIM_STATE_READY)
            return true;
        else {
            switch (SIM_STATE) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    Log.i("smsapp1", "SIM_STATE_ABSENT");
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    Log.i("smsapp1", "SIM_STATE_NETWORK_LOCKED");
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    Log.i("smsapp1", "SIM_STATE_PIN_REQUIRED");
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    Log.i("smsapp1", "SIM_STATE_PUK_REQUIRED");
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    Log.i("smsapp1", "SIM_STATE_UNKNOWN");
                    break;
            }
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
