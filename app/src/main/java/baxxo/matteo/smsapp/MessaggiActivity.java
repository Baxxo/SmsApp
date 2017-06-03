package baxxo.matteo.smsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class MessaggiActivity extends AppCompatActivity {

    ListView lista;
    DatabaseManager dbManager;
    ArrayList<String> lista_messaggi = new ArrayList<>();
    ArrayList<Messaggio> messaggi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaggi);

        dbManager = new DatabaseManager(getApplicationContext());

        lista = (ListView) findViewById(R.id.lista_messaggi_activity);

        messaggi = dbManager.getNotSentMessages();

        for (Messaggio messaggio : messaggi) {

            lista_messaggi.add(messaggio.getId() + "|" + messaggio.getNome() + ": " + messaggio.getTesto());

        }

    }
}
