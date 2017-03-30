package baxxo.matteo.smsapp;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Matteo on 27/03/2017.
 */
public class Receiver extends WakefulBroadcastReceiver {
    String numero;
    String nomeNumero;
    String testo;
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("Allarme");
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        wakeLock.acquire();
        numero = intent.getStringExtra("Numero");
        testo = intent.getStringExtra("Testo");
        nomeNumero = intent.getStringExtra("Nome");

        Intent service = new Intent(context, Sender.class);
        service.putExtra("Numero", numero);
        service.putExtra("Testo", testo);
        service.putExtra("Nome", nomeNumero);

        // launching. This is the Intent to deliver to the service.
        //this will send a notification message

        startWakefulService(context, service);

    }
}
