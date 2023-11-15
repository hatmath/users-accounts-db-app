package com.hatmath.connect;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UsagerFileUtil implements UsagerDataUtil {
    private static final String FILENAME = "usagers.dat";
    private static UsagerFileUtil instance;
    private final Context context;

    // Constructeur privé pour empêcher l'instanciation directe
    private UsagerFileUtil() {
        this.context = UsersAccountsApp.getAppContext();
    }

    // Méthode pour obtenir l'instance unique de la classe
    public static synchronized UsagerFileUtil getInstance() {
        if (instance == null) {
            instance = new UsagerFileUtil();
        }
        return instance;
    }

    @Override
    public void writeUsagers(List<Usager> usagers) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            // Écrivez la liste complète d'objets Usager dans le fichier
            objectOutputStream.writeObject(usagers);

            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Usager> readUsagers() {
        List<Usager> usagers = new ArrayList<>();

        try {
            FileInputStream fileInputStream = context.openFileInput(FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            // Lisez la liste complète d'objets Usager depuis le fichier
            Object object = objectInputStream.readObject();
            if (object instanceof List<?>) {
                List<?> objectList = (List<?>) object;
                for (Object item : objectList) {
                    if (item instanceof Usager) {
                        usagers.add((Usager) item);
                    }
                }
            }

            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usagers;
    }
}

