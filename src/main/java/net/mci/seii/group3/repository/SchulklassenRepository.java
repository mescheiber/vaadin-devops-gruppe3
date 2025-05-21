package net.mci.seii.group3.repository;

import net.mci.seii.group3.model.Schulklasse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchulklassenRepository extends JpaRepository<Schulklasse, String> {
}
