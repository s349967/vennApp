package com.example.vennapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KontaktActivity extends AppCompatActivity {
    EditText fornavnInput;
    EditText etternavnInput;
    EditText friendId;
    TextView fornavnError;
    TextView etternavnError;
    TextView telefonError;
    EditText telefonInput;
    LinearLayout message;
    String preFornavn;
    String preEtternavn;
    String preTelefon;
    TextView  responsKontakt;
    public static String PROVIDER_KONTAKT ="com.example.vennapp.contentprovider.KontaktProvider" ;

    DBHandlerKontakt dbHelper;
    DBHandlerKontaktAvtale dbHelperKontaktAvtale;
    SQLiteDatabase db;
    public static String PROVIDER_KONTAKTAVTALE ="com.example.vennapp.contentprovider.KontaktAvtaleProvider" ;

    public static final Uri CONTENT_KONTAKTAVTALE_URI = Uri.parse("content://"+ PROVIDER_KONTAKTAVTALE + "/kontaktavtale");


    public void slettKontakt(LinearLayout layout) {
        if (friendId.getText().toString().isEmpty())
            return;
        Long kontaktid = (Long.parseLong(friendId.getText().toString()));
        dbHelperKontaktAvtale.fjernAlleAvtalerFraKontakt(db, kontaktid);
        dbHelper.slettKontakt(db, kontaktid);
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);


            try {
                getContentResolver().delete(Uri.parse("content://" + PROVIDER_KONTAKT + "/kontakt/" + friendId.getText().toString()), null, null);

            } catch (Exception ex) {
            }
            try {
                getContentResolver().delete(Uri.parse("content://" + PROVIDER_KONTAKTAVTALE + "/kontaktavtale/" + String.valueOf(kontaktid)+"/*"), null, new String[]{String.valueOf(kontaktid)});
            }
            catch (Exception ex) {


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
                Intent mainIntent2= new Intent(KontaktActivity.this, KontaktListActivity.class);
                startActivity(mainIntent2);
                return true;
            case R.id.home:
                Intent mainIntentHome= new Intent(KontaktActivity.this, KontaktListActivity.class);
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

        fornavnError = (TextView) findViewById(R.id.fornavnError);
        etternavnError = (TextView) findViewById(R.id.etternavnError);
        telefonError = (TextView) findViewById(R.id.telefonError);

        Button oppdaterBtn =  findViewById(R.id.oppdaterBtn);
        Button slettBtn =  findViewById(R.id.slettBtn);
        Button resetBtn =  findViewById(R.id.resetKontakt);
        friendId = (EditText) findViewById(R.id.kontaktId);
        message = (LinearLayout) findViewById(R.id.message);
        dbHelper = new DBHandlerKontakt(this);
        db=dbHelper.getWritableDatabase();
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        dbHelperKontaktAvtale = new DBHandlerKontaktAvtale(this);
        responsKontakt = (TextView) findViewById(R.id.responsKontakt);
        String friendIdText = getIntent().getStringExtra("friendId");
        if(friendId != null)
            friendId.setText(friendIdText);

        String telefonText = getIntent().getStringExtra("telefon");
        preTelefon = telefonText;

        if(telefonText != null )
            telefonInput.setText(telefonText);

        String fornavnText  = getIntent().getStringExtra("fornavn");
        preFornavn = fornavnText;

        fornavnInput.setText(fornavnText);

        String etternavnText = getIntent().getStringExtra("etternavn");
        preEtternavn = etternavnText;

        etternavnInput.setText(etternavnText);

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fornavnInput.setText(preFornavn);
                etternavnInput.setText(preEtternavn);
                telefonInput.setText(preTelefon);
            }
        });
        slettBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slettKontakt(message);

                Intent mainIntent2= new Intent(KontaktActivity.this, KontaktListActivity.class);
                startActivity(mainIntent2);
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

        Pattern patternFornavn = Pattern.compile( "^[A-Za-z]{2,30}$");
        Matcher matcherFornavn = patternFornavn.matcher(fornavnInput.getText().toString());



        boolean found = true;
        if (!matcherFornavn.find()) {
            fornavnError.setText("Ugyldig fornavn");
            found = false;
        }
        else
            fornavnError.setText("");
        Pattern patternEtternavn = Pattern.compile( "^[A-Za-z]{2,30}$");
        Matcher matcherEtternavn = patternEtternavn.matcher(etternavnInput.getText().toString());
        if (!matcherEtternavn.find()) {
            etternavnError.setText("Ugyldig etternavn");
            found = false;
        }
        else
            etternavnError.setText("");

        Pattern patternTelefon = Pattern.compile( "^[+]?[0-9]{3,40}$");
        Matcher matcherTelefon = patternTelefon.matcher(telefonInput.getText().toString());
        if (!matcherTelefon.find()) {
            telefonError.setText("Ugyldig telefon nummer");
            found = false;
        }
        else
            telefonError.setText("");
        if(!found ){
            responsKontakt.setText("");
            return;
        }
        Kontakt kontakt = new Kontakt();

        kontakt.setFornavn(fornavnInput.getText().toString());
        kontakt.setEtternavn(etternavnInput.getText().toString());
        kontakt.setTelefonNummer(telefonInput.getText().toString());
        kontakt.set_ID(Long.parseLong(friendId.getText().toString()));
        dbHelper.oppdaterKontakt(db, kontakt);
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        responsKontakt.setText("Kontakten er oppdatert");
        preFornavn = fornavnInput.getText().toString();
        preEtternavn = etternavnInput.getText().toString();
        preTelefon = telefonInput.getText().toString();


            ContentValues v = new ContentValues();
            try {
                v.put("_ID", Long.parseLong(friendId.getText().toString()));
                v.put("Fornavn", fornavnInput.getText().toString());
                v.put("Etternavn", etternavnInput.getText().toString());
                v.put("Telefon", telefonInput.getText().toString());
                getContentResolver().update(Uri.parse("content://" + PROVIDER_KONTAKT + "/kontakt/" + friendId.getText().toString()), v, null, new String[]{"_id"});

            } catch (Exception ex) {

            }



    }
    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
