package net.mci.seii.group3.repository;

import net.mci.seii.group3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(User.Role role);
    List<User> findByRoleAndKlasse(User.Role role, String klasse);

}
