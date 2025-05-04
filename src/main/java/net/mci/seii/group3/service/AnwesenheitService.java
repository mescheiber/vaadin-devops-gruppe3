package net.mci.seii.group3.service;

import java.util.HashSet;
import java.util.Set;

public class AnwesenheitService {

    private static final AnwesenheitService instance = new AnwesenheitService();
    private String kennwort;
    private final Set<String> ips = new HashSet<>();

    public static AnwesenheitService getInstance() {
        return instance;
    }

    public void setKennwort(String kennwort) {
        this.kennwort = kennwort;
        ips.clear();
    }

    public boolean prüfeKennwort(String eingabe) {
        return kennwort != null && kennwort.equals(eingabe);
    }

    public boolean nichtSchonAngemeldet(String ip) {
        return !ips.contains(ip);
    }

    public void registriere(String ip) {
        ips.add(ip);
    }
}
