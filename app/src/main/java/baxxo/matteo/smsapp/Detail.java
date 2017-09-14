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
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class Detail extends AppCompatActivity {

    String nome;
    String numero;
    ArrayList<Messaggio> mess;
    ArrayList<Messaggio> messEsatti = new ArrayList<>();
    ArrayList<String> messNumero = new ArrayList<>();
    ListView lv;
    TextView nomnum;
    String res;
    DatabaseManager db;
    static Dialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        nome = getIntent().getStringExtra("nome");
        numero = getIntent().getStringExtra("numero");

        db = new DatabaseManager(getApplicationContext());

        mess = db.getAllMessages();

        lv = (ListView) findViewById(R.id.listaProva);

        nomnum = (TextView) findViewById(R.id.textView8);

        nomnum.setText(nome + "\n\n" + numero);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (Messaggio m : mess) {

                    if (m.getNumero().equals(numero)) {

                        messEsatti.add(m);

                    }
                }

                if (messEsatti.size() == 0) {
                    messNumero.add(getString(R.string.no_messaggi_detail));
                    lv.setEnabled(false);

                } else {

                    for (Messaggio m : messEsatti) {

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

                        messNumero.add(m.getTesto() + "\n(" + h + ":" + min + " - " + g + "/" + me + "/" + a + ")");

                    }

                }

                //Log.i("Size", String.valueOf(messNumero.size()));

                ArrayAdapter<String> list = new ArrayAdapter<>(getApplicationContext(), R.layout.list_messaggi, messNumero);

                lv.setAdapter(list);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Object o = lv.getItemAtPosition(i);
                        res = o.toString();
                        res = res.replace("{", "");
                        res = res.replace("}", "");
                        res = res.replace("First Line=", "");

                        String[] part = res.split("\\n");

                        res = part[0];

                        d = new Dialog(Detail.this);
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(true);
                        d.setContentView(R.layout.info_messaggio);

                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(messEsatti.get(i).getData());

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

                        TextView testo = (TextView) d.findViewById(R.id.textView6);

                        testo.setText(res + "\n\n(" + h + ":" + min + " - " + g + "/" + me + "/" + a + ")");

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
            }
        });

        Bitmap myBitmap = getPhoto(numero);

        Drawable drawable = new BitmapDrawable(getResources(), myBitmap);

        ImageView img = (ImageView) findViewById(R.id.circleView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            img.setImageDrawable(drawable);
        }

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
            Intent intent = new Intent(Detail.this, MessaggiActivity.class);
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
            defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_account_circle_white_48dp);
            return defaultPhoto;
        }
        if (photoUri != null) {
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, photoUri);
            if (input != null) {
                return BitmapFactory.decodeStream(input);
            }
        } else {
            defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_account_circle_white_48dp);
            return defaultPhoto;
        }
        defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_account_circle_white_48dp);
        contact.close();
        return defaultPhoto;
    }
}