package baxxo.matteo.smsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Matteo on 15/04/2017.
 */

public class DatabaseManager extends SQLiteOpenHelper {

    private static String NameDB = "Mex";
    private String NameTable = "Messaggi";

    private String Id = "id";
    private String Nome = "nome";
    private String Numero = "numero";
    private String Testo = "testo";
    private String Data = "data";
    private String Ora = "ora";
    private String Inviato = "inviato";


    public DatabaseManager(Context context) {
        super(context, NameDB, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + NameTable + "("
                + Id + " INTEGER AUTO INCREMENT PRIMARY KEY ," +
                Nome + " TEXT," +
                Numero + " TEXT, " +
                Testo + " TEXT, " +
                Data + " TEXT, " +
                Ora + " TEXT, " +
                Inviato + " BOOLEAN" +
                ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + NameTable);

        // Create tables again
        onCreate(db);

    }

    // Adding new contact
    void aggiungiMessaggio(Messaggio messaggio) {
        SQLiteDatabase db = this.getWritableDatabase();

       /* Log.i("Id", messaggio.getId());
        Log.i("Nome", messaggio.getNome());
        Log.i("Numero", messaggio.getNumero());
        Log.i("Testo", messaggio.getTesto());
        Log.i("Data", String.valueOf(messaggio.getData()));
        Log.i("Inviato", messaggio.getInviato() + "");*/

        ContentValues values = new ContentValues();
        values.put(Id, messaggio.getId()); // Messaggio Id
        values.put(Nome, messaggio.getNome()); // Messaggio Nome
        values.put(Numero, messaggio.getNumero()); // Messaggio Numero
        values.put(Testo, messaggio.getTesto()); // Messaggio testo
        values.put(Data, messaggio.getData()); // Messaggio data/ora
        values.put(Inviato, messaggio.getInviato()); // Messaggio inviato

        // Inserting Row
        db.insert(NameTable, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    Messaggio getMessaggio(int i) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(NameTable, new String[]{Id,
                        Nome, Numero, Testo, Data, Ora, Inviato}, Id + "=?",
                new String[]{String.valueOf(i)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Messaggio mess = new Messaggio(
                cursor.getString(0),//id
                cursor.getString(1),//nome
                cursor.getString(2),//numero
                cursor.getString(3),//testo
                Long.parseLong(cursor.getString(4)),//data/ora
                Boolean.parseBoolean(cursor.getString(5)));//inviato
        // return contact
        return mess;
    }

    // Getting All Contacts
    public ArrayList<Messaggio> getAllMessages() {
        ArrayList<Messaggio> messaggioList = new ArrayList<Messaggio>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + NameTable;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Messaggio mess = new Messaggio();
                mess.setId(cursor.getString(0));//id
                mess.setNome(cursor.getString(1));//nome
                mess.setNumero(cursor.getString(2));//numero
                mess.setTesto(cursor.getString(3));//testo
                mess.setData(Long.parseLong(cursor.getString(4)));//data/ora
                mess.setInviato(Boolean.parseBoolean(cursor.getString(5)));//inivato
                // Adding contact to list
                messaggioList.add(mess);
            } while (cursor.moveToNext());
        }

        // return contact list
        return messaggioList;
    }

    public ArrayList<Messaggio> getNotSentMessages() {
        ArrayList<Messaggio> messaggioList = new ArrayList<Messaggio>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + NameTable + " WHERE inviato = 'false';";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Messaggio mess = new Messaggio();
                mess.setId(cursor.getString(0));//id
                mess.setNome(cursor.getString(1));//nome
                mess.setNumero(cursor.getString(2));//numero
                mess.setTesto(cursor.getString(3));//testo
                mess.setData(Long.parseLong(cursor.getString(4)));//data/ora
                mess.setInviato(Boolean.parseBoolean(cursor.getString(5)));//inivato
                // Adding contact to list
                messaggioList.add(mess);
            } while (cursor.moveToNext());
        }

        // return contact list
        return messaggioList;
    }

    // Getting contacts Count
    public int getMessagesCount() {

        int l;
        String countQuery = "SELECT  * FROM " + NameTable;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        l = cursor.getCount();
        cursor.close();
        //Log.i("Size db", String.valueOf(l));

        // return count
        return l;
    }

    // Updating single contact
    public int updateMessaggio(Messaggio messaggio) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Id, messaggio.getId()); // Messaggio Id
        values.put(Nome, messaggio.getNome()); // Messaggio Nome
        values.put(Numero, messaggio.getNumero()); // Messaggio Numero
        values.put(Testo, messaggio.getTesto()); // Messaggio Testo
        values.put(Data, messaggio.getData()); // Messaggio Data
        values.put(Inviato, messaggio.getInviato()); // Messaggio Inviato(si o no);

        // updating row
        return db.update(NameTable, values, Id + " = ?",
                new String[]{String.valueOf(messaggio.getId())});
    }

    // Deleting single contact
    public void deleteMessage(Messaggio messaggio) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NameTable, Id + " = ?",
                new String[]{String.valueOf(messaggio.getId())});
        db.close();
    }

}
