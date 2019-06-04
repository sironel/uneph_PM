package com.edromedia.customcontact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    EditText txt_nom;
    EditText txt_prenom;
    EditText txt_tel;
    Button btn_add,btn_liste;
    TextView txt_Compteur;
    int rbtn = 1;
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
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        RadioButton rbCP = (RadioButton) findViewById(R.id.radioButtonCP);
        RadioButton rbfile = (RadioButton) findViewById(R.id.radioButtonFile);
        RadioButton rbDB = (RadioButton) findViewById(R.id.radioButtonDB);
        btn_liste.setText(("Liste (from CP)").toLowerCase());
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
                Bundle extras = new Bundle();
                extras.putInt("rbtn", rbtn);
               Intent i = new Intent(MainActivity.this, ListeContacts.class);
                i.putExtras(extras);
               startActivity(i);
            }

        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = txt_nom.getText().toString();
                String prenom = txt_prenom.getText().toString();
                String tel = txt_tel.getText().toString();
                if ((nom.length()>0) &&(prenom.length()>0)&&(tel.length()>0)){
                    Contact c = new Contact(nom, prenom, tel);

                    //Utilisation de fichier pour le stockage
                    if (rbtn == 2){
                      File_Manage f = new File_Manage("contacts.txt",MainActivity.this,true);
                      f.writeFile(c);
                    }

                    //utilisation de contentProvider Contacts du telephone
                   if (rbtn == 1){
                      Content_Provider cp =new Content_Provider(MainActivity.this,getContentResolver());
                      cp.writeContact(c);
                   }

                    //utilisation de la base de donnee
                    if (rbtn == 3){
                        MySQLiteHelper msh = new MySQLiteHelper(MainActivity.this);
                        msh.insertContact(c);
                    }

                    razo();
                    Toast.makeText(MainActivity.this,"Contact ajouté avec succès.",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(MainActivity.this,"Champs vides.",Toast.LENGTH_SHORT).show();
                }
            }


        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                switch (rb.getId()) {
                    case R.id.radioButtonCP:
                        btn_liste.setText(("Liste (from CP)").toLowerCase());
                       rbtn = 1;
                        break;
                    case R.id.radioButtonFile:
                        btn_liste.setText(("Liste (from File)").toLowerCase());
                      rbtn = 2;
                        break;
                    case R.id.radioButtonDB:
                        btn_liste.setText(("Liste (from DB)").toLowerCase());
                       rbtn = 3;

                }// End switch block
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


}