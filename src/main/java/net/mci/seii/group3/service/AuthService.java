package net.mci.seii.group3.service;

import net.mci.seii.group3.model.User;
import net.mci.seii.group3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }

    public List<String> getAlleBenutzernamen(User.Role rolle) {
        return userRepository.findByRole(rolle).stream()
                .map(User::getUsername)
                .toList();
    }
}
