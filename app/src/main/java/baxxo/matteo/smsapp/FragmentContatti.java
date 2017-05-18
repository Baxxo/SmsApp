package baxxo.matteo.smsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

/**
 * Created by Matteo on 11/02/2017.
 */

public class FragmentContatti extends android.support.v4.app.Fragment {

    Button button;
    Button search;
    EditText nomeSearch;
    LinearLayout.LayoutParams lp;
    public static ArrayList<Contact> contatti = new ArrayList<>();
    ArrayList<String> a = new ArrayList<String>();
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
    int p = 0;
    HashMap<String, String> resultsMap;
    List<HashMap<String, String>> listItems;
    ArrayAdapter<String> list;
    Cursor cur;
    ArrayList<String> mess1 = new ArrayList<String>();
    String permission = "android.permission.READ_CONTACTS";

    public FragmentContatti() {

    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        resultsMap = new HashMap<>();
        listItems = new ArrayList<>();

        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);

        search = (Button) rootView.findViewById(R.id.search);
        search.setVisibility(View.INVISIBLE);

        nomeSearch = (EditText) rootView.findViewById(R.id.nomeSearch);
        nomeSearch.setVisibility(View.INVISIBLE);

        button = (Button) rootView.findViewById(R.id.button);

        d = new Dialog(rootView.getContext());
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(true);
        d.setContentView(R.layout.dialog);

        tvNumero = (TextView) d.findViewById(R.id.numero);
        tvNome = (TextView) d.findViewById(R.id.nome);

        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(5, 10, 10, 5);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(d.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

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
                    nomeSearch.setText("");
                }
            }
        });

        button.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        contatti.clear();

        //Log.i("Permission", String.valueOf(getContext().checkCallingOrSelfPermission(permission)));

        if (getContext().checkCallingOrSelfPermission(permission) == 0) {
            //Log.i("PermissionPrimoIf", "Ciaone");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getContact();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Accetta i permessi", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }

        if (getContext().checkCallingOrSelfPermission(permission) == -1) {
            //Log.i("Permission1234", "Bello");
            do {
                //Log.i("Permission1234", "Ciaone da while");
                //Log.i("Permission1234", String.valueOf(getContext().checkCallingOrSelfPermission(permission)));
                if (getContext().checkCallingOrSelfPermission(permission) == 0) {
                    //Log.i("Permission1234", "Ciaone da if");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getContact();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Accetta i permessi", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).start();
                }
            } while (getContext().checkCallingOrSelfPermission(permission) == -1);
        }


        return rootView;
    }


    public void getContact() {
        if (!contatti.isEmpty()) {
            contatti.clear();
        }
        search.setVisibility(View.INVISIBLE);
        p = 0;
        ContentResolver cr = getContext().getContentResolver();

        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        final Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        progressBar.setMax(cur.getCount() * 2);
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
                        p++;
                        progressBar.setProgress(p);

                    }
                    phones.close();
                }

            }
        } else {
            mess1.add("Non ci sono contatti");

            list = new ArrayAdapter(rootView.getContext(), R.layout.support_simple_spinner_dropdown_item, mess1);

            listView.setAdapter(list);

            ((Activity) rootView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button.setText("Ricarica Lista");
                    button.setVisibility(View.VISIBLE);
                    search.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }

        //ordino per nome
        Collections.sort(contatti, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact c1, Contact c2) {
                        p++;
                        progressBar.setProgress(p);
                        return c1.name.compareTo(c2.name);
                    }
                }
        );


        //rimuovo gli spazi
        for (int i = 0; i < contatti.size(); i++) {
            contatti.get(i).number.replace(" ", "");
            contatti.get(i).number.replace("\u202A", "");
            contatti.get(i).number.replace("+39", "");
            contatti.get(i).number.replace("\u202A+39", "");
            contatti.get(i).number.replace("\u202C+39", "");
        }

        //rimuovo +39
        for (int i = 0; i < contatti.size(); i++) {

            if (String.valueOf(contatti.get(i).number.substring(0, 3)).equals("+39")) {
                contatti.get(i).number = contatti.get(i).number.substring(3);
            }
            if (String.valueOf(contatti.get(i).number.substring(0, 4)).equals("\u202A+39")) {
                contatti.get(i).number = contatti.get(i).number.substring(4);
            }
            p++;
            progressBar.setProgress(p);
        }

        //rimuovo numeri di casa
        for (int i = 0; i < contatti.size(); i++) {
            if (String.valueOf(contatti.get(i).number.charAt(0)).equals("0")) {
                contatti.remove(i);
            }
            if (String.valueOf(contatti.get(i).number.charAt(0)).equals("0")) {
                contatti.remove(i);
            }
            p++;
            progressBar.setProgress(p);
        }

        for (int i = 0; i < contatti.size(); i++) {
            if (contatti.get(i).number.length() == 11 && !String.valueOf(contatti.get(i).number.charAt(0)).equals("+")) {
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
                progressBar.setProgress(p);

            }
            for (int i = 1; i < contatti.size(); i++) {
                if (contatti.get(i).number.equals(contatti.get(i - 1).number)) {
                    j++;
                    contatti.remove(i - 1);
                    p++;
                }
                progressBar.setProgress(p);

            }
        }

        for (int i = 1; i < contatti.size(); i++) {
            if (contatti.get(i).name.equals(contatti.get(i - 1).name)) {
                contatti.get(i).name = contatti.get(i).name + "|";
                p++;
            }
            progressBar.setProgress(p);
        }

        ((Activity) rootView.getContext()).runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        //hash map con nomi e numeri
                        final HashMap<String, String> nomeNumero = new HashMap<>();

                        //map con valori ordinati
                        HashMap map = sortByValues(nomeNumero);

                        //inserisco i contatti
                        for (int i = 0; i < contatti.size(); i++) {
                            map.put(contatti.get(i).name, contatti.get(i).number);
                            p++;
                            progressBar.setProgress(p);
                        }

                        //lista di elementi HashMap
                        List<HashMap<String, String>> listItems = new ArrayList<>();

                        //Adapter per la listView
                        adapter = new SimpleAdapter(rootView.getContext(), listItems, R.layout.list_item,
                                new String[]{"First Line", "Second Line"},
                                new int[]{R.id.textView12, R.id.textView13}
                        );

                        //Iterator accede alla mappa e accoppia la mappa con l' adapter
                        for (Object o : map.entrySet()) {
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

                        //mostro i valori nella listView
                        listView.setAdapter(adapter);
                        progressBar.setProgress(cur.getCount() * 7);

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

                                MainActivity.n.setText(numero);
                                Toast.makeText(rootView.getContext(), "Numero caricato", Toast.LENGTH_SHORT).show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        ((Activity) rootView.getContext()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                MainActivity.mViewPager.setCurrentItem(0);
                                            }
                                        });
                                    }
                                }).start();

                            }
                        });

                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                Object o = listView.getItemAtPosition(position);
                                String res = o.toString();
                                res = res.replace("{", "");
                                res = res.replace("}", "");
                                res = res.replace("|", "");

                                String parts[] = res.split(",");

                                String dir1 = parts[1];
                                String dir2 = parts[0];

                                parts = dir1.split("=");

                                nome = parts[1];

                                parts = dir2.split("=");
                                numero = parts[1];/*

                                tvNome.setText(nome);
                                tvNumero.setText(numero);

                                d.show();
                                d.getWindow().setAttributes(layoutParams);*/

                                Intent intent = new Intent(rootView.getContext(), ContactDetail.class);
                                intent.putExtra("nome", nome);
                                intent.putExtra("numero", numero);
                                startActivity(intent);

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

    //funzione per ordinare l' hash map con i contatti(funzione trovata)
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
