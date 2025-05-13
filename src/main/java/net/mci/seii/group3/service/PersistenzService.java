package net.mci.seii.group3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.model.Veranstaltung;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PersistenzService {

    private static final Logger log = LoggerFactory.getLogger(PersistenzService.class);
    private static final String DATEI_NAME = "daten.json";

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

    public static void speichern(List<User> users, List<Veranstaltung> veranstaltungen, Map<String, Set<String>> klassen) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Speicherbild speicherbild = new Speicherbild(users, veranstaltungen, klassen);
        try {
            mapper.writeValue(new File(DATEI_NAME), speicherbild);
            log.info("Daten erfolgreich gespeichert nach '{}'", DATEI_NAME);
        } catch (IOException e) {
            log.error("Fehler beim Speichern der Datei '{}'", DATEI_NAME, e);
        }
    }

    public static Speicherbild laden() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        File file = new File(DATEI_NAME);
        if (!file.exists()) {
            log.warn("Datei '{}' existiert nicht – keine Daten geladen", DATEI_NAME);
            return null;
        }

        try {
            Speicherbild data = mapper.readValue(file, Speicherbild.class);
            log.info("Daten erfolgreich geladen aus '{}'", DATEI_NAME);
            return data;
        } catch (IOException e) {
            log.error("Fehler beim Laden der Datei '{}'", DATEI_NAME, e);
            return null;
        }
    }

    public static void speichernAlles() {
        speichern(
            AuthService.getInstance().getAllUsers(),
            VeranstaltungsService.getInstance().getAlleVeranstaltungen(),
            KlassenService.getInstance().getAlle()
        );
    }
}
