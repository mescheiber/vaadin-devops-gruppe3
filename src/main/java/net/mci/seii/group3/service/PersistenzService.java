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
import java.time.LocalDateTime;
import java.util.*;

public class PersistenzService {

    private static final Logger log = LoggerFactory.getLogger(PersistenzService.class);
    //private static final String DATEI_NAME = "daten.json";
    private static final String DATEI_NAME = System.getProperty("java.io.tmpdir") + File.separator + "daten.json";

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
            log.warn("Datei '{}' existiert nicht – Standarddaten werden erstellt", DATEI_NAME);
            Speicherbild standard = erstelleStandarddaten();
            speichern(standard.users, standard.veranstaltungen, standard.klassen);
            return standard;
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

    private static Speicherbild erstelleStandarddaten() {
        List<User> users = List.of(
                new User("Axel", "123", User.Role.STUDENT),
                new User("Ramona", "123", User.Role.STUDENT),
                new User("Wegi", "123", User.Role.STUDENT),
                new User("admin", "admin123", User.Role.ADMIN),
                new User("Matthias", "123", User.Role.STUDENT),
                new User("Hans", "123", User.Role.STUDENT),
                new User("Daniela", "123", User.Role.STUDENT),
                new User("Andrea", "123", User.Role.TEACHER)
        );

        List<Veranstaltung> veranstaltungen = new ArrayList<>();

        veranstaltungen.add(new Veranstaltung("4c6563ca-eee2-4705-a85b-e63fe7c35673", "BigData1","Andrea", LocalDateTime.of(2025, 5, 15, 21, 0), "BigData1"));
        veranstaltungen.add(new Veranstaltung("7bad6713-f498-47b6-873d-c8a272a9b1d9", "BigData2","Andrea", LocalDateTime.of(2025, 5, 15, 21, 0), "BigData2"));
        veranstaltungen.add(new Veranstaltung("4db3bfdf-0b94-4612-91d4-74e68e71122f", "BigData3","Andrea", LocalDateTime.of(2025, 5, 15, 21, 0), "BigData3"));
        veranstaltungen.add(new Veranstaltung("4c6563ca-eee2-4705-a85b-e63fe7c35673", "BigData4","Andrea", LocalDateTime.of(2025, 5, 15, 21, 0), "BigData4"));

        Map<String, Set<String>> klassen = new HashMap<>();
        klassen.put("DIBSE2023", new HashSet<>(Arrays.asList("Ramona", "Wegi", "Daniela")));
        klassen.put("DIBSE2024", new HashSet<>(Arrays.asList("Axel", "Hans")));
        klassen.put("DIBSE2025", new HashSet<>());
        klassen.put("1234", new HashSet<>());

        return new Speicherbild(users, veranstaltungen, klassen);
    }
}
