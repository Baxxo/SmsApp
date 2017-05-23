package baxxo.matteo.smsapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by bassomatteo on 22/05/2017.
 */

public class BootReceiver extends BroadcastReceiver {

    ArrayList<Messaggio> messaggioArrayList;
    int lunghezza;
    Calendar calendar = Calendar.getInstance();
    private String testo = "vuoto";
    private String numero = "vuoto";
    String nome = "vuoto";

    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseManager db = new DatabaseManager(context);

        Messaggio m = db.getMessaggio(7);
        m.setInviato(false);
        db.updateMessaggio(m);

        m = db.getMessaggio(8);
        m.setInviato(false);
        db.updateMessaggio(m);

        messaggioArrayList = db.getNotSentMessages();


        for (Messaggio messaggio : messaggioArrayList) {
            Log.i("messasggio", messaggio.getNumero() + " - " + messaggio.getTesto() + " - " + messaggio.getNome() + " - " + messaggio.getId() + " - " + messaggio.getData());

            numero = messaggio.getNumero();
            testo = messaggio.getTesto();
            nome = messaggio.getNome();
            lunghezza = Integer.parseInt(messaggio.getId());
            calendar.setTimeInMillis(messaggio.getData());


            Intent sender = new Intent(context, Receiver.class);
            sender.putExtra("Numero", numero);
            sender.putExtra("Testo", testo);
            sender.putExtra("Nome", nome);
            sender.putExtra("Id", lunghezza + "");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, lunghezza, sender, lunghezza);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }

    }
}
