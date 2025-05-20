package net.mci.seii.group3.service;

import com.vaadin.flow.server.VaadinSession;
import net.mci.seii.group3.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthService {

    private static final AuthService instance = new AuthService();
    private final Map<String, User> users = new HashMap<>();
    private User angemeldeterBenutzer;

    public static AuthService getInstance() {
        return instance;
    }

    // Konstruktor lädt vorhandene Daten (falls vorhanden)
    private AuthService() {
        users.put("admin", new User("admin", "admin123", User.Role.ADMIN));
        PersistenzService.Speicherbild data = PersistenzService.laden();
        if (data != null && data.users != null) {
            data.users.forEach(user -> users.put(user.getUsername(), user));
        }
    }

    public boolean register(String username, String password, User.Role role) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, new User(username, password, role));
        // Speichern nach erfolgreicher Registrierung
        PersistenzService.speichern(
                getAllUsers(),
                VeranstaltungsService.getInstance().getAlleVeranstaltungen(),
                KlassenService.getInstance().getAlle()
        );

        return true;
    }

    public User login(String username, String password) {
        User u = users.get(username);
        if (u != null && u.getPassword().equals(password)) {
            angemeldeterBenutzer = u; // <== merken!
            return u;
        }
        return null;
    }

    public List<String> getAlleBenutzernamen(User.Role rolle) {
        return users.values().stream()
                .filter(u -> u.getRole() == rolle)
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

    // Neue Methode für JSON-Speicherung
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }

    public void setAll(List<User> userListe) {
        users.clear();
        if (userListe != null) {
            for (User u : userListe) {
                users.put(u.getUsername(), u);
            }
        }

        // Admin-User nachladen, falls gelöscht
        if (!users.containsKey("admin")) {
            users.put("admin", new User("admin", "admin123", User.Role.ADMIN));
        }
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 6) {
            return false; // optionally enforce rules
        }

        User user = getUserByName(username);

        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);

            PersistenzService.speichern(
                    getAllUsers(),
                    VeranstaltungsService.getInstance().getAlleVeranstaltungen(),
                    KlassenService.getInstance().getAlle()
            );

            return true;
        }

        return false;
    }


    public User getAngemeldeterBenutzer;
    
    public void setAngemeldeterBenutzer(User user) {
    this.angemeldeterBenutzer = user;
}

public User getAngemeldeterBenutzer() {
    return this.angemeldeterBenutzer;
}

    public void logout() {
        // Clear in-memory user
        this.angemeldeterBenutzer = null;

        // Clear user from Vaadin session
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(User.class, null);
            session.close();
        }
    }


    public User getUserByName(String username) {
    return users.get(username);
}


}
