package baxxo.matteo.smsapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class ModifyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    long n;
    String t;
    String numero;
    String text;
    DatePickerDialog datePickerDialog;
    TextView dataora;
    Calendar cal;
    int hour;
    int min;
    int day;
    int month;
    int year;
    String m;
    String oraconzero;
    String me;
    String g;
    String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        Intent i = getIntent();

        n = i.getLongExtra("date", 0);

        dataora = (TextView) findViewById(R.id.textViewDataOra);

        cal = Calendar.getInstance();
        cal.setTimeInMillis(n);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);

        m = min + "";
        if (m.length() == 1) {
            m = "0" + m;
        }

        oraconzero = String.valueOf(hour);
        if (oraconzero.length() == 1) {
            oraconzero = "0" + oraconzero;
        }

        g = day + "";
        if (g.length() == 1) {
            g = "0" + g;
        }

        month++;
        me = month + "";
        month--;
        if (me.length() == 1) {
            me = "0" + me;
        }

        a = year + "";
        if (a.length() == 1) {
            a = "0" + a;
        }

        text = getString(R.string.snak1) + "\n" + oraconzero + ":" + m + " " + getString(R.string.snak2) + " " + g + "/" + me + "/" + a;

        dataora.setText(text);

        datePickerDialog = new DatePickerDialog(this, ModifyActivity.this, year, month, day);

        t = i.getStringExtra("t");
        numero = i.getStringExtra("m");

        EditText num = (EditText) findViewById(R.id.editNumero);
        num.setText(numero);

        EditText txt = (EditText) findViewById(R.id.editMess);
        txt.setText(t);

        Button date = (Button) findViewById(R.id.buttonDate);
        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                datePickerDialog.show();
            }

        });

        Button time = (Button) findViewById(R.id.buttonTime);
        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(ModifyActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        min = minute;
                        hour = hourOfDay;

                        m = min + "";
                        if (m.length() == 1) {
                            m = "0" + m;
                        }

                        String oraconzero = String.valueOf(hour);
                        if (oraconzero.length() == 1) {
                            oraconzero = "0" + oraconzero;
                        }

                        text = getString(R.string.snak1) + "\n" + oraconzero + ":" + m + " " + getString(R.string.snak2) + " " + g + "/" + me + "/" + a;
                        dataora.setText(text);


                    }
                }, hour, min, false);

                timePickerDialog.show();

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Coming soon♥", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        year = i;
        month = i1;
        day = i2;

        g = day + "";
        if (g.length() == 1) {
            g = "0" + g;
        }

        month++;
        me = month + "";
        month--;
        if (me.length() == 1) {
            me = "0" + me;
        }

        a = year + "";
        if (a.length() == 1) {
            a = "0" + a;
        }

        text = getString(R.string.snak1) + "\n" + oraconzero + ":" + m + " " + getString(R.string.snak2) + " " + g + "/" + me + "/" + a;
        dataora.setText(text);


    }
}
