package baxxo.matteo.smsapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private TabsPagerAdapter mTabsPagerAdapter;
    private ViewPager mViewPager;
    int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    MyFragment myFragment = null;
    private int anno = 1;
    private int mese = 1;
    private int giorno = 1;
    private int ora = 1;
    private int minuto = 1;
    private String testo;
    private String numero;
    String nomeNumero;
    static EditText n;
    static EditText t;
    static DatePicker data;
    static TimePicker timepicker;
    static EditText tempoString;
    static EditText dataString;
    static TextView hhmm;
    static TextView dataS;
    static TextView conta;
    private AlarmManager alarmManager;
    private String text;
    private Calendar calendar;
    // public static SharedPreferences sharedPreferences;
    ArrayList<Contact> contact = new ArrayList<Contact>();

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.SET_ALARM,
                    Manifest.permission.READ_CALENDAR
            }, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        mTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mTabsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //prendo l'ora
                //se android è >= M allora uso il time e date picker
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ora = timepicker.getHour();
                    minuto = timepicker.getMinute();
                    anno = data.getYear();
                    mese = data.getMonth();
                    giorno = data.getDayOfMonth();
                } else {
                    //altrimenti due campi di testo
                    String stringPar = String.valueOf(tempoString.getText());

                    String parts[] = stringPar.split(":");
                    ora = Integer.parseInt(parts[0]);
                    minuto = Integer.parseInt(parts[1]);

                    if (ora < 0) {
                        ora = 0;
                    }
                    if (ora > 23) {
                        ora = 23;
                    }
                    if (minuto < 0) {
                        minuto = 0;
                    }
                    if (minuto > 59) {
                        minuto = 59;
                    }
                    tempoString.setText(timepicker.getCurrentHour() + "/" + timepicker.getCurrentMinute());
                    stringPar = String.valueOf(dataString.getText());

                    String date[] = stringPar.split("/");
                    anno = Integer.parseInt(date[0]);
                    mese = Integer.parseInt(date[1]);
                    giorno = Integer.parseInt(date[2]);
                }
                //prendo il testo del messaggio e il numero
                testo = String.valueOf(t.getText());
                numero = String.valueOf(n.getText());

                if (testo.length() > 160) {

                    Toast.makeText(getApplicationContext(), "Troppi caratteri", Toast.LENGTH_LONG).show();

                } else {
                    alarm();
                    testo = "";
                    t.setText("");

                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                }
                String m = minuto + "";
                if (m.length() < 1) {
                    m = "0" + m;
                }
                text = "Il messaggio verrà inviato alle " + ora + ":" + m + " del " + giorno + "/" + mese + "/" + anno;
                Snackbar.make(view, text, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }

        });

    }

    public void alarm() {

        nomeNumero = numero;

        //prendo il nome del destinatario
        if (!MyFragment.contatti.isEmpty()) {
            contact = MyFragment.contatti;
            for (int i = 0; i < contact.size(); i++) {
                if (numero.equals(contact.get(i).number)) {
                    nomeNumero = contact.get(i).name;
                }
            }
        }
/*
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Numero", numero);
        editor.putString("Testo", testo);
        editor.putString("Nome", nomeNumero);
        editor.apply();*/

        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, giorno);
        calendar.set(Calendar.HOUR_OF_DAY, ora);
        calendar.set(Calendar.MINUTE, minuto);

        System.out.println(ora + " : " + minuto);

        Intent intent = new Intent(MainActivity.this, Receiver.class);
        intent.putExtra("Numero", numero);
        intent.putExtra("Testo", testo);
        intent.putExtra("Nome", nomeNumero);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
        myFragment.svuota();
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }

    //swipe----------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            n = (EditText) rootView.findViewById(R.id.Numero);
            t = (EditText) rootView.findViewById(R.id.Testo);
            conta = (TextView) rootView.findViewById(R.id.textView3);
            data = (DatePicker) rootView.findViewById(R.id.datePicker);
            timepicker = (TimePicker) rootView.findViewById(R.id.timePicker);
            tempoString = (EditText) rootView.findViewById(R.id.editText);
            dataString = (EditText) rootView.findViewById(R.id.editTextData);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            String formattedDate = df.format(c.getTime());
            dataString.setText(formattedDate);
            tempoString.setText(timepicker.getCurrentHour() + ":" + timepicker.getCurrentMinute());
            hhmm = (TextView) rootView.findViewById(R.id.textView4);
            dataS = (TextView) rootView.findViewById(R.id.textView6);
            t.addTextChangedListener(contaCaratteri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timepicker.setVisibility(View.VISIBLE);
                data.setVisibility(View.VISIBLE);

                tempoString.setVisibility(View.INVISIBLE);
                hhmm.setVisibility(View.INVISIBLE);
                dataS.setVisibility(View.INVISIBLE);
                dataString.setVisibility(View.INVISIBLE);
            } else {
                timepicker.setVisibility(View.INVISIBLE);
                data.setVisibility(View.INVISIBLE);

                tempoString.setVisibility(View.VISIBLE);
                hhmm.setVisibility(View.VISIBLE);
                dataS.setVisibility(View.VISIBLE);
                dataString.setVisibility(View.VISIBLE);
            }
            return rootView;
        }
    }


    static final TextWatcher contaCaratteri = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            conta.setText("Caratteri: " + String.valueOf(s.length()) + "/160");
        }

        public void afterTextChanged(Editable s) {
        }
    };

    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                return myFragment = new MyFragment();
            }

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getString(R.string.title2);
            }
            return getString(R.string.title1);
        }

    }
}
