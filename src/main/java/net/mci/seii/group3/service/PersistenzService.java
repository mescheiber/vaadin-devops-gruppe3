package net.mci.seii.group3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import net.mci.seii.group3.repository.SchulklassenRepository;
import net.mci.seii.group3.repository.UserRepository;
import net.mci.seii.group3.repository.VeranstaltungsRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PersistenzService {

    private final UserRepository userRepository;
    private final VeranstaltungsRepository veranstaltungsRepository;
    private final SchulklassenRepository klassenRepository;
    private final KlassenService klassenService;

    public PersistenzService(UserRepository userRepository,
                             VeranstaltungsRepository veranstaltungsRepository,
                             SchulklassenRepository klassenRepository,
                         KlassenService klassenService) {
        this.userRepository = userRepository;
        this.veranstaltungsRepository = veranstaltungsRepository;
        this.klassenRepository = klassenRepository;
        this.klassenService = klassenService;
    }

    public static class Speicherbild {
        public List<User> users;
        public List<Veranstaltung> veranstaltungen;
        public Map<String, Set<String>> klassen;

        public Speicherbild() {}

        public Speicherbild(List<User> users, List<Veranstaltung> veranstaltungen, Map<String, Set<String>> klassen) {
            this.users = users;
            this.veranstaltungen = veranstaltungen;
            this.klassen = klassen;
        }
    }

    public void speichernAlles() {
        speichern(
            userRepository.findAll(),
            veranstaltungsRepository.findAll(),
            klassenService.findAllAsMap()
        );
    }

    public void speichern(List<User> users, List<Veranstaltung> veranstaltungen, Map<String, Set<String>> klassen) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
