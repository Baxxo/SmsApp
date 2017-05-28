package baxxo.matteo.smsapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Matteo on 27/03/2017.
 */

public class Receiver extends WakefulBroadcastReceiver {
    String numero;
    String nomeNumero;
    String testo;
    String id;

    @Override
    public void onReceive(Context context, Intent intent) {

        numero = intent.getStringExtra("Numero");
        testo = intent.getStringExtra("Testo");
        nomeNumero = intent.getStringExtra("Nome");
        id = intent.getStringExtra("Id");

        Intent service = new Intent(context, Sender.class);
        service.putExtra("Numero", numero);
        service.putExtra("Testo", testo);
        service.putExtra("Nome", nomeNumero);
        service.putExtra("Id", id);

        startWakefulService(context, service);

    }
}
