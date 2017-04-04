package baxxo.matteo.smsapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Matteo on 11/02/2017.
 */

public class FragmentContatti extends android.support.v4.app.Fragment {

    String numeroTelefono = "";
    String nomeTelefono = "";
    Button tv;
    Button button;
    Button search;
    LinearLayout layout;
    LinearLayout.LayoutParams lp;
    public ArrayList<Button> buttons = new ArrayList<>();
    public static ArrayList<Contact> contatti = new ArrayList<>();
    ProgressBar progressBar;
    View rootView;
    Dialog d;
    WindowManager.LayoutParams layoutParams;
    TextView tvNome;
    TextView tvNumero;

    public FragmentContatti() {

    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        d = new Dialog(rootView.getContext());
        d.setTitle("Numero");
        d.setCancelable(true);
        d.setContentView(R.layout.dialog);

        tvNumero = (TextView) d.findViewById(R.id.numero);
        tvNome = (TextView) d.findViewById(R.id.nome);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(d.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        layout = (LinearLayout) rootView.findViewById(R.id.layout);
        search = (Button) rootView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO creare dialog per cercare nome
            }
        });

        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(5, 10, 10, 5);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        button = (Button) rootView.findViewById(R.id.button);
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


        return rootView;
    }

    View.OnClickListener btnClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int id = view.getId();
            numeroTelefono = contatti.get(id).number;
            MainActivity.n.setText(numeroTelefono);
            Toast.makeText(rootView.getContext(), "Numero caricato", Toast.LENGTH_SHORT).show();
        }
    };
    View.OnLongClickListener btnLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            // TODO trovare il numero da far vedere
            int id = v.getId();

            nomeTelefono = contatti.get(id).name;
            numeroTelefono = contatti.get(id).number;
            tvNome.setText(nomeTelefono);
            tvNumero.setText(numeroTelefono);

            d.show();
            d.getWindow().setAttributes(layoutParams);
            return true;
        }
    };


    public void getContact() {
        if (buttons.isEmpty() == false) {
            buttons.clear();
        }
        if (contatti.isEmpty() == false) {
            contatti.clear();
        }
        ((Activity) rootView.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (layout.getChildCount() > 0) {
                    layout.removeAllViews();
                }
            }
        });
        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
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
        aggiungi();

    }

    private void aggiungi() {
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
        //se j = 0 allora non ci sono contatti uguali altrimenti continuo
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
                        for (int i = 0; i < contatti.size(); i++) {
                            tv = new Button(getActivity());
                            tv.setId(i);
                            tv.setBackgroundResource(R.drawable.backbutton);
                            tv.setLayoutParams(lp);
                            tv.setText(contatti.get(i).name);
                            tv.setLongClickable(true);
                            tv.setOnClickListener(btnClick);
                            tv.setOnLongClickListener(btnLongClick);
                            buttons.add(tv);
                            layout.addView(buttons.get(i));
                        }
                        TextView text = new TextView(getActivity());
                        text.setLayoutParams(lp);
                        text.setLayoutParams(new LinearLayout.LayoutParams(10, contatti.size() / 2));
                        text.setText("");
                        layout.addView(text);
                        progressBar.setVisibility(View.INVISIBLE);
                        button.setText("Ricarica Lista");
                        button.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

}
