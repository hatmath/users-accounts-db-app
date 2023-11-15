package com.hatmath.connect;

import android.util.Base64;
import android.util.Patterns;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Usager implements Serializable, Comparable<Usager> {
    private String nom;
    private String prenom;
    private String dateNaissance;
    private Adresse adresse;
    private String profession;
    private String email;
    private String password;
    private boolean estAdmin;
    private boolean estSuperUser;
    private String encryptedPin;

    private boolean estValide;
    private String surnom;
    private String permissions;
    // Texte avec n'importe quel combinaison de caractère crud (Create Read Update Delete)
    // Par exemple, "r" "ru" "cru" "crud"

    // Constructeur
    public Usager(String email, String password, boolean estAdmin, boolean estValide) {
        this.email = email;
        this.password = password;
        this.estAdmin = estAdmin;
        this.nom = "";
        this.prenom = "";
        this.dateNaissance = "";
        this.adresse = new Adresse();
        this.profession = "";
        this.estSuperUser = false;
        this.estValide = estValide;
        this.surnom = "";
        this.permissions = "";

    }

    public Usager(String nom, String prenom, String dateNaissance, Adresse adresse,
                  String profession, String email, String password, boolean estAdmin, boolean estValide) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.profession = profession;
        this.email = email;
        this.password = password;
        this.estAdmin = estAdmin;
        this.estSuperUser = false;
        this.estValide = estValide;
        this.surnom = prenom;
        this.permissions = "";
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public String getProfession() {
        return profession;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean estAdmin() {
        return estAdmin;
    }

    public boolean estSuperUser() {
        return estSuperUser;
    }

    public boolean estValide() {
        return estValide;
    }

    public String getSurnom() {
        return surnom;
    }

    public String getPermissions() {
        return permissions;
    }

    // Setters
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean estAdmin) {
        this.estAdmin = estAdmin;
    }

    public void setSuperUser(boolean estSuperUser) {
        this.estSuperUser = estSuperUser;
    }

    public void setValide(boolean estValide) {
        this.estValide = estValide;
    }

    public void setSurnom(String surnom) {
        this.surnom = surnom;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public static boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validatePassword(String password) {
        return !password.isEmpty();
    }

    // Méthode toString
    @Override
    public String toString() {
        return "User{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateNaissance='" + dateNaissance + '\'' +
                ", adresse=" + adresse.toString() +
                ", profession='" + profession + '\'' +
                ", email='" + email + '\'' +
                ", estAdmin=" + estAdmin +
                ", estSuperUser=" + estSuperUser +
                ", estValide=" + estValide +
                ", surnom=" + surnom +
                ", permissions=" + permissions +
                '}';
    }

    @Override
    public int compareTo(Usager other) {
        return this.email.compareTo(other.email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Usager usager = (Usager) obj;
        return email != null ? email.equals(usager.email) : usager.email == null;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

    public void setEncryptedPin(String pin) {
        this.encryptedPin = encryptPin(pin);
    }
    public String getEncryptedPin() { return this.encryptedPin; }

    public boolean checkPin(String pin) {
        return encryptedPin.equals(encryptPin(pin));
    }

    private String encryptPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hash, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash PIN.", e);
        }
    }

}

