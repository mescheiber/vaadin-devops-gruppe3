package net.mci.seii.group3.service;

import net.mci.seii.group3.model.Veranstaltung;
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

    public boolean prüfenTeilnahme(String veranstaltungsId, String kennwort, String benutzer) {
        var opt = veranstaltungsRepository.findById(veranstaltungsId);
        if (opt.isEmpty()) return false;
        Veranstaltung v = opt.get();
        if (!v.getKennwort().equals(kennwort)) return false;
        v.getTeilnahmen().put(benutzer, LocalDateTime.now());
        veranstaltungsRepository.save(v);
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
