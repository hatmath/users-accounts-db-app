package com.hatmath.connect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class CourrielActivity extends AppCompatActivity {

    private Button btnEnvoyerCourriel;
    private EditText etProvenance;
    private EditText etDestination;
    private EditText etSujet;
    private EditText etTexte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courriel);

        // Activer le bouton de retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        btnEnvoyerCourriel = findViewById(R.id.btnEnvoyerCourriel);
        etProvenance = findViewById(R.id.etProvenance);
        etDestination = findViewById(R.id.etDestination);
        etSujet = findViewById(R.id.etSujet);
        etTexte = findViewById(R.id.etTexte);

        etProvenance.setText(this.getIntent().getExtras().getString("de",""));
        etDestination.setText(this.getIntent().getExtras().getString("pour",""));
        //etSujet.setText(getString(R.string.sujet_courriel_test));
        //etTexte.setText(getString(R.string.texte_courriel_test));
        btnEnvoyerCourriel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                envoyerCourrielAvecAppParDefaut();
//                envoyerCourrielAvecGmail();
//                envoyerCourrielAvecMailgun();
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gérer le clic sur le bouton de retour
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void envoyerCourrielAvecAppParDefaut() {
        // Option pour utiliser l'application de messagerie de l'appareil
        // Fonctionne bien avec des versions anciennes de l'API android comme API 24 par exemple
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // seuls les clients de messagerie doivent gérer ceci

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{etDestination.getText().toString()});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, etSujet.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, etTexte.getText().toString());

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
            setResult(100);
        } else {
            // Aucun client de messagerie installé
            Toast.makeText(CourrielActivity.this, "Aucun client de messagerie trouvé", Toast.LENGTH_SHORT).show();
            setResult(400);
        }
    }

    private void envoyerCourrielAvecGmail() {
        // Tentez de lancer Gmail directement.
        // Gmail doit être installer et configuré une première fois avant
        // Présente des problèmes avec les versions récentes de l'API android
        // À regarder dans le futur
        Intent gmailIntent = new Intent(Intent.ACTION_VIEW);
        gmailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        gmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{etDestination.getText().toString()});
        gmailIntent.putExtra(Intent.EXTRA_SUBJECT, etSujet.getText().toString());
        gmailIntent.putExtra(Intent.EXTRA_TEXT, etTexte.getText().toString());

        if (gmailIntent.resolveActivity(getPackageManager()) != null) {
            Toast.makeText(CourrielActivity.this, "IN gmail", Toast.LENGTH_SHORT).show();
            startActivity(gmailIntent);
            setResult(100);
        } else {
            Toast.makeText(CourrielActivity.this, "Envoie via Gmail impossible présentement", Toast.LENGTH_SHORT).show();
            setResult(400);
        }
    }

    private void envoyerCourrielAvecMailgun() {
        // Tentez d'envoyer via l'API Mailgun
        // Ne fonctionne pas correctement pour l'instant
        // À regarder dans le futur
        Toast.makeText(CourrielActivity.this, "envoyerCourrielAvecMailgun", Toast.LENGTH_SHORT).show();
        OkHttpClient client = new OkHttpClient();

        RequestBody formData = new FormBody.Builder()
                .add("from", etProvenance.getText().toString())
                .add("to", etDestination.getText().toString())
                .add("subject", etSujet.getText().toString())
                .add("text", etTexte.getText().toString())
                .build();

        String credentials = "api:f6b27c959ae971d73624fbbfff18b33a-3750a53b-052f2246";
        final String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        Request request = new Request.Builder()
                .url("https://api.mailgun.net/v3/sandbox2c93204c42e14f94b1527a40c3ebbab3.mailgun.org/messages")
                .addHeader("Authorization", basicAuth)
                .post(formData)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Toast.makeText(CourrielActivity.this, "!response.isSuccessful()", Toast.LENGTH_SHORT).show();
                throw new IOException("Erreur inattendue : " + response);
            } else {
                Toast.makeText(CourrielActivity.this, "SUCCÈS", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(CourrielActivity.this, "IOException", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}