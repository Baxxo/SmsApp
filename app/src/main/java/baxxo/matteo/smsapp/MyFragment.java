package baxxo.matteo.smsapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MyFragment extends android.support.v4.app.Fragment {

    String numeroTelefono = "";
    Button tv;
    Button button;
    LinearLayout layout;
    LinearLayout.LayoutParams lp;
    public ArrayList<Button> buttons = new ArrayList<>();
    public static ArrayList<Contact> contatti = new ArrayList<>();
    ProgressBar progressBar;
    View rootView;

    public MyFragment() {

    }

    public void svuota(){
        System.out.println("Svuoto");
        buttons.clear();
        contatti.clear();
        layout.removeAllViews();
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.my_fragment, container, false);

        layout = (LinearLayout) rootView.findViewById(R.id.layout);

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

    public void getContact() {

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
        int j = 0;
        while (j == 0) {
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
                            tv.setText(contatti.get(i).name + ": " + contatti.get(i).number);
                            tv.setOnClickListener(btnClick);
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
