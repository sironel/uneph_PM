package com.edromedia.customcontact;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Content_Provider {
    Context context;
    ContentResolver contentResolver;

    public Content_Provider(Context context, ContentResolver contentResolver) {
        this.context = context;
        this.contentResolver = contentResolver;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ContentResolver getContentResolver() {
        return contentResolver;
    }

    public void setContentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void writeContact(Contact c) {
        ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<ContentProviderOperation>();
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        //insert Contact Family name and Given name using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, c.getNom())

               .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, c.getPrenom()).build());

        //insert mobile number using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, c.getTel())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public List<Contact> readContact(){
        List<Contact> conta = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor phone_cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String first_name ="";
        String last_name = "";
        String name = "";
        String phoneNumber = "";

        while (phone_cursor.moveToNext()) {
            try {
                int id = Integer.parseInt(phone_cursor.getString(phone_cursor.getColumnIndex
                        (ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
                Cursor name_cursor = cr.query(ContactsContract.Data.CONTENT_URI,null,
                        ContactsContract.Data.CONTACT_ID + "  = " + id, null, null);

                name = phone_cursor.getString(phone_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

               while (name_cursor.moveToNext()) {
                     if(name_cursor.getString(name_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))!=null)
                         {
                        first_name = name_cursor.getString(name_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                        last_name = name_cursor.getString(name_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                    }
               }
                name_cursor.close();
                phoneNumber = phone_cursor.getString(phone_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            } catch (Exception e) {
            }

            Contact c = new Contact("",(last_name).toUpperCase(), upperFirst(first_name), phoneNumber);
            conta.add(c);
        }
        phone_cursor.close();
        return conta;
    }

//    public void deleteContact(String firstName, String lastName, Context context) {
//        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//        ContentProviderOperation.Builder op = ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI);
//        op.withSelection(ContactsContract.RawContacts._ID+ " = ? ", new String[] {"1"});
//        ops.add(op.build());
//          Toast.makeText(context,"Contact supprimé avec succès. ", Toast.LENGTH_LONG).show();
//
//        try {
//            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//        } catch (RemoteException e) {
//
//            e.printStackTrace();
//        } catch (OperationApplicationException e) {
//
//            e.printStackTrace();
//        }
//    }

    private long getRawContactIdByName(String givenName, String familyName) {
        ContentResolver contentResolver = getContentResolver();
        // Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.
        // Create query column array.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};
        // Create where condition clause.
        String displayName = givenName + " " + familyName;
        String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + displayName + "'";
        // Query raw contact id through RawContacts uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;
        // Return the query cursor.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);
        long rawContactId = -1;
        if (cursor != null) {
            // Get contact count that has same display name, generally it should be one.
            int queryResultCount = cursor.getCount();
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if (queryResultCount > 0) {
                // Move to the first row in the result cursor.
                cursor.moveToFirst();
                // Get raw_contact_id.
                rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            }
        }
        return rawContactId;
    }


    public void deleteContact(String prenom, String nom, Context context)
    {
        // First select raw contact id by given name and family name.
        long rawContactId = getRawContactIdByName(prenom, nom);
        Toast.makeText(context,  Long.toString(rawContactId), Toast.LENGTH_SHORT).show();
        ContentResolver contentResolver = getContentResolver();
        //******************************* delete data table related data ****************************************
        // Data table content process uri.
        Uri dataContentUri = ContactsContract.Data.CONTENT_URI;
        // Create data table where clause.
        StringBuffer dataWhereClauseBuf = new StringBuffer();
        dataWhereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        dataWhereClauseBuf.append(" = ");
        dataWhereClauseBuf.append(rawContactId);
        // Delete all this contact related data in data table.
        contentResolver.delete(dataContentUri, dataWhereClauseBuf.toString(), null);
        //******************************** delete raw_contacts table related data ***************************************
        // raw_contacts table content process uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;
        // Create raw_contacts table where clause.
        StringBuffer rawContactWhereClause = new StringBuffer();
        rawContactWhereClause.append(ContactsContract.RawContacts._ID);
        rawContactWhereClause.append(" = ");
        rawContactWhereClause.append(rawContactId);
        // Delete raw_contacts table related data.
        contentResolver.delete(rawContactUri, rawContactWhereClause.toString(), null);
        //******************************** delete contacts table related data ***************************************
        // contacts table content process uri.
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
        // Create contacts table where clause.
        StringBuffer contactWhereClause = new StringBuffer();
        contactWhereClause.append(ContactsContract.Contacts._ID);
        contactWhereClause.append(" = ");
        contactWhereClause.append(rawContactId);
        // Delete raw_contacts table related data.
        contentResolver.delete(contactUri, contactWhereClause.toString(), null);

    }


    private String upperFirst(String s){
        return(s.substring(0, 1).toUpperCase() + s.substring(1));
    }


}
