package net.mci.seii.group3.service;

import net.mci.seii.group3.model.Veranstaltung;

import java.time.LocalDateTime;
import java.util.*;

public class VeranstaltungsService {

    private static final VeranstaltungsService instance = new VeranstaltungsService();
    private final Map<String, Veranstaltung> veranstaltungen = new HashMap<>();

    public static VeranstaltungsService getInstance() {
        return instance;
    }

    // Konstruktor lädt gespeicherte Daten (falls vorhanden)
    private VeranstaltungsService() {
        PersistenzService.Speicherbild data = PersistenzService.laden();
        if (data != null && data.veranstaltungen != null) {
            data.veranstaltungen.forEach(v -> veranstaltungen.put(v.getId(), v));
        }
    }

    public Veranstaltung createVeranstaltung(String name, String lehrer, LocalDateTime startzeit) {
        Veranstaltung v = new Veranstaltung(name, lehrer, startzeit);
        veranstaltungen.put(v.getId(), v);
        speichern(); // nach Erstellung speichern
        return v;
    }

    public List<Veranstaltung> getVeranstaltungenFürLehrer(String lehrer) {
        return veranstaltungen.values().stream()
                .filter(v -> v.getLehrer().equals(lehrer))
                .sorted(Comparator.comparing(Veranstaltung::getStartzeit))
                .toList();
    }

    public List<Veranstaltung> getAlleVeranstaltungen() {
        return new ArrayList<>(veranstaltungen.values());
    }

    public void teilnehmerZuweisen(String veranstaltungId, String username) {
        Veranstaltung v = veranstaltungen.get(veranstaltungId);
        if (v != null) {
            v.getTeilnehmer().add(username);
            speichern(); // nach Zuweisung speichern
        }
    }

    public boolean prüfenTeilnahme(String veranstaltungId, String code, String username) {
        Veranstaltung v = veranstaltungen.get(veranstaltungId);
        if (v == null || !v.getTeilnehmer().contains(username)) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = v.getStartzeit();
        if (now.isBefore(start.minusMinutes(30)) || now.isAfter(start.plusMinutes(60))) {
            return false;
        }
        if (v.getKennwort().equalsIgnoreCase(code)) {
            v.getTeilnahmen().put(username, LocalDateTime.now());
            speichern(); // nach Teilnahme speichern
            return true;
        }
        return false;
    }

    // zentrale Speichermethode aufrufen
    private void speichern() {
        PersistenzService.speichern(
                AuthService.getInstance().getAllUsers(),
                getAlleVeranstaltungen(),
                KlassenService.getInstance().getAlle()
        );

    }

    public void setAll(List<Veranstaltung> list) {
        veranstaltungen.clear();
        if (list != null) {
            for (Veranstaltung v : list) {
                veranstaltungen.put(v.getId(), v);
            }
        }
    }
    
    public Veranstaltung getVeranstaltungById(String id) {
    return veranstaltungen.get(id);
}


}
