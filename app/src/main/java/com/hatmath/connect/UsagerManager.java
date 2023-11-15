package com.hatmath.connect;

import java.util.ArrayList;
import java.util.List;


public class UsagerManager {
    public enum DataType {
        FILE,
        DB
    }
    private final DataType type = DataType.DB;
    private UsagerDataUtil dataUtil;
    private List<Usager> usagersCache;
    private final Object LOCK = new Object();
    private final Usager superUser = new Usager("super@super.com", "super", true, true); // Usager qui peux tout aka GOD ;)

    public UsagerManager() {
        initializeDataUtil(type == DataType.FILE ? UsagerFileUtil.getInstance() : UsagerDbUtil.getInstance());
    }

    // Initialiser l'objet UsagerManager avec un gestionnaire de données spécifique
    private void initializeDataUtil(UsagerDataUtil dataUtil) {
        this.dataUtil = dataUtil;
        this.usagersCache = dataUtil.readUsagers();
        superUser.setSuperUser(true);
        if (!usagersCache.contains(superUser)) {
            addUsager(superUser);
        }
    }

    public Usager getAuthenticateUser(String email, String password) {
        synchronized (LOCK) {
            for (Usager usager : usagersCache) {
                if (usager.getEmail().equalsIgnoreCase(email) && usager.getPassword().equals(password)) {
                    return usager;
                }
            }
        }
        return null;
    }

    private List<Usager> getUsagers() {
        synchronized (LOCK) {
            return new ArrayList<>(usagersCache);
        }
    }

    public List<Usager> getFilteredUsagers(Usager usager) {
        List<Usager> filteredUsagers = new ArrayList<>();

        if (getUsager(usager.getEmail()) != null) {
            if (usager.estSuperUser()) {
                // Si l'usager est super, retourne tous les usagers
                return new ArrayList<>(usagersCache);
            } else if (usager.estAdmin()) {
                // Si l'usager est admin, ajoutez-le à la liste
                filteredUsagers.add(usager);
                for (Usager u : usagersCache) {
                    // Ajoutez tous les usagers qui ne sont ni admin ni super
                    if (!u.estAdmin() && !u.estSuperUser()) {
                        filteredUsagers.add(u);
                    }
                }
            } else {
                // Si l'usager n'est ni admin ni super, ajoutez uniquement lui-même à la liste
                filteredUsagers.add(usager);
            }
        }

        return filteredUsagers;
    }

    public Usager getUsager(String email) {
        synchronized (LOCK) {
            for (Usager usager : usagersCache) {
                if (usager.getEmail().equalsIgnoreCase(email)) {
                    return usager;
                }
            }
            return null;
        }
    }

    public void addUsager(Usager usager) {
        synchronized (LOCK) {
            usagersCache.add(usager);
            dataUtil.writeUsagers(usagersCache);
        }
    }

    public void removeUsager(Usager usager) {
        synchronized (LOCK) {
            usagersCache.remove(usager);
            dataUtil.writeUsagers(usagersCache);
        }
    }

    public void updateUsager(Usager oldUsager, Usager newUsager) {
        synchronized (LOCK) {
            int index = usagersCache.indexOf(oldUsager);
            if (index != -1) {
                usagersCache.set(index, newUsager);
                dataUtil.writeUsagers(usagersCache);
            }
        }
    }

}