package com.hatmath.connect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    final static int SIGNUP_CALL_ID = 1234;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;
    private UsagerManager usagerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                usagerManager = new UsagerManager();
                Usager usager = usagerManager.getAuthenticateUser(email, password);

                if (usager != null) {
                    if (usager.estValide()) {
                        Intent dashboardIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                        dashboardIntent.putExtra("userEmail", usager.getEmail());
                        startActivity(dashboardIntent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Usager non validé pour l'instant", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }


            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityIfNeeded(signUpIntent, SIGNUP_CALL_ID);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNUP_CALL_ID) {
            // Pas necessaire d'afficher de code présentement
            // Toast.makeText(LoginActivity.this, "Code de retour: " + resultCode, Toast.LENGTH_SHORT).show();
        }
    }
}
