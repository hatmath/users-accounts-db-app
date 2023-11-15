// Nom de la classe : UsagerDbUtil
// Description:
// Classe utilitaire pour la manipulation des données d'utilisateurs (usagers)
// dans une base de données SQLite.
// Hérite de la classe `SQLiteOpenHelper` et implémente l'interface `UsagerDataUtil`.
// Permet de créer une table de base de données, d'écrire des données d'utilisateurs
// dans cette table, et de lire des données d'utilisateurs à partir de cette table.
// Elle assure la persistance des données d'utilisateurs dans une base de données SQLite.



package com.hatmath.connect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UsagerDbUtil extends SQLiteOpenHelper implements UsagerDataUtil {
    private static final String DATABASE_NAME = "UsagersDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USAGERS = "usagers";
    private static final String KEY_ID = "id";
    private static final String KEY_NOM = "nom";
    private static final String KEY_PRENOM = "prenom";
    private static final String KEY_DATE_NAISSANCE = "dateNaissance";
    private static final String KEY_ADRESSE = "adresse";
    private static final String KEY_PROFESSION = "profession";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EST_ADMIN = "estAdmin";
    private static final String KEY_EST_SUPER_USER = "estSuperUser";
    private static final String KEY_EST_VALIDE = "estValide";
    private static final String KEY_SURNOM = "surnom";
    private static final String KEY_PERMISSIONS = "permissions";
    private static final String KEY_ENCRYPTED_PIN = "encryptedPin";
    // Autres attributs...
    private static UsagerDbUtil instance;

    public UsagerDbUtil() {
        super(UsersAccountsApp.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized UsagerDbUtil getInstance() {
        if (instance == null) {
            instance = new UsagerDbUtil();
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USAGERS_TABLE = "CREATE TABLE " + TABLE_USAGERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NOM + " TEXT,"
                + KEY_PRENOM + " TEXT,"
                + KEY_DATE_NAISSANCE + " TEXT,"
                + KEY_ADRESSE + " TEXT,"
                + KEY_PROFESSION + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_EST_ADMIN + " INTEGER,"
                + KEY_EST_SUPER_USER + " INTEGER,"
                + KEY_EST_VALIDE + " INTEGER,"
                + KEY_SURNOM + " TEXT,"
                + KEY_PERMISSIONS + " TEXT,"
                + KEY_ENCRYPTED_PIN + " TEXT"
                + ")";
        db.execSQL(CREATE_USAGERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USAGERS);
        onCreate(db);
    }

    @Override
    public void writeUsagers(List<Usager> usagers) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Commencer une transaction pour que l'opération soit atomique
        db.beginTransaction();
        try {
            db.delete(TABLE_USAGERS, null, null);

            for (Usager usager : usagers) {
                ContentValues values = new ContentValues();
                values.put(KEY_NOM, usager.getNom());
                values.put(KEY_PRENOM, usager.getPrenom());
                values.put(KEY_DATE_NAISSANCE, usager.getDateNaissance());
                values.put(KEY_ADRESSE, usager.getAdresse().toString()); // Supposant que Adresse a une méthode toString()
                values.put(KEY_PROFESSION, usager.getProfession());
                values.put(KEY_EMAIL, usager.getEmail());
                values.put(KEY_PASSWORD, usager.getPassword());
                values.put(KEY_EST_ADMIN, usager.estAdmin() ? 1 : 0);
                values.put(KEY_EST_SUPER_USER, usager.estSuperUser() ? 1 : 0);
                values.put(KEY_EST_VALIDE, usager.estValide() ? 1 : 0);
                values.put(KEY_SURNOM, usager.getSurnom());
                values.put(KEY_PERMISSIONS, usager.getPermissions());
                // Supposant que encryptedPin est défini quelque part dans Usager
                values.put(KEY_ENCRYPTED_PIN, usager.getEncryptedPin());

                db.insert(TABLE_USAGERS, null, values);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        db.close();
    }

    @Override
    public List<Usager> readUsagers() {
        List<Usager> usagers = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USAGERS;
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                String nom = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NOM));
                String prenom = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRENOM));
                String dateNaissance = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_NAISSANCE));
                String adresseStr = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADRESSE));
                Adresse adresse = Adresse.fromString(adresseStr);
                String profession = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PROFESSION));
                String email = cursor.getString(cursor.getColumnIndexOrThrow(KEY_EMAIL));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PASSWORD));
                boolean estAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EST_ADMIN)) == 1;
                boolean estSuperUser = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EST_SUPER_USER)) == 1;
                boolean estValide = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_EST_VALIDE)) == 1;
                String surnom = cursor.getString(cursor.getColumnIndexOrThrow(KEY_SURNOM));
                String permissions = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PERMISSIONS));
                String encryptedPin = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ENCRYPTED_PIN));

                Usager usager = new Usager(nom, prenom, dateNaissance, adresse, profession, email, password, estAdmin, estValide);

                usager.setSuperUser(estSuperUser);
                usager.setPermissions(permissions);

                usagers.add(usager);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usagers;
    }
}
