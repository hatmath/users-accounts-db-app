// Nom de la classe : UsagerDataUtil
// Description: Interface qui définie un ensemble de méthodes que les classes doivent implémenter
// pour manipuler les données propre à la méthode de persistance choisie

package com.hatmath.connect;

import java.util.List;

public interface UsagerDataUtil {
    void writeUsagers(List<Usager> usagers);
    List<Usager> readUsagers();
}
