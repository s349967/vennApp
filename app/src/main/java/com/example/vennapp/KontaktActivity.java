package com.example.vennapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.vennapp.database.DBHandlerKontakt;
import com.example.vennapp.database.DBHandlerKontaktAvtale;
import com.example.vennapp.database.models.Kontakt;

import java.util.List;

public class KontaktActivity extends AppCompatActivity {
    EditText fornavnInput;
    EditText etternavnInput;
    EditText friendId;
    EditText telefonInput;
    LinearLayout message;
    public static String PROVIDER_KONTAKT ="com.example.vennapp.contentprovider.KontaktProvider" ;
    public static final Uri CONTENT_KONTAKT_URI = Uri.parse("content://"+ PROVIDER_KONTAKT + "/kontakt");

    DBHandlerKontakt dbHelper;
    DBHandlerKontaktAvtale dbHelperKontaktAvtale;
    SQLiteDatabase db;


    public void slettKontakt(LinearLayout layout) {
        if(friendId.getText().toString().isEmpty())
            return;
        Long kontaktid = (Long.parseLong(friendId.getText().toString()));
        dbHelperKontaktAvtale.fjernAlleAvtalerFraKontakt(db,kontaktid);
        dbHelper.slettKontakt(db,kontaktid);

        try{
            getContentResolver().delete(Uri.parse("content://"+ PROVIDER_KONTAKT + "/kontakt/"+friendId.getText().toString()),null, new String[]{"_ID"});

        }
        catch (Exception ex){

    }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent mainIntent2= new Intent(KontaktActivity.this, MainActivity.class);
                startActivity(mainIntent2);
                return true;
            case R.id.home:
                Intent mainIntentHome= new Intent(KontaktActivity.this, MainActivity.class);
                startActivity(mainIntentHome);
                return true;
            case R.id.avtale:
                Intent i2 = new Intent(this, ResultatActivity.class);
                Intent mainIntentAvtale = new Intent(KontaktActivity.this, AvtaleActivity.class);
                startActivity(mainIntentAvtale);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontakt);
        fornavnInput = (EditText) findViewById(R.id.fornavnInput);
        etternavnInput = (EditText) findViewById(R.id.etternavnInput);
        telefonInput = (EditText) findViewById(R.id.telefonInput);

        Button oppdaterBtn =  findViewById(R.id.oppdaterBtn);
        Button slettBtn =  findViewById(R.id.slettBtn);

        friendId = (EditText) findViewById(R.id.kontaktId);
        message = (LinearLayout) findViewById(R.id.message);
        dbHelper = new DBHandlerKontakt(this);
        db=dbHelper.getWritableDatabase();
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        dbHelperKontaktAvtale = new DBHandlerKontaktAvtale(this);
        db=dbHelperKontaktAvtale.getWritableDatabase();
        String friendIdText = getIntent().getStringExtra("friendId");
        if(friendId != null)
            friendId.setText(friendIdText);

        String telefonText = getIntent().getStringExtra("telefon");

        if(telefonText != null )
            telefonInput.setText(telefonText);

        String fornavnText  = getIntent().getStringExtra("fornavn");
        fornavnInput.setText(fornavnText);

        String etternavnText = getIntent().getStringExtra("etternavn");
        etternavnInput.setText(etternavnText);


        slettBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slettKontakt(message);
            }
        });
        oppdaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oppdater(message);
            }
        });

    }
    public void oppdater(LinearLayout layout) {
        if(friendId.getText().toString().isEmpty())
            return;
        Kontakt kontakt = new Kontakt();

        kontakt.setFornavn(fornavnInput.getText().toString());
        kontakt.setEtternavn(etternavnInput.getText().toString());
        kontakt.setTelefonNummer(telefonInput.getText().toString());
        kontakt.set_ID(Long.parseLong(friendId.getText().toString()));
        dbHelper.oppdaterKontakt(db, kontakt);
        ContentValues v=new ContentValues();
        try{
            v.put("_ID",Long.parseLong(friendId.getText().toString()));
            v.put("Fornavn",fornavnInput.getText().toString());
            v.put("Etternavn",etternavnInput.getText().toString());
            v.put("Telefon",telefonInput.getText().toString());
            getContentResolver().update(Uri.parse("content://"+ PROVIDER_KONTAKT + "/kontakt/"+friendId.getText().toString()),v,null, new String[]{"_ID"});

        }
        catch (Exception ex){

        }


    }
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
