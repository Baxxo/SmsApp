package baxxo.matteo.smsapp;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactDetail extends AppCompatActivity {

    String nome;
    String numero;
    AppBarLayout appBar;
    ArrayList<Messaggio> mess;
    ArrayList<Messaggio> messEsatti = new ArrayList<>();
    ArrayList<String> messNumero = new ArrayList<>();
    ListView lv;
    String res;
    DatabaseManager db;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nome = getIntent().getStringExtra("nome");
        numero = getIntent().getStringExtra("numero");

        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contatto);
        toolbar.setTitle(nome);
        toolbar.setSubtitle(numero);
        setSupportActionBar(toolbar);

        appBar = (AppBarLayout) findViewById(R.id.app_bar);

        db = new DatabaseManager(getApplicationContext());

        mess = db.getAllMessages();

        lv = (ListView) findViewById(R.id.list_mess);
        Map<String, String> datum = new HashMap<String, String>(2);

        for (Messaggio m : mess) {

            if (m.getNumero().equals(numero)) {

                messNumero.add(m.getTesto());
                messEsatti.add(m);
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(m.getData());

                String min = String.valueOf(c.get(Calendar.MINUTE));
                String h = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
                String g = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                String me = String.valueOf(c.get(Calendar.MONTH));

                int mese = Integer.valueOf(me);
                mese++;
                me = String.valueOf(mese);
                String a = String.valueOf(c.get(Calendar.YEAR));

                if (min.length() == 1) {
                    min = "0" + min;
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

                datum.put("First Line", m.getTesto() + "\n(" + h + ":" + min + " - " + g + "/" + me + "/" + a + ")");
                data.add(datum);

            }

        }

        if (messNumero.size() == 0) {

            messNumero.add(getString(R.string.no_messaggi_detail));
            datum.put("First Line", messNumero.get(0));
            data.add(datum);
            lv.setEnabled(false);

        }

        SimpleAdapter list = new SimpleAdapter(this, data, R.layout.list_messaggi, new String[]{"First Line"}, new int[]{R.id.textViewMessaggi});
        lv.setAdapter(list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Object o = lv.getItemAtPosition(i);
                res = o.toString();
                res = res.replace("{", "");
                res = res.replace("}", "");
                res = res.replace("First Line=", "");

                String[] part = res.split("\\n");

                res = part[0];

                Dialog d = new Dialog(ContactDetail.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(true);
                d.setContentView(R.layout.info_messaggio);

                TextView testo = (TextView) d.findViewById(R.id.textView6);

                testo.setText(res);

                Button num = (Button) d.findViewById(R.id.button3);
                num.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(getApplicationContext(), getString(R.string.numero_caricato), Toast.LENGTH_LONG).show();

                        MainActivity.n.setText(numero);

                        MainActivity.mViewPager.setCurrentItem(1);

                        onBackPressed();

                    }
                });
                Button text = (Button) d.findViewById(R.id.button9);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(getApplicationContext(), getString(R.string.testo_caricato), Toast.LENGTH_LONG).show();

                        MainActivity.t.setText(res);

                        MainActivity.mViewPager.setCurrentItem(1);

                        onBackPressed();

                    }
                });
                Button numText = (Button) d.findViewById(R.id.button10);
                numText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(getApplicationContext(), getString(R.string.testo_numero), Toast.LENGTH_LONG).show();

                        MainActivity.n.setText(numero);
                        MainActivity.t.setText(res);

                        MainActivity.mViewPager.setCurrentItem(1);

                        onBackPressed();

                    }
                });

                d.show();

            }
        });

        Bitmap myBitmap = getPhoto(numero);

        Drawable drawable = new BitmapDrawable(getResources(), myBitmap);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            appBar.setBackground(drawable);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabContatto);
        fab.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), getString(R.string.numero_caricato), Toast.LENGTH_LONG).show();
                MainActivity.n.setText(numero);

                MainActivity.mViewPager.setCurrentItem(1);

                onBackPressed();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_messaggi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(ContactDetail.this, MessaggiActivity.class);
            intent.putExtra("Nome", nome);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public Bitmap getPhoto(String phoneNumber) {
        Uri phoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Uri photoUri;
        Bitmap defaultPhoto;
        ContentResolver cr = this.getContentResolver();
        Cursor contact = cr.query(phoneUri,
                new String[]{ContactsContract.Contacts._ID}, null, null, null);

        if (contact.moveToFirst()) {
            long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
            photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);

        } else {
            defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.default_wallpaper);
            return defaultPhoto;
        }
        if (photoUri != null) {
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, photoUri);
            if (input != null) {
                return BitmapFactory.decodeStream(input);
            }
        } else {
            defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.default_wallpaper);
            return defaultPhoto;
        }
        defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.default_wallpaper);
        contact.close();
        return defaultPhoto;
    }
}