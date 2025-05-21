package net.mci.seii.group3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_table")
public class User {
    @Id
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String klasse;

    public enum Role {
        ADMIN, TEACHER, STUDENT
    }

    public User() {}

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getKlasse() {
    return klasse;
}

public void setKlasse(String klasse) {
    this.klasse = klasse;
}
}
