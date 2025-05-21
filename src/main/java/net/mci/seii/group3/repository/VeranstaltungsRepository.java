package net.mci.seii.group3.repository;

import net.mci.seii.group3.model.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeranstaltungsRepository extends JpaRepository<Veranstaltung, String> {
    List<Veranstaltung> findByZugewieseneLehrerContaining(String lehrer);
    List<Veranstaltung> findByHauptLehrer(String hauptLehrer);
}
