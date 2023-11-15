package com.hatmath.connect;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText signUpEmailEditText;
    private EditText signUpPasswordEditText;
    private Button registerButton;
    private UsagerManager usagerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpEmailEditText = findViewById(R.id.signUpEmailEditText);
        signUpPasswordEditText = findViewById(R.id.signUpPasswordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = signUpEmailEditText.getText().toString().trim();
                String password = signUpPasswordEditText.getText().toString().trim();

                if (Usager.validateEmail(email) && Usager.validatePassword(password)) {
                    usagerManager = new UsagerManager();
                    if (usagerManager.getUsager(email) == null) {
                        Usager newUser = new Usager(email, password, false, false);
                        usagerManager.addUsager(newUser);
                        Toast.makeText(SignUpActivity.this, "Inscription réussie!", Toast.LENGTH_SHORT).show();
                        setResult(100);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "L'email existe déjà!", Toast.LENGTH_SHORT).show();
                    }
                } else if (!Usager.validateEmail(email)) {
                    Toast.makeText(SignUpActivity.this, "Veuillez entrer un email valide.", Toast.LENGTH_SHORT).show();
                } else if (!Usager.validatePassword(password)) {
                    Toast.makeText(SignUpActivity.this, "Veuillez entrer un mot de passe valide.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Activer le bouton de retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gérer le clic sur le bouton de retour
        if (item.getItemId() == android.R.id.home) {
            setResult(101);
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


