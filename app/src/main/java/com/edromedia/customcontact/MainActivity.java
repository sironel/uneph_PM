package com.edromedia.customcontact;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    EditText txt_nom;
    EditText txt_prenom;
    EditText txt_tel;
    Button btn_add,btn_liste;
    TextView txt_Compteur;
    static final String STATE_PASSAGE = "1";
    String passage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_nom = (EditText) findViewById(R.id.txt_nom);
        txt_prenom = (EditText) findViewById(R.id.txt_prenom);
        txt_tel = (EditText) findViewById(R.id.txt_tel);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_liste = (Button) findViewById(R.id.btn_liste);
        txt_Compteur = (TextView) findViewById(R.id.txt_Compteur);
        txt_nom.requestFocus();
        if (savedInstanceState != null)
            passage = savedInstanceState.getString(STATE_PASSAGE);
        else
            passage = STATE_PASSAGE;
           // Toast.makeText(MainActivity.this, "Passage: " + passage, Toast.LENGTH_SHORT).show();
            txt_Compteur.setText("Compteur de Passage: " + passage);

        btn_liste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intention = new Intent(MainActivity.this, ListeContacts.class);
               startActivity(intention);
            }

        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = txt_nom.getText().toString();
                String prenom = txt_prenom.getText().toString();
                String tel = txt_tel.getText().toString();
                if ((nom.length()>0) &&(prenom.length()>0)&&(tel.length()>0)){
                    contact c = new contact(nom, prenom, tel);
                    ajouterContact(c);
                    razo();
                    Toast.makeText(MainActivity.this,"Contact ajouté avec succès.",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(MainActivity.this,"Champs vides.",Toast.LENGTH_SHORT).show();
                }
            }


        });


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(STATE_PASSAGE,Integer.toString(Integer.parseInt(passage) + 1));
        super.onSaveInstanceState(savedInstanceState);
    }

    private void razo() {
        // remise a zero les champs de saisies
        txt_prenom.setText("");
        txt_tel.setText("");
        txt_nom.setText("");
        txt_nom.requestFocus();
    }

    private void ajouterContact(contact c){
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<ContentProviderOperation>();
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        //insert contact Family name using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, c.getNom()).build());

        //insert contact Given name using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, c.getPrenom()).build());

        //insert mobile number using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, c.getTel())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}