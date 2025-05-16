package net.mci.seii.group3.model;

import java.time.LocalDateTime;
import java.util.*;

public class Veranstaltung {

    private String id;
    private String name;
    private String lehrer;
    private String kennwort;
    private LocalDateTime startzeit;
    private Set<String> teilnehmer = new HashSet<>();
    private Map<String, LocalDateTime> teilnahmen = new HashMap<>();

    public Veranstaltung() {
        // Für Jackson
    }

    public Veranstaltung(String name, String lehrer, LocalDateTime startzeit) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.lehrer = lehrer;
        this.startzeit = startzeit;
        this.kennwort = generateKennwort();
    }

    private String generateKennwort() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLehrer() {
        return lehrer;
    }

    public String getKennwort() {
        return kennwort;
    }

    public LocalDateTime getStartzeit() {
        return startzeit;
    }

    public Set<String> getTeilnehmer() {
        return teilnehmer;
    }

    public Map<String, LocalDateTime> getTeilnahmen() {
        return teilnahmen;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLehrer(String lehrer) {
        this.lehrer = lehrer;
    }

    public void setKennwort(String kennwort) {
        this.kennwort = kennwort;
    }

    public void setStartzeit(LocalDateTime startzeit) {
        this.startzeit = startzeit;
    }

    public void setTeilnehmer(Set<String> teilnehmer) {
        this.teilnehmer = teilnehmer;
    }

    public void setTeilnahmen(Map<String, LocalDateTime> teilnahmen) {
        this.teilnahmen = teilnahmen;
    }

    private Set<String> zugewieseneLehrer = new HashSet<>();

    public Set<String> getZugewieseneLehrer() {
        return zugewieseneLehrer;
    }

}
