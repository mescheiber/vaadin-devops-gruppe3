package net.mci.seii.group3.model;

import jakarta.persistence.*;

@Entity
public class Schulklasse {
    @Id
    private String name;

    public Schulklasse() {}

    public Schulklasse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
