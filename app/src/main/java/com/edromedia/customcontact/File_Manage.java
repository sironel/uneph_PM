package com.edromedia.customcontact;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class File_Manage {
    private  String filename;
    private Context context;
    private boolean internalFile;


    public File_Manage(String filename, Context context, boolean internalFile) {
        this.filename = filename;
        this.context = context;
        this.internalFile = internalFile;

    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isInternalFile() {
        return internalFile;
    }

    public void setInternalFile(boolean internalFile) {
        this.internalFile = internalFile;
    }

    public List<Contact> readFile(){
        String[] infoContact;
        List<Contact> conta = new ArrayList<Contact>();

        try {
            // ouverture du fichier pour lecture
            BufferedReader brInfo = new BufferedReader(new InputStreamReader( context.openFileInput("contacts.txt")));
            // line est une variable qui stocke le contenu d’une ligne
            String info;
            while ((info = brInfo.readLine()) != null)
            {
                infoContact = info.split("\t\t");
                Contact c = new Contact( infoContact[0],(infoContact[1]).toUpperCase(), upperFirst(infoContact[2]), infoContact[3]);
                conta.add(c);
            }
            // fermeture du Reader
            brInfo.close();
        }
        catch (Exception e)
        {
            // Si une erreur existe, l’afficher dans un Toast
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return conta;
    }


    public boolean recordFound(String id){
        String[] infoContact;
      boolean trouver = false;
        try {
            // ouverture du fichier pour lecture
            BufferedReader brInfo = new BufferedReader(new InputStreamReader( context.openFileInput("contacts.txt")));
            // line est une variable qui stocke le contenu d’une ligne
            String info;
            while ((info = brInfo.readLine()) != null)
            {
                infoContact = info.split("\t\t");
                if((infoContact[0]).equals(id))
                    trouver = true;
                break;
            }
            // fermeture du Reader
            brInfo.close();
        }
        catch (Exception e)
        {
            // Si une erreur existe, l’afficher dans un Toast
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return trouver;
    }

    public  String generateID(){
     String id = Integer.toString(nbRcordFile() + 1);

        while (recordFound(id)){
            id = Integer.toString(Integer.parseInt(id) + 1);
        }
    return id;
    }


    public int nbRcordFile(){
      int nbrecord = 0;
        try {
            BufferedReader brInfo = new BufferedReader(new InputStreamReader( context.openFileInput("contacts.txt")));
            String info;
            while ((info = brInfo.readLine()) != null)
            {
               nbrecord++;
            }
               brInfo.close();
        }
        catch (Exception e)
        {
                 Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return nbrecord;
    }



    public void writeFile(Contact c){
        //Ecrire les infos du Contact dans le fichier nom.txt
        try {
            BufferedWriter bw;
            File file = context.getFileStreamPath(filename);
            if(file == null || !file.exists()) {
                     bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE)));
            }else{
                bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_APPEND)));
            }
            // generer l'id du contact
             String id = generateID();
            // écriture de la chaîne de caractère dans le fichier
            bw.write(id+"\t\t"+c.getNom()+"\t\t"+c.getPrenom()+"\t\t"+c.getTel());
            bw.newLine();
            // fermeture du fichier
            bw.close();
        }
        catch (Exception e) {
            // Si une erreur existe, l’afficher dans un Toast
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public void deleteContact(Contact c){
        String[] tab;
        try {
            BufferedWriter bw;
            File temp = context.getFileStreamPath("temp.txt");
            bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput("temp.txt", Context.MODE_PRIVATE)));

        String removeID = c.getId();
        String currentLine;

            File f = context.getFileStreamPath("contacts.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader( context.openFileInput("contacts.txt")));
        while((currentLine = br.readLine()) != null){
            tab = currentLine.split("\t\t");
            if((tab[0]).equals(removeID)){
                currentLine = "";
            }else {
                //   bw.write(currentLine + System.getProperty("line.separator"));
                bw.write(tab[0] + "\t\t" + tab[1] + "\t\t" + tab[2] + "\t\t" + tab[3]);
                bw.newLine();
            }
        }
        bw.close();
        br.close();
        boolean delete = f.delete();
        boolean b = temp.renameTo(f);

        }
        catch (Exception e) {
            // Si une erreur existe, l’afficher dans un Toast
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }


    private String upperFirst(String s){
        return(s.substring(0, 1).toUpperCase() + s.substring(1));
    }



}
