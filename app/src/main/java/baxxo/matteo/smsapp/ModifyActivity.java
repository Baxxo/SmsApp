package baxxo.matteo.smsapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class ModifyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    long n;
    String t;
    String m;
    DatePickerDialog datePickerDialog;
    boolean changeD = false;
    boolean changeT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        Intent i = getIntent();

        n = i.getLongExtra("date", 0);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(n);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, ModifyActivity.this, year, month, day);

        t = i.getStringExtra("t");
        m = i.getStringExtra("m");

        EditText num = (EditText) findViewById(R.id.editNumero);
        num.setText(m);

        EditText txt = (EditText) findViewById(R.id.editMess);
        txt.setText(t);

        Button date = (Button) findViewById(R.id.buttonDate);
        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                changeD = true;
                datePickerDialog.show();
            }

        });

        Button time = (Button) findViewById(R.id.buttonTime);
        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                changeT = true;

                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(ModifyActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    }
                }, hour - 1, minute, false);

                timePickerDialog.show();

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Available soon ♥", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }
}
