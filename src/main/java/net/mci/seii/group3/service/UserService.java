package net.mci.seii.group3.service;

import net.mci.seii.group3.model.User;
import net.mci.seii.group3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;

    @Autowired
    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<User> findAllUsers() {
        return repo.findAll();
    }

    public void saveUser(User user) {
        repo.save(user);
    }

    public void deleteUser(User user) {
        repo.delete(user);
    }

}
