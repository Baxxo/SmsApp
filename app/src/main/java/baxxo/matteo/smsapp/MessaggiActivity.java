package baxxo.matteo.smsapp;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessaggiActivity extends AppCompatActivity {

    String nome;
    ListView lista;
    boolean b = true;
    Boolean check = false;
    SimpleAdapter adapter;
    DatabaseManager dbManager;
    ArrayList<String> id = new ArrayList<>();
    ArrayList<Messaggio> messaggi = new ArrayList<>();
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    Map<String, String> datum = new HashMap<String, String>(2);

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

                int p = Integer.parseInt(id.get(i));

                if (!check) {

                    check = true;
                    Toast.makeText(getApplicationContext(), getString(R.string.ripremi), Toast.LENGTH_LONG).show();

                } else {

                    Intent intent = new Intent(MessaggiActivity.this, Receiver.class);

                    PendingIntent.getBroadcast(getApplicationContext(), p, intent, PendingIntent.FLAG_CANCEL_CURRENT).cancel();

                    Messaggio m = dbManager.getMessaggio(p);

                    m.setInviato(true);

                    dbManager.updateMessaggio(m);

                    check = false;

                    carica();

                }

                if (dbManager.getNotSentMessages().size() <= 0) {
                    MainActivity.btnMessaggi.setVisibility(View.GONE);
                }

            }
        });

    }

    private void carica() {

        messaggi.clear();
        datum.clear();
        data.clear();

        messaggi = dbManager.getNotSentMessages();

        if (messaggi.size() > 0) {

            for (Messaggio messaggio : messaggi) {

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(messaggio.getData());
                String m = String.valueOf(c.get(Calendar.MINUTE));
                String h = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
                String g = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                String me = String.valueOf(c.get(Calendar.MONTH));
                int mese = Integer.valueOf(me);
                mese++;
                me = String.valueOf(mese);
                String a = String.valueOf(c.get(Calendar.YEAR));

                if (m.length() == 1) {
                    m = "0" + m;
                }
                if (h.length() == 1) {
                    h = "0" + h;
                }
                if (g.length() == 1) {
                    g = "0" + g;
                }
                if (me.length() == 1) {
                    me = "0" + me;
                }
                if (a.length() == 1) {
                    a = "0" + a;
                }

                if (String.valueOf(messaggio.getNome().charAt(messaggio.getNome().length() - 1)).equals("|")) {
                    messaggio.setNome(String.valueOf(messaggio.getNome().substring(0, messaggio.getNome().length() - 1)));
                }

                if (nome.equals(messaggio.getNome())) {
                    id.add(messaggio.getId());

                    datum.put("First Line", messaggio.getNome() + ": " + messaggio.getTesto() + "\n(" + h + ":" + m + " - " + g + "/" + me + "/" + a + ")");
                    data.add(datum);

                }

                if (nome.equals("tutti_i_messaggi_da_inviare_9821")) {
                    id.add(messaggio.getId());

                    datum.put("First Line", messaggio.getNome() + ": " + messaggio.getTesto() + "\n(" + h + ":" + m + " - " + g + "/" + me + "/" + a + ")");
                    data.add(datum);
                }

            }

            lista.setEnabled(true);
            b = true;

        } else {

            datum.put("First Line", getString(R.string.no_messaggi));
            data.add(datum);
            lista.setEnabled(false);
            b = false;

        }

        adapter = new SimpleAdapter(getApplicationContext(), data, R.layout.list_messaggio_modify, new String[]{"First Line"}, new int[]{R.id.textViewMessaggiModify}) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Button modify = (Button) v.findViewById(R.id.buttonModify);
                if (!b) {
                    modify.setVisibility(View.GONE);
                }
                modify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Object o = lista.getItemAtPosition(position);
                        String res = o.toString();

                        res = res.replace("{", "");
                        res = res.replace("}", "");
                        res = res.replace("=", "");
                        res = res.replace("\n", "");
                        res = res.replace("First Line", "");
                        String parts[] = res.split("\\(");

                        String dir1 = parts[0];

                        parts = dir1.split(":");

                        String n = parts[0];
                        String t = parts[1];

                        Intent i = new Intent(MessaggiActivity.this, ModifyActivity.class);
                        i.putExtra("date", messaggi.get(position).getData());
                        i.putExtra("t", t);
                        i.putExtra("m", messaggi.get(position).getNumero());
                        startActivity(i);

                    }
                });
                return v;
            }
        };

        lista.setAdapter(adapter);

    }
}