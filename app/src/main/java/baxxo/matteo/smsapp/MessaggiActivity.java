package baxxo.matteo.smsapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MessaggiActivity extends AppCompatActivity {

    ListView lista;
    DatabaseManager dbManager;
    ArrayList<String> lista_messaggi = new ArrayList<>();
    ArrayList<Messaggio> messaggi = new ArrayList<>();
    ArrayList<String> id = new ArrayList<>();
    ArrayAdapter adapter;
    String nome;
    Boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaggi);

        nome = getIntent().getStringExtra("Nome");

        dbManager = new DatabaseManager(getApplicationContext());

        lista = (ListView) findViewById(R.id.lista_messaggi_activity);

        carica();

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int p = Integer.parseInt(id.get(i));

                Log.i("hashmap", "P: " + p);

                if (!check) {

                    check = true;
                    Toast.makeText(getApplicationContext(), getString(R.string.ripremi), Toast.LENGTH_LONG).show();

                } else {

                    Intent intent = new Intent(MessaggiActivity.this, Receiver.class);

                    PendingIntent.getBroadcast(getApplicationContext(), p, intent, PendingIntent.FLAG_CANCEL_CURRENT).cancel();

                    Messaggio m = dbManager.getMessaggio(p);

                    m.setInviato(true);

                    dbManager.updateMessaggio(m);

                    check = false;

                    carica();

                }

                Log.i("Size", String.valueOf(dbManager.getNotSentMessages().size()));
                if (dbManager.getNotSentMessages().size() <= 0) {
                    MainActivity.btnMessaggi.setVisibility(View.GONE);
                }

            }
        });

    }

    private void carica() {
        messaggi.clear();
        lista_messaggi.clear();

        messaggi = dbManager.getNotSentMessages();

        if (messaggi.size() > 0) {

            for (Messaggio messaggio : messaggi) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(messaggio.getData());
                String m = String.valueOf(c.get(Calendar.MINUTE));
                String h = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
                String g = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                String me = String.valueOf(c.get(Calendar.MONTH));
                String a = String.valueOf(c.get(Calendar.YEAR));

                if (m.length() == 1) {
                    m = "0" + m;
                }
                if (h.length() == 1) {
                    h = "0" + h;
                }
                if (g.length() == 1) {
                    g = "0" + g;
                }
                if (me.length() == 1) {
                    me = "0" + me;
                }
                if (a.length() == 1) {
                    a = "0" + a;
                }

                if (nome.equals(messaggio.getNome())) {
                    id.add(messaggio.getId());

                    lista_messaggi.add(messaggio.getNome() + ": " + messaggio.getTesto() + "\n(" + h + ":" + m + " - " + g + "/" + me + "/" + a + ")");

                }
                if (nome.equals("tutti_i_messaggi_da_inviare_9821")) {

                    id.add(messaggio.getId());

                    lista_messaggi.add(messaggio.getNome() + ": " + messaggio.getTesto() + "\n(" + h + ":" + m + " - " + g + "/" + me + "/" + a + ")");
                }

            }

            lista.setEnabled(true);

        } else {

            lista_messaggi.add(getString(R.string.no_messaggi));
            lista.setEnabled(false);
        }

        adapter = new ArrayAdapter(MessaggiActivity.this, R.layout.list_messaggi, lista_messaggi);
        lista.setAdapter(adapter);

    }
}