package com.edromedia.customcontact;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ServicesFileToDB {
    Context context;
    String filename;
    boolean externalFile;
    MySQLiteHelper Mydb = new MySQLiteHelper(context);
    File_Manage fm = new File_Manage(filename,context,externalFile);

    public ServicesFileToDB(Context context, String filename, boolean externalFile, MySQLiteHelper mydb, File_Manage fm) {
        this.context = context;
        this.filename = filename;
        this.externalFile = externalFile;
        Mydb = mydb;
        this.fm = fm;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isExternalFile() {
        return externalFile;
    }

    public void setExternalFile(boolean externalFile) {
        this.externalFile = externalFile;
    }

    public MySQLiteHelper getMydb() {
        return Mydb;
    }

    public void setMydb(MySQLiteHelper mydb) {
        Mydb = mydb;
    }

    public File_Manage getFm() {
        return fm;
    }

    public void setFm(File_Manage fm) {
        this.fm = fm;
    }

    public  int transfertFileToBD(){
        List<Contact> fmContact = new ArrayList<>();
        List<Contact> dbContact = new ArrayList<>();
        Contact cfl;
        Contact cbd;
        fmContact = fm.readFile();
        dbContact = Mydb.getAllCotacts();
        int n =0;
        boolean trouve = false;
        for(int i = 0 ; i < fmContact.size(); i++) {

            cfl = fmContact.get(i);
            for (int j = 0; j < dbContact.size(); i++) {
                cbd = dbContact.get(j);
                if ((cfl.getNom() == cbd.getNom()) && (cfl.getPrenom() == cbd.getPrenom()) && (cfl.getTel() == cbd.getTel())) {

                    trouve = true;
                    break;
                }
                n++;
            }

            if (!trouve) Mydb.insertContact(cfl);
        }
        return n;
    }

}
