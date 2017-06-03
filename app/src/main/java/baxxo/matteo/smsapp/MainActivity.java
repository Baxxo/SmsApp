package baxxo.matteo.smsapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import static baxxo.matteo.smsapp.R.id.container;


public class MainActivity extends AppCompatActivity {

    public static ViewPager mViewPager;
    static EditText n;
    static EditText t;
    static DatePicker data;
    static TimePicker timepicker;
    static TextView conta;
    static TextView num;
    static TextView tes;
    private Calendar calendar;
    private TabLayout tabLayout;
    private TabsPagerAdapter tabsPagerAdapter;
    static RelativeLayout relativeLayout;
    public static FloatingActionButton fab;
    public static SharedPreferences preferences;
    static Context context;
    static boolean[] check = {false, false};
    int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    private int lunghezza;
    private int anno = 1;
    private int mese = 1;
    private int giorno = 1;
    private int ora = 1;
    private int minuto = 1;
    private String text;//testo per snackbar con data e ora
    private String testo;//testo preso per il messaggio
    private String numero;
    FragmentContatti myFragment = null;
    String nomeNumero;
    Dialog d;
    ArrayList<Contact> contact;
    DatabaseManager db;
    static int c = 0;
    int pos;
    static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.SET_ALARM,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.INSTALL_SHORTCUT
            }, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MainActivity.context = getApplicationContext();

        db = new DatabaseManager(getApplicationContext());

        preferences = getApplicationContext().getSharedPreferences("SmsApp", Context.MODE_PRIVATE);


        if (!preferences.getBoolean("icon", false)) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("icon", true);
            editor.apply();

            Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            shortcutintent.putExtra("duplicate", false);
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
            Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.unnamed);
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), MainActivity.class));
            sendBroadcast(shortcutintent);

        }


        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(tabsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                pos = position;
                if (position == 0) {
                    if (check[0] && check[1]) {
                    } else {
                        animOut();
                    }
                    fab.setImageResource(R.drawable.ic_dialog_email);
                }
                if (position == 1) {


                    ///ho fatto questo
                    MainActivity.toolbar.animate().setDuration(150).translationY(0);

                    AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
                    appBarLayout.setExpanded(true, true);
                    //fino a qui


                    fab.setImageResource(R.drawable.ic_search);
                    if (fab.getVisibility() == View.INVISIBLE) {
                        animIn();
                    } else {
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pos == 0) {
                    //se android è >= M allora uso il time e date picker
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //prendo l'ora e tempo
                        ora = timepicker.getHour();
                        minuto = timepicker.getMinute();
                        anno = data.getYear();
                        mese = data.getMonth();
                        giorno = data.getDayOfMonth();
                    } else {
                        //altrimenti due campi di testo
                        ora = timepicker.getCurrentHour();
                        minuto = timepicker.getCurrentMinute();
                        anno = data.getYear();
                        mese = data.getMonth();
                        giorno = data.getDayOfMonth();
                        //Log.i("ora minuto", ora + "-" + minuto);
                    }

                    //prendo il testo del messaggio e il numero
                    testo = String.valueOf(t.getText());
                    numero = String.valueOf(n.getText());

                    alarm();
                    testo = "";
                    t.setText("");

                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

                    switch (am.getRingerMode()) {
                        case AudioManager.RINGER_MODE_VIBRATE:
                            vibrator.vibrate(100);
                            break;
                        case AudioManager.RINGER_MODE_NORMAL:
                            vibrator.vibrate(100);
                            break;
                    }

                    String m = minuto + "";
                    if (m.length() == 1) {
                        m = "0" + m;
                    }
                    text = getString(R.string.snak1) + ora + ":" + m + getString(R.string.snak2) + giorno + "/" + mese + "/" + anno;
                    Snackbar.make(view, text, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                }
                if (pos == 1) {

                    if (myFragment.button.getVisibility() == View.VISIBLE) {
                        myFragment.button.setVisibility(View.INVISIBLE);
                        myFragment.nomeSearch.setVisibility(View.VISIBLE);
                    } else {
                        myFragment.button.setVisibility(View.VISIBLE);
                        myFragment.nomeSearch.setVisibility(View.INVISIBLE);
                        myFragment.nomeSearch.setText("");
                    }

                }
            }

        });

        Intent intent = new Intent(MainActivity.this, BootReceiver.class);

        sendBroadcast(intent);
    }

    //fine onCreate-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //funzione per impostare messaggio----------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void alarm() {

        nomeNumero = numero;

        //prendo il nome del destinatario
        if (!FragmentContatti.contatti.isEmpty()) {
            contact = FragmentContatti.contatti;
            for (int i = 0; i < contact.size(); i++) {
                if (numero.equals(contact.get(i).number)) {
                    nomeNumero = contact.get(i).name;
                }
            }
        }

        try {
            lunghezza = db.getMessagesCount();
        } catch (Exception e) {
            lunghezza = 0;
        }
        lunghezza = lunghezza + 1;


        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, giorno);
        calendar.set(Calendar.HOUR_OF_DAY, ora);
        calendar.set(Calendar.MINUTE, minuto);

        Intent intent = new Intent(this, Receiver.class);
        intent.putExtra("Numero", numero);
        intent.putExtra("Testo", testo);
        intent.putExtra("Nome", nomeNumero);
        intent.putExtra("Id", lunghezza + "");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), lunghezza, intent, lunghezza);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        //nuovo messaggio nel database

        Messaggio m = new Messaggio();
        m.setId(String.valueOf(lunghezza));
        m.setNome(nomeNumero);
        m.setNumero(numero);
        m.setTesto(testo);
        m.setData(calendar.getTimeInMillis());
        m.setInviato(false);

        db.aggiungiMessaggio(m);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, DisplayDatabase.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.data) {
            Intent intent = new Intent(MainActivity.this, AndroidDatabaseManager.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    //fine onCreate-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("destroy");
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------

    public static void animOut() {
        Animation animFadeOut = AnimationUtils.loadAnimation(context, R.anim.slide_down_animation);
        fab.setAnimation(animFadeOut);
        fab.setVisibility(View.INVISIBLE);
    }

    public static void animIn() {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.slide_up_animation);
        fab.setAnimation(animFadeIn);
        fab.setVisibility(View.VISIBLE);
    }

    static final TextWatcher contaNumeri = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count == 0) {
                check[1] = false;

                if (fab.getVisibility() == View.VISIBLE) {
                    animOut();
                }

            } else {
                if (!check[1]) {
                    check[1] = true;

                    if (check[0]) {
                        animIn();
                    }
                }
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    static final TextWatcher contaCaratteri = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            c = s.length();
            String t = context.getString(R.string.car1) + c + context.getString(R.string.car2);
            conta.setText(t);
            if (c == 0) {
                check[0] = false;

                if (fab.getVisibility() == View.VISIBLE) {
                    animOut();
                }

            } else {
                if (!check[0]) {
                    check[0] = true;

                    if (check[1]) {
                        animIn();
                    }
                }
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    //swipe----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_message);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_account_circle);
    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {

        private TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                return myFragment = new FragmentContatti();
            }

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getString(R.string.title2);
            }
            return getString(R.string.title1);
        }

    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------


    public void onBackPressed() {
        d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(true);
        d.setContentView(R.layout.esci);
        d.show();

        Button esci = (Button) d.findViewById(R.id.esci1);
        esci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        Button torna = (Button) d.findViewById(R.id.torna);
        torna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
    }

    //fragment----------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
            num = (TextView) rootView.findViewById(R.id.textView);
            tes = (TextView) rootView.findViewById(R.id.textView2);
            conta = (TextView) rootView.findViewById(R.id.textView3);
            data = (DatePicker) rootView.findViewById(R.id.datePicker);
            timepicker = (TimePicker) rootView.findViewById(R.id.timePicker);
            relativeLayout = (RelativeLayout) rootView.findViewById(R.id.relative);

            t.addTextChangedListener(contaCaratteri);
            n.addTextChangedListener(contaNumeri);

            return rootView;
        }
    }
}
