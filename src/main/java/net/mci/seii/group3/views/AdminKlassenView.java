package net.mci.seii.group3.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import net.mci.seii.group3.service.KlassenService;
import net.mci.seii.group3.service.PersistenzService;

import java.util.Set;

@Route(value = "admin/klassen", layout = MainLayout.class)
public class AdminKlassenView extends VerticalLayout {

    private final Grid<String> schuelerGrid = new Grid<>();

    public AdminKlassenView() {
        setPadding(true);
        setSpacing(true);

        // Klassenliste
        Grid<String> klassenGrid = new Grid<>();
        klassenGrid.addColumn(String::toString).setHeader("Klassen");
        klassenGrid.setItems(KlassenService.getInstance().getAllKlassenNamen());

        // Auswahl + Anzeige Schüler einer Klasse
        ComboBox<String> klassenAuswahl = new ComboBox<>("Klasse auswählen");
        klassenAuswahl.setItems(KlassenService.getInstance().getAllKlassenNamen());
        klassenAuswahl.addValueChangeListener(e -> {
            Set<String> schueler = KlassenService.getInstance().getSchuelerEinerKlasse(e.getValue());
            schuelerGrid.setItems(schueler);
        });

        schuelerGrid.addColumn(String::toString).setHeader("Zugewiesene Schüler");

        // Neue Klasse hinzufügen
        TextField neueKlasse = new TextField("Neue Klasse");
        Button add = new Button("Klasse erstellen", e -> {
            if (!neueKlasse.isEmpty()) {
                KlassenService.getInstance().addKlasse(neueKlasse.getValue().trim());
                PersistenzService.speichernAlles();
                neueKlasse.clear();
                refreshGrids(klassenGrid, klassenAuswahl);
            }
        });

        // Zurück zur Admin-Startseite
        Button zurück = new Button("Zurück", e ->
                getUI().ifPresent(ui -> ui.navigate("admin"))
        );

        add(new H3("Klassenverwaltung"),
            new HorizontalLayout(neueKlasse, add, zurück),
            klassenGrid,
            klassenAuswahl,
            schuelerGrid
        );
    }

    private void refreshGrids(Grid<String> klassenGrid, ComboBox<String> klassenBox) {
        var namen = KlassenService.getInstance().getAllKlassenNamen();
        klassenGrid.setItems(namen);
        klassenBox.setItems(namen);
    }
}
