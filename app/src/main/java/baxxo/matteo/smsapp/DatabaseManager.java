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
                Inviato + " BOOLEAN" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + NameTable);

        onCreate(db);

    }

    public void aggiungiMessaggio(Messaggio messaggio) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Id, messaggio.getId()); // Messaggio Id
        values.put(Nome, messaggio.getNome()); // Messaggio Nome
        values.put(Numero, messaggio.getNumero()); // Messaggio Numero
        values.put(Testo, messaggio.getTesto()); // Messaggio testo
        values.put(Data, messaggio.getData()); // Messaggio data/ora
        values.put(Inviato, messaggio.getInviato()); // Messaggio inviato

        db.insert(NameTable, null, values);
        db.close();
    }

    public Messaggio getMessaggio(int i) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(NameTable, new String[]{Id, Nome, Numero, Testo, Data, Inviato}, Id + "=?", new String[]{String.valueOf(i)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Messaggio mess = new Messaggio(
                cursor.getString(0),//id
                cursor.getString(1),//nome
                cursor.getString(2),//numero
                cursor.getString(3),//testo
                Long.parseLong(cursor.getString(4)),//data/ora
                Boolean.parseBoolean(cursor.getString(5)));//inviato
        return mess;
    }

    public ArrayList<Cursor> getData(String selectQuery) {

        ArrayList<Cursor> result = new ArrayList<Cursor>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                result.add(cursor);
            } while (cursor.moveToNext());
        }
        return result;
    }

    public ArrayList<Messaggio> getAllMessages() {
        ArrayList<Messaggio> messaggioList = new ArrayList<Messaggio>();
        String selectQuery = "SELECT  * FROM " + NameTable;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Messaggio mess = new Messaggio();
                mess.setId(cursor.getString(0));//id
                mess.setNome(cursor.getString(1));//nome
                mess.setNumero(cursor.getString(2));//numero
                mess.setTesto(cursor.getString(3));//testo
                mess.setData(Long.parseLong(cursor.getString(4)));//data/ora
                mess.setInviato(Boolean.parseBoolean(cursor.getString(5)));//inivato
                messaggioList.add(mess);
            } while (cursor.moveToNext());
        }

        return messaggioList;
    }

    public ArrayList<Messaggio> getNotSentMessages() {
        ArrayList<Messaggio> messaggioList = new ArrayList<Messaggio>();
        String selectQuery = "SELECT  * FROM " + NameTable + " WHERE inviato = '0';";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Messaggio mess = new Messaggio();
                mess.setId(cursor.getString(0));//id
                mess.setNome(cursor.getString(1));//nome
                mess.setNumero(cursor.getString(2));//numero
                mess.setTesto(cursor.getString(3));//testo
                mess.setData(Long.parseLong(cursor.getString(4)));//data/ora
                mess.setInviato(Boolean.parseBoolean(cursor.getString(5)));//inivato
                messaggioList.add(mess);
            } while (cursor.moveToNext());
        }

        return messaggioList;
    }

    public int getMessagesCount() {

        int l;
        String countQuery = "SELECT  * FROM " + NameTable;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        l = cursor.getCount();
        cursor.close();
        return l;
    }

    public int updateMessaggio(Messaggio messaggio) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Id, messaggio.getId()); // Messaggio Id
        values.put(Nome, messaggio.getNome()); // Messaggio Nome
        values.put(Numero, messaggio.getNumero()); // Messaggio Numero
        values.put(Testo, messaggio.getTesto()); // Messaggio Testo
        values.put(Data, messaggio.getData()); // Messaggio Data
        values.put(Inviato, messaggio.getInviato()); // Messaggio Inviato(si o no);

        return db.update(NameTable, values, Id + " = ?",
                new String[]{String.valueOf(messaggio.getId())});
    }

    public void deleteMessage(Messaggio messaggio) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NameTable, Id + " = ?",
                new String[]{String.valueOf(messaggio.getId())});
        db.close();
    }

    public void deleteAllMessage() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + NameTable);
        db.close();
    }

}
