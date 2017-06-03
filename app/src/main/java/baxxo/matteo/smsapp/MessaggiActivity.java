package baxxo.matteo.smsapp;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MessaggiActivity extends AppCompatActivity {

    ListView lista;
    DatabaseManager dbManager;
    ArrayList<String> lista_messaggi = new ArrayList<>();
    ArrayList<Messaggio> messaggi = new ArrayList<>();
    TextView testo;
    Button elimina;

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