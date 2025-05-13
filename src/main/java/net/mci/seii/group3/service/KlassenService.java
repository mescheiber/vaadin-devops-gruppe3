package net.mci.seii.group3.service;

import java.util.*;

public class KlassenService {

    private static final KlassenService instance = new KlassenService();
    private final Map<String, Set<String>> klassen = new HashMap<>();
    
    public void setAlleKlassen(Map<String, Set<String>> neueKlassen) {
    klassen.clear();
    if (neueKlassen != null) {
        klassen.putAll(neueKlassen);
    }
}


    public static KlassenService getInstance() {
        return instance;
    }

    public void addKlasse(String name) {
        if (name != null && !name.isBlank()) {
            klassen.putIfAbsent(name.trim(), new HashSet<>());
        }
    }

    public void schuelerZurKlasse(String klasse, String schueler) {
        klassen.computeIfAbsent(klasse.trim(), k -> new HashSet<>()).add(schueler);
    }

    public Set<String> getSchuelerEinerKlasse(String klasse) {
        return klassen.getOrDefault(klasse.trim(), Collections.emptySet());
    }

    public Set<String> getAllKlassenNamen() {
        return klassen.keySet();
    }

    public Map<String, Set<String>> getAlle() {
        return klassen;
    }

    public void setAlle(Map<String, Set<String>> daten) {
        klassen.clear();
        klassen.putAll(daten);
    }
    
    public void removeBenutzerAusAllenKlassen(String benutzername) {
    klassen.values().forEach(s -> s.remove(benutzername));
}

public String getKlasseVonSchueler(String benutzername) {
    return klassen.entrySet().stream()
        .filter(e -> e.getValue().contains(benutzername))
        .map(Map.Entry::getKey)
        .findFirst()
        .orElse(null);
}

public void aktualisiereBenutzername(String alterName, String neuerName) {
    for (Set<String> schueler : klassen.values()) {
        if (schueler.remove(alterName)) {
            schueler.add(neuerName);
        }
    }
}



}
