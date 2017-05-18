package baxxo.matteo.smsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatabaseFragment extends Fragment {


    ArrayList<Messaggio> mess;
    ArrayList<String> mess1 = new ArrayList<String>();
    DatabaseManager databaseManager;
    RelativeLayout relativeLayout;
    ArrayAdapter<String> list;
    Button b;
    int s;
    View rootView;
    ListView lv;
    List<HashMap<String, String>> listItems;
    SimpleAdapter adapter;

    public DatabaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_database, container, false);

        lv = (ListView) rootView.findViewById(R.id.list);


        relativeLayout = (RelativeLayout) rootView.findViewById(R.id.relative);

        databaseManager = new DatabaseManager(rootView.getContext());

        b = (Button) rootView.findViewById(R.id.button5);

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

                    list = new ArrayAdapter(rootView.getContext(), R.layout.support_simple_spinner_dropdown_item, mess1);

                    lv.setAdapter(list);
                }
            }
        });

        return rootView;
    }

    public void caricaDb() {

        mess = databaseManager.getAllMessages();

        HashMap<String, String> map = new HashMap<>();


        for (int i = 0; i < s; i++) {
            map.put("Messaggio a: " + mess.get(i).getNome(), "Testo: " + mess.get(i).getTesto());
        }

        listItems = new ArrayList<>();

        adapter = new SimpleAdapter(rootView.getContext(), listItems, R.layout.list_item_2,
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
