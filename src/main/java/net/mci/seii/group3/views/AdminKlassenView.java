package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import net.mci.seii.group3.model.User;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.repository.UserRepository;
import net.mci.seii.group3.repository.SchulklassenRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "admin/klassen", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminKlassenView extends VerticalLayout {

    private final KlassenService klassenService;
    private final Grid<String> schuelerGrid = new Grid<>();


    @Autowired
    public AdminKlassenView(KlassenService klassenService,
                        UserRepository userRepository,
                        SchulklassenRepository klassenRepository) {
    this.klassenService = klassenService;

        setPadding(true);
        setSpacing(true);

        // Klassenliste
        Grid<String> klassenGrid = new Grid<>();
        klassenGrid.addColumn(String::toString).setHeader("Klassen");
        klassenGrid.setItems(klassenService.getAllKlassenNamen());
        klassenGrid.addClassName("grid");

        // Auswahl + Anzeige Schüler einer Klasse
        ComboBox<String> klassenAuswahl = new ComboBox<>("Klasse auswählen");
        klassenAuswahl.setItems(klassenService.getAllKlassenNamen());
        klassenAuswahl.addValueChangeListener(e -> {
            List<User> schueler = userRepository.findByRoleAndKlasse(User.Role.STUDENT, e.getValue());
            schuelerGrid.setItems(schueler.stream().map(User::getUsername).toList());

        });

        schuelerGrid.addColumn(String::toString).setHeader("Zugewiesene Schüler");

        // Neue Klasse hinzufügen
        TextField neueKlasse = new TextField();
        neueKlasse.setPlaceholder("Neue Klasse");
        neueKlasse.addClassName("form-field");
        Button add = new Button("Klasse erstellen", e -> {
            if (!neueKlasse.isEmpty()) {
                klassenService.addKlasse(neueKlasse.getValue().trim());
                neueKlasse.clear();
                refreshGrids(klassenGrid, klassenAuswahl);
            }
        });
        add.setHeight("40px");
        add.addClassName("button");



        HorizontalLayout aktionLayout = new HorizontalLayout(neueKlasse, add);
        aktionLayout.setAlignItems(Alignment.BASELINE);
        aktionLayout.setSpacing(true);

        H3 ueberschrift = new H3("Klassenverwaltung");
        ueberschrift.addClassName("title");

        add(
            ueberschrift,
            aktionLayout,
            klassenGrid,
            klassenAuswahl,
            schuelerGrid
        );
    }

    private void refreshGrids(Grid<String> klassenGrid, ComboBox<String> klassenBox) {
        var namen = klassenService.getAllKlassenNamen();
        klassenGrid.setItems(namen);
        klassenBox.setItems(namen);
    }
}