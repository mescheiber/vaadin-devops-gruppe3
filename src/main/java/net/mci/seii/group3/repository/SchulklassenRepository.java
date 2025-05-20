package net.mci.seii.group3.repository;

import net.mci.seii.group3.model.Schulklasse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SchulklassenRepository extends JpaRepository<Schulklasse, String> {
   Optional<Schulklasse> findByName(String name);
}
