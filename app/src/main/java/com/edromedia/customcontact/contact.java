package com.edromedia.customcontact;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.ContactsContract;
import android.widget.Toast;

import java.util.ArrayList;

public class contact {
    private String nom;
    private String prenom;
    private String tel;

    public contact(String nom, String prenom, String tel) {
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }





}
