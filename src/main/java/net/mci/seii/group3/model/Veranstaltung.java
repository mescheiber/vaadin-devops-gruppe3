package net.mci.seii.group3.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Veranstaltung {
    private String id = UUID.randomUUID().toString();
    private String name;
    private String lehrer;
    private String kennwort;
    private LocalDateTime startzeit;
    private Set<String> teilnehmer = new HashSet<>();
    private Map<String, LocalDateTime> teilnahmen = new HashMap<>();

    public Veranstaltung(String name, String lehrer, LocalDateTime startzeit) {
        this.name = name;
        this.lehrer = lehrer;
        this.startzeit = startzeit;
        this.kennwort = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLehrer() { return lehrer; }
    public String getKennwort() { return kennwort; }
    public LocalDateTime getStartzeit() { return startzeit; }
    public Set<String> getTeilnehmer() { return teilnehmer; }
    public Map<String, LocalDateTime> getTeilnahmen() { return teilnahmen; }
}
