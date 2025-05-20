package net.mci.seii.group3.repository;

import net.mci.seii.group3.model.Veranstaltung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VeranstaltungRepository extends JpaRepository<Veranstaltung, String> {
    List<Veranstaltung> findByZugewieseneLehrerContains(String lehrer);
    
    
    
    // Für Hauptlehrer (funktioniert automatisch)
    List<Veranstaltung> findByHauptLehrer(String hauptLehrer);

    // Für alle Lehrer, auch zugewiesene (benötigt eigene Query)
    @Query("SELECT v FROM Veranstaltung v WHERE :lehrer MEMBER OF v.zugewieseneLehrer")
    List<Veranstaltung> findByZugewiesenerLehrer(String lehrer);
}

