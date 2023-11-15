package com.hatmath.connect;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class Adresse implements Serializable {
    private String numero;
    private String rue;
    private String ville;

    private String province;
    private String codePostal;

    private String pays;

    // Constructeur
    public Adresse() {
        this.numero = "";
        this.rue = "";
        this.ville = "";
        this.province = "";
        this.codePostal = "";
        this.pays = "";
    }

    public Adresse(String numero, String rue, String ville, String province, String codePostal, String pays) {
        this.numero = numero;
        this.rue = rue;
        this.ville = ville;
        this.province = province;
        this.codePostal = codePostal;
        this.pays = pays;
    }

    // Getters
    public String getNumero() {
        return numero;
    }

    public String getRue() {
        return rue;
    }

    public String getVille() {
        return ville;
    }

    public String getProvince() {
        return province;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public String getPays() {
        return pays;
    }

    // Setters
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public void setProvince(String province) {
        this.province = province;
    }


    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    //    A Canadian postal code is a six-character string.
    //    A valid Canadian postcode is –
    //    - in the format A1A 1A1, where A is a letter and 1 is a digit.
    //    - a space separates the third and fourth characters.
    //    - do not include the letters D, F, I, O, Q or U.
    //    - the first position does not make use of the letters W or Z.
    public boolean codePostalCanadienValide(String codePostal) {
        String codePostalUpperCaseNoSpace = codePostal.toUpperCase();
        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(codePostalUpperCaseNoSpace).matches();
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    // Un pays est valide s'il ce trouve dans la liste de tout les pays
    public boolean paysValide(String pays, Context context) {
        String JSON_DATA = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.countrydialcode);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }

            JSON_DATA = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String jsonString = JSON_DATA;
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String countrycode = jsonObject.optString("code");
                String dialnumber = jsonObject.optString("dial_code");
                String countryname = jsonObject.optString("name");

                if (countryname.equalsIgnoreCase(pays)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public String toString() {
        return numero + "," + rue + "," + ville + "," + province + "," + codePostal + "," + pays;
    }

    public static Adresse fromString(String str) {
        String[] parts = str.split(",\\s*", -1); // conserve les chaînes vides
        if (parts.length != 6) {
            throw new IllegalArgumentException("String mal formée: " + str);
        }
        return new Adresse(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
    }

}
