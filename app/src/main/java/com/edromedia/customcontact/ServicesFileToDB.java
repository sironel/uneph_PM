package com.edromedia.customcontact;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ServicesFileToDB {
    Context context;
    String filename;
    boolean internalFile=true;
    MySQLiteHelper Mydb = new MySQLiteHelper(context);
    File_Manage fm = new File_Manage(filename,context,internalFile);

    public ServicesFileToDB(Context context, String filename, boolean internalFile, MySQLiteHelper mydb, File_Manage fm) {
        this.context = context;
        this.filename = filename;
        this.internalFile = internalFile;
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
        return internalFile;
    }

    public void setExternalFile(boolean externalFile) {
        this.internalFile = internalFile;
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
        boolean trouve;
        for(int i = 0 ; i < fmContact.size(); i++) {
            trouve = false;
            cfl = fmContact.get(i);
            for (int j = 0; j < dbContact.size(); j++) {
                cbd = dbContact.get(j);
                if (cfl.getNom().equals(cbd.getNom()) && cfl.getPrenom().equals(cbd.getPrenom()) && cfl.getTel().equals(cbd.getTel())) {
                    trouve = true;
                    break;
                }

            }

            if (!trouve){
                Mydb.insertContact(cfl);
                n++;
            }
        }
        return n;
    }

}
