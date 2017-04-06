package baxxo.matteo.smsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Matteo on 11/02/2017.
 */

public class FragmentContatti extends android.support.v4.app.Fragment {

    String numeroTelefono = "";
    String nomeTelefono = "";
    Button button;
    Button search;
    EditText nomeSearch;
    LinearLayout.LayoutParams lp;
    public static ArrayList<Contact> contatti = new ArrayList<>();
    ProgressBar progressBar;
    View rootView;
    Dialog d;
    WindowManager.LayoutParams layoutParams;
    TextView tvNome;
    TextView tvNumero;
    static ListView listView;
    SimpleAdapter adapter;
    String nome;
    String numero;

    public FragmentContatti() {

    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);

        search = (Button) rootView.findViewById(R.id.search);
        search.setVisibility(View.INVISIBLE);

        nomeSearch = (EditText) rootView.findViewById(R.id.nomeSearch);
        nomeSearch.setVisibility(View.INVISIBLE);

        d = new Dialog(rootView.getContext());
        d.setTitle("Numero");
        d.setCancelable(true);
        d.setContentView(R.layout.dialog);

        button = (Button) rootView.findViewById(R.id.button);

        tvNumero = (TextView) d.findViewById(R.id.numero);
        tvNome = (TextView) d.findViewById(R.id.nome);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(5, 10, 10, 5);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(d.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        progressBar.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                contatti.clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getContact();
                    }
                }).start();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getVisibility() == View.VISIBLE) {
                    button.setVisibility(View.INVISIBLE);
                    nomeSearch.setVisibility(View.VISIBLE);
                } else {
                    button.setVisibility(View.VISIBLE);
                    nomeSearch.setVisibility(View.INVISIBLE);
                }
            }
        });


        return rootView;
    }


    public void getContact() {
        if (!contatti.isEmpty()) {
            contatti.clear();
        }
        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                    while (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phoneNumber.replaceAll("\\s+", "");
                        phoneNumber = phoneNumber.replaceAll("-", "");
                        contatti.add(new Contact(name, phoneNumber));
                    }
                    phones.close();
                }

            }
        }

        Collections.sort(contatti, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact c1, Contact c2) {
                        return c1.name.compareTo(c2.name);
                    }
                }
        );

        for (int i = 1; i < contatti.size(); i++) {
            if (String.valueOf(contatti.get(i).number.substring(0, 3)).equals("+39")) {
                contatti.get(i).number = contatti.get(i).number.substring(3);
            }
        }

        for (int i = 0; i < contatti.size(); i++) {
            if (String.valueOf(contatti.get(i).number.charAt(0)).equals("0")) {
                contatti.remove(i);
            }
        }

        int j = 0;
        //se j = 0 allora non ci sono contatti uguali altrimenti continuo l'eliminazione
        while (j == 0) {
            j = 0;
            for (int i = 1; i < contatti.size(); i++) {
                if (contatti.get(i).number.equals(contatti.get(i - 1).number)) {
                    j++;
                    contatti.remove(i - 1);
                }
            }
            j = 0;
            for (int i = 1; i < contatti.size(); i++) {
                if (contatti.get(i).number.equals(contatti.get(i - 1).number)) {
                    j++;
                    contatti.remove(i - 1);
                }
            }
        }

        ((Activity) rootView.getContext()).runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        //hash map con nomi e numeri
                        HashMap<String, String> nomeNumero = new HashMap<>();

                        Set set = nomeNumero.entrySet();
                       // Iterator iterator = set.iterator();
                        /*while (iterator.hasNext()) {
                           // Map.Entry me = (Map.Entry) iterator.next();
                        }*/

                        //map con valori ordinati
                        Map<String, String> map = sortByValues(nomeNumero);

                        //inserisco i contatti
                        for (int i = 0; i < contatti.size(); i++) {
                            map.put(contatti.get(i).name, contatti.get(i).number);

                        }

                        //lista di elementi HashMap
                        List<HashMap<String, String>> listItems = new ArrayList<>();

                        //Adapter per la listView
                        adapter = new SimpleAdapter(rootView.getContext(), listItems, R.layout.list_item,
                                new String[]{"First Line", "Second Line"},
                                new int[]{R.id.textView12, R.id.textView13});

                        //Iterator accede alla mappa e accoppia la mappa con l' adapter
                        Iterator it = map.entrySet().iterator();
                        while (it.hasNext()) {
                            HashMap<String, String> resultsMap = new HashMap<>();
                            Map.Entry pair = (Map.Entry) it.next();
                            resultsMap.put("First Line", pair.getKey().toString());
                            resultsMap.put("Second Line", pair.getValue().toString());
                            listItems.add(resultsMap);
                        }

                        //mostro i valori nella listView
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Object o = listView.getItemAtPosition(position);
                                String res = o.toString();
                                res = res.replace("{", "");
                                res = res.replace("}", "");

                                String parts[] = res.split(",");

                                String dir1 = parts[1];
                                String dir2 = parts[0];

                                parts = dir1.split("=");

                                nome = parts[1];

                                parts = dir2.split("=");
                                numero = parts[1];

                                numeroTelefono = contatti.get(position).number;
                                MainActivity.n.setText(numeroTelefono);
                                Toast.makeText(rootView.getContext(), "Numero caricato", Toast.LENGTH_SHORT).show();
                            }
                        });

                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                                nomeTelefono = contatti.get(position).name;
                                numeroTelefono = contatti.get(position).number;
                                tvNome.setText(nomeTelefono);
                                tvNumero.setText(numeroTelefono);

                                d.show();
                                d.getWindow().setAttributes(layoutParams);

                                return true;
                            }
                        });

                        nomeSearch.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                FragmentContatti.this.adapter.getFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        progressBar.setVisibility(View.INVISIBLE);
                        button.setText("Ricarica Lista");
                        button.setVisibility(View.VISIBLE);
                        search.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    //funzione per ordinare l' hash map con i contatti
    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

}
