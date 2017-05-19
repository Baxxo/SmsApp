package baxxo.matteo.smsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayDatabase extends AppCompatActivity {
    ArrayList<Messaggio> mess;
    ArrayList<String> mess1 = new ArrayList<String>();
    DatabaseManager databaseManager;
    RelativeLayout relativeLayout;
    ArrayAdapter<String> list;
    Button b;
    Button n;
    Button t;
    Button e;
    int s;
    ListView lv;
    List<HashMap<String, String>> listItems;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_database);

        lv = (ListView) findViewById(R.id.list);
        b = (Button) findViewById(R.id.button5);
        n = (Button) findViewById(R.id.button4);
        t = (Button) findViewById(R.id.button7);
        e = (Button) findViewById(R.id.button2);

        relativeLayout = (RelativeLayout) findViewById(R.id.relative);

        databaseManager = new DatabaseManager(getApplicationContext());

        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseManager.deleteAllMessage();
            }
        });

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mess1.clear();
                s = databaseManager.getMessagesCount();
                //Log.i("size", String.valueOf(databaseManager.getMessagesCount()));
                if (s > 0) {

                    caricaAllDb();

                } else {
                    mess1.add("Nessun messaggio");

                    list = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, mess1);

                    lv.setAdapter(list);
                }
            }
        });
        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseManager.deleteMessage(new Messaggio("100", "NomeProva1", "NumeroProva1", "TestoProva1", 1, false));
                databaseManager.deleteMessage(new Messaggio("200", "NomeProva2", "NumeroProva2", "TestoProva2", 2, false));
                databaseManager.deleteMessage(new Messaggio("300", "NomeProva3", "NumeroProva3", "TestoProva3", 3, false));
                databaseManager.deleteMessage(new Messaggio("400", "NomeProva4", "NumeroProva4", "TestoProva4", 4, true));
                databaseManager.deleteMessage(new Messaggio("500", "NomeProva5", "NumeroProva5", "TestoProva5", 5, true));
                databaseManager.aggiungiMessaggio(new Messaggio("100", "NomeProva1", "NumeroProva1", "TestoProva1", 1, false));
                databaseManager.aggiungiMessaggio(new Messaggio("200", "NomeProva2", "NumeroProva2", "TestoProva2", 2, false));
                databaseManager.aggiungiMessaggio(new Messaggio("300", "NomeProva3", "NumeroProva3", "TestoProva3", 3, false));
                databaseManager.aggiungiMessaggio(new Messaggio("400", "NomeProva4", "NumeroProva4", "TestoProva4", 4, true));
                databaseManager.aggiungiMessaggio(new Messaggio("500", "NomeProva5", "NumeroProva5", "TestoProva5", 5, true));
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mess1.clear();
                s = databaseManager.getMessagesCount();
                //Log.i("size", String.valueOf(databaseManager.getMessagesCount()));
                if (s > 0) {

                    caricaNonInviatiDb();

                } else {
                    mess1.add("Nessun messaggio");

                    list = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, mess1);

                    lv.setAdapter(list);
                }
            }
        });
    }

    public void caricaNonInviatiDb() {

        mess = databaseManager.getNotSentMessages();

        Log.i("Messaggio", "Sono Qui non inviati");

        caricaDb();
    }

    public void caricaAllDb() {

        mess = databaseManager.getAllMessages();

        Log.i("Messaggio", "Sono Qui tutti");

        caricaDb();

    }

    public void caricaDb() {
        HashMap<String, String> map = new HashMap<>();


        for (int i = 0; i < mess.size(); i++) {
            map.put("Messaggio a: " + mess.get(i).getNome(), "Testo: " + mess.get(i).getTesto());
        }

        listItems = new ArrayList<>();

        adapter = new SimpleAdapter(getApplicationContext(), listItems, R.layout.list_item_2,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.textView20, R.id.textView21}
        );

        for (Object o : map.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            String n = pair.getKey().toString();
            if (String.valueOf(n.charAt(n.length() - 1)).equals("|")) {
                n = String.valueOf(n.substring(0, n.length() - 1));
            }
            HashMap<String, String> resultsMap = new HashMap<>();
            resultsMap.put("First Line", n);
            resultsMap.put("Second Line", pair.getValue().toString());
            listItems.add(resultsMap);
        }

        lv.setAdapter(adapter);
    }
}
