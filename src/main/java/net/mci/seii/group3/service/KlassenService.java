package net.mci.seii.group3.service;

import net.mci.seii.group3.model.Schulklasse;
import net.mci.seii.group3.repository.UserRepository;
import net.mci.seii.group3.model.User;
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
    private final UserRepository userRepository;

    @Autowired
    public KlassenService(SchulklassenRepository schulklassenRepository, UserRepository userRepository) {
        this.schulklassenRepository = schulklassenRepository;
        this.userRepository = userRepository;
    }

    public List<String> getAllKlassenNamen() {
        return schulklassenRepository.findAll().stream().map(Schulklasse::getName).toList();
    }

    public Set<String> getSchuelerEinerKlasse(String klassenname) {
        return userRepository.findByRoleAndKlasse(User.Role.STUDENT, klassenname)
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
    }

    public void addKlasse(String name) {
        if (!schulklassenRepository.existsById(name)) {
            schulklassenRepository.save(new Schulklasse(name));
        }
    }

    public void schuelerZurKlasse(String klasse, String benutzername) {
        userRepository.findById(benutzername).ifPresent(user -> {
            if (user.getRole() == User.Role.STUDENT) {
                user.setKlasse(klasse);
                userRepository.save(user);
            }
        });
    }

    public Optional<Schulklasse> getKlasse(String name) {
        return schulklassenRepository.findById(name);
    }

    public String getKlasseVonSchueler(String benutzername) {
        return userRepository.findById(benutzername)
                .map(User::getKlasse)
                .orElse(null);
    }

    public Map<String, Set<String>> findAllAsMap() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == User.Role.STUDENT)
                .filter(user -> user.getKlasse() != null)
                .collect(Collectors.groupingBy(
                        User::getKlasse,
                        Collectors.mapping(User::getUsername, Collectors.toSet())
                ));
    }

}
