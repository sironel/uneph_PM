package com.edromedia.customcontact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_contacts);
        txt_contact = (TextView) findViewById(R.id.txt_contact);
        listView = (ListView) findViewById(R.id.listView);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Integer b = extras.getInt("rbtn");

        cont = new ArrayList<Contact>();

        //Utilisation de fichier pour lire les contacts
        if(b==2) {
            txt_contact.setText("Liste à partir de fichier");
            File_Manage f = new File_Manage("contacts.txt",this,true);
            cont = f.readFile();
        }
        if(b==3) {
            txt_contact.setText("Liste à partir de Database");
          //  File_Manage f = new File_Manage("contacts.txt",this);
          //  cont = f.readFile();
        }

        //Utilisation de content provider pour lire les contacts
        if(b==1) {
            txt_contact.setText("Liste à partir de content Provider");
            Content_Provider cp = new Content_Provider(this, getContentResolver());
            cont = cp.readContact();
        }

        //Utilisation de la DB pour lire les contacts
        if(b==3) {
            txt_contact.setText("Liste à partir de la base de donnees");
            MySQLiteHelper msh = new MySQLiteHelper(this);
            cont = msh.getAllCotacts();

        }

        adapter = new MonAdapter(this,R.layout.layout,cont);
        listView.setAdapter(adapter);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menucontacts, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lst_cp:
                txt_contact.setText("Liste à partir de content Provider");
                Content_Provider cp = new Content_Provider(this, getContentResolver());
                cont = cp.readContact();
                MonAdapter adapter = new MonAdapter(this,R.layout.layout,cont);
                listView.setAdapter(adapter);
                return true;
            case R.id.lst_fl:
                txt_contact.setText("Liste à partir de fichier");
                File_Manage f = new File_Manage("contacts.txt",this,true);
                cont = f.readFile();
                adapter = new MonAdapter(this,R.layout.layout,cont);
                listView.setAdapter(adapter);
                return true;
            case R.id.lst_db:
                txt_contact.setText("Liste à partir de Database");
                MySQLiteHelper msh = new MySQLiteHelper(this);
                cont = msh.getAllCotacts();
                adapter = new MonAdapter(this,R.layout.layout,cont);
                listView.setAdapter(adapter);
                return true;
            case R.id.fltodb:
                txt_contact.setText("Liste à partir de Database");
                MySQLiteHelper ms = new MySQLiteHelper(this);
                File_Manage fi = new File_Manage("contacts.txt",this,true);
                ServicesFileToDB sftDB = new ServicesFileToDB(this,"contacts.txt",true,ms,fi);
                int nbTrans = sftDB.transfertFileToBD();
                Toast.makeText(this,nbTrans + " contact ajouté dans la Base de données. ",Toast.LENGTH_SHORT).show();

              cont = ms.getAllCotacts();
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
