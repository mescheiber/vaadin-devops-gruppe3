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

    public Veranstaltung createVeranstaltung(String name, String lehrer, LocalDateTime startzeit) {
        Veranstaltung v = new Veranstaltung(name, lehrer, startzeit);
        veranstaltungen.put(v.getId(), v);
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
        }
    }

    public boolean prüfenTeilnahme(String veranstaltungId, String code, String username) {
        Veranstaltung v = veranstaltungen.get(veranstaltungId);
        if (v == null || !v.getTeilnehmer().contains(username)) return false;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = v.getStartzeit();
        if (now.isBefore(start.minusMinutes(30)) || now.isAfter(start.plusMinutes(60))) {
            return false;
        }
        if (v.getKennwort().equalsIgnoreCase(code)) {
            v.getTeilnahmen().put(username, LocalDateTime.now());
            return true;
        }
        return false;
    }
}
