package baxxo.matteo.smsapp;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentMessaggi extends Fragment {


    public FragmentMessaggi() {
        // Required empty public constructor
    }

    ListView lv;
    DatabaseManager db;
    ArrayList<Messaggio> messaggi;
    ArrayList<String> m;
    SimpleAdapter adapter;
    View rootView;
    ArrayAdapter<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fragment_messaggi, container, false);
        // Inflate the layout for this fragment

        lv = (ListView) rootView.findViewById(R.id.lista_messaggi);
        db = new DatabaseManager(rootView.getContext());

        //caricaMessaggi();

        return rootView;
    }

    public void caricaMessaggi() {
        messaggi = db.getAllMessages();
        Log.i("mess", messaggi.size() + "");

        if (messaggi.size() > 0) {


            Collections.sort(messaggi, new Comparator<Messaggio>() {
                @Override
                public int compare(Messaggio m1, Messaggio m2) {
                    return m1.getNome().compareTo(m2.getNome());
                }
            });


            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //hash map con nomi e numeri
                    final HashMap<String, String> nomeNumero = new HashMap<>();

                    //inserisco i contatti
                    for (int i = 0; i < messaggi.size(); i++) {
                        nomeNumero.put(messaggi.get(i).getNome(), messaggi.get(i).getTesto());
                    }

                    //lista di elementi HashMap
                    List<HashMap<String, String>> listItems = new ArrayList<>();

                    //Adapter per la listView
                    adapter = new SimpleAdapter(rootView.getContext(), listItems, R.layout.list_item_3,
                            new String[]{"First Line", "Second Line"},
                            new int[]{R.id.textView40, R.id.textView41}
                    );

                    //Iterator accede alla mappa e accoppia la mappa con l' adapter
                    for (Object o : nomeNumero.entrySet()) {
                        HashMap<String, String> resultsMap = new HashMap<>();
                        Map.Entry pair = (Map.Entry) o;
                        String n = pair.getKey().toString();
                        if (String.valueOf(n.charAt(n.length() - 1)).equals("|")) {
                            n = String.valueOf(n.substring(0, n.length() - 1));
                        }
                        resultsMap.put("First Line", n);
                        resultsMap.put("Second Line", pair.getValue().toString());
                        listItems.add(resultsMap);
                    }
                }
            });

            lv.setAdapter(adapter);
        } else {

            m = new ArrayList<>();
            m.add("Non ci sono messaggi");

            list = new ArrayAdapter(rootView.getContext(), R.layout.support_simple_spinner_dropdown_item, m);

            lv.setAdapter(list);

        }
    }

}
