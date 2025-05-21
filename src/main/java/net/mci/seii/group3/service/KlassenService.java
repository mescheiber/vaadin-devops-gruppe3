package net.mci.seii.group3.service;

import net.mci.seii.group3.model.Schulklasse;
import net.mci.seii.group3.repository.SchulklassenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class KlassenService {

    private final SchulklassenRepository schulklassenRepository;

    @Autowired
    public KlassenService(SchulklassenRepository schulklassenRepository) {
        this.schulklassenRepository = schulklassenRepository;
    }

    public List<String> getAllKlassenNamen() {
        return schulklassenRepository.findAll().stream().map(Schulklasse::getName).toList();
    }

    public Set<String> getSchuelerEinerKlasse(String klassenname) {
        return schulklassenRepository.findById(klassenname)
                .map(Schulklasse::getSchueler)
                .orElse(Set.of());
    }

    public void addKlasse(String name) {
        if (!schulklassenRepository.existsById(name)) {
            schulklassenRepository.save(new Schulklasse(name));
        }
    }

    public void schuelerZurKlasse(String klasse, String schueler) {
        Optional<Schulklasse> k = schulklassenRepository.findById(klasse);
        if (k.isPresent()) {
            k.get().getSchueler().add(schueler);
            schulklassenRepository.save(k.get());
        }
    }

    public Optional<Schulklasse> getKlasse(String name) {
        return schulklassenRepository.findById(name);
    }

    public String getKlasseVonSchueler(String benutzername) {
        return schulklassenRepository.findAll().stream()
                .filter(k -> k.getSchueler().contains(benutzername))
                .map(Schulklasse::getName)
                .findFirst()
                .orElse(null);
    }
    
    public Map<String, Set<String>> findAllAsMap() {
        return schulklassenRepository.findAll().stream()
            .collect(Collectors.toMap(
                Schulklasse::getName,
                Schulklasse::getSchueler
            ));
    }
}
