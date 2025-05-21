package net.mci.seii.group3.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Schulklasse {
    @Id
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> schueler = new HashSet<>();

    public Schulklasse() {}

    public Schulklasse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<String> getSchueler() {
        return schueler;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSchueler(Set<String> schueler) {
        this.schueler = schueler;
    }
}
