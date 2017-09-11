package baxxo.matteo.smsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FragmentContatti extends android.support.v4.app.Fragment {

    static ArrayList<Contact> contatti = new ArrayList<>();
    static ListView listView;
    static boolean carica = false;
    Button button;
    EditText nomeSearch;
    LinearLayout.LayoutParams lp;
    ProgressBar progressBar;
    View rootView;
    Dialog d;
    WindowManager.LayoutParams layoutParams;
    TextView tvNome;
    TextView tvNumero;
    SimpleAdapter adapter;
    String nome;
    String numero;
    int p = 0;
    HashMap<String, String> resultsMap;
    List<HashMap<String, String>> listItems;
    ArrayAdapter<String> list;
    Cursor cur;
    ArrayList<String> mess1 = new ArrayList<>();
    String permission = "android.permission.READ_CONTACTS";

    public FragmentContatti() {

    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        resultsMap = new HashMap<>();
        listItems = new ArrayList<>();

        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setTextFilterEnabled(true);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;
            private boolean dis = true;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    if (dis) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.animOut();
                                dis = false;
                            }
                        });

                    }
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    if (!dis) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.animIn();
                                dis = true;
                            }
                        });

                    }
                }
                mLastFirstVisibleItem = firstVisibleItem;

            }
        });

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

        if (getContext().checkCallingOrSelfPermission(permission) == -1) {
            while (getContext().checkCallingOrSelfPermission(permission) == -1) {
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getContact();
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            button.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getContext(), getString(R.string.errore_caricamento), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();

        return rootView;
    }

    public void setVisibitlyButton(int vis) {
        try {
            button = (Button) rootView.findViewById(R.id.button);
            button.setVisibility(vis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVisibitlySearch(int vis) {
        try {
            nomeSearch = (EditText) rootView.findViewById(R.id.nomeSearch);
            nomeSearch.setVisibility(vis);
            if (vis == View.VISIBLE) {
                nomeSearch.requestFocus();
                nomeSearch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.showSoftInput(nomeSearch, 0);
                    }
                }, 200);
            } else {
                nomeSearch.requestFocus();
                nomeSearch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                }, 200);
            }
            nomeSearch.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getContact() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                MainActivity.animOut();
            }
        });
        carica = true;

        if (!contatti.isEmpty()) {
            contatti.clear();
        }
        p = 0;
        ContentResolver cr = getContext().getContentResolver();

        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        final Cursor cur;
        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        progressBar.setMax(cur.getCount()*2);
        try {
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
                mess1.clear();
                mess1.add(getString(R.string.no_contatti));

                list = new ArrayAdapter(rootView.getContext(), R.layout.support_simple_spinner_dropdown_item, mess1);

                ((Activity) rootView.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(list);
                        button.setText(getString(R.string.lista));
                        button.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        Collections.sort(contatti, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact c1, Contact c2) {
                        p++;
                        progressBar.setProgress(p);
                        return c1.name.compareTo(c2.name);
                    }
                }
        );

        try {
            if (contatti.size() > 1) {

                int j = 0;

                while (j == 0) {

                    j = 0;

                    try {
                        for (int i = 1; i < contatti.size(); i++) {
                            if (contatti.get(i).number.equals(contatti.get(i - 1).number)) {
                                j++;
                                contatti.remove(i - 1);
                            }
                            p++;
                            progressBar.setProgress(p);

                        }
                        for (int i = 1; i < contatti.size(); i++) {
                            if (contatti.get(i).number.equals(contatti.get(i - 1).number)) {
                                j++;
                                contatti.remove(i - 1);
                            }
                            p++;
                            progressBar.setProgress(p);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 1; i < contatti.size(); i++) {
                    if (contatti.get(i).name.equals(contatti.get(i - 1).name)) {
                        contatti.get(i).name = contatti.get(i).name + "|";
                    }
                    p++;
                    progressBar.setProgress(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Activity) rootView.getContext()).runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        final HashMap<String, String> nomeNumero = new HashMap<>();

                        HashMap map = sortByValues(nomeNumero);

                        for (int i = 0; i < contatti.size(); i++) {
                            map.put(contatti.get(i).name, contatti.get(i).number);
                            p++;
                            progressBar.setProgress(p);
                        }

                        final List<HashMap<String, String>> listItems = new ArrayList<>();

                        adapter = new SimpleAdapter(rootView.getContext(), listItems, R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.textView12, R.id.textView13}) {
                            @Override
                            public View getView(final int position, View convertView, ViewGroup parent) {
                                View v = super.getView(position, convertView, parent);

                                Button detail = (Button) v.findViewById(R.id.buttonDettagli);
                                detail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
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
                                        numero = parts[1];

                                        Intent intent = new Intent(rootView.getContext(), ContactDetail.class);
                                        intent.putExtra("nome", nome);
                                        intent.putExtra("numero", numero);
                                        startActivity(intent);
                                    }
                                });
                                return v;
                            }
                        };

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
                                Toast.makeText(rootView.getContext(), getString(R.string.numero_caricato), Toast.LENGTH_SHORT).show();

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
                                                MainActivity.mViewPager.setCurrentItem(1);
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
                                numero = parts[1];

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
                        button.setText(getString(R.string.lista));
                        button.setVisibility(View.VISIBLE);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (MainActivity.pos == 0) {
                                    MainActivity.fab.setImageResource(R.drawable.ic_search);
                                    MainActivity.animIn();
                                }
                                Toast.makeText(getContext(), getString(R.string.caricata), Toast.LENGTH_SHORT).show();

                            }
                        });
                        carica = false;
                    }
                }
        );

    }

    private static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        HashMap sortedHashMap = new LinkedHashMap();
        for (Object aList : list) {
            Map.Entry entry = (Map.Entry) aList;
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

}