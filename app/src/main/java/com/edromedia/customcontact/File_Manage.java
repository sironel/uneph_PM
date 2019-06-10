package com.edromedia.customcontact;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

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
        FileInputStream br;
        if (internalFile) {
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
        }else {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/download");
            dir.mkdirs();
            File file = new File(dir, "contacts.txt");
            try {//

                // ouverture du fichier pour lecture
                br = new FileInputStream(file);
                DataInputStream in = new DataInputStream(br);
                BufferedReader bro = new BufferedReader(new InputStreamReader(in));
                String info;
                while ((info = bro.readLine()) != null) {
                    infoContact = info.split("\t\t");
                    Contact c = new Contact(infoContact[0], (infoContact[1]).toUpperCase(), upperFirst(infoContact[2]), infoContact[3]);
                    conta.add(c);
                }
                // fermeture du Reader
                bro.close();
            } catch (Exception e) {
                // Si une erreur existe, l’afficher dans un Toast
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return conta;
    }


    private boolean recordFound(String id){
        String[] infoContact;
      boolean trouver = false;
        BufferedReader brInfo;
        FileInputStream br;
        if (internalFile) {
            try {
                // ouverture du fichier pour lecture
                brInfo = new BufferedReader(new InputStreamReader(context.openFileInput("contacts.txt")));
                // line est une variable qui stocke le contenu d’une ligne
                String info;
                while ((info = brInfo.readLine()) != null) {
                    infoContact = info.split("\t\t");
                    if ((infoContact[0]).equals(id))
                        trouver = true;
                    break;
                }
                // fermeture du Reader
                brInfo.close();
            } catch (Exception e) {
                // Si une erreur existe, l’afficher dans un Toast
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else{
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/download");
            dir.mkdirs();
            File file = new File(dir, "contacts.txt");
            try {
                // ouverture du fichier pour lecture
                br = new FileInputStream(file);
                DataInputStream in = new DataInputStream(br);
                BufferedReader bro = new BufferedReader(new InputStreamReader(in));
                   // info est une variable qui stocke le contenu d’une ligne
                String info;
                while ((info = bro.readLine()) != null) {
                    infoContact = info.split("\t\t");
                    if ((infoContact[0]).equals(id))
                        trouver = true;
                    Toast.makeText(context, "ID Trouvé", Toast.LENGTH_LONG).show();
                    break;
                }
                // fermeture du Reader
                br.close();
            } catch (Exception e) {
                // Si une erreur existe, l’afficher dans un Toast
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
        return trouver;
    }

    public  String generateID(){
        int li = nbRcordFile()+1;
     String id = Integer.toString(li);

        while (recordFound(id)){
            li++;
            id = Integer.toString(li);
        }
    return id;
    }


    public int nbRcordFile(){
      int nbrecord = 0;
      BufferedReader brInfo;
      FileInputStream br;
      if (internalFile) {
        try {
             brInfo = new BufferedReader(new InputStreamReader(context.openFileInput("contacts.txt")));
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
      }else{

          File root = android.os.Environment.getExternalStorageDirectory();
          File dir = new File (root.getAbsolutePath() + "/download");
          dir.mkdirs();
          File file = new File(dir, "contacts.txt");

          try {
              br = new FileInputStream(file);
              DataInputStream in = new DataInputStream(br);
              BufferedReader bro = new BufferedReader(new InputStreamReader(in));
              // info est une variable qui stocke le contenu d’une ligne

              while ((bro.readLine()) != null) {
                   nbrecord++;
              }
              Toast.makeText(context, "NB_record: "+nbrecord, Toast.LENGTH_LONG).show();
              br.close();
          }
          catch (Exception e)
          {
              Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
          }

      }
        return nbrecord;
    }



    public void writeFile(Contact c){
        //Ecrire les infos du Contact dans le fichier nom.txt
        if (internalFile) {
            try {

                BufferedWriter bw;
                File file = context.getFileStreamPath(filename);
                if (file == null || !file.exists()) {
                    bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE)));
                } else {
                    bw = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_APPEND)));
                }
                // generer l'id du contact
                String id = generateID();
                // écriture de la chaîne de caractère dans le fichier
                bw.write(id + "\t\t" + c.getNom() + "\t\t" + c.getPrenom() + "\t\t" + c.getTel());
                bw.newLine();
                // fermeture du fichier
                bw.close();
            }
        catch(Exception e){
                // Si une erreur existe, l’afficher dans un Toast
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else{
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/download");
            FileOutputStream f;
            dir.mkdirs();
            File file = new File(dir, "contacts.txt");
            try {
                if (!file.exists()) {
                     f = new FileOutputStream(file);
                }  else{
                     f = new FileOutputStream(file, true);}
                PrintWriter pw = new PrintWriter(f);
                String id = generateID();
                pw.println(id + "\t\t" + c.getNom() + "\t\t" + c.getPrenom() + "\t\t" + c.getTel());
                pw.flush();
                pw.close();
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

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

    public void updateContact(Contact c){
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
                    bw.write(c.getId() + "\t\t" + c.getNom() + "\t\t" + c.getPrenom() + "\t\t" + c.getTel());
                    bw.newLine();
                   // currentLine = "";
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
