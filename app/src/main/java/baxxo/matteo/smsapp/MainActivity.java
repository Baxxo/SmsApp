package baxxo.matteo.smsapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static baxxo.matteo.smsapp.R.id.container;


public class MainActivity extends AppCompatActivity {

    static ViewPager mViewPager;
    static EditText n;
    static EditText t;
    static DatePicker data;
    static TimePicker timepicker;
    static TextView conta;
    static TextView num;
    static TextView tes;
    static TextView nMessaggi;
    static RelativeLayout relativeLayout;
    static FloatingActionButton fab;
    static SharedPreferences preferences;
    static Context context;
    static Toolbar toolbar;
    static Button btnMessaggi;
    static boolean[] check = {false, false};
    static int c = 0;
    static int pos;
    static int nm = 1;
    private TabLayout tabLayout;
    private int anno = 1;
    private int mese = 1;
    private int giorno = 1;
    private int ora = 1;
    private int minuto = 1;
    private String text;//testo per snackbar con data e ora
    private String testo;//testo preso per il messaggio
    private String numero;
    boolean isSearch = false;
    FragmentContatti myFragment = null;
    String nomeNumero;
    Dialog d;
    ArrayList<Contact> contact;
    DatabaseManager db;
    Handler handler;
    AppBarLayout appBarLayout;
    Boolean pos0 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.SET_ALARM,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.INSTALL_SHORTCUT
            }, 1);
        }


        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true, true);

        context = getApplicationContext();

        db = new DatabaseManager(getApplicationContext());

        preferences = getApplicationContext().getSharedPreferences("SmsApp", Context.MODE_PRIVATE);

/*
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

        }*/


        TabsPagerAdapter tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(tabsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                pos = position;

                if (position == 1) {

                    if (!check[0] || !check[1]) {
                        if (fab.getVisibility() == View.VISIBLE) {
                            animOut();
                        }
                        fab.setVisibility(View.INVISIBLE);
                    }
                    if (check[0] && check[1]) {
                        if (fab.getVisibility() == View.INVISIBLE) {
                            animIn();
                        }
                    }

                    animFade();

                    fab.setImageResource(R.drawable.ic_dialog_email);

                }

                if (position == 0) {

                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    View v = getCurrentFocus();
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    toolbar.animate().setDuration(100).translationY(0);
                    appBarLayout.setExpanded(true, true);

                    animFade();

                    try {
                        if (!isSearch) {
                            fab.setImageResource(R.drawable.ic_search);
                            pos0 = true;
                        } else {
                            fab.setImageResource(R.drawable.ic_clear);
                            pos0 = false;
                        }

                        if (!FragmentContatti.carica) {
                            if (fab.getVisibility() == View.INVISIBLE) {
                                animIn();
                            }
                        } else {
                            if (fab.getVisibility() == View.VISIBLE) {
                                animOut();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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

                if (pos == 1) {

                    //se android è >= M allora uso getHour e getMinute
                    //prendo l'ora e tempo
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ora = timepicker.getHour();
                        minuto = timepicker.getMinute();
                        anno = data.getYear();
                        mese = data.getMonth();
                        giorno = data.getDayOfMonth();
                    } else {

                        ora = timepicker.getCurrentHour();
                        minuto = timepicker.getCurrentMinute();
                        anno = data.getYear();
                        mese = data.getMonth();
                        giorno = data.getDayOfMonth();
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
                    String oraconzero = String.valueOf(ora);
                    if (oraconzero.length() == 1) {
                        oraconzero = "0" + oraconzero;
                    }
                    String g = giorno + "";
                    if (g.length() == 1) {
                        g = "0" + g;
                    }
                    mese++;
                    String me = mese + "";
                    mese--;
                    if (me.length() == 1) {
                        me = "0" + me;
                    }

                    String a = anno + "";
                    if (a.length() == 1) {
                        a = "0" + a;
                    }
                    text = getString(R.string.snak1) + " " + oraconzero + ":" + m + " " + getString(R.string.snak2) + " " + g + "/" + me + "/" + a;
                    Snackbar.make(view, text, Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                }
                if (pos == 0) {

                    try {
                        ObjectAnimator.ofFloat(fab, "rotation", 0f, 360f).setDuration(500).start();
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isSearch) {
                                    fab.setImageResource(R.drawable.ic_clear);
                                    myFragment.setVisibitlyButton(View.INVISIBLE);
                                    myFragment.setVisibitlySearch(View.VISIBLE);
                                    isSearch = true;
                                } else {
                                    fab.setImageResource(R.drawable.ic_search);
                                    myFragment.setVisibitlyButton(View.VISIBLE);
                                    myFragment.setVisibitlySearch(View.INVISIBLE);
                                    isSearch = false;
                                }
                            }
                        }, 400);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        });

        btnMessaggi = (Button) findViewById(R.id.buttonMessaggi);
        if (db.getNotSentMessages().size() <= 0) {
            Log.i("Sizedb", "Sizedb: " + String.valueOf(db.getNotSentMessages().size()));
            btnMessaggi.setVisibility(View.GONE);
        }

        btnMessaggi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MessaggiActivity.class);
                intent.putExtra("Nome", "tutti_i_messaggi_da_inviare_9821");
                startActivity(intent);
            }
        });

        btnMessaggi.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                btnMessaggi.animate()
                        .translationY(-view.getHeight())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                btnMessaggi.setVisibility(View.INVISIBLE);
                            }
                        });
                return true;
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

        int lunghezza;
        try {
            lunghezza = db.getMessagesCount();
        } catch (Exception e) {
            lunghezza = 0;
        }
        lunghezza = lunghezza + 1;


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, giorno);
        calendar.set(Calendar.HOUR_OF_DAY, ora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.MONTH, mese);
        calendar.set(Calendar.YEAR, anno);

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
        btnMessaggi.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

      /*  if (id == R.id.action_settings) {

            d = new Dialog(this);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setCancelable(true);
            d.setContentView(R.layout.dialog);
            d.show();

            final EditText nome = (EditText) d.findViewById(R.id.editNome);
            final EditText pass = (EditText) d.findViewById(R.id.editPass);

           Button conf = (Button) d.findViewById(R.id.buttonConferma);
            conf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String n = String.valueOf(nome.getText());
                    String p = String.valueOf(pass.getText());
                    if (n.equals("Matteo")) {
                        if (p.equals("fufi")) {
                            Intent intent = new Intent(MainActivity.this, DisplayDatabase.class);
                            startActivity(intent);
                       }
                    } else {
                        Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT).show();

                    }
                }
            });
            Intent intent = new Intent(MainActivity.this, DisplayDatabase.class);
            startActivity(intent);
            return true;
        }
        ;
        if (id == R.id.data) {
            d = new Dialog(this);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setCancelable(true);
            d.setContentView(R.layout.dialog);
            d.show();

            final EditText nome = (EditText) d.findViewById(R.id.editNome);
            final EditText pass = (EditText) d.findViewById(R.id.editPass);

           Button conf = (Button) d.findViewById(R.id.buttonConferma);
            conf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String n = String.valueOf(nome.getText());
                   String p = String.valueOf(pass.getText());
                   if (n.equals("Matteo")) {
                        if (p.equals("fufi")) {
                            Intent intent = new Intent(MainActivity.this, AndroidDatabaseManager.class);
                            startActivity(intent);
                     }
                    } else {
                        Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Intent intent = new Intent(MainActivity.this, AndroidDatabaseManager.class);
            startActivity(intent);
            return true;
        }*/
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

    static void animFade() {
        if (fab.getVisibility() == View.VISIBLE) {
            Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            fab.setAnimation(animIn);
        }
    }

    public static void animIn() {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.slide_up_animation);
        fab.setAnimation(animFadeIn);
        fab.setVisibility(View.VISIBLE);
    }

    static final TextWatcher contaNumeri = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            contaNum(s.length());
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            contaNum(s.length());
        }

        public void afterTextChanged(Editable s) {
            contaNum(s.length());
        }
    };

    static final TextWatcher contaCaratteri = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            contaCar(s);
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            contaCar(s);
        }

        public void afterTextChanged(Editable s) {
            contaCar(s);
        }
    };

    static void contaNum(int count) {
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

    static void contaCar(CharSequence s) {
        c = s.length();

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


        if (c == 0) {
            nm = 0;
        } else {
            if (c > 160) {
                if (c > 160) {
                    c = c - (160 * nm);
                }
                while (c > 160) {
                    c = c - 160;
                    if (c > 160) {
                        nm++;
                    }
                }
                nm++;

            } else {
                nm = c / 160;
                if (nm <= 0) {
                    nm = 1;
                }
            }

        }


        conta.setText(context.getString(R.string.car1) + c + context.getString(R.string.car2));
        nMessaggi.setText(context.getString(R.string.n_sms) + " " + nm);
        nm = 1;
    }

    //swipe----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_account_circle);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_message);
    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {

        private TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (myFragment == null) {
                        return myFragment = new FragmentContatti();
                    } else {
                        return myFragment;
                    }
                case 1:
                    return new PlaceholderFragment();
                default:
                    return myFragment = new FragmentContatti();
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) {
                return getString(R.string.title1);
            }
            return getString(R.string.title2);
        }

    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------


    public void onBackPressed() {
        Log.i("Size", String.valueOf(db.getNotSentMessages().size()));
        if (db.getNotSentMessages().size() <= 0) {
            btnMessaggi.setVisibility(View.GONE);
        }
        if (pos == 0) {
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
        } else {

            mViewPager.setCurrentItem(0);

        }
    }

    //fragment----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {

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
            nMessaggi = (TextView) rootView.findViewById(R.id.textView4);

            t.addTextChangedListener(contaCaratteri);
            n.addTextChangedListener(contaNumeri);

            return rootView;
        }
    }
}