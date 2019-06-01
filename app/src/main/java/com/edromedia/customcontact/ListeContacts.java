package com.edromedia.customcontact;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListeContacts extends AppCompatActivity {
    TextView txt_contact;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_contacts);
        txt_contact = (TextView) findViewById(R.id.txt_contact);
        listView = (ListView) findViewById(R.id.listView);
        List<contact> cont = new ArrayList<contact>();

        displayContact(cont);

        MonAdapter adapter = new MonAdapter(this,R.layout.layout,cont);
        listView.setAdapter(adapter);

    }

    private void displayContact(List<contact> conta){

        String selection = "HAS_PHONE_NUMBER <> 0";
        String order = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor crContacts = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, order);

        while (crContacts.moveToNext()) {
            String nom = crContacts.getString(crContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = crContacts.getString(crContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contact c = new contact(upperFirst(nom), "", phoneNumber);
            conta.add(c);
        }
        crContacts.close();
    }

    private String upperFirst(String s){

        return(s.substring(0, 1).toUpperCase() + s.substring(1));
    }

}
