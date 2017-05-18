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
    int s;
    ListView lv;
    List<HashMap<String, String>> listItems;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_database);

        lv = (ListView) findViewById(R.id.list);

        relativeLayout = (RelativeLayout) findViewById(R.id.relative);

        databaseManager = new DatabaseManager(getApplicationContext());

        b = (Button) findViewById(R.id.button5);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mess1.clear();
                s = databaseManager.getMessagesCount();
                //Log.i("size", String.valueOf(databaseManager.getMessagesCount()));
                if (s > 0) {

                    caricaDb();

                } else {
                    mess1.add("Nessun messaggio");

                    list = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, mess1);

                    lv.setAdapter(list);
                }
            }
        });
    }

    public void caricaDb() {

        mess = databaseManager.getAllMessages();

        HashMap<String, String> map = new HashMap<>();


        for (int i = 0; i < s; i++) {
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
