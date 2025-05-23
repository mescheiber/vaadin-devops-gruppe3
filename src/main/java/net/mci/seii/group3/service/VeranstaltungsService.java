package net.mci.seii.group3.service;

import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.utils.NetzwerkChecker;
import net.mci.seii.group3.repository.VeranstaltungsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VeranstaltungsService {

    private final VeranstaltungsRepository veranstaltungsRepository;

    @Autowired
    public VeranstaltungsService(VeranstaltungsRepository veranstaltungsRepository) {
        this.veranstaltungsRepository = veranstaltungsRepository;
    }

    public List<Veranstaltung> getAlleVeranstaltungen() {
        return veranstaltungsRepository.findAll();
    }

    public List<Veranstaltung> getVeranstaltungenFürLehrer(String lehrer) {
        return veranstaltungsRepository.findByZugewieseneLehrerContaining(lehrer);
    }

    public Veranstaltung createVeranstaltung(String name, String hauptLehrer, LocalDateTime startzeit) {
        Veranstaltung v = new Veranstaltung(name, hauptLehrer, startzeit);
        return veranstaltungsRepository.save(v);
    }

    public boolean prüfenTeilnahme(String veranstaltungsId, String kennwort, String username, String ipAdresse) {
    Optional<Veranstaltung> optional = veranstaltungsRepository.findById(veranstaltungsId);
    if (optional.isEmpty()) {
        System.out.println("❌ Veranstaltung nicht gefunden: " + veranstaltungsId);
        return false;
    }

    Veranstaltung v = optional.get();
    LocalDateTime now = LocalDateTime.now();

    // Zeitprüfung
    if (now.isBefore(v.getStartzeit().minusMinutes(30)) || now.isAfter(v.getStartzeit().plusMinutes(60))) {
        System.out.println("❌ Zeitfenster ungültig. Jetzt: " + now + ", Start: " + v.getStartzeit());
        return false;
    }

    // Netzwerkprüfung
    if (!NetzwerkChecker.istImUniNetz(ipAdresse)) {
        System.out.println("❌ IP-Adresse nicht erlaubt: " + ipAdresse);
        return false;
    }

    // Kennwort prüfen
    if (!v.getKennwort().equalsIgnoreCase(kennwort)) {
        System.out.println("❌ Falsches Kennwort: " + kennwort + " vs " + v.getKennwort());
        return false;
    }

    // Bereits teilgenommen?
    if (v.getTeilnahmen().containsKey(username)) {
        System.out.println("❌ User hat bereits teilgenommen: " + username);
        return false;
    }

    // Teilnahme erfolgreich
    v.getTeilnehmer().add(username);
    v.getTeilnahmen().put(username, now);
    veranstaltungsRepository.save(v);
    System.out.println("✅ Teilnahme erfolgreich für: " + username);
    return true;
}


    public void teilnehmerZuweisen(String veranstaltungsId, String username) {
        veranstaltungsRepository.findById(veranstaltungsId).ifPresent(v -> {
            v.getTeilnehmer().add(username);
            veranstaltungsRepository.save(v);
        });
    }

    public Optional<Veranstaltung> findById(String id) {
        return veranstaltungsRepository.findById(id);
    }

}
