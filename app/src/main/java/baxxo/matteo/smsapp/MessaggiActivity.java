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

public class MessaggiActivity extends AppCompatActivity {

    ListView lista;
    DatabaseManager dbManager;
    ArrayList<String> lista_messaggi = new ArrayList<>();
    ArrayList<Messaggio> messaggi = new ArrayList<>();
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
                String t = String.valueOf(lista.getItemAtPosition(i));
                //Log.i("messaggi", t);

                String string = t;
                String[] parts = string.split(" - ");
                String part1 = parts[0];
                part1.replace(" ", "");

                int p = Integer.parseInt(part1);

                if (!check) {
                    check = true;
                    Toast.makeText(getApplicationContext(), "Ripremi per eliminare", Toast.LENGTH_LONG).show();
                } else {

                    Intent intent = new Intent(MessaggiActivity.this, Receiver.class);

                    PendingIntent.getBroadcast(getApplicationContext(), p, intent, PendingIntent.FLAG_CANCEL_CURRENT).cancel();

                    Messaggio m = dbManager.getMessaggio(p);

                    m.setInviato(true);

                    dbManager.updateMessaggio(m);

                    check = false;
                    carica();

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
                if (nome.equals(messaggio.getNome())) {
                    lista_messaggi.add(messaggio.getId() + " - " + messaggio.getNome() + ": " + messaggio.getTesto());
                }
            }
            lista.setEnabled(true);

        } else {

            lista_messaggi.add(getString(R.string.no_messaggi));
            lista.setEnabled(false);

        }

        adapter = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, lista_messaggi);
        lista.setAdapter(adapter);
    }
}