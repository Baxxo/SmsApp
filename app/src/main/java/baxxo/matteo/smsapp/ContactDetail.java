package baxxo.matteo.smsapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;

public class ContactDetail extends AppCompatActivity {

    String nome;
    String numero;
    AppBarLayout appBar;
    ArrayList<Messaggio> mess;
    ArrayList<String> messNumero = new ArrayList<>();
    ListView lv;
    ArrayAdapter<String> list;
    DatabaseManager db;

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
        //Log.i("mess", mess.size() + "");

        lv = (ListView) findViewById(R.id.list_mess);

        for (Messaggio m : mess) {
            if (m.getNumero().equals(numero)) {
                messNumero.add(m.getTesto());
            }
        }

        if (messNumero.size() == 0) {

            messNumero.add(getString(R.string.no_messaggi));
            lv.setEnabled(false);

        }

        list = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, messNumero);

        lv.setAdapter(list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = lv.getItemAtPosition(i);
                String res = o.toString();
                res = res.replace("{", "");
                res = res.replace("}", "");


                Toast.makeText(getApplicationContext(), getString(R.string.testo_numero), Toast.LENGTH_LONG).show();

                MainActivity.n.setText(numero);
                MainActivity.t.setText(res);

                onBackPressed();

            }
        });

        Bitmap myBitmap = getPhoto(numero);

        Drawable drawable = new BitmapDrawable(getResources(), myBitmap);
        appBar.setBackground(drawable);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabContatto);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), getString(R.string.numero_caricato), Toast.LENGTH_LONG).show();
                MainActivity.n.setText(numero);
                onBackPressed();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messaggi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(ContactDetail.this, MessaggiActivity.class);
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
        ContentResolver cr = this.getContentResolver();
        Cursor contact = cr.query(phoneUri,
                new String[]{ContactsContract.Contacts._ID}, null, null, null);

        if (contact.moveToFirst()) {
            long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
            photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);

        } else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.default_wallpaper);
            return defaultPhoto;
        }
        if (photoUri != null) {
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, photoUri);
            if (input != null) {
                return BitmapFactory.decodeStream(input);
            }
        } else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.default_wallpaper);
            return defaultPhoto;
        }
        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.default_wallpaper);
        return defaultPhoto;
    }
}
