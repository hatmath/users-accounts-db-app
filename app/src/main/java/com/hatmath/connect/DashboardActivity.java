package com.hatmath.connect;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    public enum UserContext {
        USER_SELECTED,
        NO_USER_SELECTED,
        ADMIN_USER_SELECTED,
        ADMIN_NO_USER_SELECTED,
        USER_USER_SELECTED,
        USER_NO_USER_SELECTED,
        DEFAULT
    }

    private TextView usersListTitle;
    private TextView connectedUser;
    private ListView usersListView;
    private ExtendedFloatingActionButton addUserFAB;
    private ExtendedFloatingActionButton deleteUserFAB;
    private ExtendedFloatingActionButton infoUserFAB;
    private ExtendedFloatingActionButton emailUserFAB;
    private UsagerManager usagerManager;
    private UsagerAdapter adapter;
    private List<UsagerListItem> namesList;
    private Usager selectedUsager = null;
    private int selectedPosition = -1;
    final static int USAGER_CALL_ID = 1234;
    final static int COURRIEL_CALL_ID = 5678;
    String loggedUserEmail;
    Usager loggedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        usagerManager = new UsagerManager();
        loggedUserEmail = this.getIntent().getExtras().getString("userEmail");
        loggedUser = usagerManager.getUsager(loggedUserEmail);

        usersListTitle = findViewById(R.id.usersListTitle);
        usersListTitle.setText("");

        connectedUser = findViewById(R.id.connectedUser);
        connectedUser.setText(connectedUser.getText().toString() + " ( " + loggedUserEmail + " )");

        usersListView = findViewById(R.id.usersListView);
        usersListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        namesList = new ArrayList<>();
        adapter = new UsagerAdapter(this, namesList);
        adapter.setSelectedItemPosition(selectedPosition);
        usersListView.setAdapter(adapter);

        addUserFAB = findViewById(R.id.addUserFAB);
        deleteUserFAB = findViewById(R.id.deleteUserFAB);
        infoUserFAB = findViewById(R.id.infoUserFAB);
        emailUserFAB = findViewById(R.id.emailUserFAB);
        setButtonVisibility(loggedUser.estAdmin() ? UserContext.ADMIN_NO_USER_SELECTED : UserContext.USER_NO_USER_SELECTED);

        loadUsagers();

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == selectedPosition) {
                    // L'utilisateur a cliqué sur l'élément déjà sélectionné, donc le désélectionner
                    usersListView.setItemChecked(position, false);
                    selectedUsager = null;
                    setButtonVisibility(loggedUser.estAdmin() ? UserContext.ADMIN_NO_USER_SELECTED : UserContext.USER_NO_USER_SELECTED);
                    selectedPosition = -1; // Réinitialiser la position sélectionnée
                } else {
                    // L'utilisateur a cliqué sur un nouvel élément, donc le sélectionner
                    usersListView.setItemChecked(position, true);
                    selectedUsager = usagerManager.getFilteredUsagers(loggedUser).get(position);
                    setButtonVisibility(loggedUser.estAdmin() ? UserContext.ADMIN_USER_SELECTED : UserContext.USER_USER_SELECTED);
                    selectedPosition = position; // Mettre à jour la position sélectionnée
                }

                adapter.setSelectedItemPosition(selectedPosition);
                adapter.notifyDataSetChanged();

            }
        });

        deleteUserFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedUsager != null) {
                    removeUsager(selectedUsager);
                    Toast.makeText(DashboardActivity.this, "Usager supprimé", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addUserFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent usagerIntent = new Intent(DashboardActivity.this, UsagerActivity.class);
                usagerIntent.putExtra("email", "");
                startActivityIfNeeded(usagerIntent, USAGER_CALL_ID);
            }
        });

        infoUserFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent infoIntent = new Intent(DashboardActivity.this, UsagerActivity.class);
                infoIntent.putExtra("email", selectedUsager.getEmail());
                startActivityIfNeeded(infoIntent, USAGER_CALL_ID);
            }
        });

        emailUserFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent courrielIntent = new Intent(DashboardActivity.this, CourrielActivity.class);
                courrielIntent.putExtra("de", loggedUserEmail);
                if (!loggedUser.estSuperUser() && !loggedUser.estAdmin()) {
                    courrielIntent.putExtra("pour", getString(R.string.courriel_support_usager));
                } else {
                    courrielIntent.putExtra("pour", selectedUsager.getEmail());
                }
                startActivityIfNeeded(courrielIntent, COURRIEL_CALL_ID);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String message = "Rien à signaler";
        if (requestCode == USAGER_CALL_ID) {
            message = (resultCode == 100) ? "Opération sur usager réussie" : "Opération sur usager annulée";
            loadUsagers();
            adapter.setSelectedItemPosition(-1);
            adapter.notifyDataSetChanged();
        } else if (requestCode == COURRIEL_CALL_ID) {
            message = (resultCode == 100) ? "Courriel envoyé" : "Envoie courriel annulé";
        }
        Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadUsagers() {
        usagerManager = new UsagerManager();
        List<Usager> usagers = usagerManager.getFilteredUsagers(loggedUser);
        namesList.clear();
        for (Usager usager : usagers) {
            int icon = getIcon(usager);
            if (!usager.getPrenom().isEmpty() || !usager.getNom().isEmpty()) {
                namesList.add(new UsagerListItem(usager.getPrenom() + " " + usager.getNom(), icon));
            } else {
                namesList.add(new UsagerListItem(usager.getEmail(), icon));
            }
        }
        usersListView.clearChoices();
        adapter.notifyDataSetChanged();
        selectedUsager = null;
        setButtonVisibility(loggedUser.estAdmin() ? UserContext.ADMIN_NO_USER_SELECTED : UserContext.USER_NO_USER_SELECTED);

    }

    private void removeUsager(Usager usager) {
        usagerManager.removeUsager(usager);
        loadUsagers();
    }

    private int getIcon(Usager usager) {
        if (usager.estSuperUser()) {
            return R.drawable.ic_super_usager;
        } else if (usager.estAdmin()) {
            return R.drawable.ic_admin;
        } else if (usager.estValide()) {
            return R.drawable.ic_usager;
        } else {
            return R.drawable.ic_usager_invalide;
        }
    }

    public void setButtonVisibility(UserContext context) {
        switch (context) {
            case USER_SELECTED:
                addUserFAB.setVisibility(View.INVISIBLE);
                deleteUserFAB.setVisibility(View.VISIBLE);
                infoUserFAB.setVisibility(View.VISIBLE);
                emailUserFAB.setVisibility(View.VISIBLE);
                break;

            case NO_USER_SELECTED:
                addUserFAB.setVisibility(View.VISIBLE);
                deleteUserFAB.setVisibility(View.INVISIBLE);
                infoUserFAB.setVisibility(View.INVISIBLE);
                emailUserFAB.setVisibility(View.INVISIBLE);
                break;

            case ADMIN_USER_SELECTED:
                addUserFAB.setVisibility(View.INVISIBLE);
                deleteUserFAB.setVisibility(View.VISIBLE);
                infoUserFAB.setVisibility(View.VISIBLE);
                emailUserFAB.setVisibility(View.VISIBLE);
                break;

            case ADMIN_NO_USER_SELECTED:
                addUserFAB.setVisibility(View.VISIBLE);
                deleteUserFAB.setVisibility(View.INVISIBLE);
                infoUserFAB.setVisibility(View.INVISIBLE);
                emailUserFAB.setVisibility(View.INVISIBLE);
                break;

            case USER_USER_SELECTED:
                addUserFAB.setVisibility(View.INVISIBLE);
                deleteUserFAB.setVisibility(View.VISIBLE);
                infoUserFAB.setVisibility(View.VISIBLE);
                emailUserFAB.setVisibility(View.VISIBLE);
                break;

            case USER_NO_USER_SELECTED:
                addUserFAB.setVisibility(View.INVISIBLE);
                deleteUserFAB.setVisibility(View.INVISIBLE);
                infoUserFAB.setVisibility(View.INVISIBLE);
                emailUserFAB.setVisibility(View.INVISIBLE);
                break;

            case DEFAULT:
            default:
                addUserFAB.setVisibility(View.VISIBLE);
                deleteUserFAB.setVisibility(View.VISIBLE);
                infoUserFAB.setVisibility(View.VISIBLE);
                emailUserFAB.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setTitle(getResources().getString(R.string.dashboard_title_fr));
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_parametres) {
            Toast.makeText(this, "Aucun paramètres", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_deconnexion) {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}