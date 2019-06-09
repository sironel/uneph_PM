package com.edromedia.customcontact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Contact.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NOM = "nom";
    public static final String CONTACTS_COLUMN_PRENOM = "prenom";
    public static final String CONTACTS_COLUMN_TEL = "tel";
    private HashMap hp;

    // Commande sql pour la création de la base de données
//    private static final String DATABASE_CREATE = "create table "
//            + CONTACTS_TABLE_NAME + "(" + CONTACTS_COLUMN_ID
//            + " integer primary key autoincrement, " + COLUMN_NOM + " text, " + COLUMN_PRENOM + " text, " + COLUMN_TEL +" text);";



    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
     }

    @Override
    public void onCreate(SQLiteDatabase database) {

        database.execSQL(
                "create table contacts " +
                        "(id integer primary key, nom text,prenom text,tel text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + "contacts");
        onCreate(db);
    }

    public boolean insertContact (Contact c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nom", c.getNom());
        contentValues.put("prenom", c.getPrenom());
        contentValues.put("tel", c.getTel());
        db.insert("contacts", null, contentValues);
        return true;
    }

    public boolean updateContact (Contact c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", c.getId());
        contentValues.put("nom", c.getNom());
        contentValues.put("prenom", c.getPrenom());
        contentValues.put("tel", c.getTel());

        db.update("contacts", contentValues, "id = ? ", new String[] {c.getId()} );
        return true;
    }



    public void deleteContact(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "id=?";
        String whereArgs[] = {id};
        db.delete("contacts", whereClause, whereArgs);
    }

    public List<Contact> getAllCotacts() {
        ArrayList<Contact> array_list = new ArrayList<Contact>();
        Contact conta = new Contact("","","","");
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            conta = cursorToContact(res);
            array_list.add(conta);
            res.moveToNext();
        }
        return array_list;
    }

    private Contact cursorToContact(Cursor cursor) {
        Contact co= new Contact("","","","");
        co.setId((cursor.getString(0)));
        co.setNom((cursor.getString(1)).toUpperCase());
        co.setPrenom(upperFirst(cursor.getString(2)));
        co.setTel(cursor.getString(3));

        return co;
    }

    private String upperFirst(String s){
        return(s.substring(0, 1).toUpperCase() + s.substring(1));
    }

}
