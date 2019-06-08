package com.edromedia.customcontact;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListeContacts extends AppCompatActivity {
    TextView txt_contact;
    ListView listView;
    List<Contact> cont;
    MonAdapter adapter;
    Integer b;
    int pos;
    boolean selected = false;
    Contact contact;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_contacts);
        txt_contact = (TextView) findViewById(R.id.txt_contact);
        listView = (ListView) findViewById(R.id.listView);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
         b = extras.getInt("rbtn");

        cont = new ArrayList<Contact>();

        //Utilisation de fichier pour lire les contacts
        if(b==2) {

            File_Manage f = new File_Manage("contacts.txt",this,true);
            cont = f.readFile();
            txt_contact.setText("Liste à partir de fichier ---- " + cont.size() + " Contacts");
        }
         //Utilisation de content provider pour lire les contacts
        if(b==1) {

            Content_Provider cp = new Content_Provider(this, getContentResolver());
            cont = cp.readContact();
            txt_contact.setText("Liste à partir de CP ----- " + cont.size() + " Contacts");
        }

        //Utilisation de la DB pour lire les contacts
        if(b==3) {

            MySQLiteHelper msh = new MySQLiteHelper(this);
            cont = msh.getAllCotacts();
            txt_contact.setText("Liste à partir de la BD ----- " + cont.size() + " Contacts");
        }

        adapter = new MonAdapter(this,R.layout.layout,cont);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                for (int i = 0; i < listView.getChildCount(); i++) {
                    if (position == i) {
                        listView.getChildAt(i).setBackgroundColor(Color.BLACK);
                    } else {
                        listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }
             pos = position;
                selected = true;
                    contact = new Contact(cont.get(position).getId(), cont.get(position).getNom(), cont.get(position).getPrenom(), cont.get(position).getTel());

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menucontacts, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        adapter = new MonAdapter(this,R.layout.layout,cont);
        listView.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String question ="Contact sera supprimé dans ";
        if (b==1)
            question = question + "le CP";
        if (b==2)
            question = question + "le fichier";
        if (b==3)
            question = question + "la DB";
        builder.setMessage(question);
        builder.setCancelable(false);
        builder.setTitle("Suppression de contact.");
        switch (item.getItemId()) {
            case R.id.delete:
             if (selected) {
                 builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         if (b == 1) {
                             Content_Provider cp1 = new Content_Provider(getApplicationContext(), getContentResolver());
                             cp1.deleteContact(getApplicationContext(),contact.getTel(),contact.getPrenom() + " " + contact.getNom());
                             cont.remove(pos);
                             Toast.makeText(getApplicationContext(), "Contact supprimé avec succès", Toast.LENGTH_LONG).show();

                         }

                         if (b == 2) {
                             File_Manage fl1 = new File_Manage("contacts.txt", getApplicationContext(), true);
                             Contact c = new Contact(contact.getId(), contact.getNom(), contact.getPrenom(), contact.getTel());
                             fl1.deleteContact(c);
                             cont.remove(pos);
                             Toast.makeText(getApplicationContext(), "Contact supprimé avec succès", Toast.LENGTH_LONG).show();
                         }

                         if (b == 3) {
                             MySQLiteHelper msh1 = new MySQLiteHelper(getApplicationContext());
                             msh1.deleteContact(contact.getId());
                             cont.remove(pos);

                             Toast.makeText(getApplicationContext(), "Contact supprimé avec succès", Toast.LENGTH_LONG).show();
                         }
                         adapter.notifyDataSetChanged();
                     }
                 });

                 // Set the negative/no button click click listener
                 builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         // Do something when click the negative button
                     }
                 });
                 AlertDialog dialog = builder.create();
                 // Display the alert dialog on interface
                 dialog.show();
                 selected = false;
             }else{

                 Toast.makeText(getApplicationContext(), "Aucun contact selectionné.", Toast.LENGTH_LONG).show();
             }
                return true;

            case R.id.lst_cp:

                Content_Provider cp = new Content_Provider(this, getContentResolver());
                cont = cp.readContact();
                txt_contact.setText("Liste à partir de CP ----- " + cont.size() + " Contacts");
                MonAdapter adapter = new MonAdapter(this,R.layout.layout,cont);
                b = 1;
                listView.setAdapter(adapter);
                return true;


            case R.id.lst_fl:

                File_Manage f = new File_Manage("contacts.txt",this,true);
                cont = f.readFile();
                txt_contact.setText("Liste à partir de fichier ---- " + cont.size() + " Contacts");
                adapter = new MonAdapter(this,R.layout.layout,cont);
                b = 2;
                listView.setAdapter(adapter);
                return true;

             case R.id.lst_db:
                MySQLiteHelper msh = new MySQLiteHelper(this);
                cont = msh.getAllCotacts();
                txt_contact.setText("Liste à partir de la BD ----- " + cont.size() + " Contacts");
                adapter = new MonAdapter(this,R.layout.layout,cont);
                b = 3;
                listView.setAdapter(adapter);
                return true;

            case R.id.fltodb:
                MySQLiteHelper ms = new MySQLiteHelper(this);
                File_Manage fi = new File_Manage("contacts.txt",this,true);
                ServicesFileToDB sftDB = new ServicesFileToDB(this,"contacts.txt",true,ms,fi);
                int nbTrans = sftDB.transfertFileToBD();
                Toast.makeText(this,nbTrans + " contact ajouté dans la Base de données. ",Toast.LENGTH_SHORT).show();

              cont = ms.getAllCotacts();
                txt_contact.setText("Liste à partir de la base de donnees ----- " + cont.size() + " Contacts");
              adapter = new MonAdapter(this,R.layout.layout,cont);
              listView.setAdapter(adapter);
                 return true;
            case R.id.exit:
                finish();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}
