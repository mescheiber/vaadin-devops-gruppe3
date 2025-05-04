package net.mci.seii.group3.service;

import net.mci.seii.group3.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthService {
    private static final AuthService instance = new AuthService();
    private final Map<String, User> users = new HashMap<>();

    public static AuthService getInstance() {
        return instance;
    }

    public boolean register(String username, String password, User.Role role) {
        if (users.containsKey(username)) return false;
        users.put(username, new User(username, password, role));
        return true;
    }

    public User login(String username, String password) {
        User u = users.get(username);
        return (u != null && u.getPassword().equals(password)) ? u : null;
    }

    public List<String> getAlleBenutzernamen(User.Role rolle) {
        return users.values().stream()
            .filter(u -> u.getRole() == rolle)
            .map(User::getUsername)
            .collect(Collectors.toList());
    }
}
