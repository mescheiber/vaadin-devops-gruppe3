package net.mci.seii.group3.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Veranstaltung {
    @Id
    private String id;

    private String name;
    private LocalDateTime startzeit;
    private String kennwort;
    private String hauptLehrer;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> teilnehmer = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "veranstaltung_teilnahmen")
    @MapKeyColumn(name = "username")
    @Column(name = "zeitpunkt")
    private Map<String, LocalDateTime> teilnahmen = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> zugewieseneLehrer = new HashSet<>();
    



    public Veranstaltung() {}

    public Veranstaltung(String name, String hauptLehrer, LocalDateTime startzeit) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.hauptLehrer = hauptLehrer;
        this.startzeit = startzeit;
        this.kennwort = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.zugewieseneLehrer.add(hauptLehrer);
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLehrer() {
        return hauptLehrer;
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
        this.hauptLehrer = lehrer;
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


    public Set<String> getZugewieseneLehrer() {
        return zugewieseneLehrer;
    }
}
