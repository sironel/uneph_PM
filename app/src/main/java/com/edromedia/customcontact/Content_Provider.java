package com.edromedia.customcontact;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
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
        int id = 0;
        String phoneNumber = "";

        while (phone_cursor.moveToNext()) {
            try {
                id = Integer.parseInt(phone_cursor.getString(phone_cursor.getColumnIndex
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

            Contact c = new Contact(String.valueOf(id),(last_name).toUpperCase(), upperFirst(first_name), phoneNumber);
            conta.add(c);
        }
        phone_cursor.close();
        return conta;
    }




    public static boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }


    private final static String[] DATA_COLS = {

            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.DATA1,//phone number
            ContactsContract.Data.CONTACT_ID
    };


   /// public static boolean updateContact(final Context context, String number, String newName, String newNumber) {
    public static boolean updateContact(final Context context, Contact oldContact, Contact newContact) {

        if (context == null || oldContact.getTel() == null || oldContact.getTel().trim().isEmpty()) return false;
        if (newContact.getTel() != null && newContact.getTel().trim().isEmpty()) newContact.setTel(null);
        if (newContact.getTel() == null) return false;

        String contactId = oldContact.getId();
        if (contactId == null) return false;

        //selection for name
        String where = String.format(
                "%s = '%s' AND %s = ?",
                DATA_COLS[0], //mimetype
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                DATA_COLS[2]/*contactId*/);

        String[] args = {contactId};
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(where, args)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, newContact.getPrenom())
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, newContact.getNom())
                        .build()
        );

        //change selection for number
        where = String.format(
                "%s = '%s' AND %s = ?",
                DATA_COLS[0],//mimetype
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                DATA_COLS[1]/*number*/);

        //change args for number
        args[0] = oldContact.getTel();

        operations.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(where, args)
                        .withValue(DATA_COLS[1]/*number*/, newContact.getTel())
                        .build()
        );

        try {

            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);

            for (ContentProviderResult result : results) {

                Log.d("Update Result", result.toString());
            }

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String upperFirst(String s){
        return(s.substring(0, 1).toUpperCase() + s.substring(1));
    }


}
