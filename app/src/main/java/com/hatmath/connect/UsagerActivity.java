package com.hatmath.connect;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class UsagerActivity extends AppCompatActivity {

    public enum UserContext {
        JUST_SAVE,
        DEFAULT
    }

    private Usager usager;
    private Usager usagerOld;
    private boolean newUsager;
    private UsagerManager usagerManager;
    private EditText prenomEditText, nomEditText, surnomEditText, dateNaissanceEditText, professionEditText, emailEditText, passwordEditText, pinEditText, permissionsEditText;
    private EditText numeroEditText, rueEditText, villeEditText, provinceEditText, codePostalEditText, paysEditText;
    private CheckBox estAdminCheckBox, estValideCheckBox;
    private ExtendedFloatingActionButton addUserFAB, deleteUserFAB, uploadUserFAB, saveUserFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usager);

        // Activer le bouton de retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Initialisation des widgets
        prenomEditText = findViewById(R.id.prenomEditText);
        nomEditText = findViewById(R.id.nomEditText);
        surnomEditText = findViewById(R.id.surnomEditText);
        dateNaissanceEditText = findViewById(R.id.dateNaissanceEditText);
        professionEditText = findViewById(R.id.professionEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        pinEditText = findViewById(R.id.pinEditText);
        permissionsEditText = findViewById(R.id.permissionsEditText);
        estAdminCheckBox = findViewById(R.id.estAdminCheckBox);
        estValideCheckBox = findViewById(R.id.estValideCheckBox);

        numeroEditText = findViewById(R.id.numeroEditText);
        rueEditText = findViewById(R.id.rueEditText);
        villeEditText = findViewById(R.id.villeEditText);
        provinceEditText = findViewById(R.id.provinceEditText);
        codePostalEditText = findViewById(R.id.codePostalEditText);
        paysEditText = findViewById(R.id.paysEditText);

        addUserFAB = findViewById(R.id.addUserFAB);
        deleteUserFAB = findViewById(R.id.deleteUserFAB);
        uploadUserFAB = findViewById(R.id.uploadUserFAB);
        saveUserFAB = findViewById(R.id.saveUserFAB);
        setButtonVisibility(UserContext.JUST_SAVE);

        usagerManager = new UsagerManager();
        usager = usagerManager.getUsager(this.getIntent().getExtras().getString("email"));
        newUsager = (usager == null);
        if (!newUsager) {
            usagerOld = usager;
        }

        fillFieldsFromUsager();

        saveUserFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateUserFromFields()) {
                    if (newUsager) {
                        usagerManager.addUsager(usager);
                    } else {
                        usagerManager.updateUsager(usagerOld, usager);
                    }
                    Toast.makeText(UsagerActivity.this, "Usager sauvegardé", Toast.LENGTH_SHORT).show();
                    setResult(100);
                    finish();
                } else {
                    Toast.makeText(UsagerActivity.this, "Sauvegarde impossible. Vérifier vos données.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void fillFieldsFromUsager() {
        if (usager != null) {
            prenomEditText.setText(usager.getPrenom());
            nomEditText.setText(usager.getNom());
            surnomEditText.setText(usager.getSurnom());
            dateNaissanceEditText.setText(usager.getDateNaissance());
            professionEditText.setText(usager.getProfession());
            emailEditText.setText(usager.getEmail());
            passwordEditText.setText(usager.getPassword());
            estAdminCheckBox.setChecked(usager.estAdmin());
            estValideCheckBox.setChecked(usager.estValide());

            if (usager.getAdresse() != null) {
                numeroEditText.setText(usager.getAdresse().getNumero());
                rueEditText.setText(usager.getAdresse().getRue());
                villeEditText.setText(usager.getAdresse().getVille());
                provinceEditText.setText(usager.getAdresse().getProvince());
                codePostalEditText.setText(usager.getAdresse().getCodePostal());
                paysEditText.setText(usager.getAdresse().getPays());
            }
        }
    }

    private boolean updateUserFromFields() {
        boolean ok = false;
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (createUpdateUserIfValidEmailPassword(email, password)) {
            if (updateUserWithSecondaryFields()) {
                ok = true;
            }
        }
        return ok;
    }

    private boolean createUpdateUserIfValidEmailPassword(String email, String password) {
        boolean valide = false;
        if (Usager.validateEmail(email) && Usager.validatePassword(password)) {
            if (usagerManager.getUsager(email) == null && newUsager) {
                usager = new Usager(email, password, false, false);
                valide = true;
            } else if (usagerManager.getUsager(email) == null) {
                usager.setEmail(email);
                usager.setPassword(password);
                valide = true;
            } else if (!newUsager) {
                usager.setPassword(password);
                valide = true;
            } else if (newUsager) {
                Toast.makeText(UsagerActivity.this, "L'email existe déjà!", Toast.LENGTH_SHORT).show();
            }
        } else if (!Usager.validateEmail(email)) {
            Toast.makeText(UsagerActivity.this, "Veuillez entrer un email valide.", Toast.LENGTH_SHORT).show();
        } else if (!Usager.validatePassword(password)) {
            Toast.makeText(UsagerActivity.this, "Veuillez entrer un mot de passe valide.", Toast.LENGTH_SHORT).show();
        }
        return valide;
    }

    private boolean updateUserWithSecondaryFields() {
        boolean ok = false;
        usager.setPrenom(prenomEditText.getText().toString());
        usager.setNom(nomEditText.getText().toString());
        usager.setSurnom(surnomEditText.getText().toString());
        usager.setDateNaissance(dateNaissanceEditText.getText().toString());
        usager.setProfession(professionEditText.getText().toString());
        usager.setAdmin(estAdminCheckBox.isChecked());
        usager.setValide(estValideCheckBox.isChecked());
        usager.setPermissions(permissionsEditText.getText().toString());
        Adresse adresse = new Adresse();
        adresse.setNumero(numeroEditText.getText().toString());
        adresse.setRue(rueEditText.getText().toString());
        adresse.setVille(villeEditText.getText().toString());
        adresse.setProvince(provinceEditText.getText().toString());
        adresse.setCodePostal(codePostalEditText.getText().toString());
        adresse.setPays(paysEditText.getText().toString());
        usager.setAdresse(adresse);
        ok = true;
        return ok;
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

    public void setButtonVisibility(UserContext context) {
        switch (context) {
            case JUST_SAVE:
                saveUserFAB.setVisibility(View.VISIBLE);
                addUserFAB.setVisibility(View.INVISIBLE);
                uploadUserFAB.setVisibility(View.INVISIBLE);
                deleteUserFAB.setVisibility(View.INVISIBLE);
                break;

            case DEFAULT:
            default:
                saveUserFAB.setVisibility(View.VISIBLE);
                addUserFAB.setVisibility(View.VISIBLE);
                uploadUserFAB.setVisibility(View.VISIBLE);
                deleteUserFAB.setVisibility(View.VISIBLE);
                break;
        }
    }
}
