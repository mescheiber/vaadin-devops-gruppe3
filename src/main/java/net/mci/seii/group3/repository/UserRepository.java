package net.mci.seii.group3.repository;

import net.mci.seii.group3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameAndPassword(String username, String password);
   
    List<User> findByRole(User.Role role);
    
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    
}
